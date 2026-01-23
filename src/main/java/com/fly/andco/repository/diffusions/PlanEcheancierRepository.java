package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.PlanEcheancier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanEcheancierRepository extends JpaRepository<PlanEcheancier, Long> {
    
    Optional<PlanEcheancier> findByFactureIdFacture(Long idFacture);
    
    @Query("SELECT p FROM PlanEcheancier p WHERE p.statut = 'actif' ORDER BY p.dateDebut ASC")
    List<PlanEcheancier> findAllActifs();
    
    @Query("SELECT p FROM PlanEcheancier p " +
           "WHERE p.facture.societeDiffuseur.idSocieteDiffuseur = :idSociete " +
           "ORDER BY p.dateDebut DESC")
    List<PlanEcheancier> findBySociete(@Param("idSociete") Long idSociete);
}
