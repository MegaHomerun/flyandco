package com.fly.andco.model.places;

import java.math.BigDecimal;

/**
 * DTO pour afficher la configuration d'un avion avec ses types de places
 */
public class ConfigurationAvionDTO {

    private Long idAvion;
    private String modele;
    private String numeroImmatriculation;
    private int capaciteTotale;
    private String typePlace;
    private int nombrePlaces;
    private BigDecimal prixRoute;
    private String route;

    // Constructeurs
    public ConfigurationAvionDTO() {}

    public ConfigurationAvionDTO(Long idAvion, String modele, String numeroImmatriculation, 
                                  int capaciteTotale, String typePlace, int nombrePlaces) {
        this.idAvion = idAvion;
        this.modele = modele;
        this.numeroImmatriculation = numeroImmatriculation;
        this.capaciteTotale = capaciteTotale;
        this.typePlace = typePlace;
        this.nombrePlaces = nombrePlaces;
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

    public int getCapaciteTotale() {
        return capaciteTotale;
    }

    public void setCapaciteTotale(int capaciteTotale) {
        this.capaciteTotale = capaciteTotale;
    }

    public String getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(String typePlace) {
        this.typePlace = typePlace;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public BigDecimal getPrixRoute() {
        return prixRoute;
    }

    public void setPrixRoute(BigDecimal prixRoute) {
        this.prixRoute = prixRoute;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getPrixFormate() {
        if (prixRoute != null) {
            return String.format("%,.0f Ar", prixRoute);
        }
        return "N/A";
    }
}
