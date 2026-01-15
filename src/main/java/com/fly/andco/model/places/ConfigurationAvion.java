package com.fly.andco.model.places;

import com.fly.andco.model.avions.Avion;
import jakarta.persistence.*;

@Entity
@Table(name = "configuration_avion", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_avion", "id_type_place"}))
public class ConfigurationAvion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuration")
    private Long idConfiguration;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    // Constructeurs
    public ConfigurationAvion() {}

    public ConfigurationAvion(Avion avion, TypePlace typePlace, int nombrePlaces) {
        this.avion = avion;
        this.typePlace = typePlace;
        this.nombrePlaces = nombrePlaces;
    }

    // Getters & Setters
    public Long getIdConfiguration() {
        return idConfiguration;
    }

    public void setIdConfiguration(Long idConfiguration) {
        this.idConfiguration = idConfiguration;
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

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }
}
