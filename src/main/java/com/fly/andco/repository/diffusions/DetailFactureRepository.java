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
    
    // Total des diffusions sans filtre
    @Query("SELECT COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée'")
    Integer compterDiffusionsTous();
    
    // Total des diffusions par société
    @Query("SELECT COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée' AND f.societeDiffuseur.idSocieteDiffuseur = :idSociete")
    Integer compterDiffusionsBySociete(@Param("idSociete") Long idSociete);
    
    // Total des diffusions par période
    @Query("SELECT COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée' AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin")
    Integer compterDiffusionsByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    // Total des diffusions par société et période
    @Query("SELECT COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée' AND f.societeDiffuseur.idSocieteDiffuseur = :idSociete AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin")
    Integer compterDiffusionsBySocieteAndPeriode(@Param("idSociete") Long idSociete, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    // Diffusions par société (groupé) - sans filtre
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom, COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée' GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom ORDER BY f.societeDiffuseur.nom")
    List<Object[]> compterDiffusionsParSocieteTous();
    
    // Diffusions par société (groupé) - par période
    @Query("SELECT f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom, COALESCE(SUM(d.nombreDiffusions), 0) FROM DetailFacture d JOIN d.facture f WHERE f.statut != 'annulée' AND f.dateDebutPeriode >= :dateDebut AND f.dateFinPeriode <= :dateFin GROUP BY f.societeDiffuseur.idSocieteDiffuseur, f.societeDiffuseur.nom ORDER BY f.societeDiffuseur.nom")
    List<Object[]> compterDiffusionsParSocieteByPeriode(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}
