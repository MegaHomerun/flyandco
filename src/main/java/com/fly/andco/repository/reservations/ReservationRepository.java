package com.fly.andco.repository.reservations;

import com.fly.andco.model.reservations.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Réservations par client
    List<Reservation> findByClientIdClient(Long idClient);
    
    // Réservations par email du client
    @Query("SELECT r FROM Reservation r WHERE r.client.email = :email ORDER BY r.dateReservation DESC")
    List<Reservation> findByClientEmail(@Param("email") String email);
    
    // Réservations pour un vol programmé
    List<Reservation> findByVolProgrammeIdVolProgramme(Long idVolProgramme);
    
    // Nombre de places réservées pour un vol programmé
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.volProgramme.idVolProgramme = :idVolProgramme")
    int countPlacesReservees(@Param("idVolProgramme") Long idVolProgramme);
}
