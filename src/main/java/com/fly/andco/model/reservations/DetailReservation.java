package com.fly.andco.model.reservations;

import com.fly.andco.model.passagers.CategoriePassager;
import com.fly.andco.model.places.TypePlace;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detail_reservation")
public class DetailReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detail_reservation")
    private Long idDetailReservation;

    @ManyToOne
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "id_type_place", nullable = false)
    private TypePlace typePlace;

    @ManyToOne
    @JoinColumn(name = "id_categorie_passager", nullable = false)
    private CategoriePassager categoriePassager;

    @Column(name = "prix_paye", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixPaye;

    // Constructeurs
    public DetailReservation() {}

    public DetailReservation(Reservation reservation, TypePlace typePlace, 
                              CategoriePassager categoriePassager, BigDecimal prixPaye) {
        this.reservation = reservation;
        this.typePlace = typePlace;
        this.categoriePassager = categoriePassager;
        this.prixPaye = prixPaye;
    }

    // Getters & Setters
    public Long getIdDetailReservation() {
        return idDetailReservation;
    }

    public void setIdDetailReservation(Long idDetailReservation) {
        this.idDetailReservation = idDetailReservation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
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

    public BigDecimal getPrixPaye() {
        return prixPaye;
    }

    public void setPrixPaye(BigDecimal prixPaye) {
        this.prixPaye = prixPaye;
    }

    // Méthode utile pour affichage
    public String getPrixPayeFormate() {
        return String.format("%,.0f Ar", prixPaye);
    }
}
