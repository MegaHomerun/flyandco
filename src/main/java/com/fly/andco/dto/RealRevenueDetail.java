package com.fly.andco.dto;

public class RealRevenueDetail {

    private String classe;
    private String typePassager; // "ADULTE" ou "ENFANT"
    private double prixUnitaire;
    private long placesOccupees;
    private double total;

    public RealRevenueDetail(String classe, String typePassager, double prixUnitaire, long placesOccupees) {
        this.classe = classe;
        this.typePassager = typePassager;
        this.prixUnitaire = prixUnitaire;
        this.placesOccupees = placesOccupees;
        this.total = placesOccupees * prixUnitaire;
    }

    public String getClasse() {
        return classe;
    }

    public String getTypePassager() {
        return typePassager;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public long getPlacesOccupees() {
        return placesOccupees;
    }

    public double getTotal() {
        return total;
    }
}
