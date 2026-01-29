package com.fly.andco.model.produits;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorie_produit")
public class CategorieProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie_produit")
    private Long idCategorieProduit;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @Column
    private Boolean actif = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "categorieProduit", cascade = CascadeType.ALL)
    private List<ProduitExtra> produits = new ArrayList<>();

    // Constructeurs
    public CategorieProduit() {
        this.dateCreation = LocalDateTime.now();
    }

    public CategorieProduit(String nom, String description) {
        this();
        this.nom = nom;
        this.description = description;
    }

    // Getters & Setters
    public Long getIdCategorieProduit() {
        return idCategorieProduit;
    }

    public void setIdCategorieProduit(Long idCategorieProduit) {
        this.idCategorieProduit = idCategorieProduit;
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

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<ProduitExtra> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitExtra> produits) {
        this.produits = produits;
    }
}
