package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.SocieteDiffuseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocieteDiffuseurRepository extends JpaRepository<SocieteDiffuseur, Long> {
    
    Optional<SocieteDiffuseur> findByNom(String nom);
    
    Optional<SocieteDiffuseur> findByEmail(String email);
    
    @Query("SELECT s FROM SocieteDiffuseur s ORDER BY s.nom ASC")
    List<SocieteDiffuseur> findAllOrderByNom();
    
    @Query("SELECT DISTINCT s FROM SocieteDiffuseur s " +
           "JOIN Facture f ON f.societeDiffuseur = s " +
           "WHERE f.statut != 'annulée' " +
           "ORDER BY s.nom ASC")
    List<SocieteDiffuseur> findSocietesAvecFactures();
}
