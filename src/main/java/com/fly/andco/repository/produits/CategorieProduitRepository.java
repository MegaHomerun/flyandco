package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.CategorieProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorieProduitRepository extends JpaRepository<CategorieProduit, Long> {
    
    @Query("SELECT c FROM CategorieProduit c ORDER BY c.nom")
    List<CategorieProduit> findAllOrderByNom();
    
    @Query("SELECT c FROM CategorieProduit c WHERE c.actif = true ORDER BY c.nom")
    List<CategorieProduit> findAllActives();
}
