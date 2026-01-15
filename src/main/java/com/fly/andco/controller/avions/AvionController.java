package com.fly.andco.controller.avions;

import com.fly.andco.model.avions.Avion;
import com.fly.andco.model.places.ConfigurationAvion;
import com.fly.andco.model.places.ValeurMaxAvionVol;
import com.fly.andco.service.avions.AvionService;
import com.fly.andco.service.places.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class AvionController {

    @Autowired
    private AvionService avionService;

    @Autowired
    private PlaceService placeService;

    @GetMapping("/avions")
    public String listAvions(Model model) {
        List<Avion> avions = avionService.getAllAvions();
        List<ConfigurationAvion> configurations = placeService.getAllConfigurations();
        model.addAttribute("avions", avions);
        model.addAttribute("configurations", configurations);
        return "views/avions/list";
    }

    @GetMapping("/avions/{id}/valeur-maximale")
    public String valeurMaximale(@PathVariable Long id, Model model) {
        Avion avion = avionService.getAvionById(id).orElse(null);
        if (avion == null) {
            return "redirect:/avions";
        }
        List<ConfigurationAvion> configurations = avionService.getConfigurationAvion(id);
        List<ValeurMaxAvionVol> valeursMax = avionService.getValeursMaximalesByAvion(id);
        
        model.addAttribute("avion", avion);
        model.addAttribute("configurations", configurations);
        model.addAttribute("valeursMax", valeursMax);
        return "views/avions/valeur-maximale";
    }

    @GetMapping("/avions/valeurs-maximales")
    public String toutesValeursMaximales(Model model) {
        List<ValeurMaxAvionVol> valeursMax = avionService.getAllValeursMaximales();
        List<Avion> avions = avionService.getAllAvions();
        model.addAttribute("valeursMax", valeursMax);
        model.addAttribute("avions", avions);
        return "views/avions/valeurs-maximales";
    }
}
