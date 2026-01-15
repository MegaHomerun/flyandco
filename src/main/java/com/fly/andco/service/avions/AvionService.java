package com.fly.andco.service.avions;

import com.fly.andco.model.avions.Avion;
import com.fly.andco.model.places.ConfigurationAvion;
import com.fly.andco.model.places.ValeurMaxAvionVol;
import com.fly.andco.repository.avions.AvionRepository;
import com.fly.andco.service.places.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvionService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private PlaceService placeService;

    public List<Avion> getAllAvions() {
        return avionRepository.findAll();
    }

    public Optional<Avion> getAvionById(Long id) {
        return avionRepository.findById(id);
    }

    public List<ConfigurationAvion> getConfigurationAvion(Long idAvion) {
        return placeService.getConfigurationByAvion(idAvion);
    }

    public List<ValeurMaxAvionVol> getValeursMaximalesByAvion(Long idAvion) {
        return placeService.getValeursMaximalesByAvion(idAvion);
    }

    public List<ValeurMaxAvionVol> getAllValeursMaximales() {
        return placeService.getValeursMaximales();
    }
}
