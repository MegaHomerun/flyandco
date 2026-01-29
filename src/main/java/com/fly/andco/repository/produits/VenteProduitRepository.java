package com.fly.andco.repository.produits;

import com.fly.andco.model.produits.VenteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VenteProduitRepository extends JpaRepository<VenteProduit, Long> {
    
    @Query("SELECT v FROM VenteProduit v " +
           "JOIN FETCH v.volProgramme vp " +
           "JOIN FETCH vp.vol vol " +
           "JOIN FETCH vol.aeroportDepart " +
           "JOIN FETCH vol.aeroportArrivee " +
           "JOIN FETCH v.produitExtra pe " +
           "JOIN FETCH pe.categorieProduit " +
           "ORDER BY v.dateVente DESC")
    List<VenteProduit> findAllWithDetails();
    
    @Query("SELECT v FROM VenteProduit v " +
           "JOIN FETCH v.volProgramme vp " +
           "JOIN FETCH vp.vol vol " +
           "JOIN FETCH vol.aeroportDepart " +
           "JOIN FETCH vol.aeroportArrivee " +
           "JOIN FETCH v.produitExtra pe " +
           "WHERE v.dateVente BETWEEN :debut AND :fin " +
           "ORDER BY v.dateVente DESC")
    List<VenteProduit> findByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT v FROM VenteProduit v WHERE v.volProgramme.idVolProgramme = :idVolProgramme")
    List<VenteProduit> findByVolProgramme(@Param("idVolProgramme") Long idVolProgramme);
    
    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM VenteProduit v " +
           "WHERE v.dateVente BETWEEN :debut AND :fin")
    BigDecimal sumMontantByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COALESCE(SUM(v.quantite), 0) FROM VenteProduit v " +
           "WHERE v.dateVente BETWEEN :debut AND :fin")
    Integer countQuantiteByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
