package com.fly.andco.model.avions;

import jakarta.persistence.*;

@Entity
@Table(name = "Avion")
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion")
    private Long idAvion;

    @Column(nullable = false, length = 50)
    private String modele;

    @Column(nullable = false)
    private int capacite;

    @Column(name = "numero_immatriculation", nullable = false, unique = true, length = 20)
    private String numeroImmatriculation;

    // Constructeurs
    public Avion() {}

    public Avion(String modele, int capacite, String numeroImmatriculation) {
        this.modele = modele;
        this.capacite = capacite;
        this.numeroImmatriculation = numeroImmatriculation;
    }

    // Getters & Setters
    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getNumeroImmatriculation() {
        return numeroImmatriculation;
    }

    public void setNumeroImmatriculation(String numeroImmatriculation) {
        this.numeroImmatriculation = numeroImmatriculation;
    }

    // Méthode utile pour affichage
    public String getDisplayName() {
        return modele + " (" + numeroImmatriculation + ")";
    }
}
