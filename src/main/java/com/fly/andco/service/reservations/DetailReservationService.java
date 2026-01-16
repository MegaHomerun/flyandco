package com.fly.andco.service.reservations;

import com.fly.andco.model.reservations.DetailReservation;
import com.fly.andco.repository.reservations.DetailReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DetailReservationService {

    @Autowired
    private DetailReservationRepository detailReservationRepository;

    public List<DetailReservation> getAll() {
        return detailReservationRepository.findAll();
    }

    public Optional<DetailReservation> getById(Long id) {
        return detailReservationRepository.findById(id);
    }

    public List<DetailReservation> getByReservation(Long idReservation) {
        return detailReservationRepository.findByReservationIdReservation(idReservation);
    }

    public DetailReservation save(DetailReservation detailReservation) {
        return detailReservationRepository.save(detailReservation);
    }

    public void delete(Long id) {
        detailReservationRepository.deleteById(id);
    }

    /**
     * Calcule le montant total d'une réservation
     */
    public BigDecimal getMontantTotal(Long idReservation) {
        return detailReservationRepository.getMontantTotalReservation(idReservation);
    }
}
