package com.fly.andco.service.diffusions;

import com.fly.andco.model.diffusions.DetailFacture;
import com.fly.andco.model.diffusions.Facture;
import com.fly.andco.model.diffusions.Paiement;
import com.fly.andco.model.diffusions.SocieteDiffuseur;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.diffusions.*;
import com.fly.andco.repository.vols.VolProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
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
    private final VolProgrammeRepository volProgrammeRepository;
    
    @Autowired
    public DiffusionService(
            SocieteDiffuseurRepository societeDiffuseurRepository,
            FactureRepository factureRepository,
            DetailFactureRepository detailFactureRepository,
            TarifDiffusionRepository tarifDiffusionRepository,
            PaiementRepository paiementRepository,
            VolProgrammeRepository volProgrammeRepository) {
        this.societeDiffuseurRepository = societeDiffuseurRepository;
        this.factureRepository = factureRepository;
        this.detailFactureRepository = detailFactureRepository;
        this.tarifDiffusionRepository = tarifDiffusionRepository;
        this.paiementRepository = paiementRepository;
        this.volProgrammeRepository = volProgrammeRepository;
    }
    
    // ===============================================
    // SOCIÉTÉS
    // ===============================================
    
    public List<SocieteDiffuseur> getAllSocietes() {
        return societeDiffuseurRepository.findAllOrderByNom();
    }
    
    public SocieteDiffuseur getSocieteById(Long idSociete) {
        return societeDiffuseurRepository.findById(idSociete).orElse(null);
    }
    
    // ===============================================
    // VOLS PROGRAMMÉS
    // ===============================================
    
    public List<VolProgramme> getAllVolsProgrammes() {
        return volProgrammeRepository.findAll();
    }
    
    public VolProgramme getVolProgrammeById(Long id) {
        return volProgrammeRepository.findById(id).orElse(null);
    }
    
    // ===============================================
    // CRÉATION DE COMMANDE (FACTURE)
    // ===============================================
    
    /**
     * Crée une nouvelle facture à partir d'une commande de diffusion
     * La facture est immédiatement clôturée (non modifiable)
     */
    public Facture creerCommande(CommandeDiffusionDTO commande) {
        // Récupérer la société
        SocieteDiffuseur societe = societeDiffuseurRepository.findById(commande.getIdSociete())
                .orElseThrow(() -> new RuntimeException("Société non trouvée"));
        
        // Générer le numéro de facture
        String numeroFacture = genererNumeroFacture();
        
        // Créer la facture
        Facture facture = new Facture();
        facture.setNumeroFacture(numeroFacture);
        facture.setSocieteDiffuseur(societe);
        facture.setDateFacture(commande.getDateFacture() != null ? commande.getDateFacture() : LocalDate.now());
        facture.setDateDebutPeriode(LocalDate.now());
        facture.setDateFinPeriode(LocalDate.now());
        facture.setNotes(commande.getNotes());
        facture.setStatut("émise");
        
        // Créer les lignes de facture
        BigDecimal montantTotal = BigDecimal.ZERO;
        
        for (LigneDiffusionDTO ligne : commande.getLignes()) {
            if (ligne.getNombreDiffusions() == null || ligne.getNombreDiffusions() <= 0) {
                continue;
            }
            
            DetailFacture detail = new DetailFacture();
            detail.setFacture(facture);
            
            // Récupérer le vol programmé si spécifié
            if (ligne.getIdVolProgramme() != null) {
                VolProgramme vol = volProgrammeRepository.findById(ligne.getIdVolProgramme()).orElse(null);
                detail.setVolProgramme(vol);
                if (vol != null) {
                    detail.setDescription("Diffusion sur vol " + vol.getVol().getAeroportDepart().getCodeIata() 
                            + " → " + vol.getVol().getAeroportArrivee().getCodeIata()
                            + " du " + vol.getDateHeureDepart().toLocalDate());
                }
            } else {
                detail.setDescription("Diffusion publicitaire");
            }
            
            detail.setNombreDiffusions(ligne.getNombreDiffusions());
            
            // Récupérer le tarif applicable
            BigDecimal prixUnitaire = getTarifApplicable(commande.getIdSociete(), 
                    ligne.getIdVolProgramme() != null ? ligne.getIdVolProgramme() : null, null);
            detail.setPrixUnitaire(prixUnitaire);
            detail.calculerMontant();
            detail.setMontantPaye(BigDecimal.ZERO);
            
            facture.getDetails().add(detail);
            montantTotal = montantTotal.add(detail.getMontantLigne());
        }
        
        if (facture.getDetails().isEmpty()) {
            throw new RuntimeException("La commande doit contenir au moins une ligne de diffusion");
        }
        
        facture.setMontant(montantTotal);
        
        // Sauvegarder
        return factureRepository.save(facture);
    }
    
    /**
     * Génère un numéro de facture au format FAC-YYYY-XXX
     */
    private String genererNumeroFacture() {
        String annee = String.valueOf(Year.now().getValue());
        String lastNumero = factureRepository.findLastNumeroFactureForYear(annee);
        
        int sequence = 1;
        if (lastNumero != null) {
            // Extraire le numéro de séquence du dernier numéro
            String[] parts = lastNumero.split("-");
            if (parts.length == 3) {
                try {
                    sequence = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException e) {
                    sequence = 1;
                }
            }
        }
        
        return String.format("FAC-%s-%03d", annee, sequence);
    }
    
    // ===============================================
    // TARIFICATION
    // ===============================================
    
    public BigDecimal getTarifApplicable(Long idSociete, Long idVol, Long idTypePlace) {
        BigDecimal tarif = tarifDiffusionRepository.getTarifApplicable(idSociete, idVol, idTypePlace);
        return tarif != null ? tarif : new BigDecimal("400000"); // Tarif par défaut
    }
    
    public BigDecimal getTarifDefaut() {
        return tarifDiffusionRepository.getTarifDefaut();
    }
    
    // ===============================================
    // GESTION DES PAIEMENTS AVEC PRORATA
    // ===============================================
    
    /**
     * Ajoute un paiement à une facture et répartit au prorata sur les lignes
     */
    public Paiement ajouterPaiement(Long idFacture, LocalDate datePaiement, BigDecimal montant, 
                                     String modePaiement, String reference, String notes) {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        // Vérifier que le montant ne dépasse pas le reste à payer
        BigDecimal resteAPayer = facture.getMontantRestant();
        if (montant.compareTo(resteAPayer) > 0) {
            throw new RuntimeException("Le montant du paiement (" + montant + ") dépasse le reste à payer (" + resteAPayer + ")");
        }
        
        // Créer le paiement
        Paiement paiement = new Paiement();
        paiement.setFacture(facture);
        paiement.setDatePaiement(datePaiement);
        paiement.setMontantPaye(montant);
        paiement.setModePaiement(modePaiement);
        paiement.setReferencePaiement(reference);
        paiement.setNotes(notes);
        
        paiement = paiementRepository.save(paiement);
        
        // Répartir le paiement au prorata sur les lignes
        repartirPaiementProrata(facture, montant);
        
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
     * Répartit un paiement au prorata proportionnel sur les lignes de facture
     * 
     * Exemple: Facture avec 3 lignes (200, 200, 600 = Total 1000)
     * Paiement de 200 (20% du total):
     * - Ligne 1: 200 × 20% = 40
     * - Ligne 2: 200 × 20% = 40  
     * - Ligne 3: 600 × 20% = 120
     */
    private void repartirPaiementProrata(Facture facture, BigDecimal montantPaiement) {
        BigDecimal montantTotal = facture.getMontant();
        
        if (montantTotal.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        
        // Calculer le pourcentage du paiement par rapport au total
        BigDecimal pourcentage = montantPaiement.divide(montantTotal, 6, RoundingMode.HALF_UP);
        
        BigDecimal totalReparti = BigDecimal.ZERO;
        List<DetailFacture> details = facture.getDetails();
        
        for (int i = 0; i < details.size(); i++) {
            DetailFacture detail = details.get(i);
            BigDecimal partLigne;
            
            if (i == details.size() - 1) {
                // Dernière ligne: on prend le reste pour éviter les erreurs d'arrondi
                partLigne = montantPaiement.subtract(totalReparti);
            } else {
                // Part proportionnelle: montant_ligne × pourcentage
                partLigne = detail.getMontantLigne().multiply(pourcentage)
                        .setScale(2, RoundingMode.HALF_UP);
            }
            
            // Ajouter au montant déjà payé sur cette ligne
            BigDecimal nouveauMontantPaye = detail.getMontantPaye().add(partLigne);
            
            // S'assurer de ne pas dépasser le montant de la ligne
            if (nouveauMontantPaye.compareTo(detail.getMontantLigne()) > 0) {
                nouveauMontantPaye = detail.getMontantLigne();
            }
            
            detail.setMontantPaye(nouveauMontantPaye);
            detailFactureRepository.save(detail);
            
            totalReparti = totalReparti.add(partLigne);
        }
    }
    
    // ===============================================
    // CONSULTATION DES FACTURES
    // ===============================================
    
    public List<Facture> getFacturesAvecResteAPayer(Long idSociete) {
        return factureRepository.findFacturesAvecResteAPayer(idSociete);
    }
    
    public List<Facture> getFacturesBySociete(Long idSociete) {
        return factureRepository.findBySociete(idSociete);
    }
    
    public Facture getFactureById(Long idFacture) {
        return factureRepository.findById(idFacture).orElse(null);
    }
    
    public List<Paiement> getPaiementsByFacture(Long idFacture) {
        return paiementRepository.findByFactureIdFactureOrderByDatePaiementDesc(idFacture);
    }
    
    // ===============================================
    // RÉSUMÉS ET STATISTIQUES
    // ===============================================
    
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
    
    // ===============================================
    // CALCUL DU CHIFFRE D'AFFAIRES
    // ===============================================
    
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
        
        Map<Long, Integer> diffusionsParSociete = new HashMap<>();
        for (Object[] row : diffusionsResults) {
            Long id = (Long) row[0];
            Number nbDiffusions = (Number) row[2];
            diffusionsParSociete.put(id, nbDiffusions != null ? nbDiffusions.intValue() : 0);
        }
        
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
    
    public List<CAParSocieteDTO> calculerCAParSociete(Long idSociete, LocalDate dateDebut, LocalDate dateFin) {
        if (idSociete == null) {
            return calculerCAParSociete(dateDebut, dateFin);
        }
        
        CADiffusionDTO caDto = calculerCA(idSociete, dateDebut, dateFin);
        
        SocieteDiffuseur societe = societeDiffuseurRepository.findById(idSociete).orElse(null);
        if (societe == null) {
            return new ArrayList<>();
        }
        
        List<CAParSocieteDTO> resultats = new ArrayList<>();
        resultats.add(new CAParSocieteDTO(idSociete, societe.getNom(), caDto.getTotalDiffusions(), caDto.getTotalCA()));
        
        return resultats;
    }
}
