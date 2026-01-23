package com.fly.andco.controller.diffusions;

import com.fly.andco.model.diffusions.Facture;
import com.fly.andco.model.diffusions.Paiement;
import com.fly.andco.model.diffusions.SocieteDiffuseur;
import com.fly.andco.service.diffusions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/diffusions")
public class DiffusionController {
    
    private final DiffusionService diffusionService;
    
    @Autowired
    public DiffusionController(DiffusionService diffusionService) {
        this.diffusionService = diffusionService;
    }
    
    /**
     * Page de Chiffre d'Affaires des Diffusions Publicitaires
     * URL: /diffusions/ca
     */
    @GetMapping("/ca")
    public String afficherCA(
            @RequestParam(required = false) Long idSociete,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {
        
        // Liste des sociétés pour le dropdown
        List<SocieteDiffuseur> societes = diffusionService.getAllSocietes();
        model.addAttribute("societes", societes);
        
        // Conserver les valeurs des filtres
        model.addAttribute("idSocieteSelectionnee", idSociete);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        
        // Vérifier si un filtre est appliqué
        boolean filtreActif = idSociete != null || dateDebut != null || dateFin != null;
        model.addAttribute("filtreActif", filtreActif);
        
        if (filtreActif) {
            // Calcul du CA global
            CADiffusionDTO caGlobal = diffusionService.calculerCA(idSociete, dateDebut, dateFin);
            model.addAttribute("totalDiffusions", caGlobal.getTotalDiffusions());
            model.addAttribute("totalCA", caGlobal.getTotalCA());
            model.addAttribute("totalCAFormate", caGlobal.getTotalCAFormate());
            
            // Détails par société
            List<CAParSocieteDTO> detailsParSociete = diffusionService.calculerCAParSociete(idSociete, dateDebut, dateFin);
            model.addAttribute("detailsParSociete", detailsParSociete);
            
            model.addAttribute("resultatsAffiches", true);
        } else {
            model.addAttribute("resultatsAffiches", false);
        }
        
        return "views/diffusions/ca";
    }
    
    /**
     * Page de gestion des paiements
     * URL: /diffusions/paiements
     */
    @GetMapping("/paiements")
    public String afficherPaiements(
            @RequestParam(required = false) Long idSociete,
            Model model) {
        
        // Liste des sociétés pour le dropdown
        List<SocieteDiffuseur> societes = diffusionService.getAllSocietes();
        model.addAttribute("societes", societes);
        model.addAttribute("idSocieteSelectionnee", idSociete);
        
        if (idSociete != null) {
            // Récupérer le résumé des paiements
            ResumePaiementDTO resume = diffusionService.getResumePaiementSociete(idSociete);
            model.addAttribute("resume", resume);
            
            // Récupérer les factures avec détails
            List<Facture> factures = diffusionService.getFacturesBySociete(idSociete);
            model.addAttribute("factures", factures);
            
            // Société sélectionnée
            SocieteDiffuseur societe = diffusionService.getSocieteById(idSociete);
            model.addAttribute("societeSelectionnee", societe);
            
            model.addAttribute("resultatsAffiches", true);
        } else {
            model.addAttribute("resultatsAffiches", false);
        }
        
        return "views/diffusions/paiements";
    }
    
    /**
     * Enregistrer un nouveau paiement
     */
    @PostMapping("/paiements/ajouter")
    public String ajouterPaiement(
            @RequestParam Long idFacture,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam BigDecimal montant,
            @RequestParam(required = false) String modePaiement,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            Paiement paiement = diffusionService.ajouterPaiement(idFacture, datePaiement, montant, modePaiement, reference, notes);
            Facture facture = paiement.getFacture();
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Paiement de %,.0f Ar enregistré avec succès", montant));
            
            return "redirect:/diffusions/paiements?idSociete=" + facture.getSocieteDiffuseur().getIdSocieteDiffuseur();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement du paiement: " + e.getMessage());
            return "redirect:/diffusions/paiements";
        }
    }
    
    /**
     * Détails d'une facture (AJAX ou modal)
     */
    @GetMapping("/factures/{id}")
    @ResponseBody
    public Facture getFacture(@PathVariable Long id) {
        return diffusionService.getFactureById(id);
    }
}
