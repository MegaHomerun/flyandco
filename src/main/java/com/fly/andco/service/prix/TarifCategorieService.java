package com.fly.andco.service.prix;

import com.fly.andco.model.places.TarifVol;
import com.fly.andco.model.prix.TarifCategorie;
import com.fly.andco.repository.places.TarifVolRepository;
import com.fly.andco.repository.prix.TarifCategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TarifCategorieService {

    @Autowired
    private TarifCategorieRepository tarifCategorieRepository;
    
    @Autowired
    private TarifVolRepository tarifVolRepository;

    public List<TarifCategorie> getAll() {
        return tarifCategorieRepository.findAll();
    }

    public Optional<TarifCategorie> getById(Long id) {
        return tarifCategorieRepository.findById(id);
    }

    public List<TarifCategorie> getByVol(Long idVol) {
        return tarifCategorieRepository.findByVolIdVol(idVol);
    }

    public TarifCategorie save(TarifCategorie tarifCategorie) {
        return tarifCategorieRepository.save(tarifCategorie);
    }

    public void delete(Long id) {
        tarifCategorieRepository.deleteById(id);
    }

    /**
     * Calcule le prix pour un passager selon le vol, le type de place et la catégorie.
     * 
     * Logique:
     * 1. Récupérer le tarif adulte de référence (tarif_vol)
     * 2. Chercher un tarif spécifique dans tarif_categorie
     * 3. Si tarif_categorie existe:
     *    - Si prix fixe défini → l'utiliser
     *    - Sinon si pourcentage défini → tarif_adulte * pourcentage / 100
     *    - Si frais_reduction défini → soustraire les frais
     * 4. Sinon utiliser le tarif adulte standard
     */
    public BigDecimal getPrix(Long idVol, Long idTypePlace, Long idCategoriePassager) {
        // Récupérer le tarif adulte de référence
        BigDecimal tarifAdulte = getTarifAdulte(idVol, idTypePlace);
        
        // Chercher un tarif spécifique par catégorie
        Optional<TarifCategorie> tarifSpecifique = tarifCategorieRepository
            .findByVolAndTypePlaceAndCategorie(idVol, idTypePlace, idCategoriePassager);
        
        if (tarifSpecifique.isPresent()) {
            TarifCategorie tc = tarifSpecifique.get();
            return tc.calculerPrixEffectif(tarifAdulte);
        }
        
        // Sinon, utiliser le tarif adulte standard
        return tarifAdulte;
    }
    
    /**
     * Récupère le tarif adulte de référence pour un vol et type de place
     */
    public BigDecimal getTarifAdulte(Long idVol, Long idTypePlace) {
        Optional<TarifVol> tarifVol = tarifVolRepository.findByVolIdVolAndTypePlaceIdTypePlace(idVol, idTypePlace);
        return tarifVol.map(TarifVol::getPrix).orElse(BigDecimal.ZERO);
    }

    /**
     * Récupère le tarif spécifique si existant
     */
    public Optional<TarifCategorie> getTarifSpecifique(Long idVol, Long idTypePlace, Long idCategoriePassager) {
        return tarifCategorieRepository.findByVolAndTypePlaceAndCategorie(idVol, idTypePlace, idCategoriePassager);
    }
}
