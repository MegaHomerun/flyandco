package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.PaiementProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaiementProduitRepository extends JpaRepository<PaiementProduit, Long> {
    
    @Query("SELECT p FROM PaiementProduit p WHERE p.factureProduit.idFactureProduit = :idFacture ORDER BY p.datePaiement")
    List<PaiementProduit> findByFacture(@Param("idFacture") Long idFacture);
    
    @Query("SELECT COALESCE(SUM(p.montantPaye), 0) FROM PaiementProduit p WHERE p.factureProduit.idFactureProduit = :idFacture")
    BigDecimal sumPaiementsByFacture(@Param("idFacture") Long idFacture);
}
