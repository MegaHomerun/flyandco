package com.fly.andco.controller.vols;

import com.fly.andco.service.vols.CAVolProgrammeDTO;
import com.fly.andco.service.vols.CAVolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller pour la page Chiffre d'Affaires par Vol Programmé
 */
@Controller
@RequestMapping("/ca")
public class CAVolController {

    private final CAVolService caVolService;

    @Autowired
    public CAVolController(CAVolService caVolService) {
        this.caVolService = caVolService;
    }

    /**
     * Affiche la page du CA par vol programmé
     */
    @GetMapping("/par-vol")
    public String afficherCAParVol(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {

        // Conserver les filtres
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        // Récupérer les données
        List<CAVolProgrammeDTO> listeCA = caVolService.getCAParVolProgramme(dateDebut, dateFin);
        model.addAttribute("listeCA", listeCA);

        // Calculer les totaux
        CAVolProgrammeDTO totaux = caVolService.getTotaux(listeCA);
        model.addAttribute("totaux", totaux);

        return "views/vols/ca-par-vol";
    }
}
