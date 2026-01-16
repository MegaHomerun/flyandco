package com.fly.andco.service.passagers;

import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.repository.passagers.CategoriePassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriePassagerService {

    @Autowired
    private CategoriePassagerRepository categoriePassagerRepository;

    public List<CategoriePassager> getAll() {
        return categoriePassagerRepository.findAll();
    }

    public Optional<CategoriePassager> getById(Long id) {
        return categoriePassagerRepository.findById(id);
    }

    public Optional<CategoriePassager> getByNom(String nom) {
        return categoriePassagerRepository.findByNom(nom);
    }

    public CategoriePassager save(CategoriePassager categoriePassager) {
        return categoriePassagerRepository.save(categoriePassager);
    }

    public void delete(Long id) {
        categoriePassagerRepository.deleteById(id);
    }
}
