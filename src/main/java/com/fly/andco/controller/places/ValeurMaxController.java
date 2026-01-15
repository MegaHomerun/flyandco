package com.fly.andco.controller.places;

import com.fly.andco.model.avions.Avion;
import com.fly.andco.model.places.ValeurMaxAvionVol;
import com.fly.andco.model.vols.Vol;
import com.fly.andco.service.avions.AvionService;
import com.fly.andco.service.places.PlaceService;
import com.fly.andco.service.vols.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ValeurMaxController {

    @Autowired
    private PlaceService placeService;

    @Autowired
    private AvionService avionService;

    @Autowired
    private VolService volService;

    @GetMapping("/valeur-max")
    public String valeurMax(
            @RequestParam(required = false) Long idVol,
            @RequestParam(required = false) Long idAvion,
            Model model) {
        
        List<ValeurMaxAvionVol> resultats;
        
        if (idVol != null && idAvion != null) {
            ValeurMaxAvionVol result = placeService.getValeurMaxByAvionAndVol(idAvion, idVol);
            resultats = result != null ? List.of(result) : List.of();
        } else if (idVol != null) {
            resultats = placeService.getValeurMaxByVol(idVol);
        } else if (idAvion != null) {
            resultats = placeService.getValeurMaxByAvion(idAvion);
        } else {
            resultats = placeService.getAllValeurMax();
        }
        
        List<Avion> avions = avionService.getAllAvions();
        List<Vol> vols = volService.getAll();
        
        model.addAttribute("resultats", resultats);
        model.addAttribute("avions", avions);
        model.addAttribute("vols", vols);
        model.addAttribute("idVolSelected", idVol);
        model.addAttribute("idAvionSelected", idAvion);
        
        return "views/places/valeur-max";
    }
}
