package com.fly.andco.repository.diffusions;

import com.fly.andco.model.diffusions.TarifDiffusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifDiffusionRepository extends JpaRepository<TarifDiffusion, Long> {
    
    List<TarifDiffusion> findBySocieteDiffuseurIdSocieteDiffuseurAndActifTrue(Long idSociete);
    
    @Query("SELECT t FROM TarifDiffusion t WHERE t.societeDiffuseur IS NULL AND t.vol IS NULL AND t.typePlace IS NULL AND t.actif = true")
    Optional<TarifDiffusion> findTarifDefaut();
    
    @Query("SELECT t.prixUnitaire FROM TarifDiffusion t " +
           "WHERE t.actif = true " +
           "AND (t.dateDebutValidite IS NULL OR t.dateDebutValidite <= CURRENT_DATE) " +
           "AND (t.dateFinValidite IS NULL OR t.dateFinValidite >= CURRENT_DATE) " +
           "AND (t.societeDiffuseur.idSocieteDiffuseur = :idSociete OR t.societeDiffuseur IS NULL) " +
           "AND (t.vol IS NULL OR t.vol.idVol = :idVol) " +
           "AND (t.typePlace IS NULL OR t.typePlace.idTypePlace = :idTypePlace) " +
           "ORDER BY t.societeDiffuseur.idSocieteDiffuseur DESC NULLS LAST, t.vol.idVol DESC NULLS LAST, t.typePlace.idTypePlace DESC NULLS LAST")
    List<BigDecimal> findTarifApplicable(
            @Param("idSociete") Long idSociete,
            @Param("idVol") Long idVol,
            @Param("idTypePlace") Long idTypePlace);
    
    default BigDecimal getTarifApplicable(Long idSociete, Long idVol, Long idTypePlace) {
        List<BigDecimal> tarifs = findTarifApplicable(idSociete, idVol, idTypePlace);
        if (!tarifs.isEmpty()) {
            return tarifs.get(0);
        }
        // Fallback au tarif par défaut
        return findTarifDefaut()
                .map(TarifDiffusion::getPrixUnitaire)
                .orElse(BigDecimal.valueOf(400000));
    }
    
    default BigDecimal getTarifDefaut() {
        return findTarifDefaut()
                .map(TarifDiffusion::getPrixUnitaire)
                .orElse(BigDecimal.valueOf(400000));
    }
}
