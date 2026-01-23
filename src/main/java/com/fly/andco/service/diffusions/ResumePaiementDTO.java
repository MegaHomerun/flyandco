package com.fly.andco.service.diffusions;

import java.math.BigDecimal;

/**
 * DTO pour le résumé des paiements d'une société
 */
public class ResumePaiementDTO {
    
    private Long idSociete;
    private String nomSociete;
    private Integer totalDiffusions;
    private BigDecimal totalFacture;
    private BigDecimal totalPaye;
    private BigDecimal resteAPayer;
    
    public ResumePaiementDTO() {}
    
    public ResumePaiementDTO(Long idSociete, String nomSociete, Integer totalDiffusions, 
                             BigDecimal totalFacture, BigDecimal totalPaye, BigDecimal resteAPayer) {
        this.idSociete = idSociete;
        this.nomSociete = nomSociete;
        this.totalDiffusions = totalDiffusions != null ? totalDiffusions : 0;
        this.totalFacture = totalFacture != null ? totalFacture : BigDecimal.ZERO;
        this.totalPaye = totalPaye != null ? totalPaye : BigDecimal.ZERO;
        this.resteAPayer = resteAPayer != null ? resteAPayer : BigDecimal.ZERO;
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
    
    public Integer getTotalDiffusions() {
        return totalDiffusions;
    }
    
    public void setTotalDiffusions(Integer totalDiffusions) {
        this.totalDiffusions = totalDiffusions;
    }
    
    public BigDecimal getTotalFacture() {
        return totalFacture;
    }
    
    public void setTotalFacture(BigDecimal totalFacture) {
        this.totalFacture = totalFacture;
    }
    
    public BigDecimal getTotalPaye() {
        return totalPaye;
    }
    
    public void setTotalPaye(BigDecimal totalPaye) {
        this.totalPaye = totalPaye;
    }
    
    public BigDecimal getResteAPayer() {
        return resteAPayer;
    }
    
    public void setResteAPayer(BigDecimal resteAPayer) {
        this.resteAPayer = resteAPayer;
    }
    
    // Formatage pour l'affichage
    public String getTotalFactureFormate() {
        if (totalFacture == null) return "0 Ar";
        return String.format("%,.0f Ar", totalFacture);
    }
    
    public String getTotalPayeFormate() {
        if (totalPaye == null) return "0 Ar";
        return String.format("%,.0f Ar", totalPaye);
    }
    
    public String getResteAPayerFormate() {
        if (resteAPayer == null) return "0 Ar";
        return String.format("%,.0f Ar", resteAPayer);
    }
}
