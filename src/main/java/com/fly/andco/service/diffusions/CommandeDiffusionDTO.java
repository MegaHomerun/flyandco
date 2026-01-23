package com.fly.andco.service.diffusions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour commander des diffusions publicitaires
 * Une commande crée une facture avec plusieurs lignes (1 par vol)
 */
public class CommandeDiffusionDTO {
    
    private Long idSociete;
    private LocalDate dateFacture;
    private List<LigneDiffusionDTO> lignes = new ArrayList<>();
    private String notes;
    
    // Calculés
    private int totalDiffusions;
    private BigDecimal montantTotal;
    
    public CommandeDiffusionDTO() {
        this.dateFacture = LocalDate.now();
    }
    
    // Méthodes utilitaires
    public void calculerTotaux() {
        this.totalDiffusions = lignes.stream()
                .mapToInt(l -> l.getNombreDiffusions() != null ? l.getNombreDiffusions() : 0)
                .sum();
        
        this.montantTotal = lignes.stream()
                .map(l -> l.getMontantLigne() != null ? l.getMontantLigne() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void ajouterLigne(LigneDiffusionDTO ligne) {
        this.lignes.add(ligne);
    }
    
    public void supprimerLigne(int index) {
        if (index >= 0 && index < lignes.size()) {
            this.lignes.remove(index);
        }
    }
    
    // Getters & Setters
    public Long getIdSociete() {
        return idSociete;
    }
    
    public void setIdSociete(Long idSociete) {
        this.idSociete = idSociete;
    }
    
    public LocalDate getDateFacture() {
        return dateFacture;
    }
    
    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }
    
    public List<LigneDiffusionDTO> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneDiffusionDTO> lignes) {
        this.lignes = lignes;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public int getTotalDiffusions() {
        return totalDiffusions;
    }
    
    public void setTotalDiffusions(int totalDiffusions) {
        this.totalDiffusions = totalDiffusions;
    }
    
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    
    public String getMontantTotalFormate() {
        if (montantTotal == null) return "0 Ar";
        return String.format("%,.0f Ar", montantTotal);
    }
}
