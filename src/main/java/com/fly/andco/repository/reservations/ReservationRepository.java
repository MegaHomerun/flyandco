package com.fly.andco.repository.reservations;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fly.andco.model.reservations.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPassager_IdPassager(Long passagerId);

    List<Reservation> findByPassager_IdPassagerAndDateReservationBetween(
            Long passagerId, LocalDateTime start, LocalDateTime end);

    List<Reservation> findByVolInstance_Vol_IdVol(Long idVol);
}
