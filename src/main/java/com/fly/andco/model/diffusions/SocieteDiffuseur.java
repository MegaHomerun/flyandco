package com.fly.andco.model.diffusions;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "societe_diffuseur")
public class SocieteDiffuseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_societe_diffuseur")
    private Long idSocieteDiffuseur;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 150, unique = true)
    private String email;

    @Column(length = 30)
    private String telephone;

    @Column(length = 255)
    private String adresse;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    // Constructeurs
    public SocieteDiffuseur() {
        this.dateCreation = LocalDateTime.now();
    }

    public SocieteDiffuseur(String nom, String email, String telephone) {
        this();
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters & Setters
    public Long getIdSocieteDiffuseur() {
        return idSocieteDiffuseur;
    }

    public void setIdSocieteDiffuseur(Long idSocieteDiffuseur) {
        this.idSocieteDiffuseur = idSocieteDiffuseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
