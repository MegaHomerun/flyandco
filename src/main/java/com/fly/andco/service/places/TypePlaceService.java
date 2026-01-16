package com.fly.andco.service.places;

import com.fly.andco.model.places.TypePlace;
import com.fly.andco.repository.places.TypePlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypePlaceService {

    @Autowired
    private TypePlaceRepository typePlaceRepository;

    public List<TypePlace> getAll() {
        return typePlaceRepository.findAll();
    }

    public Optional<TypePlace> getById(Long id) {
        return typePlaceRepository.findById(id);
    }

    public TypePlace save(TypePlace typePlace) {
        return typePlaceRepository.save(typePlace);
    }

    public void delete(Long id) {
        typePlaceRepository.deleteById(id);
    }
}
