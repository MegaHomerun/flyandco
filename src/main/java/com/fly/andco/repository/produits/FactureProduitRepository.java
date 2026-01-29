package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.FactureProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureProduitRepository extends JpaRepository<FactureProduit, Long> {
    
    @Query("SELECT f FROM FactureProduit f ORDER BY f.dateFacture DESC")
    List<FactureProduit> findAllOrderByDate();
    
    @Query("SELECT f FROM FactureProduit f LEFT JOIN FETCH f.details WHERE f.idFactureProduit = :id")
    Optional<FactureProduit> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT f FROM FactureProduit f WHERE f.statut != 'payée' AND f.statut != 'annulée' ORDER BY f.dateFacture")
    List<FactureProduit> findFacturesImpayees();
    
    @Query("SELECT COUNT(f) FROM FactureProduit f WHERE YEAR(f.dateFacture) = :annee")
    long countByAnnee(@Param("annee") int annee);
    
    @Query("SELECT f FROM FactureProduit f WHERE f.dateFacture BETWEEN :debut AND :fin ORDER BY f.dateFacture")
    List<FactureProduit> findByPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
