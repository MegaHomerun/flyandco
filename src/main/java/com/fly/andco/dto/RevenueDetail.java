package com.fly.andco.dto;

public class RevenueDetail {
    private String classe;
    private double prixAdulte;
    private double prixEnfant;
    private long nombreSieges;
    private double totalAdulte;
    private double totalEnfant;

    public RevenueDetail(String classe, double prixAdulte, double prixEnfant, long nombreSieges) {
        this.classe = classe;
        this.prixAdulte = prixAdulte;
        this.prixEnfant = prixEnfant;
        this.nombreSieges = nombreSieges;
        this.totalAdulte = nombreSieges * prixAdulte;
        this.totalEnfant = nombreSieges * prixEnfant;
    }
    
    public String getClasse() { return classe; }
    public double getPrixAdulte() { return prixAdulte; }
    public double getPrixEnfant() { return prixEnfant; }
    public long getNombreSieges() { return nombreSieges; }
    public double getTotalAdulte() { return totalAdulte; }
    public double getTotalEnfant() { return totalEnfant; }
    public double getTotal() { return totalAdulte; } // Pour compatibilité
    public double getPrixUnitaire() { return prixAdulte; } // Pour compatibilité
}
