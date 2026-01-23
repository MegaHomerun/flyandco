package com.fly.andco.service.diffusions;

import java.math.BigDecimal;

/**
 * DTO représentant une ligne de commande de diffusion
 */
public class LigneDiffusionDTO {
    
    private Long idVolProgramme;
    private Integer nombreDiffusions;
    private BigDecimal prixUnitaire;
    private BigDecimal montantLigne;
    
    // Info vol pour affichage
    private String infoVol;
    
    public LigneDiffusionDTO() {}
    
    public LigneDiffusionDTO(Long idVolProgramme, Integer nombreDiffusions) {
        this.idVolProgramme = idVolProgramme;
        this.nombreDiffusions = nombreDiffusions;
    }
    
    // Getters & Setters
    public Long getIdVolProgramme() {
        return idVolProgramme;
    }
    
    public void setIdVolProgramme(Long idVolProgramme) {
        this.idVolProgramme = idVolProgramme;
    }
    
    public Integer getNombreDiffusions() {
        return nombreDiffusions;
    }
    
    public void setNombreDiffusions(Integer nombreDiffusions) {
        this.nombreDiffusions = nombreDiffusions;
    }
    
    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
    
    public BigDecimal getMontantLigne() {
        return montantLigne;
    }
    
    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }
    
    public String getInfoVol() {
        return infoVol;
    }
    
    public void setInfoVol(String infoVol) {
        this.infoVol = infoVol;
    }
    
    public void calculerMontant() {
        if (nombreDiffusions != null && prixUnitaire != null) {
            this.montantLigne = prixUnitaire.multiply(BigDecimal.valueOf(nombreDiffusions));
        }
    }
}
