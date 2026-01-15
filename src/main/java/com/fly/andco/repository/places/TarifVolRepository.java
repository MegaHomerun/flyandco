package com.fly.andco.repository.places;

import com.fly.andco.model.places.TarifVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarifVolRepository extends JpaRepository<TarifVol, Long> {
    
    List<TarifVol> findByVolIdVol(Long idVol);
    
    @Query("SELECT tv FROM TarifVol tv WHERE tv.vol.idVol = :idVol ORDER BY tv.typePlace.nom DESC")
    List<TarifVol> findTarifsByVol(@Param("idVol") Long idVol);
}
