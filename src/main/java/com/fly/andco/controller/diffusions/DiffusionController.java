package com.fly.andco.controller.diffusions;

import com.fly.andco.model.diffusions.Facture;
import com.fly.andco.model.diffusions.Paiement;
import com.fly.andco.model.diffusions.SocieteDiffuseur;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.service.diffusions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/diffusions")
public class DiffusionController {
    
    private final DiffusionService diffusionService;
    
    @Autowired
    public DiffusionController(DiffusionService diffusionService) {
        this.diffusionService = diffusionService;
    }
    
    // ===============================================
    // PAGE COMMANDE DE DIFFUSIONS
    // ===============================================
    
    /**
     * Affiche le formulaire de commande de diffusions
     */
    @GetMapping("/commande")
    public String afficherFormulaireCommande(Model model) {
        // Liste des sociétés
        List<SocieteDiffuseur> societes = diffusionService.getAllSocietes();
        model.addAttribute("societes", societes);
        
        // Liste des vols programmés
        List<VolProgramme> vols = diffusionService.getAllVolsProgrammes();
        model.addAttribute("vols", vols);
        
        // Tarif par défaut
        BigDecimal tarifDefaut = diffusionService.getTarifDefaut();
        model.addAttribute("tarifDefaut", tarifDefaut);
        
        // Commande vide pour le formulaire
        CommandeDiffusionDTO commande = new CommandeDiffusionDTO();
        commande.setDateFacture(LocalDate.now());
        model.addAttribute("commande", commande);
        
        return "views/diffusions/commande";
    }
    
    /**
     * Crée une nouvelle facture à partir de la commande
     */
    @PostMapping("/commande")
    public String creerCommande(
            @RequestParam Long idSociete,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFacture,
            @RequestParam(required = false) String notes,
            @RequestParam(name = "idVolProgramme[]", required = false) Long[] idVolProgrammes,
            @RequestParam(name = "nombreDiffusions[]", required = false) Integer[] nombreDiffusions,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Construire le DTO
            CommandeDiffusionDTO commande = new CommandeDiffusionDTO();
            commande.setIdSociete(idSociete);
            commande.setDateFacture(dateFacture);
            commande.setNotes(notes);
            
            // Ajouter les lignes
            if (idVolProgrammes != null && nombreDiffusions != null) {
                for (int i = 0; i < idVolProgrammes.length; i++) {
                    if (nombreDiffusions[i] != null && nombreDiffusions[i] > 0) {
                        LigneDiffusionDTO ligne = new LigneDiffusionDTO();
                        ligne.setIdVolProgramme(idVolProgrammes[i]);
                        ligne.setNombreDiffusions(nombreDiffusions[i]);
                        commande.ajouterLigne(ligne);
                    }
                }
            }
            
            if (commande.getLignes().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Veuillez ajouter au moins une ligne de diffusion");
                return "redirect:/diffusions/commande";
            }
            
            // Créer la facture
            Facture facture = diffusionService.creerCommande(commande);
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Facture %s créée avec succès ! Montant total: %s", 
                    facture.getNumeroFacture(), facture.getMontantFormate()));
            
            // Rediriger vers la page de paiements de cette société
            return "redirect:/diffusions/paiements?idSociete=" + idSociete;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            return "redirect:/diffusions/commande";
        }
    }
    
    /**
     * API pour récupérer le tarif applicable (AJAX)
     */
    @GetMapping("/api/tarif")
    @ResponseBody
    public BigDecimal getTarif(
            @RequestParam(required = false) Long idSociete,
            @RequestParam(required = false) Long idVol) {
        return diffusionService.getTarifApplicable(idSociete, idVol, null);
    }
    
    // ===============================================
    // PAGE CHIFFRE D'AFFAIRES
    // ===============================================
    
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
    
    // ===============================================
    // PAGE GESTION DES PAIEMENTS
    // ===============================================
    
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
     * Enregistrer un nouveau paiement avec répartition prorata
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
                String.format("Paiement de %,.0f Ar enregistré avec succès (réparti au prorata sur les lignes)", montant));
            
            return "redirect:/diffusions/paiements?idSociete=" + facture.getSocieteDiffuseur().getIdSocieteDiffuseur();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement du paiement: " + e.getMessage());
            return "redirect:/diffusions/paiements";
        }
    }
    
    // ===============================================
    // API FACTURES (pour AJAX/modals)
    // ===============================================
    
    @GetMapping("/factures/{id}")
    @ResponseBody
    public Facture getFacture(@PathVariable Long id) {
        return diffusionService.getFactureById(id);
    }
    
    @GetMapping("/factures/{id}/details")
    public String afficherDetailsFacture(@PathVariable Long id, Model model) {
        Facture facture = diffusionService.getFactureById(id);
        if (facture == null) {
            return "redirect:/diffusions/paiements";
        }
        
        model.addAttribute("facture", facture);
        model.addAttribute("paiements", diffusionService.getPaiementsByFacture(id));
        
        return "views/diffusions/facture-details";
    }
}
