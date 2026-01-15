package com.fly.andco.model.places;

import java.math.BigDecimal;

/**
 * DTO pour afficher la valeur maximale qu'un avion peut générer pour un vol
 */
public class ValeurMaxAvionVol {

    private Long idAvion;
    private String modele;
    private String numeroImmatriculation;
    private Long idVol;
    private String depart;
    private String arrivee;
    private BigDecimal valeurMaximale;

    // Constructeurs
    public ValeurMaxAvionVol() {}

    public ValeurMaxAvionVol(Long idAvion, String modele, String numeroImmatriculation, 
                              Long idVol, String depart, String arrivee, BigDecimal valeurMaximale) {
        this.idAvion = idAvion;
        this.modele = modele;
        this.numeroImmatriculation = numeroImmatriculation;
        this.idVol = idVol;
        this.depart = depart;
        this.arrivee = arrivee;
        this.valeurMaximale = valeurMaximale;
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

    public String getNumeroImmatriculation() {
        return numeroImmatriculation;
    }

    public void setNumeroImmatriculation(String numeroImmatriculation) {
        this.numeroImmatriculation = numeroImmatriculation;
    }

    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
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

    public BigDecimal getValeurMaximale() {
        return valeurMaximale;
    }

    public void setValeurMaximale(BigDecimal valeurMaximale) {
        this.valeurMaximale = valeurMaximale;
    }

    public String getRoute() {
        return depart + " → " + arrivee;
    }

    public String getValeurMaximaleFormatee() {
        if (valeurMaximale != null) {
            return String.format("%,.0f Ar", valeurMaximale);
        }
        return "N/A";
    }
}
