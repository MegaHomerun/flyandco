package com.fly.andco.service.diffusions;

import com.fly.andco.model.diffusions.SocieteDiffuseur;
import com.fly.andco.repository.diffusions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DiffusionService {
    
    private final SocieteDiffuseurRepository societeDiffuseurRepository;
    private final FactureRepository factureRepository;
    private final DetailFactureRepository detailFactureRepository;
    private final TarifDiffusionRepository tarifDiffusionRepository;
    
    @Autowired
    public DiffusionService(
            SocieteDiffuseurRepository societeDiffuseurRepository,
            FactureRepository factureRepository,
            DetailFactureRepository detailFactureRepository,
            TarifDiffusionRepository tarifDiffusionRepository) {
        this.societeDiffuseurRepository = societeDiffuseurRepository;
        this.factureRepository = factureRepository;
        this.detailFactureRepository = detailFactureRepository;
        this.tarifDiffusionRepository = tarifDiffusionRepository;
    }
    
    /**
     * Récupère toutes les sociétés diffuseurs pour la liste déroulante
     */
    public List<SocieteDiffuseur> getAllSocietes() {
        return societeDiffuseurRepository.findAllOrderByNom();
    }
    
    /**
     * Calcule le CA total des diffusions avec filtres optionnels
     * @param idSociete ID de la société (null = toutes)
     * @param dateDebut Date de début de période (null = pas de limite)
     * @param dateFin Date de fin de période (null = pas de limite)
     * @return DTO avec total diffusions et CA
     */
    public CADiffusionDTO calculerCA(Long idSociete, LocalDate dateDebut, LocalDate dateFin) {
        BigDecimal ca = factureRepository.calculerCA(idSociete, dateDebut, dateFin);
        Integer diffusions = detailFactureRepository.compterDiffusions(idSociete, dateDebut, dateFin);
        
        return new CADiffusionDTO(diffusions, ca);
    }
    
    /**
     * Calcule le CA par société pour une période donnée
     * @param dateDebut Date de début de période (null = pas de limite)
     * @param dateFin Date de fin de période (null = pas de limite)
     * @return Liste des CA par société
     */
    public List<CAParSocieteDTO> calculerCAParSociete(LocalDate dateDebut, LocalDate dateFin) {
        List<Object[]> caResults = factureRepository.calculerCAParSociete(dateDebut, dateFin);
        List<Object[]> diffusionsResults = detailFactureRepository.compterDiffusionsParSociete(dateDebut, dateFin);
        
        // Créer une map pour les diffusions par société
        Map<Long, Integer> diffusionsParSociete = new HashMap<>();
        for (Object[] row : diffusionsResults) {
            Long idSociete = (Long) row[0];
            Number nbDiffusions = (Number) row[2];
            diffusionsParSociete.put(idSociete, nbDiffusions != null ? nbDiffusions.intValue() : 0);
        }
        
        // Combiner les résultats
        List<CAParSocieteDTO> resultats = new ArrayList<>();
        for (Object[] row : caResults) {
            Long idSociete = (Long) row[0];
            String nomSociete = (String) row[1];
            BigDecimal ca = (BigDecimal) row[2];
            Integer nbDiffusions = diffusionsParSociete.getOrDefault(idSociete, 0);
            
            resultats.add(new CAParSocieteDTO(idSociete, nomSociete, nbDiffusions, ca));
        }
        
        return resultats;
    }
    
    /**
     * Calcule le CA par société pour une période, filtré par société
     */
    public List<CAParSocieteDTO> calculerCAParSociete(Long idSociete, LocalDate dateDebut, LocalDate dateFin) {
        if (idSociete == null) {
            return calculerCAParSociete(dateDebut, dateFin);
        }
        
        // Si une société est sélectionnée, on filtre
        BigDecimal ca = factureRepository.calculerCA(idSociete, dateDebut, dateFin);
        Integer diffusions = detailFactureRepository.compterDiffusions(idSociete, dateDebut, dateFin);
        
        SocieteDiffuseur societe = societeDiffuseurRepository.findById(idSociete).orElse(null);
        if (societe == null) {
            return new ArrayList<>();
        }
        
        List<CAParSocieteDTO> resultats = new ArrayList<>();
        resultats.add(new CAParSocieteDTO(idSociete, societe.getNom(), diffusions, ca));
        
        return resultats;
    }
    
    /**
     * Récupère le tarif applicable pour une diffusion
     */
    public BigDecimal getTarifApplicable(Long idSociete, Long idVol, Long idTypePlace) {
        return tarifDiffusionRepository.getTarifApplicable(idSociete, idVol, idTypePlace);
    }
}
