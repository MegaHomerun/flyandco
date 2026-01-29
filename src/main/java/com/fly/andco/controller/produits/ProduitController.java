package com.fly.andco.controller.produits;

import com.fly.andco.model.produits.*;
import com.fly.andco.service.produits.ProduitService;
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
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;

    @Autowired
    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    // ===============================================
    // LISTE DES PRODUITS
    // ===============================================
    
    @GetMapping
    public String listeProduits(Model model) {
        List<ProduitExtra> produits = produitService.getAllProduits();
        model.addAttribute("produits", produits);
        return "views/produits/list";
    }

    // ===============================================
    // FORMULAIRE NOUVEAU PRODUIT
    // ===============================================
    
    @GetMapping("/nouveau")
    public String formulaireNouveau(Model model) {
        model.addAttribute("produit", new ProduitExtra());
        model.addAttribute("categories", produitService.getCategoriesActives());
        model.addAttribute("titre", "Nouveau Produit");
        return "views/produits/form";
    }

    // ===============================================
    // FORMULAIRE ÉDITION
    // ===============================================
    
    @GetMapping("/edit/{id}")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        ProduitExtra produit = produitService.getProduitById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        model.addAttribute("produit", produit);
        model.addAttribute("categories", produitService.getCategoriesActives());
        model.addAttribute("titre", "Modifier Produit");
        return "views/produits/form";
    }

    // ===============================================
    // SAUVEGARDE
    // ===============================================
    
    @PostMapping("/save")
    public String sauvegarder(
            @RequestParam(required = false) Long idProduitExtra,
            @RequestParam String codeProduit,
            @RequestParam String nom,
            @RequestParam(required = false) String description,
            @RequestParam Long idCategorieProduit,
            @RequestParam BigDecimal prixUnitaire,
            @RequestParam(required = false, defaultValue = "true") Boolean actif,
            RedirectAttributes redirectAttributes) {
        
        try {
            ProduitExtra produit;
            if (idProduitExtra != null) {
                produit = produitService.getProduitById(idProduitExtra)
                        .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            } else {
                produit = new ProduitExtra();
            }
            
            produit.setCodeProduit(codeProduit);
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setPrixUnitaire(prixUnitaire);
            produit.setActif(actif);
            
            CategorieProduit categorie = produitService.getCategorieById(idCategorieProduit)
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            produit.setCategorieProduit(categorie);
            
            produitService.saveProduit(produit);
            redirectAttributes.addFlashAttribute("success", "Produit enregistré avec succès !");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/produits";
    }

    // ===============================================
    // SUPPRESSION
    // ===============================================
    
    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.deleteProduit(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/produits";
    }
}
