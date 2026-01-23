package com.fly.andco.model.diffusions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long idPaiement;

    @ManyToOne
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "montant_paye", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantPaye;

    @Column(name = "mode_paiement", length = 50)
    private String modePaiement;

    @Column(name = "reference_paiement", length = 100)
    private String referencePaiement;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    // Constructeurs
    public Paiement() {
        this.dateCreation = LocalDateTime.now();
    }

    public Paiement(Facture facture, LocalDate datePaiement, BigDecimal montantPaye, String modePaiement) {
        this();
        this.facture = facture;
        this.datePaiement = datePaiement;
        this.montantPaye = montantPaye;
        this.modePaiement = modePaiement;
    }
    
    // Méthode utilitaire
    public String getMontantPayeFormate() {
        if (montantPaye == null) return "0 Ar";
        return String.format("%,.0f Ar", montantPaye);
    }

    // Getters & Setters
    public Long getIdPaiement() {
        return idPaiement;
    }

    public void setIdPaiement(Long idPaiement) {
        this.idPaiement = idPaiement;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
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
}
