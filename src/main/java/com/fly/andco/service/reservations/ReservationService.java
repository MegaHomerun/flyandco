package com.fly.andco.service.reservations;

import com.fly.andco.model.clients.Client;
import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.reservations.DetailReservation;
import com.fly.andco.model.reservations.Reservation;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.reservations.DetailReservationRepository;
import com.fly.andco.repository.reservations.ReservationRepository;
import com.fly.andco.service.clients.ClientService;
import com.fly.andco.service.passagers.CategoriePassagerService;
import com.fly.andco.service.places.PlaceService;
import com.fly.andco.service.prix.TarifCategorieService;
import com.fly.andco.service.vols.VolProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private DetailReservationRepository detailReservationRepository;
    
    @Autowired
    private VolProgrammeService volProgrammeService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private PlaceService placeService;
    
    @Autowired
    private CategoriePassagerService categoriePassagerService;
    
    @Autowired
    private TarifCategorieService tarifCategorieService;

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
     * Effectue une réservation avec type de place et catégorie de passager
     */
    @Transactional
    public Reservation effectuerReservation(Long idVolProgramme, String email, 
            String nom, String prenom, String telephone, 
            Long idTypePlace, Long idCategoriePassager, int nombrePlaces) throws Exception {
        
        // Vérifier disponibilité
        if (!volProgrammeService.hasPlacesDisponibles(idVolProgramme, nombrePlaces)) {
            throw new Exception("Pas assez de places disponibles");
        }
        
        // Récupérer ou créer le client
        Client client = clientService.findOrCreate(email, nom, prenom, telephone);
        
        // Récupérer le vol programmé
        VolProgramme volProgramme = volProgrammeService.getById(idVolProgramme)
            .orElseThrow(() -> new Exception("Vol programmé non trouvé"));
        
        // Récupérer le type de place et la catégorie
        TypePlace typePlace = placeService.getAllTypePlaces().stream()
            .filter(tp -> tp.getIdTypePlace().equals(idTypePlace))
            .findFirst()
            .orElseThrow(() -> new Exception("Type de place non trouvé"));
        
        CategoriePassager categorie = categoriePassagerService.getById(idCategoriePassager)
            .orElseThrow(() -> new Exception("Catégorie de passager non trouvée"));
        
        // Calculer le prix unitaire
        Long idVol = volProgramme.getVol().getIdVol();
        BigDecimal prixUnitaire = tarifCategorieService.getPrix(idVol, idTypePlace, idCategoriePassager);
        
        // Créer la réservation
        Reservation reservation = new Reservation(client, volProgramme, nombrePlaces);
        reservation = reservationRepository.save(reservation);
        
        // Créer les détails de réservation pour chaque passager
        for (int i = 0; i < nombrePlaces; i++) {
            DetailReservation detail = new DetailReservation(
                reservation, typePlace, categorie, prixUnitaire);
            detailReservationRepository.save(detail);
        }
        
        return reservation;
    }
    
    /**
     * Effectue une réservation (méthode simplifiée pour compatibilité)
     */
    @Transactional
    public Reservation effectuerReservation(Long idVolProgramme, String email, 
            String nom, String prenom, String telephone, int nombrePlaces) throws Exception {
        // Utilise les valeurs par défaut: Économique (id=2) et Adulte (id=1)
        return effectuerReservation(idVolProgramme, email, nom, prenom, telephone, 2L, 1L, nombrePlaces);
    }
    
    /**
     * Compte les places réservées pour un vol
     */
    public int countPlacesReservees(Long idVolProgramme) {
        return reservationRepository.countPlacesReservees(idVolProgramme);
    }
}
