package com.fly.andco.service.diffusions;

import com.fly.andco.model.diffusions.Facture;
import com.fly.andco.model.diffusions.Paiement;
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
    private final PaiementRepository paiementRepository;
    
    @Autowired
    public DiffusionService(
            SocieteDiffuseurRepository societeDiffuseurRepository,
            FactureRepository factureRepository,
            DetailFactureRepository detailFactureRepository,
            TarifDiffusionRepository tarifDiffusionRepository,
            PaiementRepository paiementRepository) {
        this.societeDiffuseurRepository = societeDiffuseurRepository;
        this.factureRepository = factureRepository;
        this.detailFactureRepository = detailFactureRepository;
        this.tarifDiffusionRepository = tarifDiffusionRepository;
        this.paiementRepository = paiementRepository;
    }
    
    /**
     * Récupère toutes les sociétés diffuseurs pour la liste déroulante
     */
    public List<SocieteDiffuseur> getAllSocietes() {
        return societeDiffuseurRepository.findAllOrderByNom();
    }
    
    /**
     * Récupère une société par ID
     */
    public SocieteDiffuseur getSocieteById(Long idSociete) {
        return societeDiffuseurRepository.findById(idSociete).orElse(null);
    }
    
    /**
     * Calcule le CA total des diffusions avec filtres optionnels
     */
    public CADiffusionDTO calculerCA(Long idSociete, LocalDate dateDebut, LocalDate dateFin) {
        BigDecimal ca;
        Integer diffusions;
        
        boolean hasSociete = idSociete != null;
        boolean hasPeriode = dateDebut != null && dateFin != null;
        
        if (hasSociete && hasPeriode) {
            ca = factureRepository.calculerCABySocieteAndPeriode(idSociete, dateDebut, dateFin);
            diffusions = detailFactureRepository.compterDiffusionsBySocieteAndPeriode(idSociete, dateDebut, dateFin);
        } else if (hasSociete) {
            ca = factureRepository.calculerCABySociete(idSociete);
            diffusions = detailFactureRepository.compterDiffusionsBySociete(idSociete);
        } else if (hasPeriode) {
            ca = factureRepository.calculerCAByPeriode(dateDebut, dateFin);
            diffusions = detailFactureRepository.compterDiffusionsByPeriode(dateDebut, dateFin);
        } else {
            ca = factureRepository.calculerCATous();
            diffusions = detailFactureRepository.compterDiffusionsTous();
        }
        
        return new CADiffusionDTO(diffusions, ca);
    }
    
    /**
     * Calcule le CA par société pour une période donnée
     */
    public List<CAParSocieteDTO> calculerCAParSociete(LocalDate dateDebut, LocalDate dateFin) {
        List<Object[]> caResults;
        List<Object[]> diffusionsResults;
        
        boolean hasPeriode = dateDebut != null && dateFin != null;
        
        if (hasPeriode) {
            caResults = factureRepository.calculerCAParSocieteByPeriode(dateDebut, dateFin);
            diffusionsResults = detailFactureRepository.compterDiffusionsParSocieteByPeriode(dateDebut, dateFin);
        } else {
            caResults = factureRepository.calculerCAParSocieteTous();
            diffusionsResults = detailFactureRepository.compterDiffusionsParSocieteTous();
        }
        
        // Créer une map pour les diffusions par société
        Map<Long, Integer> diffusionsParSociete = new HashMap<>();
        for (Object[] row : diffusionsResults) {
            Long id = (Long) row[0];
            Number nbDiffusions = (Number) row[2];
            diffusionsParSociete.put(id, nbDiffusions != null ? nbDiffusions.intValue() : 0);
        }
        
        // Combiner les résultats
        List<CAParSocieteDTO> resultats = new ArrayList<>();
        for (Object[] row : caResults) {
            Long id = (Long) row[0];
            String nomSociete = (String) row[1];
            BigDecimal ca = (BigDecimal) row[2];
            Integer nbDiffusions = diffusionsParSociete.getOrDefault(id, 0);
            
            resultats.add(new CAParSocieteDTO(id, nomSociete, nbDiffusions, ca));
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
        CADiffusionDTO caDto = calculerCA(idSociete, dateDebut, dateFin);
        
        SocieteDiffuseur societe = societeDiffuseurRepository.findById(idSociete).orElse(null);
        if (societe == null) {
            return new ArrayList<>();
        }
        
        List<CAParSocieteDTO> resultats = new ArrayList<>();
        resultats.add(new CAParSocieteDTO(idSociete, societe.getNom(), caDto.getTotalDiffusions(), caDto.getTotalCA()));
        
        return resultats;
    }
    
    /**
     * Récupère le tarif applicable pour une diffusion
     */
    public BigDecimal getTarifApplicable(Long idSociete, Long idVol, Long idTypePlace) {
        return tarifDiffusionRepository.getTarifApplicable(idSociete, idVol, idTypePlace);
    }
    
    // ===============================================
    // GESTION DES PAIEMENTS
    // ===============================================
    
    /**
     * Récupère les factures avec reste à payer pour une société
     */
    public List<Facture> getFacturesAvecResteAPayer(Long idSociete) {
        return factureRepository.findFacturesAvecResteAPayer(idSociete);
    }
    
    /**
     * Récupère toutes les factures d'une société
     */
    public List<Facture> getFacturesBySociete(Long idSociete) {
        return factureRepository.findBySociete(idSociete);
    }
    
    /**
     * Récupère une facture par ID
     */
    public Facture getFactureById(Long idFacture) {
        return factureRepository.findById(idFacture).orElse(null);
    }
    
    /**
     * Récupère les paiements d'une facture
     */
    public List<Paiement> getPaiementsByFacture(Long idFacture) {
        return paiementRepository.findByFactureIdFactureOrderByDatePaiementDesc(idFacture);
    }
    
    /**
     * Ajoute un paiement à une facture
     */
    public Paiement ajouterPaiement(Long idFacture, LocalDate datePaiement, BigDecimal montant, 
                                     String modePaiement, String reference, String notes) {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        Paiement paiement = new Paiement();
        paiement.setFacture(facture);
        paiement.setDatePaiement(datePaiement);
        paiement.setMontantPaye(montant);
        paiement.setModePaiement(modePaiement);
        paiement.setReferencePaiement(reference);
        paiement.setNotes(notes);
        
        paiement = paiementRepository.save(paiement);
        
        // Mettre à jour le statut de la facture
        BigDecimal totalPaye = paiementRepository.sumMontantPayeByFacture(idFacture);
        if (totalPaye.compareTo(facture.getMontant()) >= 0) {
            facture.setStatut("payée");
        } else if (totalPaye.compareTo(BigDecimal.ZERO) > 0) {
            facture.setStatut("partiellement_payée");
        }
        factureRepository.save(facture);
        
        return paiement;
    }
    
    /**
     * Calcule le résumé des paiements pour une société
     */
    public ResumePaiementDTO getResumePaiementSociete(Long idSociete) {
        List<Facture> factures = factureRepository.findBySociete(idSociete);
        
        BigDecimal totalFacture = BigDecimal.ZERO;
        BigDecimal totalPaye = BigDecimal.ZERO;
        int totalDiffusions = 0;
        
        for (Facture f : factures) {
            totalFacture = totalFacture.add(f.getMontant());
            totalPaye = totalPaye.add(f.getMontantPaye());
            totalDiffusions += f.getTotalDiffusions();
        }
        
        BigDecimal resteAPayer = totalFacture.subtract(totalPaye);
        
        SocieteDiffuseur societe = societeDiffuseurRepository.findById(idSociete).orElse(null);
        String nomSociete = societe != null ? societe.getNom() : "Inconnu";
        
        return new ResumePaiementDTO(idSociete, nomSociete, totalDiffusions, totalFacture, totalPaye, resteAPayer);
    }
}
