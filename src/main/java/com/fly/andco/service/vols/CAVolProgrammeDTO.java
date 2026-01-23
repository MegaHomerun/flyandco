package com.fly.andco.service.vols;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DTO pour afficher le Chiffre d'Affaires par Vol Programmé
 * Inclut les revenus des billets et des diffusions publicitaires
 */
public class CAVolProgrammeDTO {

    private Long idVolProgramme;
    
    // Informations vol
    private String aeroportDepart;
    private String aeroportArrivee;
    private String codeDepart;
    private String codeArrivee;
    private String avionModele;
    private String avionImmatriculation;
    private LocalDateTime dateHeureDepart;
    
    // CA Billets
    private BigDecimal montantBillets = BigDecimal.ZERO;
    private Integer nombrePassagers = 0;
    
    // CA Diffusions
    private BigDecimal montantDiffusionsFacture = BigDecimal.ZERO;   // Montant total facturé
    private BigDecimal montantDiffusionsPaye = BigDecimal.ZERO;      // Montant déjà payé
    private BigDecimal montantDiffusionsRestant = BigDecimal.ZERO;   // Reste à payer
    private Integer nombreDiffusions = 0;
    
    // CA Total
    private BigDecimal caTotal = BigDecimal.ZERO;

    // Constructeurs
    public CAVolProgrammeDTO() {}

    // Méthodes utilitaires
    public void calculerTotaux() {
        // CA Total = Billets + Diffusions payées
        this.caTotal = this.montantBillets.add(this.montantDiffusionsPaye);
        // Reste à payer
        this.montantDiffusionsRestant = this.montantDiffusionsFacture.subtract(this.montantDiffusionsPaye);
    }

    // Formatage des montants
    private String formaterMontant(BigDecimal montant) {
        if (montant == null) return "0 Ar";
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(montant) + " Ar";
    }

    public String getMontantBilletsFormate() {
        return formaterMontant(montantBillets);
    }

    public String getMontantDiffusionsFactureFormate() {
        return formaterMontant(montantDiffusionsFacture);
    }

    public String getMontantDiffusionsPayeFormate() {
        return formaterMontant(montantDiffusionsPaye);
    }

    public String getMontantDiffusionsRestantFormate() {
        return formaterMontant(montantDiffusionsRestant);
    }

    public String getCaTotalFormate() {
        return formaterMontant(caTotal);
    }

    // Formatage de la date
    public String getDateDepartFormatee() {
        if (dateHeureDepart == null) return "";
        return dateHeureDepart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getHeureDepartFormatee() {
        if (dateHeureDepart == null) return "";
        return dateHeureDepart.format(DateTimeFormatter.ofPattern("HH'h'mm"));
    }

    public String getRoute() {
        return codeDepart + " → " + codeArrivee;
    }

    // Getters & Setters
    public Long getIdVolProgramme() {
        return idVolProgramme;
    }

    public void setIdVolProgramme(Long idVolProgramme) {
        this.idVolProgramme = idVolProgramme;
    }

    public String getAeroportDepart() {
        return aeroportDepart;
    }

    public void setAeroportDepart(String aeroportDepart) {
        this.aeroportDepart = aeroportDepart;
    }

    public String getAeroportArrivee() {
        return aeroportArrivee;
    }

    public void setAeroportArrivee(String aeroportArrivee) {
        this.aeroportArrivee = aeroportArrivee;
    }

    public String getCodeDepart() {
        return codeDepart;
    }

    public void setCodeDepart(String codeDepart) {
        this.codeDepart = codeDepart;
    }

    public String getCodeArrivee() {
        return codeArrivee;
    }

    public void setCodeArrivee(String codeArrivee) {
        this.codeArrivee = codeArrivee;
    }

    public String getAvionModele() {
        return avionModele;
    }

    public void setAvionModele(String avionModele) {
        this.avionModele = avionModele;
    }

    public String getAvionImmatriculation() {
        return avionImmatriculation;
    }

    public void setAvionImmatriculation(String avionImmatriculation) {
        this.avionImmatriculation = avionImmatriculation;
    }

    public LocalDateTime getDateHeureDepart() {
        return dateHeureDepart;
    }

    public void setDateHeureDepart(LocalDateTime dateHeureDepart) {
        this.dateHeureDepart = dateHeureDepart;
    }

    public BigDecimal getMontantBillets() {
        return montantBillets;
    }

    public void setMontantBillets(BigDecimal montantBillets) {
        this.montantBillets = montantBillets != null ? montantBillets : BigDecimal.ZERO;
    }

    public Integer getNombrePassagers() {
        return nombrePassagers;
    }

    public void setNombrePassagers(Integer nombrePassagers) {
        this.nombrePassagers = nombrePassagers != null ? nombrePassagers : 0;
    }

    public BigDecimal getMontantDiffusionsFacture() {
        return montantDiffusionsFacture;
    }

    public void setMontantDiffusionsFacture(BigDecimal montantDiffusionsFacture) {
        this.montantDiffusionsFacture = montantDiffusionsFacture != null ? montantDiffusionsFacture : BigDecimal.ZERO;
    }

    public BigDecimal getMontantDiffusionsPaye() {
        return montantDiffusionsPaye;
    }

    public void setMontantDiffusionsPaye(BigDecimal montantDiffusionsPaye) {
        this.montantDiffusionsPaye = montantDiffusionsPaye != null ? montantDiffusionsPaye : BigDecimal.ZERO;
    }

    public BigDecimal getMontantDiffusionsRestant() {
        return montantDiffusionsRestant;
    }

    public void setMontantDiffusionsRestant(BigDecimal montantDiffusionsRestant) {
        this.montantDiffusionsRestant = montantDiffusionsRestant != null ? montantDiffusionsRestant : BigDecimal.ZERO;
    }

    public Integer getNombreDiffusions() {
        return nombreDiffusions;
    }

    public void setNombreDiffusions(Integer nombreDiffusions) {
        this.nombreDiffusions = nombreDiffusions != null ? nombreDiffusions : 0;
    }

    public BigDecimal getCaTotal() {
        return caTotal;
    }

    public void setCaTotal(BigDecimal caTotal) {
        this.caTotal = caTotal != null ? caTotal : BigDecimal.ZERO;
    }
}
