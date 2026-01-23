package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.Echeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EcheanceRepository extends JpaRepository<Echeance, Long> {
    
    List<Echeance> findByPlanEcheancierIdPlanEcheancierOrderByNumeroEcheance(Long idPlan);
    
    @Query("SELECT e FROM Echeance e WHERE e.statut = 'en_attente' AND e.dateEcheancePrevue < CURRENT_DATE")
    List<Echeance> findEcheancesEnRetard();
    
    @Query("SELECT e FROM Echeance e " +
           "WHERE e.statut = 'en_attente' " +
           "AND e.dateEcheancePrevue BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY e.dateEcheancePrevue ASC")
    List<Echeance> findEcheancesAVenir(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT e FROM Echeance e " +
           "WHERE e.planEcheancier.facture.societeDiffuseur.idSocieteDiffuseur = :idSociete " +
           "AND e.statut = 'en_attente' " +
           "ORDER BY e.dateEcheancePrevue ASC")
    List<Echeance> findEcheancesEnAttenteBySociete(@Param("idSociete") Long idSociete);
}
