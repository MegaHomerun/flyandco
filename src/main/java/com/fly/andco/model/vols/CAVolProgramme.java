package com.fly.andco.model.vols;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour le Chiffre d'Affaires par Vol Programmé
 * Utilisé avec la vue v_ca_vol_programme
 */
public class CAVolProgramme {

    private Long idVolProgramme;
    private Long idVol;
    private Long idAvion;
    private String avionModele;
    private String numeroImmatriculation;
    private String depart;
    private String arrivee;
    private LocalDateTime dateHeureDepart;
    private Long idTypePlace;
    private String typePlace;
    private Long idCategoriePassager;
    private String categorie;
    private Long nbReservations;
    private BigDecimal caTotal;

    // Constructeurs
    public CAVolProgramme() {}

    public CAVolProgramme(Long idVolProgramme, Long idVol, Long idAvion, String avionModele,
                          String numeroImmatriculation, String depart, String arrivee,
                          LocalDateTime dateHeureDepart, Long idTypePlace, String typePlace,
                          Long idCategoriePassager, String categorie, Long nbReservations, BigDecimal caTotal) {
        this.idVolProgramme = idVolProgramme;
        this.idVol = idVol;
        this.idAvion = idAvion;
        this.avionModele = avionModele;
        this.numeroImmatriculation = numeroImmatriculation;
        this.depart = depart;
        this.arrivee = arrivee;
        this.dateHeureDepart = dateHeureDepart;
        this.idTypePlace = idTypePlace;
        this.typePlace = typePlace;
        this.idCategoriePassager = idCategoriePassager;
        this.categorie = categorie;
        this.nbReservations = nbReservations;
        this.caTotal = caTotal;
    }

    // Getters & Setters
    public Long getIdVolProgramme() {
        return idVolProgramme;
    }

    public void setIdVolProgramme(Long idVolProgramme) {
        this.idVolProgramme = idVolProgramme;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public Long getIdAvion() {
        return idAvion;
    }

    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
    }

    public String getAvionModele() {
        return avionModele;
    }

    public void setAvionModele(String avionModele) {
        this.avionModele = avionModele;
    }

    public String getNumeroImmatriculation() {
        return numeroImmatriculation;
    }

    public void setNumeroImmatriculation(String numeroImmatriculation) {
        this.numeroImmatriculation = numeroImmatriculation;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArrivee() {
        return arrivee;
    }

    public void setArrivee(String arrivee) {
        this.arrivee = arrivee;
    }

    public LocalDateTime getDateHeureDepart() {
        return dateHeureDepart;
    }

    public void setDateHeureDepart(LocalDateTime dateHeureDepart) {
        this.dateHeureDepart = dateHeureDepart;
    }

    public Long getIdTypePlace() {
        return idTypePlace;
    }

    public void setIdTypePlace(Long idTypePlace) {
        this.idTypePlace = idTypePlace;
    }

    public String getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(String typePlace) {
        this.typePlace = typePlace;
    }

    public Long getIdCategoriePassager() {
        return idCategoriePassager;
    }

    public void setIdCategoriePassager(Long idCategoriePassager) {
        this.idCategoriePassager = idCategoriePassager;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Long getNbReservations() {
        return nbReservations;
    }

    public void setNbReservations(Long nbReservations) {
        this.nbReservations = nbReservations;
    }

    public BigDecimal getCaTotal() {
        return caTotal;
    }

    public void setCaTotal(BigDecimal caTotal) {
        this.caTotal = caTotal;
    }

    // Méthodes utiles
    public String getRoute() {
        return depart + " → " + arrivee;
    }

    public String getCaTotalFormate() {
        return String.format("%,.0f Ar", caTotal);
    }

    public String getAvionDisplay() {
        return avionModele + " (" + numeroImmatriculation + ")";
    }
}
