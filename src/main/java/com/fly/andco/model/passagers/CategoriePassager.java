package com.fly.andco.model.passagers;

import jakarta.persistence.*;

@Entity
@Table(name = "categorie_passager")
public class CategoriePassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie_passager")
    private Long idCategoriePassager;

    @Column(nullable = false, unique = true, length = 50)
    private String nom;

    @Column(length = 200)
    private String description;

    @Column(name = "age_min")
    private Integer ageMin = 0;

    @Column(name = "age_max")
    private Integer ageMax = 999;

    // Constructeurs
    public CategoriePassager() {}

    public CategoriePassager(String nom, String description, Integer ageMin, Integer ageMax) {
        this.nom = nom;
        this.description = description;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }

    // Getters & Setters
    public Long getIdCategoriePassager() {
        return idCategoriePassager;
    }

    public void setIdCategoriePassager(Long idCategoriePassager) {
        this.idCategoriePassager = idCategoriePassager;
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

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public Integer getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(Integer ageMax) {
        this.ageMax = ageMax;
    }

    // Méthode utile
    public String getTrancheAge() {
        if (ageMax >= 999) {
            return ageMin + "+ ans";
        }
        return ageMin + " - " + ageMax + " ans";
    }
}
