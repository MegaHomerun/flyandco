package com.fly.andco.controller.produits;

import com.fly.andco.model.produits.ProduitExtra;
import com.fly.andco.model.produits.VenteProduit;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.service.produits.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ventes-produits")
public class VenteProduitController {

    private final ProduitService produitService;

    @Autowired
    public VenteProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // ===============================================
    // LISTE DES VENTES
    // ===============================================
    
    @GetMapping
    public String listeVentes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {
        
        List<VenteProduit> ventes;
        
        if (dateDebut != null && dateFin != null) {
            ventes = produitService.getVentesByPeriode(dateDebut, dateFin);
            model.addAttribute("dateDebut", dateDebut);
            model.addAttribute("dateFin", dateFin);
        } else {
            ventes = produitService.getAllVentes();
        }
        
        // Calculer les totaux
        int totalQuantite = ventes.stream().mapToInt(VenteProduit::getQuantite).sum();
        double totalMontant = ventes.stream()
                .mapToDouble(v -> v.getMontantTotal().doubleValue())
                .sum();
        
        model.addAttribute("ventes", ventes);
        model.addAttribute("totalQuantite", totalQuantite);
        model.addAttribute("totalMontant", String.format("%,.0f Ar", totalMontant));
        
        return "views/produits/ventes-list";
    }

    // ===============================================
    // FORMULAIRE NOUVELLE VENTE
    // ===============================================
    
    @GetMapping("/nouveau")
    public String formulaireVente(Model model) {
        List<VolProgramme> vols = produitService.getAllVolsProgrammes();
        List<ProduitExtra> produits = produitService.getProduitsActifs();
        
        model.addAttribute("vols", vols);
        model.addAttribute("produits", produits);
        
        return "views/produits/vente-form";
    }

    // ===============================================
    // SAUVEGARDE VENTE
    // ===============================================
    
    @PostMapping("/save")
    public String sauvegarderVente(
            @RequestParam Long idVolProgramme,
            @RequestParam Long idProduitExtra,
            @RequestParam Integer quantite,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            VenteProduit vente = produitService.creerVente(idVolProgramme, idProduitExtra, quantite, notes);
            redirectAttributes.addFlashAttribute("success", 
                    String.format("Vente enregistrée: %d × %s = %s", 
                            quantite, 
                            vente.getProduitExtra().getNom(),
                            vente.getMontantFormate()));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/ventes-produits";
    }
}
