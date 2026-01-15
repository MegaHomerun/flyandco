package com.fly.andco.repository.places;

import com.fly.andco.model.places.PrixVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrixVolRepository extends JpaRepository<PrixVol, Long> {

    /**
     * Trouver tous les prix pour un vol donné
     */
    @Query("SELECT pr FROM PrixVol pr WHERE pr.vol.idVol = :idVol")
    List<PrixVol> findByVolId(@Param("idVol") Long idVol);

    /**
     * Trouver tous les prix avec les détails du vol et du type de place
     */
    @Query("SELECT pr FROM PrixVol pr " +
           "JOIN FETCH pr.vol v " +
           "JOIN FETCH v.aeroportDepart ad " +
           "JOIN FETCH v.aeroportArrivee aa " +
           "JOIN FETCH pr.typePlace tp " +
           "ORDER BY v.idVol, tp.idTypePlace")
    List<PrixVol> findAllWithDetails();
}
