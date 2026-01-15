package com.fly.andco.service.places;

import com.fly.andco.model.places.*;
import com.fly.andco.repository.places.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private TypePlaceRepository typePlaceRepository;

    @Autowired
    private AvionPlaceRepository avionPlaceRepository;

    @Autowired
    private TarifVolRepository tarifVolRepository;

    @Autowired
    private ValeurMaxAvionVolRepository valeurMaxRepository;

    // ===== Type Place =====
    
    public List<TypePlace> getAllTypePlaces() {
        return typePlaceRepository.findAll();
    }

    // ===== Avion Place =====
    
    public List<AvionPlace> getPlacesByAvion(Long idAvion) {
        return avionPlaceRepository.findPlacesByAvion(idAvion);
    }

    // ===== Tarif Vol =====
    
    public List<TarifVol> getTarifsByVol(Long idVol) {
        return tarifVolRepository.findTarifsByVol(idVol);
    }

    // ===== Valeur Maximale =====
    
    /**
     * Récupère la valeur maximale pour tous les avions sur tous les vols
     */
    public List<ValeurMaxAvionVol> getAllValeurMax() {
        return valeurMaxRepository.findAll();
    }

    /**
     * Récupère la valeur maximale pour un vol spécifique (tous les avions)
     */
    public List<ValeurMaxAvionVol> getValeurMaxByVol(Long idVol) {
        return valeurMaxRepository.findByVol(idVol);
    }

    /**
     * Récupère la valeur maximale pour un avion spécifique (tous les vols)
     */
    public List<ValeurMaxAvionVol> getValeurMaxByAvion(Long idAvion) {
        return valeurMaxRepository.findByAvion(idAvion);
    }

    /**
     * Récupère la valeur maximale pour un avion et un vol spécifique
     */
    public ValeurMaxAvionVol getValeurMaxByAvionAndVol(Long idAvion, Long idVol) {
        return valeurMaxRepository.findByAvionAndVol(idAvion, idVol);
    }
}
