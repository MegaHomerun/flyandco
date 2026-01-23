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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

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
                   LocalDate dateDebutPeriode, LocalDate dateFinPeriode, BigDecimal montant) {
        this();
        this.numeroFacture = numeroFacture;
        this.societeDiffuseur = societeDiffuseur;
        this.dateFacture = dateFacture;
        this.dateDebutPeriode = dateDebutPeriode;
        this.dateFinPeriode = dateFinPeriode;
        this.montant = montant;
    }

    // Méthodes utilitaires
    public BigDecimal getMontantPaye() {
        return paiements.stream()
                .map(Paiement::getMontantPaye)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant() {
        return montant.subtract(getMontantPaye());
    }

    public int getTotalDiffusions() {
        return details.stream()
                .mapToInt(DetailFacture::getNombreDiffusions)
                .sum();
    }
    
    public String getMontantFormate() {
        if (montant == null) return "0 Ar";
        return String.format("%,.0f Ar", montant);
    }
    
    public String getMontantPayeFormate() {
        return String.format("%,.0f Ar", getMontantPaye());
    }
    
    public String getMontantRestantFormate() {
        return String.format("%,.0f Ar", getMontantRestant());
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
