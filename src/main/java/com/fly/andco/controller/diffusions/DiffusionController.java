package com.fly.andco.controller.diffusions;

import com.fly.andco.model.diffusions.SocieteDiffuseur;
import com.fly.andco.service.diffusions.CADiffusionDTO;
import com.fly.andco.service.diffusions.CAParSocieteDTO;
import com.fly.andco.service.diffusions.DiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        
        // Vérifier si un filtre est appliqué (au moins un paramètre non null)
        boolean filtreActif = idSociete != null || dateDebut != null || dateFin != null;
        model.addAttribute("filtreActif", filtreActif);
        
        // Si des filtres sont appliqués ou si on veut afficher les résultats par défaut
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
}
