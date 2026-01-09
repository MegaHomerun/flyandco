package com.fly.andco.model.aeroports;

import jakarta.persistence.*;

@Entity
@Table(name = "Aeroport")
public class Aeroport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aeroport")
    private Long idAeroport;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 50)
    private String ville;

    @Column(length = 50)
    private String pays;

    @Column(name = "code_iata", length = 3, unique = true)
    private String codeIata;

    @Column(name = "code_icao", length = 4, unique = true)
    private String codeIcao;

    // Constructeurs
    public Aeroport() {}

    public Aeroport(String nom, String ville, String pays, String codeIata) {
        this.nom = nom;
        this.ville = ville;
        this.pays = pays;
        this.codeIata = codeIata;
    }

    // Getters & Setters
    public Long getIdAeroport() {
        return idAeroport;
    }

    public void setIdAeroport(Long idAeroport) {
        this.idAeroport = idAeroport;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getCodeIata() {
        return codeIata;
    }

    public void setCodeIata(String codeIata) {
        this.codeIata = codeIata;
    }

    public String getCodeIcao() {
        return codeIcao;
    }

    public void setCodeIcao(String codeIcao) {
        this.codeIcao = codeIcao;
    }

    // Méthode utile pour affichage
    public String getDisplayName() {
        return codeIata + " - " + ville;
    }
}
