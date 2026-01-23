package com.fly.andco.model.diffusions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_echeancier")
public class PlanEcheancier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan_echeancier")
    private Long idPlanEcheancier;

    @OneToOne
    @JoinColumn(name = "id_facture", nullable = false, unique = true)
    private Facture facture;

    @Column(name = "montant_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "nombre_echeances", nullable = false)
    private Integer nombreEcheances;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin_prevue", nullable = false)
    private LocalDate dateFinPrevue;

    @Column(length = 30)
    private String statut = "actif";

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "planEcheancier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Echeance> echeances = new ArrayList<>();

    // Constructeurs
    public PlanEcheancier() {
        this.dateCreation = LocalDateTime.now();
    }

    public PlanEcheancier(Facture facture, Integer nombreEcheances, LocalDate dateDebut) {
        this();
        this.facture = facture;
        this.montantTotal = facture.getMontantTtc();
        this.nombreEcheances = nombreEcheances;
        this.dateDebut = dateDebut;
    }

    // Méthodes utilitaires
    public void genererEcheances() {
        BigDecimal montantParEcheance = montantTotal.divide(BigDecimal.valueOf(nombreEcheances), 2, java.math.RoundingMode.HALF_UP);
        LocalDate dateEcheance = dateDebut;
        
        for (int i = 1; i <= nombreEcheances; i++) {
            Echeance echeance = new Echeance();
            echeance.setPlanEcheancier(this);
            echeance.setNumeroEcheance(i);
            echeance.setDateEcheancePrevue(dateEcheance);
            echeance.setMontantPrevu(montantParEcheance);
            echeances.add(echeance);
            
            dateEcheance = dateEcheance.plusMonths(1);
        }
        
        this.dateFinPrevue = dateEcheance.minusMonths(1);
    }

    public BigDecimal getMontantPaye() {
        return echeances.stream()
                .map(e -> e.getMontantPaye() != null ? e.getMontantPaye() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMontantRestant() {
        return montantTotal.subtract(getMontantPaye());
    }

    // Getters & Setters
    public Long getIdPlanEcheancier() {
        return idPlanEcheancier;
    }

    public void setIdPlanEcheancier(Long idPlanEcheancier) {
        this.idPlanEcheancier = idPlanEcheancier;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Integer getNombreEcheances() {
        return nombreEcheances;
    }

    public void setNombreEcheances(Integer nombreEcheances) {
        this.nombreEcheances = nombreEcheances;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFinPrevue() {
        return dateFinPrevue;
    }

    public void setDateFinPrevue(LocalDate dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Echeance> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<Echeance> echeances) {
        this.echeances = echeances;
    }
}
