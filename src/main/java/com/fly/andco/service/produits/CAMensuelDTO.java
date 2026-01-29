package com.fly.andco.service.produits;

import java.math.BigDecimal;

/**
 * DTO pour le CA mensuel consolidé
 */
public class CAMensuelDTO {
    
    private int annee;
    private int mois;
    private String moisLibelle;
    
    // Tickets
    private int nbTickets;
    private BigDecimal caTickets;
    
    // Diffusions
    private int nbDiffusions;
    private BigDecimal caDiffusions;
    
    // Produits
    private int nbProduits;
    private BigDecimal caProduits;
    
    // Total
    private BigDecimal caTotal;
    
    public CAMensuelDTO() {
        this.caTickets = BigDecimal.ZERO;
        this.caDiffusions = BigDecimal.ZERO;
        this.caProduits = BigDecimal.ZERO;
        this.caTotal = BigDecimal.ZERO;
    }
    
    public CAMensuelDTO(int annee, int mois) {
        this();
        this.annee = annee;
        this.mois = mois;
        this.moisLibelle = getMoisNom(mois) + " " + annee;
    }
    
    private String getMoisNom(int mois) {
        String[] noms = {"", "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                         "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        return mois >= 1 && mois <= 12 ? noms[mois] : "";
    }
    
    public void calculerTotal() {
        this.caTotal = caTickets.add(caDiffusions).add(caProduits);
    }
    
    // Formatages
    public String getCaTicketsFormate() {
        return String.format("%,.0f Ar", caTickets);
    }
    
    public String getCaDiffusionsFormate() {
        return String.format("%,.0f Ar", caDiffusions);
    }
    
    public String getCaProduitsFormate() {
        return String.format("%,.0f Ar", caProduits);
    }
    
    public String getCaTotalFormate() {
        return String.format("%,.0f Ar", caTotal);
    }
    
    public int getNbTotal() {
        return nbTickets + nbDiffusions + nbProduits;
    }
    
    // Getters & Setters
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    
    public int getMois() { return mois; }
    public void setMois(int mois) { this.mois = mois; }
    
    public String getMoisLibelle() { return moisLibelle; }
    public void setMoisLibelle(String moisLibelle) { this.moisLibelle = moisLibelle; }
    
    public int getNbTickets() { return nbTickets; }
    public void setNbTickets(int nbTickets) { this.nbTickets = nbTickets; }
    
    public BigDecimal getCaTickets() { return caTickets; }
    public void setCaTickets(BigDecimal caTickets) { this.caTickets = caTickets; }
    
    public int getNbDiffusions() { return nbDiffusions; }
    public void setNbDiffusions(int nbDiffusions) { this.nbDiffusions = nbDiffusions; }
    
    public BigDecimal getCaDiffusions() { return caDiffusions; }
    public void setCaDiffusions(BigDecimal caDiffusions) { this.caDiffusions = caDiffusions; }
    
    public int getNbProduits() { return nbProduits; }
    public void setNbProduits(int nbProduits) { this.nbProduits = nbProduits; }
    
    public BigDecimal getCaProduits() { return caProduits; }
    public void setCaProduits(BigDecimal caProduits) { this.caProduits = caProduits; }
    
    public BigDecimal getCaTotal() { return caTotal; }
    public void setCaTotal(BigDecimal caTotal) { this.caTotal = caTotal; }
}
