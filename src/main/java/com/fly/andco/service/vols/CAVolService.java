package com.fly.andco.service.vols;

import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.vols.VolProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour calculer le Chiffre d'Affaires par Vol Programmé
 */
@Service
@Transactional(readOnly = true)
public class CAVolService {

    @PersistenceContext
    private EntityManager entityManager;

    private final VolProgrammeRepository volProgrammeRepository;

    @Autowired
    public CAVolService(VolProgrammeRepository volProgrammeRepository) {
        this.volProgrammeRepository = volProgrammeRepository;
    }

    /**
     * Récupère le CA par vol programmé avec billets et diffusions
     * @param dateDebut Date de début de filtrage (optionnel)
     * @param dateFin Date de fin de filtrage (optionnel)
     * @return Liste des CA par vol programmé
     */
    public List<CAVolProgrammeDTO> getCAParVolProgramme(LocalDate dateDebut, LocalDate dateFin) {
        List<CAVolProgrammeDTO> result = new ArrayList<>();

        // Requête native pour obtenir les données agrégées
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  vp.id_vol_programme, ");
        sql.append("  ad.nom AS aeroport_depart, ");
        sql.append("  aa.nom AS aeroport_arrivee, ");
        sql.append("  ad.code_iata AS code_depart, ");
        sql.append("  aa.code_iata AS code_arrivee, ");
        sql.append("  av.modele AS avion_modele, ");
        sql.append("  av.numero_immatriculation, ");
        sql.append("  vp.date_heure_depart, ");
        // CA Billets (somme des prix payés dans detail_reservation)
        sql.append("  COALESCE((SELECT SUM(dr.prix_paye) FROM detail_reservation dr ");
        sql.append("    JOIN reservation r ON dr.id_reservation = r.id_reservation ");
        sql.append("    WHERE r.id_vol_programme = vp.id_vol_programme AND r.statut = 'confirmée'), 0) AS montant_billets, ");
        // Nombre de passagers
        sql.append("  COALESCE((SELECT COUNT(*) FROM detail_reservation dr ");
        sql.append("    JOIN reservation r ON dr.id_reservation = r.id_reservation ");
        sql.append("    WHERE r.id_vol_programme = vp.id_vol_programme AND r.statut = 'confirmée'), 0) AS nb_passagers, ");
        // CA Diffusions facturé (montant_ligne des detail_facture liés à ce vol)
        sql.append("  COALESCE((SELECT SUM(df.montant_ligne) FROM detail_facture df ");
        sql.append("    JOIN facture f ON df.id_facture = f.id_facture ");
        sql.append("    WHERE df.id_vol_programme = vp.id_vol_programme AND f.statut != 'annulée'), 0) AS montant_diffusions_facture, ");
        // CA Diffusions payé (montant_paye des detail_facture liés à ce vol)
        sql.append("  COALESCE((SELECT SUM(df.montant_paye) FROM detail_facture df ");
        sql.append("    JOIN facture f ON df.id_facture = f.id_facture ");
        sql.append("    WHERE df.id_vol_programme = vp.id_vol_programme AND f.statut != 'annulée'), 0) AS montant_diffusions_paye, ");
        // Nombre de diffusions
        sql.append("  COALESCE((SELECT SUM(df.nombre_diffusions) FROM detail_facture df ");
        sql.append("    JOIN facture f ON df.id_facture = f.id_facture ");
        sql.append("    WHERE df.id_vol_programme = vp.id_vol_programme AND f.statut != 'annulée'), 0) AS nb_diffusions ");
        sql.append("FROM vol_programme vp ");
        sql.append("JOIN vol v ON vp.id_vol = v.id_vol ");
        sql.append("JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport ");
        sql.append("JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport ");
        sql.append("JOIN avion av ON vp.id_avion = av.id_avion ");
        sql.append("WHERE 1=1 ");

        // Filtres de dates
        if (dateDebut != null) {
            sql.append("AND CAST(vp.date_heure_depart AS date) >= :dateDebut ");
        }
        if (dateFin != null) {
            sql.append("AND CAST(vp.date_heure_depart AS date) <= :dateFin ");
        }

        sql.append("ORDER BY vp.date_heure_depart DESC");

        Query query = entityManager.createNativeQuery(sql.toString());

        if (dateDebut != null) {
            query.setParameter("dateDebut", dateDebut);
        }
        if (dateFin != null) {
            query.setParameter("dateFin", dateFin);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        for (Object[] row : rows) {
            CAVolProgrammeDTO dto = new CAVolProgrammeDTO();
            
            dto.setIdVolProgramme(((Number) row[0]).longValue());
            dto.setAeroportDepart((String) row[1]);
            dto.setAeroportArrivee((String) row[2]);
            dto.setCodeDepart(((String) row[3]).trim());
            dto.setCodeArrivee(((String) row[4]).trim());
            dto.setAvionModele((String) row[5]);
            dto.setAvionImmatriculation((String) row[6]);
            
            // Conversion de timestamp
            if (row[7] instanceof java.sql.Timestamp) {
                dto.setDateHeureDepart(((java.sql.Timestamp) row[7]).toLocalDateTime());
            } else if (row[7] instanceof java.time.LocalDateTime) {
                dto.setDateHeureDepart((java.time.LocalDateTime) row[7]);
            }
            
            dto.setMontantBillets(toBigDecimal(row[8]));
            dto.setNombrePassagers(((Number) row[9]).intValue());
            dto.setMontantDiffusionsFacture(toBigDecimal(row[10]));
            dto.setMontantDiffusionsPaye(toBigDecimal(row[11]));
            dto.setNombreDiffusions(((Number) row[12]).intValue());
            
            // Calculs
            dto.calculerTotaux();
            
            result.add(dto);
        }

        return result;
    }

    /**
     * Calcul des totaux globaux
     */
    public CAVolProgrammeDTO getTotaux(List<CAVolProgrammeDTO> listeCA) {
        CAVolProgrammeDTO totaux = new CAVolProgrammeDTO();
        totaux.setCodeDepart("TOTAL");
        totaux.setCodeArrivee("");
        
        BigDecimal totalBillets = BigDecimal.ZERO;
        BigDecimal totalDiffFacture = BigDecimal.ZERO;
        BigDecimal totalDiffPaye = BigDecimal.ZERO;
        int totalPassagers = 0;
        int totalDiffusions = 0;
        
        for (CAVolProgrammeDTO ca : listeCA) {
            totalBillets = totalBillets.add(ca.getMontantBillets());
            totalDiffFacture = totalDiffFacture.add(ca.getMontantDiffusionsFacture());
            totalDiffPaye = totalDiffPaye.add(ca.getMontantDiffusionsPaye());
            totalPassagers += ca.getNombrePassagers();
            totalDiffusions += ca.getNombreDiffusions();
        }
        
        totaux.setMontantBillets(totalBillets);
        totaux.setMontantDiffusionsFacture(totalDiffFacture);
        totaux.setMontantDiffusionsPaye(totalDiffPaye);
        totaux.setNombrePassagers(totalPassagers);
        totaux.setNombreDiffusions(totalDiffusions);
        totaux.calculerTotaux();
        
        return totaux;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return BigDecimal.ZERO;
    }
}
