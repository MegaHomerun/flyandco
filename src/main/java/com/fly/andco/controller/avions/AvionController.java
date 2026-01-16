package com.fly.andco.controller.avions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fly.andco.dto.RealRevenueDetail;
import com.fly.andco.dto.RevenueDetail;
import com.fly.andco.model.avions.Avion;
import com.fly.andco.service.avions.AvionService;

@Controller
@RequestMapping("/avions")
public class AvionController {

    @Autowired
    private AvionService avionService;

    // Lister tous les avions
    @GetMapping
    public String listAvions(Model model) {
        List<Avion> avions = avionService.getAllAvions();
        model.addAttribute("avions", avions);
        return "views/avions/list";
    }

    @Autowired
    private com.fly.andco.service.avions.SiegeService siegeService;

    @Autowired
    private com.fly.andco.service.vols.VolService volService;

    @GetMapping("/place")
    public String showRevenueForm(Model model) { // Modified to serve as the revenue calc page as requested
        model.addAttribute("avions", avionService.getAllAvions());
        model.addAttribute("vols", volService.getAll());
        return "views/avions/place";
    }

    @PostMapping("/revenue")
    public String calculateRevenue(@RequestParam("idVol") Long idVol,
            @RequestParam(value = "typeCalcul", defaultValue = "max") String typeCalcul,
            Model model) {

        if ("real".equals(typeCalcul)) {
            // Calcul du prix réel basé sur les réservations
            List<RealRevenueDetail> realDetails = siegeService.calculateRealRevenue(idVol);

            double grandTotalReal = realDetails.stream().mapToDouble(RealRevenueDetail::getTotal).sum();
            long totalPlacesOccupees = realDetails.stream().mapToLong(RealRevenueDetail::getPlacesOccupees).sum();

            model.addAttribute("realRevenueDetails", realDetails);
            model.addAttribute("grandTotalReal", grandTotalReal);
            model.addAttribute("totalPlacesOccupees", totalPlacesOccupees);
            model.addAttribute("typeCalcul", "real");
        } else {
            // Calcul du prix maximal
            List<RevenueDetail> details = siegeService.calculateMaxRevenue(idVol);

            double grandTotal = details.stream().mapToDouble(RevenueDetail::getTotalAdulte).sum();
            long totalPlaces = details.stream().mapToLong(RevenueDetail::getNombreSieges).sum();

            model.addAttribute("revenueDetails", details);
            model.addAttribute("grandTotal", grandTotal);
            model.addAttribute("totalPlaces", totalPlaces);
            model.addAttribute("typeCalcul", "max");
        }

        model.addAttribute("vols", volService.getAll());
        model.addAttribute("selectedVolId", idVol);
        return "views/avions/place";
    }
}
