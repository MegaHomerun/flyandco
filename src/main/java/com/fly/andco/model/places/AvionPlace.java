package com.fly.andco.model.places;

import com.fly.andco.model.avions.Avion;
import jakarta.persistence.*;

@Entity
@Table(name = "avion_place", uniqueConstraints = @UniqueConstraint(columnNames = {"id_avion", "id_type_place"}))
public class AvionPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion_place")
    private Long idAvionPlace;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    // Constructeurs
    public AvionPlace() {}

    public AvionPlace(Avion avion, TypePlace typePlace, Integer nombrePlaces) {
        this.avion = avion;
        this.typePlace = typePlace;
        this.nombrePlaces = nombrePlaces;
    }

    // Getters & Setters
    public Long getIdAvionPlace() {
        return idAvionPlace;
    }

    public void setIdAvionPlace(Long idAvionPlace) {
        this.idAvionPlace = idAvionPlace;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public TypePlace getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(TypePlace typePlace) {
        this.typePlace = typePlace;
    }

    public Integer getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(Integer nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }
}
