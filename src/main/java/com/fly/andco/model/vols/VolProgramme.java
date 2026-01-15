package com.fly.andco.model.vols;

import com.fly.andco.model.avions.Avion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vol_programme")
public class VolProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol_programme")
    private Long idVolProgramme;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;

    @Column(name = "date_heure_arrivee", nullable = false)
    private LocalDateTime dateHeureArrivee;

    @Column(nullable = false)
    private Double prix;

    @Column(length = 20)
    private String statut = "prévu";

    // Constructeurs
    public VolProgramme() {}

    public VolProgramme(Vol vol, Avion avion, LocalDateTime dateHeureDepart, LocalDateTime dateHeureArrivee, Double prix) {
        this.vol = vol;
        this.avion = avion;
        this.dateHeureDepart = dateHeureDepart;
        this.dateHeureArrivee = dateHeureArrivee;
        this.prix = prix;
        this.statut = "prévu";
    }

    // Getters & Setters
    public Long getIdVolProgramme() {
        return idVolProgramme;
    }

    public void setIdVolProgramme(Long idVolProgramme) {
        this.idVolProgramme = idVolProgramme;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public Avion getAvion() {
        return avion;
    }

    public void setAvion(Avion avion) {
        this.avion = avion;
    }

    public LocalDateTime getDateHeureDepart() {
        return dateHeureDepart;
    }

    public void setDateHeureDepart(LocalDateTime dateHeureDepart) {
        this.dateHeureDepart = dateHeureDepart;
    }

    public LocalDateTime getDateHeureArrivee() {
        return dateHeureArrivee;
    }

    public void setDateHeureArrivee(LocalDateTime dateHeureArrivee) {
        this.dateHeureArrivee = dateHeureArrivee;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Méthodes utiles
    public int getPlacesDisponibles(int placesReservees) {
        return avion.getCapacite() - placesReservees;
    }
}
