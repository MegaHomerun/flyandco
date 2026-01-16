package com.fly.andco.controller.tarifs;

import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.model.places.TarifVol;
import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.prix.TarifCategorie;
import com.fly.andco.model.vols.Vol;
import com.fly.andco.service.passagers.CategoriePassagerService;
import com.fly.andco.service.places.TarifVolService;
import com.fly.andco.service.places.TypePlaceService;
import com.fly.andco.service.prix.TarifCategorieService;
import com.fly.andco.service.vols.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tarifs")
public class TarifController {

    @Autowired
    private TarifVolService tarifVolService;

    @Autowired
    private TarifCategorieService tarifCategorieService;

    @Autowired
    private TypePlaceService typePlaceService;

    @Autowired
    private CategoriePassagerService categoriePassagerService;

    @Autowired
    private VolService volService;

    // =====================================
    // GESTION DES TARIFS PAR TYPE DE PLACE
    // =====================================

    @GetMapping("/types-places")
    public String listTarifsTypesPlaces(Model model) {
        List<TarifVol> tarifsVol = tarifVolService.getAll();
        List<Vol> vols = volService.getAll();
        List<TypePlace> typesPlaces = typePlaceService.getAll();
        
        model.addAttribute("tarifsVol", tarifsVol);
        model.addAttribute("vols", vols);
        model.addAttribute("typesPlaces", typesPlaces);
        return "views/tarifs/types-places";
    }

    @PostMapping("/types-places/save")
    public String saveTarifTypePlace(
            @RequestParam Long idVol,
            @RequestParam Long idTypePlace,
            @RequestParam BigDecimal prix,
            @RequestParam(required = false) Long idTarifVol,
            RedirectAttributes redirectAttributes) {
        
        try {
            TarifVol tarifVol;
            if (idTarifVol != null) {
                tarifVol = tarifVolService.getById(idTarifVol)
                    .orElseThrow(() -> new RuntimeException("Tarif non trouvé"));
            } else {
                tarifVol = new TarifVol();
            }
            
            Vol vol = volService.getById(idVol)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
            TypePlace typePlace = typePlaceService.getById(idTypePlace)
                .orElseThrow(() -> new RuntimeException("Type de place non trouvé"));
            
            tarifVol.setVol(vol);
            tarifVol.setTypePlace(typePlace);
            tarifVol.setPrix(prix);
            
            tarifVolService.save(tarifVol);
            redirectAttributes.addFlashAttribute("success", "Tarif enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/admin/tarifs/types-places";
    }

    @GetMapping("/types-places/delete/{id}")
    public String deleteTarifTypePlace(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tarifVolService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Tarif supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/admin/tarifs/types-places";
    }

    // =====================================
    // GESTION DES TARIFS PAR CATÉGORIE
    // =====================================

    @GetMapping("/categories")
    public String listTarifsCategories(Model model) {
        List<TarifCategorie> tarifsCategorie = tarifCategorieService.getAll();
        List<Vol> vols = volService.getAll();
        List<TypePlace> typesPlaces = typePlaceService.getAll();
        List<CategoriePassager> categories = categoriePassagerService.getAll();
        
        model.addAttribute("tarifsCategorie", tarifsCategorie);
        model.addAttribute("vols", vols);
        model.addAttribute("typesPlaces", typesPlaces);
        model.addAttribute("categories", categories);
        return "views/tarifs/categories";
    }

    @PostMapping("/categories/save")
    public String saveTarifCategorie(
            @RequestParam Long idVol,
            @RequestParam Long idTypePlace,
            @RequestParam Long idCategoriePassager,
            @RequestParam(required = false) BigDecimal prix,
            @RequestParam(required = false) BigDecimal pourcentage,
            @RequestParam(required = false) BigDecimal fraisReduction,
            @RequestParam(required = false) Long idTarifCategorie,
            RedirectAttributes redirectAttributes) {
        
        try {
            TarifCategorie tarifCategorie;
            if (idTarifCategorie != null) {
                tarifCategorie = tarifCategorieService.getById(idTarifCategorie)
                    .orElseThrow(() -> new RuntimeException("Tarif catégorie non trouvé"));
            } else {
                tarifCategorie = new TarifCategorie();
            }
            
            Vol vol = volService.getById(idVol)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
            TypePlace typePlace = typePlaceService.getById(idTypePlace)
                .orElseThrow(() -> new RuntimeException("Type de place non trouvé"));
            CategoriePassager categorie = categoriePassagerService.getById(idCategoriePassager)
                .orElseThrow(() -> new RuntimeException("Catégorie passager non trouvée"));
            
            tarifCategorie.setVol(vol);
            tarifCategorie.setTypePlace(typePlace);
            tarifCategorie.setCategoriePassager(categorie);
            tarifCategorie.setPrix(prix);
            tarifCategorie.setPourcentage(pourcentage);
            tarifCategorie.setFraisReduction(fraisReduction);
            
            tarifCategorieService.save(tarifCategorie);
            redirectAttributes.addFlashAttribute("success", "Tarif catégorie enregistré avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/admin/tarifs/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteTarifCategorie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tarifCategorieService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Tarif catégorie supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/admin/tarifs/categories";
    }

    // API pour récupérer un tarif (pour édition)
    @GetMapping("/types-places/get/{id}")
    @ResponseBody
    public TarifVol getTarifVol(@PathVariable Long id) {
        return tarifVolService.getById(id).orElse(null);
    }

    @GetMapping("/categories/get/{id}")
    @ResponseBody
    public TarifCategorie getTarifCategorie(@PathVariable Long id) {
        return tarifCategorieService.getById(id).orElse(null);
    }
}
