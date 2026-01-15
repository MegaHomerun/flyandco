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

    @Column(nullable = false)
    private int premiere_classe;

    @Column(nullable = false)
    private int classe_economique ;

    @Column(name = "numero_immatriculation", nullable = false, unique = true, length = 20)
    private String numeroImmatriculation;

    // Constructeurs
    public Avion() {}

    public Avion(String modele, int capacite, String numeroImmatriculation, int premiere_classe, int classe_economique) {
        this.modele = modele;
        this.capacite = capacite;
        this.numeroImmatriculation = numeroImmatriculation;
        this.premiere_classe = premiere_classe;
        this.classe_economique = classe_economique;
    }

    // Getters & Setters
    public Long getIdAvion() {
        return idAvion;
    }

    public int getPremiere_classe() {
        return premiere_classe;
    }

    public int getClasse_economique() {
        return classe_economique;
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

    public void setPremiere_classe(int premiere_classe) {
        this.premiere_classe = premiere_classe;
    }

    public void setClasse_economique(int classe_economique) {
        this.classe_economique = classe_economique;
    }

    // Méthode utile pour affichage
    public String getDisplayName() {
        return modele + " (" + numeroImmatriculation + ")";
    }
}
