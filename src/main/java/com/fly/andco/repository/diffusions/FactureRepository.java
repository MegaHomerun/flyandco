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
    
    // Factures d'une société
    @Query("SELECT f FROM Facture f WHERE f.societeDiffuseur.idSocieteDiffuseur = :idSociete AND f.statut != 'annulée' ORDER BY f.dateFacture DESC")
    List<Facture> findBySociete(@Param("idSociete") Long idSociete);
    
    // CA total sans filtre
    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée'")
    BigDecimal calculerCATous();
    
    // CA par société
    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée' AND f.societeDiffuseur.idSocieteDiffuseur = :idSociete")
    BigDecimal calculerCABySociete(@Param("idSociete") Long idSociete);
    
    // CA par période
    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée' AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin")
    BigDecimal calculerCAByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    // CA par société et période
    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée' AND f.societeDiffuseur.idSocieteDiffuseur = :idSociete AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin")
    BigDecimal calculerCABySocieteAndPeriode(@Param("idSociete") Long idSociete, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    // CA par société (groupé) - sans filtre
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom, COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée' GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom ORDER BY f.societeDiffuseur.nom")
    List<Object[]> calculerCAParSocieteTous();
    
    // CA par société (groupé) - par période
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom, COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut != 'annulée' AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom ORDER BY f.societeDiffuseur.nom")
    List<Object[]> calculerCAParSocieteByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    // Prochain numéro de facture
    @Query("SELECT MAX(f.numeroFacture) FROM Facture f WHERE f.numeroFacture LIKE CONCAT('FAC-', :annee, '-%')")
    String findLastNumeroFactureForYear(@Param("annee") String annee);
    
    // Factures avec reste à payer pour une société
    @Query("SELECT f FROM Facture f WHERE f.societeDiffuseur.idSocieteDiffuseur = :idSociete AND f.statut IN ('émise', 'partiellement_payée') ORDER BY f.dateFacture DESC")
    List<Facture> findFacturesAvecResteAPayer(@Param("idSociete") Long idSociete);
}
