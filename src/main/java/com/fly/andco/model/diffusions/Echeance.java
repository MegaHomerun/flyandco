package com.fly.andco.model.diffusions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "echeance", uniqueConstraints = @UniqueConstraint(columnNames = {"id_plan_echeancier", "numero_echeance"}))
public class Echeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_echeance")
    private Long idEcheance;

    @ManyToOne
    @JoinColumn(name = "id_plan_echeancier", nullable = false)
    private PlanEcheancier planEcheancier;

    @Column(name = "numero_echeance", nullable = false)
    private Integer numeroEcheance;

    @Column(name = "date_echeance_prevue", nullable = false)
    private LocalDate dateEcheancePrevue;

    @Column(name = "montant_prevu", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantPrevu;

    @Column(name = "date_paiement_reel")
    private LocalDate datePaiementReel;

    @Column(name = "montant_paye", precision = 12, scale = 2)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(length = 30)
    private String statut = "en_attente";

    // Constructeurs
    public Echeance() {}

    public Echeance(PlanEcheancier planEcheancier, Integer numeroEcheance, LocalDate dateEcheancePrevue, BigDecimal montantPrevu) {
        this.planEcheancier = planEcheancier;
        this.numeroEcheance = numeroEcheance;
        this.dateEcheancePrevue = dateEcheancePrevue;
        this.montantPrevu = montantPrevu;
    }

    // Méthodes utilitaires
    public boolean isEnRetard() {
        return "en_attente".equals(statut) && LocalDate.now().isAfter(dateEcheancePrevue);
    }

    public void marquerPayee(LocalDate datePaiement, BigDecimal montant) {
        this.datePaiementReel = datePaiement;
        this.montantPaye = montant;
        this.statut = "payée";
    }

    // Getters & Setters
    public Long getIdEcheance() {
        return idEcheance;
    }

    public void setIdEcheance(Long idEcheance) {
        this.idEcheance = idEcheance;
    }

    public PlanEcheancier getPlanEcheancier() {
        return planEcheancier;
    }

    public void setPlanEcheancier(PlanEcheancier planEcheancier) {
        this.planEcheancier = planEcheancier;
    }

    public Integer getNumeroEcheance() {
        return numeroEcheance;
    }

    public void setNumeroEcheance(Integer numeroEcheance) {
        this.numeroEcheance = numeroEcheance;
    }

    public LocalDate getDateEcheancePrevue() {
        return dateEcheancePrevue;
    }

    public void setDateEcheancePrevue(LocalDate dateEcheancePrevue) {
        this.dateEcheancePrevue = dateEcheancePrevue;
    }

    public BigDecimal getMontantPrevu() {
        return montantPrevu;
    }

    public void setMontantPrevu(BigDecimal montantPrevu) {
        this.montantPrevu = montantPrevu;
    }

    public LocalDate getDatePaiementReel() {
        return datePaiementReel;
    }

    public void setDatePaiementReel(LocalDate datePaiementReel) {
        this.datePaiementReel = datePaiementReel;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
