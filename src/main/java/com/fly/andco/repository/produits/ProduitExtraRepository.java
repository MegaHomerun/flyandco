package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.ProduitExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitExtraRepository extends JpaRepository<ProduitExtra, Long> {
    
    @Query("SELECT p FROM ProduitExtra p JOIN FETCH p.categorieProduit ORDER BY p.nom")
    List<ProduitExtra> findAllWithCategorie();
    
    @Query("SELECT p FROM ProduitExtra p JOIN FETCH p.categorieProduit WHERE p.actif = true ORDER BY p.nom")
    List<ProduitExtra> findAllActifs();
    
    Optional<ProduitExtra> findByCodeProduit(String codeProduit);
    
    @Query("SELECT p FROM ProduitExtra p WHERE p.categorieProduit.idCategorieProduit = :idCategorie")
    List<ProduitExtra> findByCategorie(Long idCategorie);
}
