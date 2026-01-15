package com.fly.andco.model.places;

import com.fly.andco.model.vols.Vol;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarif_vol", uniqueConstraints = @UniqueConstraint(columnNames = {"id_vol", "id_type_place"}))
public class TarifVol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif_vol")
    private Long idTarifVol;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    // Constructeurs
    public TarifVol() {}

    public TarifVol(Vol vol, TypePlace typePlace, BigDecimal prix) {
        this.vol = vol;
        this.typePlace = typePlace;
        this.prix = prix;
    }

    // Getters & Setters
    public Long getIdTarifVol() {
        return idTarifVol;
    }

    public void setIdTarifVol(Long idTarifVol) {
        this.idTarifVol = idTarifVol;
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
