package com.fly.andco.model.produits;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture_produit")
public class FactureProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture_produit")
    private Long idFactureProduit;

    @Column(name = "numero_facture", nullable = false, unique = true, length = 50)
    private String numeroFacture;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "date_debut_periode", nullable = false)
    private LocalDate dateDebutPeriode;

    @Column(name = "date_fin_periode", nullable = false)
    private LocalDate dateFinPeriode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(length = 30)
    private String statut = "émise";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "factureProduit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailFactureProduit> details = new ArrayList<>();

    @OneToMany(mappedBy = "factureProduit", cascade = CascadeType.ALL)
    private List<PaiementProduit> paiements = new ArrayList<>();

    // Constructeurs
    public FactureProduit() {
        this.dateCreation = LocalDateTime.now();
    }

    public FactureProduit(String numeroFacture, LocalDate dateFacture, LocalDate dateDebutPeriode, 
                          LocalDate dateFinPeriode, BigDecimal montant) {
        this();
        this.numeroFacture = numeroFacture;
        this.dateFacture = dateFacture;
        this.dateDebutPeriode = dateDebutPeriode;
        this.dateFinPeriode = dateFinPeriode;
        this.montant = montant;
    }

    // Méthodes utilitaires
    public BigDecimal getMontantPaye() {
        return paiements.stream()
                .map(PaiementProduit::getMontantPaye)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant() {
        return montant.subtract(getMontantPaye());
    }

    public int getTotalQuantite() {
        return details.stream()
                .mapToInt(DetailFactureProduit::getQuantite)
                .sum();
    }

    public String getMontantFormate() {
        if (montant == null) return "0 Ar";
        return String.format("%,.0f Ar", montant);
    }

    public String getMontantPayeFormate() {
        BigDecimal paye = getMontantPaye();
        return String.format("%,.0f Ar", paye);
    }

    public String getMontantRestantFormate() {
        BigDecimal restant = getMontantRestant();
        return String.format("%,.0f Ar", restant);
    }

    public String getStatutBadgeClass() {
        return switch (statut) {
            case "payée" -> "bg-success";
            case "partiellement_payée" -> "bg-warning";
            case "annulée" -> "bg-secondary";
            default -> "bg-info";
        };
    }

    // Getters & Setters
    public Long getIdFactureProduit() {
        return idFactureProduit;
    }

    public void setIdFactureProduit(Long idFactureProduit) {
        this.idFactureProduit = idFactureProduit;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    public LocalDate getDateDebutPeriode() {
        return dateDebutPeriode;
    }

    public void setDateDebutPeriode(LocalDate dateDebutPeriode) {
        this.dateDebutPeriode = dateDebutPeriode;
    }

    public LocalDate getDateFinPeriode() {
        return dateFinPeriode;
    }

    public void setDateFinPeriode(LocalDate dateFinPeriode) {
        this.dateFinPeriode = dateFinPeriode;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<DetailFactureProduit> getDetails() {
        return details;
    }

    public void setDetails(List<DetailFactureProduit> details) {
        this.details = details;
    }

    public List<PaiementProduit> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<PaiementProduit> paiements) {
        this.paiements = paiements;
    }
}
