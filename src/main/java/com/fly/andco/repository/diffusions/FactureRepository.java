package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    
    Optional<Facture> findByNumeroFacture(String numeroFacture);
    
    List<Facture> findBySocieteDiffuseurIdSocieteDiffuseur(Long idSociete);
    
    @Query("SELECT f FROM Facture f WHERE f.statut != 'annulée' ORDER BY f.dateFacture DESC")
    List<Facture> findAllActiveOrderByDate();
    
    // Calcul du CA avec filtres optionnels
    @Query("SELECT COALESCE(SUM(f.montantHt), 0) FROM Facture f " +
           "WHERE f.statut != 'annulée' " +
           "AND (:idSociete IS NULL OR f.societeDiffuseur.idSocieteDiffuseur = :idSociete) " +
           "AND (:dateDebut IS NULL OR f.dateDebutPeriode >= :dateDebut) " +
           "AND (:dateFin IS NULL OR f.dateFinPeriode <= :dateFin)")
    BigDecimal calculerCA(
            @Param("idSociete") Long idSociete,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    // CA par société pour une période
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, " +
           "f.societeDiffuseur.nom, " +
           "COALESCE(SUM(f.montantHt), 0) " +
           "FROM Facture f " +
           "WHERE f.statut != 'annulée' " +
           "AND (:dateDebut IS NULL OR f.dateDebutPeriode >= :dateDebut) " +
           "AND (:dateFin IS NULL OR f.dateFinPeriode <= :dateFin) " +
           "GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom " +
           "ORDER BY f.societeDiffuseur.nom")
    List<Object[]> calculerCAParSociete(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    // Factures par période
    @Query("SELECT f FROM Facture f " +
           "WHERE f.statut != 'annulée' " +
           "AND (:idSociete IS NULL OR f.societeDiffuseur.idSocieteDiffuseur = :idSociete) " +
           "AND (:dateDebut IS NULL OR f.dateDebutPeriode >= :dateDebut) " +
           "AND (:dateFin IS NULL OR f.dateFinPeriode <= :dateFin) " +
           "ORDER BY f.dateFacture DESC")
    List<Facture> findByFilters(
            @Param("idSociete") Long idSociete,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    // Prochain numéro de facture
    @Query("SELECT MAX(f.numeroFacture) FROM Facture f WHERE f.numeroFacture LIKE CONCAT('FAC-', :annee, '-%')")
    String findLastNumeroFactureForYear(@Param("annee") String annee);
}
