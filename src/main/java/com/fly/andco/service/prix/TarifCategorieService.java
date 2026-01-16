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
     * Calcule le prix pour un passager selon le vol, le type de place et la catégorie
     * Priorité: tarif_categorie (spécifique) > tarif_vol (standard)
     */
    public BigDecimal getPrix(Long idVol, Long idTypePlace, Long idCategoriePassager) {
        // D'abord chercher le tarif spécifique par catégorie
        Optional<BigDecimal> prixCategorie = tarifCategorieRepository.getPrix(
            idVol, idTypePlace, idCategoriePassager);
        
        if (prixCategorie.isPresent()) {
            return prixCategorie.get();
        }
        
        // Sinon, utiliser le tarif standard (pour adulte par défaut)
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
