package com.fly.andco.repository.reservations;

import com.fly.andco.model.reservations.DetailReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetailReservationRepository extends JpaRepository<DetailReservation, Long> {
    
    /**
     * Trouve les détails d'une réservation
     */
    @Query("SELECT dr FROM DetailReservation dr WHERE dr.reservation.idReservation = :idReservation")
    List<DetailReservation> findByReservationIdReservation(@Param("idReservation") Long idReservation);
    
    /**
     * Calcule le montant total d'une réservation
     */
    @Query("SELECT COALESCE(SUM(dr.prixPaye), 0) FROM DetailReservation dr " +
           "WHERE dr.reservation.idReservation = :idReservation")
    BigDecimal getMontantTotalReservation(@Param("idReservation") Long idReservation);
}
