package com.fly.andco.model.diffusions;

import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.vols.Vol;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tarif_diffusion")
public class TarifDiffusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif_diffusion")
    private Long idTarifDiffusion;

    @ManyToOne
    @JoinColumn(name = "id_societe_diffuseur")
    private SocieteDiffuseur societeDiffuseur;

    @ManyToOne
    @JoinColumn(name = "id_vol")
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_type_place")
    private TypePlace typePlace;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "date_debut_validite")
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;

    // Constructeurs
    public TarifDiffusion() {}

    public TarifDiffusion(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        this.actif = true;
    }

    // Getters & Setters
    public Long getIdTarifDiffusion() {
        return idTarifDiffusion;
    }

    public void setIdTarifDiffusion(Long idTarifDiffusion) {
        this.idTarifDiffusion = idTarifDiffusion;
    }

    public SocieteDiffuseur getSocieteDiffuseur() {
        return societeDiffuseur;
    }

    public void setSocieteDiffuseur(SocieteDiffuseur societeDiffuseur) {
        this.societeDiffuseur = societeDiffuseur;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public TypePlace getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(TypePlace typePlace) {
        this.typePlace = typePlace;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDate getDateDebutValidite() {
        return dateDebutValidite;
    }

    public void setDateDebutValidite(LocalDate dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public LocalDate getDateFinValidite() {
        return dateFinValidite;
    }

    public void setDateFinValidite(LocalDate dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }
}
