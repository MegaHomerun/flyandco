package com.fly.andco.repository.vols;

import com.fly.andco.model.vols.VolProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VolProgrammeRepository extends JpaRepository<VolProgramme, Long> {
    
    // Recherche de vols programmés par aéroports et date
    @Query("SELECT vp FROM VolProgramme vp " +
           "WHERE vp.vol.aeroportDepart.idAeroport = :idDepart " +
           "AND vp.vol.aeroportArrivee.idAeroport = :idArrivee " +
           "AND DATE(vp.dateHeureDepart) = DATE(:dateDepart) " +
           "ORDER BY vp.dateHeureDepart")
    List<VolProgramme> findByAeroportsAndDate(
        @Param("idDepart") Long idDepart,
        @Param("idArrivee") Long idArrivee,
        @Param("dateDepart") LocalDateTime dateDepart
    );
    
    // Recherche par vol
    List<VolProgramme> findByVolIdVol(Long idVol);
    
    // Recherche des vols disponibles (avec places)
    @Query("SELECT vp FROM VolProgramme vp " +
           "WHERE vp.vol.aeroportDepart.idAeroport = :idDepart " +
           "AND vp.vol.aeroportArrivee.idAeroport = :idArrivee " +
           "AND vp.dateHeureDepart >= :dateDebut " +
           "AND vp.statut = 'prévu' " +
           "ORDER BY vp.dateHeureDepart")
    List<VolProgramme> findVolsDisponibles(
        @Param("idDepart") Long idDepart,
        @Param("idArrivee") Long idArrivee,
        @Param("dateDebut") LocalDateTime dateDebut
    );
}
