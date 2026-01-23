package com.fly.andco.service.diffusions;

import java.math.BigDecimal;

/**
 * DTO pour le résultat global du calcul du CA des diffusions
 */
public class CADiffusionDTO {
    
    private Integer totalDiffusions;
    private BigDecimal totalCA;
    
    public CADiffusionDTO() {
        this.totalDiffusions = 0;
        this.totalCA = BigDecimal.ZERO;
    }
    
    public CADiffusionDTO(Integer totalDiffusions, BigDecimal totalCA) {
        this.totalDiffusions = totalDiffusions != null ? totalDiffusions : 0;
        this.totalCA = totalCA != null ? totalCA : BigDecimal.ZERO;
    }
    
    // Getters & Setters
    public Integer getTotalDiffusions() {
        return totalDiffusions;
    }
    
    public void setTotalDiffusions(Integer totalDiffusions) {
        this.totalDiffusions = totalDiffusions;
    }
    
    public BigDecimal getTotalCA() {
        return totalCA;
    }
    
    public void setTotalCA(BigDecimal totalCA) {
        this.totalCA = totalCA;
    }
    
    // Formatage pour l'affichage
    public String getTotalCAFormate() {
        if (totalCA == null) return "0 Ar";
        return String.format("%,.0f Ar", totalCA);
    }
}
