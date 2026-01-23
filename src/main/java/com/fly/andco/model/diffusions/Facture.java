package com.fly.andco.model.diffusions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facture")
    private Long idFacture;

    @Column(name = "numero_facture", nullable = false, unique = true, length = 50)
    private String numeroFacture;

    @ManyToOne
    @JoinColumn(name = "id_societe_diffuseur", nullable = false)
    private SocieteDiffuseur societeDiffuseur;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "date_debut_periode", nullable = false)
    private LocalDate dateDebutPeriode;

    @Column(name = "date_fin_periode", nullable = false)
    private LocalDate dateFinPeriode;

    @Column(name = "montant_ht", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantHt;

    @Column(name = "taux_tva", precision = 5, scale = 2)
    private BigDecimal tauxTva = BigDecimal.ZERO;

    @Column(name = "montant_tva", precision = 12, scale = 2)
    private BigDecimal montantTva = BigDecimal.ZERO;

    @Column(name = "montant_ttc", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTtc;

    @Column(length = 30)
    private String statut = "émise";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailFacture> details = new ArrayList<>();

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL)
    private List<Paiement> paiements = new ArrayList<>();

    // Constructeurs
    public Facture() {
        this.dateCreation = LocalDateTime.now();
    }

    public Facture(String numeroFacture, SocieteDiffuseur societeDiffuseur, LocalDate dateFacture,
                   LocalDate dateDebutPeriode, LocalDate dateFinPeriode, BigDecimal montantHt) {
        this();
        this.numeroFacture = numeroFacture;
        this.societeDiffuseur = societeDiffuseur;
        this.dateFacture = dateFacture;
        this.dateDebutPeriode = dateDebutPeriode;
        this.dateFinPeriode = dateFinPeriode;
        this.montantHt = montantHt;
        this.montantTtc = montantHt; // Sans TVA par défaut
    }

    // Méthodes utilitaires
    public void calculerMontants() {
        if (this.tauxTva != null && this.montantHt != null) {
            this.montantTva = this.montantHt.multiply(this.tauxTva).divide(BigDecimal.valueOf(100));
            this.montantTtc = this.montantHt.add(this.montantTva);
        }
    }

    public BigDecimal getMontantPaye() {
        return paiements.stream()
                .map(Paiement::getMontantPaye)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant() {
        return montantTtc.subtract(getMontantPaye());
    }

    public int getTotalDiffusions() {
        return details.stream()
                .mapToInt(DetailFacture::getNombreDiffusions)
                .sum();
    }

    // Getters & Setters
    public Long getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(Long idFacture) {
        this.idFacture = idFacture;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public SocieteDiffuseur getSocieteDiffuseur() {
        return societeDiffuseur;
    }

    public void setSocieteDiffuseur(SocieteDiffuseur societeDiffuseur) {
        this.societeDiffuseur = societeDiffuseur;
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

    public BigDecimal getMontantHt() {
        return montantHt;
    }

    public void setMontantHt(BigDecimal montantHt) {
        this.montantHt = montantHt;
    }

    public BigDecimal getTauxTva() {
        return tauxTva;
    }

    public void setTauxTva(BigDecimal tauxTva) {
        this.tauxTva = tauxTva;
    }

    public BigDecimal getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(BigDecimal montantTva) {
        this.montantTva = montantTva;
    }

    public BigDecimal getMontantTtc() {
        return montantTtc;
    }

    public void setMontantTtc(BigDecimal montantTtc) {
        this.montantTtc = montantTtc;
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

    public List<DetailFacture> getDetails() {
        return details;
    }

    public void setDetails(List<DetailFacture> details) {
        this.details = details;
    }

    public List<Paiement> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<Paiement> paiements) {
        this.paiements = paiements;
    }
}
