package com.fly.andco.model.prix;

import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.vols.Vol;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarif_categorie", uniqueConstraints = 
    @UniqueConstraint(columnNames = {"id_vol", "id_type_place", "id_categorie_passager"}))
public class TarifCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif_categorie")
    private Long idTarifCategorie;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    // Constructeurs
    public TarifCategorie() {}

    public TarifCategorie(Vol vol, TypePlace typePlace, CategoriePassager categoriePassager, BigDecimal prix) {
        this.vol = vol;
        this.typePlace = typePlace;
        this.categoriePassager = categoriePassager;
        this.prix = prix;
    }

    // Getters & Setters
    public Long getIdTarifCategorie() {
        return idTarifCategorie;
    }

    public void setIdTarifCategorie(Long idTarifCategorie) {
        this.idTarifCategorie = idTarifCategorie;
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

    public CategoriePassager getCategoriePassager() {
        return categoriePassager;
    }

    public void setCategoriePassager(CategoriePassager categoriePassager) {
        this.categoriePassager = categoriePassager;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    // Méthode utile pour affichage
    public String getPrixFormate() {
        return String.format("%,.0f Ar", prix);
    }
}
