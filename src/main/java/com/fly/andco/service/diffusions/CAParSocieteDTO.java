package com.fly.andco.service.diffusions;

import java.math.BigDecimal;

/**
 * DTO pour le CA détaillé par société
 */
public class CAParSocieteDTO {
    
    private Long idSociete;
    private String nomSociete;
    private Integer nombreDiffusions;
    private BigDecimal ca;
    
    public CAParSocieteDTO() {}
    
    public CAParSocieteDTO(Long idSociete, String nomSociete, Integer nombreDiffusions, BigDecimal ca) {
        this.idSociete = idSociete;
        this.nomSociete = nomSociete;
        this.nombreDiffusions = nombreDiffusions != null ? nombreDiffusions : 0;
        this.ca = ca != null ? ca : BigDecimal.ZERO;
    }
    
    // Getters & Setters
    public Long getIdSociete() {
        return idSociete;
    }
    
    public void setIdSociete(Long idSociete) {
        this.idSociete = idSociete;
    }
    
    public String getNomSociete() {
        return nomSociete;
    }
    
    public void setNomSociete(String nomSociete) {
        this.nomSociete = nomSociete;
    }
    
    public Integer getNombreDiffusions() {
        return nombreDiffusions;
    }
    
    public void setNombreDiffusions(Integer nombreDiffusions) {
        this.nombreDiffusions = nombreDiffusions;
    }
    
    public BigDecimal getCa() {
        return ca;
    }
    
    public void setCa(BigDecimal ca) {
        this.ca = ca;
    }
    
    // Formatage pour l'affichage
    public String getCaFormate() {
        if (ca == null) return "0 Ar";
        return String.format("%,.0f Ar", ca);
    }
}
