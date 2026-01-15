package com.fly.andco.repository.places;

import com.fly.andco.model.places.ConfigurationAvion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigurationAvionRepository extends JpaRepository<ConfigurationAvion, Long> {

    /**
     * Trouver toutes les configurations pour un avion donné
     */
    @Query("SELECT ca FROM ConfigurationAvion ca WHERE ca.avion.idAvion = :idAvion")
    List<ConfigurationAvion> findByAvionId(@Param("idAvion") Long idAvion);

    /**
     * Trouver toutes les configurations avec leurs avions et types de places
     */
    @Query("SELECT ca FROM ConfigurationAvion ca " +
           "JOIN FETCH ca.avion a " +
           "JOIN FETCH ca.typePlace tp " +
           "ORDER BY a.idAvion, tp.idTypePlace")
    List<ConfigurationAvion> findAllWithDetails();
}
