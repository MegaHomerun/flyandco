package com.fly.andco.service.vols;

import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.reservations.ReservationRepository;
import com.fly.andco.repository.vols.VolProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VolProgrammeService {

    @Autowired
    private VolProgrammeRepository volProgrammeRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;

    public List<VolProgramme> getAll() {
        return volProgrammeRepository.findAll();
    }

    public Optional<VolProgramme> getById(Long id) {
        return volProgrammeRepository.findById(id);
    }

    public VolProgramme save(VolProgramme volProgramme) {
        return volProgrammeRepository.save(volProgramme);
    }

    public void delete(Long id) {
        volProgrammeRepository.deleteById(id);
    }
    
    /**
     * Recherche des vols programmés par aéroports de départ/arrivée et date
     */
    public List<VolProgramme> rechercherVols(Long idDepart, Long idArrivee, LocalDate date) {
        LocalDateTime dateTime = date.atStartOfDay();
        return volProgrammeRepository.findByAeroportsAndDate(idDepart, idArrivee, dateTime);
    }
    
    /**
     * Calcule les places disponibles pour un vol programmé
     */
    public int getPlacesDisponibles(Long idVolProgramme) {
        Optional<VolProgramme> vp = volProgrammeRepository.findById(idVolProgramme);
        if (vp.isEmpty()) {
            return 0;
        }
        int capacite = vp.get().getAvion().getCapacite();
        int reservees = reservationRepository.countPlacesReservees(idVolProgramme);
        return capacite - reservees;
    }
    
    /**
     * Vérifie si des places sont disponibles
     */
    public boolean hasPlacesDisponibles(Long idVolProgramme, int nombrePlaces) {
        return getPlacesDisponibles(idVolProgramme) >= nombrePlaces;
    }
}
