package com.fly.andco.repository.prix;

import com.fly.andco.model.prix.TarifCategorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifCategorieRepository extends JpaRepository<TarifCategorie, Long> {
    
    /**
     * Trouve le tarif pour un vol, type de place et catégorie de passager
     */
    @Query("SELECT tc FROM TarifCategorie tc WHERE tc.vol.idVol = :idVol " +
           "AND tc.typePlace.idTypePlace = :idTypePlace " +
           "AND tc.categoriePassager.idCategoriePassager = :idCategoriePassager")
    Optional<TarifCategorie> findByVolAndTypePlaceAndCategorie(
        @Param("idVol") Long idVol,
        @Param("idTypePlace") Long idTypePlace,
        @Param("idCategoriePassager") Long idCategoriePassager
    );
    
    /**
     * Liste tous les tarifs pour un vol donné
     */
    @Query("SELECT tc FROM TarifCategorie tc WHERE tc.vol.idVol = :idVol")
    List<TarifCategorie> findByVolIdVol(@Param("idVol") Long idVol);
    
    /**
     * Récupère le prix pour un vol, type de place et catégorie
     */
    @Query("SELECT tc.prix FROM TarifCategorie tc WHERE tc.vol.idVol = :idVol " +
           "AND tc.typePlace.idTypePlace = :idTypePlace " +
           "AND tc.categoriePassager.idCategoriePassager = :idCategoriePassager")
    Optional<BigDecimal> getPrix(
        @Param("idVol") Long idVol,
        @Param("idTypePlace") Long idTypePlace,
        @Param("idCategoriePassager") Long idCategoriePassager
    );
}
