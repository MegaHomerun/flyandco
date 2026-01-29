package com.fly.andco.model.produits;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produit_extra")
public class ProduitExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit_extra")
    private Long idProduitExtra;

    @ManyToOne
    @JoinColumn(name = "id_categorie_produit", nullable = false)
    private CategorieProduit categorieProduit;

    @Column(name = "code_produit", nullable = false, unique = true, length = 20)
    private String codeProduit;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column
    private Boolean actif = true;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    // Constructeurs
    public ProduitExtra() {
        this.dateCreation = LocalDateTime.now();
    }

    public ProduitExtra(CategorieProduit categorieProduit, String codeProduit, String nom, BigDecimal prixUnitaire) {
        this();
        this.categorieProduit = categorieProduit;
        this.codeProduit = codeProduit;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
    }

    // Méthodes utilitaires
    public String getPrixFormate() {
        if (prixUnitaire == null) return "0 Ar";
        return String.format("%,.0f Ar", prixUnitaire);
    }

    // Getters & Setters
    public Long getIdProduitExtra() {
        return idProduitExtra;
    }

    public void setIdProduitExtra(Long idProduitExtra) {
        this.idProduitExtra = idProduitExtra;
    }

    public CategorieProduit getCategorieProduit() {
        return categorieProduit;
    }

    public void setCategorieProduit(CategorieProduit categorieProduit) {
        this.categorieProduit = categorieProduit;
    }

    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
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

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
