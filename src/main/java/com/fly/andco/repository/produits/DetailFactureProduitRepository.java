package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.DetailFactureProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailFactureProduitRepository extends JpaRepository<DetailFactureProduit, Long> {
    
    @Query("SELECT d FROM DetailFactureProduit d WHERE d.factureProduit.idFactureProduit = :idFacture")
    List<DetailFactureProduit> findByFacture(@Param("idFacture") Long idFacture);
}
