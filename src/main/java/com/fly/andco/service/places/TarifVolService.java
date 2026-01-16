package com.fly.andco.service.places;

import com.fly.andco.model.places.TarifVol;
import com.fly.andco.repository.places.TarifVolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarifVolService {

    @Autowired
    private TarifVolRepository tarifVolRepository;

    public List<TarifVol> getAll() {
        return tarifVolRepository.findAll();
    }

    public Optional<TarifVol> getById(Long id) {
        return tarifVolRepository.findById(id);
    }

    public List<TarifVol> getByVol(Long idVol) {
        return tarifVolRepository.findByVolIdVol(idVol);
    }

    public TarifVol save(TarifVol tarifVol) {
        return tarifVolRepository.save(tarifVol);
    }

    public void delete(Long id) {
        tarifVolRepository.deleteById(id);
    }
}
