package com.fly.andco.model.produits;

import com.fly.andco.model.vols.VolProgramme;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detail_facture_produit")
public class DetailFactureProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail_facture_produit")
    private Long idDetailFactureProduit;

    @ManyToOne
    @JoinColumn(name = "id_facture_produit", nullable = false)
    private FactureProduit factureProduit;

    @ManyToOne
    @JoinColumn(name = "id_vente_produit")
    private VenteProduit venteProduit;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme")
    private VolProgramme volProgramme;

    @ManyToOne
    @JoinColumn(name = "id_produit_extra")
    private ProduitExtra produitExtra;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ligne", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantLigne;

    @Column(name = "montant_paye", precision = 12, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    // Constructeurs
    public DetailFactureProduit() {}

    public DetailFactureProduit(FactureProduit factureProduit, String description, Integer quantite, BigDecimal prixUnitaire) {
        this.factureProduit = factureProduit;
        this.description = description;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.calculerMontant();
    }

    // Méthodes utilitaires
    public void calculerMontant() {
        if (this.quantite != null && this.prixUnitaire != null) {
            this.montantLigne = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        }
    }

    public String getMontantLigneFormate() {
        if (montantLigne == null) return "0 Ar";
        return String.format("%,.0f Ar", montantLigne);
    }

    // Getters & Setters
    public Long getIdDetailFactureProduit() {
        return idDetailFactureProduit;
    }

    public void setIdDetailFactureProduit(Long idDetailFactureProduit) {
        this.idDetailFactureProduit = idDetailFactureProduit;
    }

    public FactureProduit getFactureProduit() {
        return factureProduit;
    }

    public void setFactureProduit(FactureProduit factureProduit) {
        this.factureProduit = factureProduit;
    }

    public VenteProduit getVenteProduit() {
        return venteProduit;
    }

    public void setVenteProduit(VenteProduit venteProduit) {
        this.venteProduit = venteProduit;
    }

    public VolProgramme getVolProgramme() {
        return volProgramme;
    }

    public void setVolProgramme(VolProgramme volProgramme) {
        this.volProgramme = volProgramme;
    }

    public ProduitExtra getProduitExtra() {
        return produitExtra;
    }

    public void setProduitExtra(ProduitExtra produitExtra) {
        this.produitExtra = produitExtra;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }
}
