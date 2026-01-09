package com.fly.andco.service.reservations;

import com.fly.andco.model.clients.Client;
import com.fly.andco.model.reservations.Reservation;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.reservations.ReservationRepository;
import com.fly.andco.service.clients.ClientService;
import com.fly.andco.service.vols.VolProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private VolProgrammeService volProgrammeService;
    
    @Autowired
    private ClientService clientService;

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
    
    /**
     * Récupère les réservations d'un client par son email
     */
    public List<Reservation> getByClientEmail(String email) {
        return reservationRepository.findByClientEmail(email);
    }
    
    /**
     * Effectue une réservation
     */
    @Transactional
    public Reservation effectuerReservation(Long idVolProgramme, String email, 
            String nom, String prenom, String telephone, int nombrePlaces) throws Exception {
        
        // Vérifier disponibilité
        if (!volProgrammeService.hasPlacesDisponibles(idVolProgramme, nombrePlaces)) {
            throw new Exception("Pas assez de places disponibles");
        }
        
        // Récupérer ou créer le client
        Client client = clientService.findOrCreate(email, nom, prenom, telephone);
        
        // Récupérer le vol programmé
        VolProgramme volProgramme = volProgrammeService.getById(idVolProgramme)
            .orElseThrow(() -> new Exception("Vol programmé non trouvé"));
        
        // Créer la réservation
        Reservation reservation = new Reservation(client, volProgramme, nombrePlaces);
        
        return reservationRepository.save(reservation);
    }
    
    /**
     * Compte les places réservées pour un vol
     */
    public int countPlacesReservees(Long idVolProgramme) {
        return reservationRepository.countPlacesReservees(idVolProgramme);
    }
}
