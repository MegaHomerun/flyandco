package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    
    List<Paiement> findByFactureIdFacture(Long idFacture);
    
    List<Paiement> findByFactureIdFactureOrderByDatePaiementDesc(Long idFacture);
    
    @Query("SELECT COALESCE(SUM(p.montantPaye), 0) FROM Paiement p WHERE p.facture.idFacture = :idFacture")
    BigDecimal sumMontantPayeByFacture(@Param("idFacture") Long idFacture);
    
    @Query("SELECT p FROM Paiement p WHERE p.datePaiement BETWEEN :dateDebut AND :dateFin ORDER BY p.datePaiement DESC")
    List<Paiement> findByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}
