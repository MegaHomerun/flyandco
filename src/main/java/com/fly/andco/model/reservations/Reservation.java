package com.fly.andco.model.reservations;

import com.fly.andco.model.clients.Client;
import com.fly.andco.model.vols.VolProgramme;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_vol_programme", nullable = false)
    private VolProgramme volProgramme;

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces = 1;

    @Column(name = "date_reservation")
    private LocalDateTime dateReservation = LocalDateTime.now();

    @Column(length = 20)
    private String statut = "confirmée";

    // Constructeurs
    public Reservation() {}

    public Reservation(Client client, VolProgramme volProgramme, int nombrePlaces) {
        this.client = client;
        this.volProgramme = volProgramme;
        this.nombrePlaces = nombrePlaces;
        this.dateReservation = LocalDateTime.now();
        this.statut = "confirmée";
    }

    // Getters & Setters
    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public VolProgramme getVolProgramme() {
        return volProgramme;
    }

    public void setVolProgramme(VolProgramme volProgramme) {
        this.volProgramme = volProgramme;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // Méthode utile
    public Double getMontantTotal() {
        return volProgramme.getPrix() * nombrePlaces;
    }
}
