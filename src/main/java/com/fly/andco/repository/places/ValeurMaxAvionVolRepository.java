package com.fly.andco.repository.places;

import com.fly.andco.model.places.ValeurMaxAvionVol;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ValeurMaxAvionVolRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Récupère la valeur maximale pour tous les avions sur tous les vols
     */
    public List<ValeurMaxAvionVol> findAll() {
        String sql = """
            SELECT 
                a.id_avion,
                a.modele,
                a.numero_immatriculation,
                v.id_vol,
                ad.code_iata AS depart,
                aa.code_iata AS arrivee,
                SUM(ap.nombre_places * tv.prix) AS valeur_max
            FROM avion a
            JOIN avion_place ap ON a.id_avion = ap.id_avion
            JOIN type_place tp ON ap.id_type_place = tp.id_type_place
            CROSS JOIN vol v
            JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
            JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
            JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
            GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata
            ORDER BY a.modele, ad.code_iata, aa.code_iata
            """;
        
        return executeQuery(sql);
    }

    /**
     * Récupère la valeur maximale pour un vol spécifique (tous les avions)
     */
    public List<ValeurMaxAvionVol> findByVol(Long idVol) {
        String sql = """
            SELECT 
                a.id_avion,
                a.modele,
                a.numero_immatriculation,
                v.id_vol,
                ad.code_iata AS depart,
                aa.code_iata AS arrivee,
                SUM(ap.nombre_places * tv.prix) AS valeur_max
            FROM avion a
            JOIN avion_place ap ON a.id_avion = ap.id_avion
            JOIN type_place tp ON ap.id_type_place = tp.id_type_place
            JOIN vol v ON v.id_vol = :idVol
            JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
            JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
            JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
            GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata
            ORDER BY valeur_max DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idVol", idVol);
        return mapResults(query.getResultList());
    }

    /**
     * Récupère la valeur maximale pour un avion spécifique (tous les vols)
     */
    public List<ValeurMaxAvionVol> findByAvion(Long idAvion) {
        String sql = """
            SELECT 
                a.id_avion,
                a.modele,
                a.numero_immatriculation,
                v.id_vol,
                ad.code_iata AS depart,
                aa.code_iata AS arrivee,
                SUM(ap.nombre_places * tv.prix) AS valeur_max
            FROM avion a
            JOIN avion_place ap ON a.id_avion = ap.id_avion
            JOIN type_place tp ON ap.id_type_place = tp.id_type_place
            CROSS JOIN vol v
            JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
            JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
            JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
            WHERE a.id_avion = :idAvion
            GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata
            ORDER BY valeur_max DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idAvion", idAvion);
        return mapResults(query.getResultList());
    }

    /**
     * Récupère la valeur maximale pour un avion et un vol spécifique
     */
    public ValeurMaxAvionVol findByAvionAndVol(Long idAvion, Long idVol) {
        String sql = """
            SELECT 
                a.id_avion,
                a.modele,
                a.numero_immatriculation,
                v.id_vol,
                ad.code_iata AS depart,
                aa.code_iata AS arrivee,
                SUM(ap.nombre_places * tv.prix) AS valeur_max
            FROM avion a
            JOIN avion_place ap ON a.id_avion = ap.id_avion
            JOIN type_place tp ON ap.id_type_place = tp.id_type_place
            JOIN vol v ON v.id_vol = :idVol
            JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
            JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
            JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
            WHERE a.id_avion = :idAvion
            GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("idAvion", idAvion);
        query.setParameter("idVol", idVol);
        
        List<ValeurMaxAvionVol> results = mapResults(query.getResultList());
        return results.isEmpty() ? null : results.get(0);
    }

    private List<ValeurMaxAvionVol> executeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        return mapResults(query.getResultList());
    }

    @SuppressWarnings("unchecked")
    private List<ValeurMaxAvionVol> mapResults(List<Object[]> results) {
        List<ValeurMaxAvionVol> list = new ArrayList<>();
        for (Object[] row : results) {
            ValeurMaxAvionVol v = new ValeurMaxAvionVol();
            v.setIdAvion(((Number) row[0]).longValue());
            v.setModele((String) row[1]);
            v.setNumeroImmatriculation((String) row[2]);
            v.setIdVol(((Number) row[3]).longValue());
            v.setDepart((String) row[4]);
            v.setArrivee((String) row[5]);
            v.setValeurMax((BigDecimal) row[6]);
            list.add(v);
        }
        return list;
    }
}
