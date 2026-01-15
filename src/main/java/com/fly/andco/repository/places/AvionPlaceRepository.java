package com.fly.andco.repository.places;

import com.fly.andco.model.places.AvionPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvionPlaceRepository extends JpaRepository<AvionPlace, Long> {
    
    List<AvionPlace> findByAvionIdAvion(Long idAvion);
    
    @Query("SELECT ap FROM AvionPlace ap WHERE ap.avion.idAvion = :idAvion ORDER BY ap.typePlace.nom")
    List<AvionPlace> findPlacesByAvion(@Param("idAvion") Long idAvion);
}
