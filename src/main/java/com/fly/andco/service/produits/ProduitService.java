package com.fly.andco.service.produits;

import com.fly.andco.model.produits.*;
import com.fly.andco.model.vols.VolProgramme;
import com.fly.andco.repository.produits.*;
import com.fly.andco.repository.vols.VolProgrammeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProduitService {

    private final CategorieProduitRepository categorieProduitRepository;
    private final ProduitExtraRepository produitExtraRepository;
    private final VenteProduitRepository venteProduitRepository;
    private final FactureProduitRepository factureProduitRepository;
    private final DetailFactureProduitRepository detailFactureProduitRepository;
    private final PaiementProduitRepository paiementProduitRepository;
    private final VolProgrammeRepository volProgrammeRepository;

    @Autowired
    public ProduitService(
            CategorieProduitRepository categorieProduitRepository,
            ProduitExtraRepository produitExtraRepository,
            VenteProduitRepository venteProduitRepository,
            FactureProduitRepository factureProduitRepository,
            DetailFactureProduitRepository detailFactureProduitRepository,
            PaiementProduitRepository paiementProduitRepository,
            VolProgrammeRepository volProgrammeRepository) {
        this.categorieProduitRepository = categorieProduitRepository;
        this.produitExtraRepository = produitExtraRepository;
        this.venteProduitRepository = venteProduitRepository;
        this.factureProduitRepository = factureProduitRepository;
        this.detailFactureProduitRepository = detailFactureProduitRepository;
        this.paiementProduitRepository = paiementProduitRepository;
        this.volProgrammeRepository = volProgrammeRepository;
    }

    // ===============================================
    // CATÉGORIES
    // ===============================================
    
    public List<CategorieProduit> getAllCategories() {
        return categorieProduitRepository.findAllOrderByNom();
    }
    
    public List<CategorieProduit> getCategoriesActives() {
        return categorieProduitRepository.findAllActives();
    }
    
    public Optional<CategorieProduit> getCategorieById(Long id) {
        return categorieProduitRepository.findById(id);
    }

    // ===============================================
    // PRODUITS
    // ===============================================
    
    public List<ProduitExtra> getAllProduits() {
        return produitExtraRepository.findAllWithCategorie();
    }
    
    public List<ProduitExtra> getProduitsActifs() {
        return produitExtraRepository.findAllActifs();
    }
    
    public Optional<ProduitExtra> getProduitById(Long id) {
        return produitExtraRepository.findById(id);
    }
    
    public ProduitExtra saveProduit(ProduitExtra produit) {
        return produitExtraRepository.save(produit);
    }
    
    public void deleteProduit(Long id) {
        produitExtraRepository.deleteById(id);
    }

    // ===============================================
    // VOLS PROGRAMMÉS
    // ===============================================
    
    public List<VolProgramme> getAllVolsProgrammes() {
        return volProgrammeRepository.findAll();
    }
    
    public Optional<VolProgramme> getVolProgrammeById(Long id) {
        return volProgrammeRepository.findById(id);
    }

    // ===============================================
    // VENTES
    // ===============================================
    
    public List<VenteProduit> getAllVentes() {
        return venteProduitRepository.findAllWithDetails();
    }
    
    public List<VenteProduit> getVentesByPeriode(LocalDate debut, LocalDate fin) {
        LocalDateTime debutDT = debut.atStartOfDay();
        LocalDateTime finDT = fin.atTime(23, 59, 59);
        return venteProduitRepository.findByPeriode(debutDT, finDT);
    }
    
    public Optional<VenteProduit> getVenteById(Long id) {
        return venteProduitRepository.findById(id);
    }
    
    public VenteProduit saveVente(VenteProduit vente) {
        vente.calculerMontant();
        return venteProduitRepository.save(vente);
    }
    
    public VenteProduit creerVente(Long idVolProgramme, Long idProduit, Integer quantite, String notes) {
        VolProgramme vol = volProgrammeRepository.findById(idVolProgramme)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
        ProduitExtra produit = produitExtraRepository.findById(idProduit)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        VenteProduit vente = new VenteProduit(vol, produit, quantite);
        vente.setNotes(notes);
        return venteProduitRepository.save(vente);
    }
    
    public BigDecimal getTotalVentesMois(int annee, int mois) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDateTime debut = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);
        return venteProduitRepository.sumMontantByPeriode(debut, fin);
    }
    
    public Integer getQuantiteVentesMois(int annee, int mois) {
        YearMonth ym = YearMonth.of(annee, mois);
        LocalDateTime debut = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);
        return venteProduitRepository.countQuantiteByPeriode(debut, fin);
    }

    // ===============================================
    // FACTURES PRODUITS
    // ===============================================
    
    public List<FactureProduit> getAllFactures() {
        return factureProduitRepository.findAllOrderByDate();
    }
    
    public Optional<FactureProduit> getFactureById(Long id) {
        return factureProduitRepository.findByIdWithDetails(id);
    }
    
    public List<FactureProduit> getFacturesImpayees() {
        return factureProduitRepository.findFacturesImpayees();
    }
    
    /**
     * Génère un numéro de facture produit au format FPROD-YYYY-XXX
     */
    private String genererNumeroFacture() {
        int annee = Year.now().getValue();
        long count = factureProduitRepository.countByAnnee(annee);
        return String.format("FPROD-%d-%03d", annee, count + 1);
    }
    
    /**
     * Crée une facture produit pour une période donnée
     */
    public FactureProduit creerFacture(LocalDate dateDebut, LocalDate dateFin, String notes) {
        // Récupérer les ventes de la période
        List<VenteProduit> ventes = getVentesByPeriode(dateDebut, dateFin);
        
        if (ventes.isEmpty()) {
            throw new RuntimeException("Aucune vente sur cette période");
        }
        
        // Créer la facture
        FactureProduit facture = new FactureProduit();
        facture.setNumeroFacture(genererNumeroFacture());
        facture.setDateFacture(LocalDate.now());
        facture.setDateDebutPeriode(dateDebut);
        facture.setDateFinPeriode(dateFin);
        facture.setNotes(notes);
        facture.setStatut("émise");
        
        // Créer les lignes
        BigDecimal montantTotal = BigDecimal.ZERO;
        
        for (VenteProduit vente : ventes) {
            DetailFactureProduit detail = new DetailFactureProduit();
            detail.setFactureProduit(facture);
            detail.setVenteProduit(vente);
            detail.setVolProgramme(vente.getVolProgramme());
            detail.setProduitExtra(vente.getProduitExtra());
            detail.setDescription(vente.getProduitExtra().getNom() + " - Vol " + vente.getRouteVol());
            detail.setQuantite(vente.getQuantite());
            detail.setPrixUnitaire(vente.getPrixUnitaire());
            detail.setMontantLigne(vente.getMontantTotal());
            detail.setMontantPaye(BigDecimal.ZERO);
            
            facture.getDetails().add(detail);
            montantTotal = montantTotal.add(vente.getMontantTotal());
        }
        
        facture.setMontant(montantTotal);
        
        return factureProduitRepository.save(facture);
    }

    // ===============================================
    // PAIEMENTS PRODUITS
    // ===============================================
    
    public List<PaiementProduit> getPaiementsByFacture(Long idFacture) {
        return paiementProduitRepository.findByFacture(idFacture);
    }
    
    /**
     * Enregistre un paiement et répartit au prorata sur les lignes
     */
    public PaiementProduit enregistrerPaiement(Long idFacture, LocalDate datePaiement, 
            BigDecimal montant, String modePaiement, String reference, String notes) {
        
        FactureProduit facture = factureProduitRepository.findByIdWithDetails(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        
        // Vérifier que le montant ne dépasse pas le reste à payer
        BigDecimal resteAPayer = facture.getMontantRestant();
        if (montant.compareTo(resteAPayer) > 0) {
            throw new RuntimeException("Le montant dépasse le reste à payer (" + resteAPayer + " Ar)");
        }
        
        // Créer le paiement
        PaiementProduit paiement = new PaiementProduit(facture, datePaiement, montant, modePaiement);
        paiement.setReferencePaiement(reference);
        paiement.setNotes(notes);
        paiement = paiementProduitRepository.save(paiement);
        
        // Répartir au prorata sur les lignes
        repartirPaiementProrata(facture, montant);
        
        // Mettre à jour le statut de la facture
        BigDecimal totalPaye = paiementProduitRepository.sumPaiementsByFacture(idFacture);
        if (totalPaye.compareTo(facture.getMontant()) >= 0) {
            facture.setStatut("payée");
        } else if (totalPaye.compareTo(BigDecimal.ZERO) > 0) {
            facture.setStatut("partiellement_payée");
        }
        factureProduitRepository.save(facture);
        
        return paiement;
    }
    
    /**
     * Répartit un paiement au prorata sur les lignes de la facture
     */
    private void repartirPaiementProrata(FactureProduit facture, BigDecimal montantPaiement) {
        BigDecimal montantTotal = facture.getMontant();
        
        for (DetailFactureProduit ligne : facture.getDetails()) {
            // Calcul prorata: part = paiement × (montant_ligne / montant_total)
            BigDecimal partLigne = montantPaiement
                    .multiply(ligne.getMontantLigne())
                    .divide(montantTotal, 2, RoundingMode.HALF_UP);
            
            BigDecimal nouveauMontantPaye = ligne.getMontantPaye().add(partLigne);
            ligne.setMontantPaye(nouveauMontantPaye);
            detailFactureProduitRepository.save(ligne);
        }
    }
    
    public BigDecimal getResteAPayer(Long idFacture) {
        FactureProduit facture = factureProduitRepository.findById(idFacture).orElse(null);
        if (facture == null) return BigDecimal.ZERO;
        
        BigDecimal totalPaye = paiementProduitRepository.sumPaiementsByFacture(idFacture);
        return facture.getMontant().subtract(totalPaye);
    }
}
