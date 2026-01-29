package com.fly.andco.model.produits;

import com.fly.andco.model.vols.VolProgramme;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vente_produit")
public class VenteProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vente_produit")
    private Long idVenteProduit;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme", nullable = false)
    private VolProgramme volProgramme;

    @ManyToOne
    @JoinColumn(name = "id_produit_extra", nullable = false)
    private ProduitExtra produitExtra;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "date_vente")
    private LocalDateTime dateVente;

    @Column(length = 255)
    private String notes;

    // Constructeurs
    public VenteProduit() {
        this.dateVente = LocalDateTime.now();
    }

    public VenteProduit(VolProgramme volProgramme, ProduitExtra produitExtra, Integer quantite) {
        this();
        this.volProgramme = volProgramme;
        this.produitExtra = produitExtra;
        this.quantite = quantite;
        this.prixUnitaire = produitExtra.getPrixUnitaire();
        this.calculerMontant();
    }

    // Méthodes utilitaires
    public void calculerMontant() {
        if (this.quantite != null && this.prixUnitaire != null) {
            this.montantTotal = this.prixUnitaire.multiply(BigDecimal.valueOf(this.quantite));
        }
    }

    public String getMontantFormate() {
        if (montantTotal == null) return "0 Ar";
        return String.format("%,.0f Ar", montantTotal);
    }

    public String getRouteVol() {
        if (volProgramme == null || volProgramme.getVol() == null) return "";
        return volProgramme.getVol().getAeroportDepart().getCodeIata() + " → " 
             + volProgramme.getVol().getAeroportArrivee().getCodeIata();
    }

    // Getters & Setters
    public Long getIdVenteProduit() {
        return idVenteProduit;
    }

    public void setIdVenteProduit(Long idVenteProduit) {
        this.idVenteProduit = idVenteProduit;
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

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
