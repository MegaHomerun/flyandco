package com.fly.andco.model.diffusions;

import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.vols.VolProgramme;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detail_facture")
public class DetailFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail_facture")
    private Long idDetailFacture;

    @ManyToOne
    @JoinColumn(name = "id_facture", nullable = false)
    private Facture facture;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme")
    private VolProgramme volProgramme;

    @ManyToOne
    @JoinColumn(name = "id_type_place")
    private TypePlace typePlace;

    @Column(length = 255)
    private String description;

    @Column(name = "nombre_diffusions", nullable = false)
    private Integer nombreDiffusions;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ligne", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantLigne;

    // Constructeurs
    public DetailFacture() {}

    public DetailFacture(Facture facture, String description, Integer nombreDiffusions, BigDecimal prixUnitaire) {
        this.facture = facture;
        this.description = description;
        this.nombreDiffusions = nombreDiffusions;
        this.prixUnitaire = prixUnitaire;
        this.calculerMontant();
    }

    // Méthodes utilitaires
    public void calculerMontant() {
        if (this.nombreDiffusions != null && this.prixUnitaire != null) {
            this.montantLigne = this.prixUnitaire.multiply(BigDecimal.valueOf(this.nombreDiffusions));
        }
    }

    // Getters & Setters
    public Long getIdDetailFacture() {
        return idDetailFacture;
    }

    public void setIdDetailFacture(Long idDetailFacture) {
        this.idDetailFacture = idDetailFacture;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public VolProgramme getVolProgramme() {
        return volProgramme;
    }

    public void setVolProgramme(VolProgramme volProgramme) {
        this.volProgramme = volProgramme;
    }

    public TypePlace getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(TypePlace typePlace) {
        this.typePlace = typePlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNombreDiffusions() {
        return nombreDiffusions;
    }

    public void setNombreDiffusions(Integer nombreDiffusions) {
        this.nombreDiffusions = nombreDiffusions;
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
}
