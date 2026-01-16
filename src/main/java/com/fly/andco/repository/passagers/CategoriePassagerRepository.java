package com.fly.andco.repository.passagers;

import com.fly.andco.model.passagers.CategoriePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriePassagerRepository extends JpaRepository<CategoriePassager, Long> {
    
    Optional<CategoriePassager> findByNom(String nom);
}
