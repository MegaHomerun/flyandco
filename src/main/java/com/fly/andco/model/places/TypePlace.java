package com.fly.andco.model.places;

import jakarta.persistence.*;

@Entity
@Table(name = "type_place")
public class TypePlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_place")
    private Long idTypePlace;

    @Column(nullable = false, unique = true, length = 50)
    private String nom;

    @Column(length = 200)
    private String description;

    // Constructeurs
    public TypePlace() {}

    public TypePlace(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    // Getters & Setters
    public Long getIdTypePlace() {
        return idTypePlace;
    }

    public void setIdTypePlace(Long idTypePlace) {
        this.idTypePlace = idTypePlace;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
