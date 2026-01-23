package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.DetailFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetailFactureRepository extends JpaRepository<DetailFacture, Long> {
    
    List<DetailFacture> findByFactureIdFacture(Long idFacture);
    
    // Total des diffusions avec filtres optionnels
    @Query("SELECT COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d " +
           "JOIN d.facture f " +
           "WHERE f.statut != 'annulée' " +
           "AND (:idSociete IS NULL OR f.societeDiffuseur.idSocieteDiffuseur = :idSociete) " +
           "AND (:dateDebut IS NULL OR f.dateDebutPeriode >= :dateDebut) " +
           "AND (:dateFin IS NULL OR f.dateFinPeriode <= :dateFin)")
    Integer compterDiffusions(
            @Param("idSociete") Long idSociete,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
    
    // Diffusions par société pour une période
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, " +
           "f.societeDiffuseur.nom, " +
           "COALESCE(SUM(d.nombreDiffusions), 0) " +
           "FROM DetailFacture d " +
           "JOIN d.facture f " +
           "WHERE f.statut != 'annulée' " +
           "AND (:dateDebut IS NULL OR f.dateDebutPeriode >= :dateDebut) " +
           "AND (:dateFin IS NULL OR f.dateFinPeriode <= :dateFin) " +
           "GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom " +
           "ORDER BY f.societeDiffuseur.nom")
    List<Object[]> compterDiffusionsParSociete(
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);
}
