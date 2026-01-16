package com.fly.andco.repository.prix;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fly.andco.model.prix.PrixVol;

@Repository
public interface PrixVolRepository extends JpaRepository<PrixVol, Long> {
    List<PrixVol> findByVol_IdVol(Long idVol);
}
