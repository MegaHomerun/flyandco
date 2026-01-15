package com.fly.andco.model.places;

import com.fly.andco.model.vols.Vol;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prix_vol", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_vol", "id_type_place"}))
public class PrixVol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prix_vol")
    private Long idPrixVol;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    // Constructeurs
    public PrixVol() {}

    public PrixVol(Vol vol, TypePlace typePlace, BigDecimal prix) {
        this.vol = vol;
        this.typePlace = typePlace;
        this.prix = prix;
    }

    // Getters & Setters
    public Long getIdPrixVol() {
        return idPrixVol;
    }

    public void setIdPrixVol(Long idPrixVol) {
        this.idPrixVol = idPrixVol;
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
}
