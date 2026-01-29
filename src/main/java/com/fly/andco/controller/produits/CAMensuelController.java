package com.fly.andco.controller.produits;

import com.fly.andco.model.produits.FactureProduit;
import com.fly.andco.model.produits.PaiementProduit;
import com.fly.andco.model.produits.VenteProduit;
import com.fly.andco.service.produits.CAMensuelDTO;
import com.fly.andco.service.produits.CAMensuelService;
import com.fly.andco.service.produits.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/ca-mensuel")
public class CAMensuelController {

    private final CAMensuelService caMensuelService;
    private final ProduitService produitService;

    @Autowired
    public CAMensuelController(CAMensuelService caMensuelService, ProduitService produitService) {
        this.caMensuelService = caMensuelService;
        this.produitService = produitService;
    }

    // ===============================================
    // PAGE CA MENSUEL
    // ===============================================
    
    @GetMapping
    public String afficherCAMensuel(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            Model model) {
        
        // Valeurs par défaut: mois actuel
        if (annee == null) annee = LocalDate.now().getYear();
        if (mois == null) mois = LocalDate.now().getMonthValue();
        
        CAMensuelDTO ca = caMensuelService.getCAMensuel(annee, mois);
        List<YearMonth> moisDisponibles = caMensuelService.getMoisDisponibles();
        
        model.addAttribute("ca", ca);
        model.addAttribute("moisDisponibles", moisDisponibles);
        model.addAttribute("anneeSelectionnee", annee);
        model.addAttribute("moisSelectionne", mois);
        
        return "views/produits/ca-mensuel";
    }

    // ===============================================
    // DÉTAIL CA PRODUITS
    // ===============================================
    
    @GetMapping("/produits")
    public String detailProduits(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            Model model) {
        
        if (annee == null) annee = LocalDate.now().getYear();
        if (mois == null) mois = LocalDate.now().getMonthValue();
        
        List<VenteProduit> ventes = caMensuelService.getDetailProduitsMois(annee, mois);
        
        // Calculer les totaux
        int totalQuantite = ventes.stream().mapToInt(VenteProduit::getQuantite).sum();
        double totalMontant = ventes.stream()
                .mapToDouble(v -> v.getMontantTotal().doubleValue())
                .sum();
        
        String[] moisNoms = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                             "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        
        model.addAttribute("ventes", ventes);
        model.addAttribute("totalQuantite", totalQuantite);
        model.addAttribute("totalMontant", String.format("%,.0f Ar", totalMontant));
        model.addAttribute("moisLibelle", moisNoms[mois] + " " + annee);
        model.addAttribute("annee", annee);
        model.addAttribute("mois", mois);
        
        return "views/produits/ca-detail-produits";
    }

    // ===============================================
    // FACTURES PRODUITS
    // ===============================================
    
    @GetMapping("/factures-produits")
    public String listeFactures(Model model) {
        List<FactureProduit> factures = produitService.getAllFactures();
        model.addAttribute("factures", factures);
        return "views/produits/factures-list";
    }
    
    @GetMapping("/factures-produits/{id}")
    public String detailFacture(@PathVariable Long id, Model model) {
        FactureProduit facture = produitService.getFactureById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        List<PaiementProduit> paiements = produitService.getPaiementsByFacture(id);
        
        model.addAttribute("facture", facture);
        model.addAttribute("paiements", paiements);
        
        return "views/produits/facture-detail";
    }
    
    @GetMapping("/factures-produits/creer")
    public String formulaireFacture(Model model) {
        return "views/produits/facture-form";
    }
    
    @PostMapping("/factures-produits/creer")
    public String creerFacture(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            FactureProduit facture = produitService.creerFacture(dateDebut, dateFin, notes);
            redirectAttributes.addFlashAttribute("success", 
                    String.format("Facture %s créée: %s", facture.getNumeroFacture(), facture.getMontantFormate()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/ca-mensuel/factures-produits";
    }

    // ===============================================
    // PAIEMENTS
    // ===============================================
    
    @GetMapping("/factures-produits/{id}/paiement")
    public String formulairePaiement(@PathVariable Long id, Model model) {
        FactureProduit facture = produitService.getFactureById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        model.addAttribute("facture", facture);
        model.addAttribute("resteAPayer", produitService.getResteAPayer(id));
        
        return "views/produits/paiement-form";
    }
    
    @PostMapping("/factures-produits/{id}/paiement")
    public String enregistrerPaiement(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam BigDecimal montant,
            @RequestParam String modePaiement,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            produitService.enregistrerPaiement(id, datePaiement, montant, modePaiement, reference, notes);
            redirectAttributes.addFlashAttribute("success", 
                    String.format("Paiement de %,.0f Ar enregistré avec succès !", montant));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/ca-mensuel/factures-produits/" + id;
    }
}
