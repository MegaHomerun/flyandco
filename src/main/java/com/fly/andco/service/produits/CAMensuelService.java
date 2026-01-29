package com.fly.andco.service.produits;

import com.fly.andco.model.produits.VenteProduit;
import com.fly.andco.repository.produits.VenteProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CAMensuelService {

    @PersistenceContext
    private EntityManager entityManager;

    private final VenteProduitRepository venteProduitRepository;

    @Autowired
    public CAMensuelService(VenteProduitRepository venteProduitRepository) {
        this.venteProduitRepository = venteProduitRepository;
    }

    /**
     * Calcule le CA mensuel consolidé (Tickets + Diffusions + Produits)
     */
    public CAMensuelDTO getCAMensuel(int annee, int mois) {
        CAMensuelDTO ca = new CAMensuelDTO(annee, mois);
        
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDate debutMois = ym.atDay(1);
        LocalDate finMois = ym.atEndOfMonth();
        
        // CA Tickets (réservations confirmées)
        Object[] ticketsResult = getCATickets(debutMois, finMois);
        ca.setNbTickets(((Number) ticketsResult[0]).intValue());
        ca.setCaTickets((BigDecimal) ticketsResult[1]);
        
        // CA Diffusions
        Object[] diffusionsResult = getCADiffusions(debutMois, finMois);
        ca.setNbDiffusions(((Number) diffusionsResult[0]).intValue());
        ca.setCaDiffusions((BigDecimal) diffusionsResult[1]);
        
        // CA Produits
        Object[] produitsResult = getCAProduits(debutMois, finMois);
        ca.setNbProduits(((Number) produitsResult[0]).intValue());
        ca.setCaProduits((BigDecimal) produitsResult[1]);
        
        ca.calculerTotal();
        return ca;
    }
    
    /**
     * CA Tickets: SUM(prix_paye) depuis detail_reservation 
     * JOIN reservation + vol_programme pour filtrer par date
     */
    private Object[] getCATickets(LocalDate debut, LocalDate fin) {
        String sql = """
            SELECT COALESCE(COUNT(dr.id_detail_reservation), 0), 
                   COALESCE(SUM(dr.prix_paye), 0)
            FROM detail_reservation dr
            JOIN reservation r ON dr.id_reservation = r.id_reservation
            JOIN vol_programme vp ON r.id_vol_programme = vp.id_vol_programme
            WHERE r.statut = 'confirmée'
            AND DATE(vp.date_heure_depart) BETWEEN :debut AND :fin
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        
        Object[] result = (Object[]) query.getSingleResult();
        return new Object[]{
            result[0] != null ? result[0] : 0,
            result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO
        };
    }
    
    /**
     * CA Diffusions: SUM(montant_ligne) depuis detail_facture
     * JOIN facture pour filtrer par date et statut
     */
    private Object[] getCADiffusions(LocalDate debut, LocalDate fin) {
        String sql = """
            SELECT COALESCE(SUM(df.nombre_diffusions), 0),
                   COALESCE(SUM(df.montant_ligne), 0)
            FROM detail_facture df
            JOIN facture f ON df.id_facture = f.id_facture
            WHERE f.statut != 'annulée'
            AND f.date_facture BETWEEN :debut AND :fin
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        
        Object[] result = (Object[]) query.getSingleResult();
        return new Object[]{
            result[0] != null ? result[0] : 0,
            result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO
        };
    }
    
    /**
     * CA Produits: SUM(montant_total) depuis vente_produit
     * Filtré par date_vente
     */
    private Object[] getCAProduits(LocalDate debut, LocalDate fin) {
        String sql = """
            SELECT COALESCE(SUM(quantite), 0),
                   COALESCE(SUM(montant_total), 0)
            FROM vente_produit
            WHERE DATE(date_vente) BETWEEN :debut AND :fin
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        
        Object[] result = (Object[]) query.getSingleResult();
        return new Object[]{
            result[0] != null ? result[0] : 0,
            result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO
        };
    }
    
    /**
     * Récupère la liste des mois disponibles (ayant des données)
     */
    public List<YearMonth> getMoisDisponibles() {
        String sql = """
            SELECT DISTINCT DATE_TRUNC('month', date_ref) AS mois
            FROM (
                SELECT vp.date_heure_depart AS date_ref FROM vol_programme vp
                UNION ALL
                SELECT f.date_facture AS date_ref FROM facture f
                UNION ALL
                SELECT vp2.date_vente AS date_ref FROM vente_produit vp2
            ) AS dates
            WHERE date_ref IS NOT NULL
            ORDER BY mois DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        List<?> results = query.getResultList();
        
        List<YearMonth> mois = new ArrayList<>();
        for (Object result : results) {
            if (result != null) {
                java.sql.Timestamp ts = (java.sql.Timestamp) result;
                mois.add(YearMonth.from(ts.toLocalDateTime()));
            }
        }
        return mois;
    }
    
    /**
     * Détail des ventes produits pour un mois
     */
    public List<VenteProduit> getDetailProduitsMois(int annee, int mois) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDateTime debut = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);
        return venteProduitRepository.findByPeriode(debut, fin);
    }
}
