package com.fly.andco.service.places;

import com.fly.andco.model.avions.Avion;
import com.fly.andco.model.places.*;
import com.fly.andco.model.vols.Vol;
import com.fly.andco.repository.avions.AvionRepository;
import com.fly.andco.repository.places.ConfigurationAvionRepository;
import com.fly.andco.repository.places.PrixVolRepository;
import com.fly.andco.repository.places.TypePlaceRepository;
import com.fly.andco.repository.vols.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlaceService {

    @Autowired
    private TypePlaceRepository typePlaceRepository;

    @Autowired
    private ConfigurationAvionRepository configurationAvionRepository;

    @Autowired
    private PrixVolRepository prixVolRepository;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private VolRepository volRepository;

    /**
     * Récupérer tous les types de places
     */
    public List<TypePlace> getAllTypePlaces() {
        return typePlaceRepository.findAll();
    }

    /**
     * Récupérer toutes les configurations d'avions
     */
    public List<ConfigurationAvion> getAllConfigurations() {
        return configurationAvionRepository.findAllWithDetails();
    }

    /**
     * Récupérer la configuration d'un avion spécifique
     */
    public List<ConfigurationAvion> getConfigurationByAvion(Long idAvion) {
        return configurationAvionRepository.findByAvionId(idAvion);
    }

    /**
     * Récupérer tous les prix par vol
     */
    public List<PrixVol> getAllPrixVols() {
        return prixVolRepository.findAllWithDetails();
    }

    /**
     * Récupérer les prix pour un vol spécifique
     */
    public List<PrixVol> getPrixByVol(Long idVol) {
        return prixVolRepository.findByVolId(idVol);
    }

    /**
     * Calculer la valeur maximale qu'un avion peut générer pour un vol spécifique
     * Formule: Somme(nombre_places_type * prix_type) pour chaque type de place
     */
    public BigDecimal calculerValeurMaximale(Long idAvion, Long idVol) {
        List<ConfigurationAvion> configurations = configurationAvionRepository.findByAvionId(idAvion);
        List<PrixVol> prixVols = prixVolRepository.findByVolId(idVol);
        
        BigDecimal valeurMax = BigDecimal.ZERO;
        
        for (ConfigurationAvion config : configurations) {
            for (PrixVol prixVol : prixVols) {
                if (config.getTypePlace().getIdTypePlace().equals(prixVol.getTypePlace().getIdTypePlace())) {
                    BigDecimal valeurType = prixVol.getPrix().multiply(BigDecimal.valueOf(config.getNombrePlaces()));
                    valeurMax = valeurMax.add(valeurType);
                }
            }
        }
        
        return valeurMax;
    }

    /**
     * Calculer la valeur maximale pour tous les avions et tous les vols
     */
    public List<ValeurMaxAvionVol> getValeursMaximales() {
        List<ValeurMaxAvionVol> resultats = new ArrayList<>();
        List<Avion> avions = avionRepository.findAll();
        List<Vol> vols = volRepository.findAll();

        for (Avion avion : avions) {
            for (Vol vol : vols) {
                BigDecimal valeurMax = calculerValeurMaximale(avion.getIdAvion(), vol.getIdVol());
                
                if (valeurMax.compareTo(BigDecimal.ZERO) > 0) {
                    ValeurMaxAvionVol item = new ValeurMaxAvionVol(
                        avion.getIdAvion(),
                        avion.getModele(),
                        avion.getNumeroImmatriculation(),
                        vol.getIdVol(),
                        vol.getAeroportDepart().getCodeIata(),
                        vol.getAeroportArrivee().getCodeIata(),
                        valeurMax
                    );
                    resultats.add(item);
                }
            }
        }

        return resultats;
    }

    /**
     * Calculer la valeur maximale pour un avion spécifique sur toutes les routes
     */
    public List<ValeurMaxAvionVol> getValeursMaximalesByAvion(Long idAvion) {
        List<ValeurMaxAvionVol> resultats = new ArrayList<>();
        Avion avion = avionRepository.findById(idAvion).orElse(null);
        if (avion == null) return resultats;

        List<Vol> vols = volRepository.findAll();

        for (Vol vol : vols) {
            BigDecimal valeurMax = calculerValeurMaximale(avion.getIdAvion(), vol.getIdVol());
            
            if (valeurMax.compareTo(BigDecimal.ZERO) > 0) {
                ValeurMaxAvionVol item = new ValeurMaxAvionVol(
                    avion.getIdAvion(),
                    avion.getModele(),
                    avion.getNumeroImmatriculation(),
                    vol.getIdVol(),
                    vol.getAeroportDepart().getCodeIata(),
                    vol.getAeroportArrivee().getCodeIata(),
                    valeurMax
                );
                resultats.add(item);
            }
        }

        return resultats;
    }
}
