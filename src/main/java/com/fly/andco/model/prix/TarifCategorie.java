package com.fly.andco.model.prix;

import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.model.places.TypePlace;
import com.fly.andco.model.vols.Vol;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "tarif_categorie", uniqueConstraints = 
    @UniqueConstraint(columnNames = {"id_vol", "id_type_place", "id_categorie_passager"}))
public class TarifCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif_categorie")
    private Long idTarifCategorie;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;

    @Column(precision = 12, scale = 2)
    private BigDecimal prix;  // Prix fixe (nullable)

    @Column(precision = 5, scale = 2)
    private BigDecimal pourcentage;  // % du tarif adulte (nullable)

    @Column(name = "frais_reduction", precision = 12, scale = 2)
    private BigDecimal fraisReduction;  // Frais à soustraire (nullable)

    // Constructeurs
    public TarifCategorie() {}

    public TarifCategorie(Vol vol, TypePlace typePlace, CategoriePassager categoriePassager, BigDecimal prix) {
        this.vol = vol;
        this.typePlace = typePlace;
        this.categoriePassager = categoriePassager;
        this.prix = prix;
    }

    public TarifCategorie(Vol vol, TypePlace typePlace, CategoriePassager categoriePassager, 
                          BigDecimal prix, BigDecimal pourcentage, BigDecimal fraisReduction) {
        this.vol = vol;
        this.typePlace = typePlace;
        this.categoriePassager = categoriePassager;
        this.prix = prix;
        this.pourcentage = pourcentage;
        this.fraisReduction = fraisReduction;
    }

    // Getters & Setters
    public Long getIdTarifCategorie() {
        return idTarifCategorie;
    }

    public void setIdTarifCategorie(Long idTarifCategorie) {
        this.idTarifCategorie = idTarifCategorie;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public TypePlace getTypePlace() {
        return typePlace;
    }

    public void setTypePlace(TypePlace typePlace) {
        this.typePlace = typePlace;
    }

    public CategoriePassager getCategoriePassager() {
        return categoriePassager;
    }

    public void setCategoriePassager(CategoriePassager categoriePassager) {
        this.categoriePassager = categoriePassager;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public BigDecimal getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(BigDecimal pourcentage) {
        this.pourcentage = pourcentage;
    }

    public BigDecimal getFraisReduction() {
        return fraisReduction;
    }

    public void setFraisReduction(BigDecimal fraisReduction) {
        this.fraisReduction = fraisReduction;
    }

    /**
     * Calcule le prix effectif en fonction du tarif adulte de référence.
     * Logique:
     * 1. Si prix fixe défini → utiliser le prix
     * 2. Sinon si pourcentage défini → tarif_adulte * pourcentage / 100
     * 3. Si frais_reduction défini → soustraire les frais
     * 4. Prix minimum = 0
     */
    public BigDecimal calculerPrixEffectif(BigDecimal tarifAdulte) {
        // 1. Si prix fixe défini, l'utiliser
        if (prix != null) {
            return prix;
        }
        
        // 2. Calculer avec pourcentage
        BigDecimal prixCalcule = tarifAdulte != null ? tarifAdulte : BigDecimal.ZERO;
        
        if (pourcentage != null) {
            prixCalcule = tarifAdulte.multiply(pourcentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        // 3. Soustraire frais de réduction si définis
        if (fraisReduction != null) {
            prixCalcule = prixCalcule.subtract(fraisReduction);
        }
        
        // 4. Prix minimum = 0
        return prixCalcule.max(BigDecimal.ZERO);
    }

    // Méthode utile pour affichage
    public String getPrixFormate() {
        if (prix != null) {
            return String.format("%,.0f Ar", prix);
        }
        if (pourcentage != null) {
            return String.format("%.0f%% du tarif adulte", pourcentage);
        }
        return "Non défini";
    }
}
