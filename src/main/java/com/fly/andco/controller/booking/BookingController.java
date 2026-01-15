package com.fly.andco.controller.booking;

import com.fly.andco.model.aeroports.Aeroport;
import com.fly.andco.model.reservations.Reservation;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.service.aeroports.AeroportService;
import com.fly.andco.service.reservations.ReservationService;
import com.fly.andco.service.vols.VolProgrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private AeroportService aeroportService;
    
    @Autowired
    private VolProgrammeService volProgrammeService;
    
    @Autowired
    private ReservationService reservationService;

    /**
     * Page de recherche de vols
     */
    @GetMapping
    public String index(Model model) {
        List<Aeroport> aeroports = aeroportService.getAll();
        model.addAttribute("aeroports", aeroports);
        return "views/booking/search";
    }
    
    /**
     * Recherche de vols disponibles
     */
    @GetMapping("/search")
    public String search(
            @RequestParam("depart") Long idDepart,
            @RequestParam("arrivee") Long idArrivee,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        
        List<Aeroport> aeroports = aeroportService.getAll();
        model.addAttribute("aeroports", aeroports);
        
        // Recherche des vols
        List<VolProgramme> vols = volProgrammeService.rechercherVols(idDepart, idArrivee, date);
        
        // Calculer les places disponibles pour chaque vol
        List<VolAvecPlaces> volsAvecPlaces = new ArrayList<>();
        for (VolProgramme vp : vols) {
            int placesDisponibles = volProgrammeService.getPlacesDisponibles(vp.getIdVolProgramme());
            volsAvecPlaces.add(new VolAvecPlaces(vp, placesDisponibles));
        }
        
        model.addAttribute("vols", volsAvecPlaces);
        model.addAttribute("dateRecherche", date);
        model.addAttribute("idDepart", idDepart);
        model.addAttribute("idArrivee", idArrivee);
        
        // Récupérer les noms des aéroports sélectionnés
        aeroportService.getById(idDepart).ifPresent(a -> model.addAttribute("aeroportDepart", a));
        aeroportService.getById(idArrivee).ifPresent(a -> model.addAttribute("aeroportArrivee", a));
        
        return "views/booking/search";
    }
    
    /**
     * Page de réservation pour un vol spécifique
     */
    @GetMapping("/reserve/{id}")
    public String reserveForm(@PathVariable("id") Long idVolProgramme, Model model) {
        VolProgramme vol = volProgrammeService.getById(idVolProgramme).orElse(null);
        if (vol == null) {
            return "redirect:/booking";
        }
        
        int placesDisponibles = volProgrammeService.getPlacesDisponibles(idVolProgramme);
        
        model.addAttribute("vol", vol);
        model.addAttribute("placesDisponibles", placesDisponibles);
        
        return "views/booking/reserve";
    }
    
    /**
     * Traitement de la réservation
     */
    @PostMapping("/reserve")
    public String reserve(
            @RequestParam("idVolProgramme") Long idVolProgramme,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("email") String email,
            @RequestParam("telephone") String telephone,
            @RequestParam("nombrePlaces") int nombrePlaces,
            RedirectAttributes redirectAttributes) {
        
        try {
            Reservation reservation = reservationService.effectuerReservation(
                idVolProgramme, email, nom, prenom, telephone, nombrePlaces);
            
            redirectAttributes.addFlashAttribute("success", 
                "Réservation confirmée ! Numéro: " + reservation.getIdReservation());
            return "redirect:/booking/mes-reservations?email=" + email;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/reserve/" + idVolProgramme;
        }
    }
    
    /**
     * Affiche les réservations d'un client ou toutes les réservations
     */
    @GetMapping("/mes-reservations")
    public String mesReservations(@RequestParam(value = "email", required = false) String email, Model model) {
        List<Reservation> reservations;
        
        if (email != null && !email.isEmpty()) {
            reservations = reservationService.getByClientEmail(email);
            model.addAttribute("email", email);
        } else {
            // Afficher toutes les réservations
            reservations = reservationService.getAll();
        }
        
        model.addAttribute("reservations", reservations);
        return "views/booking/reservations";
    }
    
    /**
     * Classe interne pour afficher les vols avec leurs places disponibles
     */
    public static class VolAvecPlaces {
        private VolProgramme volProgramme;
        private int placesDisponibles;
        
        public VolAvecPlaces(VolProgramme volProgramme, int placesDisponibles) {
            this.volProgramme = volProgramme;
            this.placesDisponibles = placesDisponibles;
        }
        
        public VolProgramme getVolProgramme() {
            return volProgramme;
        }
        
        public int getPlacesDisponibles() {
            return placesDisponibles;
        }
    }
}
