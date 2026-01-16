package com.fly.andco.service.avions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fly.andco.dto.RealRevenueDetail;
import com.fly.andco.dto.RevenueDetail;
import com.fly.andco.model.avions.Siege;
import com.fly.andco.model.reservations.Reservation;
import com.fly.andco.repository.avions.SiegeRepository;
import com.fly.andco.repository.prix.PrixVolRepository;
import com.fly.andco.repository.reservations.ReservationRepository;

@Service
public class SiegeService {

    private final SiegeRepository siegeRepository;

    private final PrixVolRepository prixVolRepository;

    private final ReservationRepository reservationRepository;

    @Autowired
    public SiegeService(SiegeRepository siegeRepository, PrixVolRepository prixVolRepository, ReservationRepository reservationRepository) {
        this.siegeRepository = siegeRepository;
        this.prixVolRepository = prixVolRepository;
        this.reservationRepository = reservationRepository;
    }

    // Lister tous les sièges
    public List<Siege> getAllSieges() {
        return siegeRepository.findAll();
    }

    public List<RevenueDetail> calculateMaxRevenue(Long volId) {
        // Fetch seats for the specific vol (now that Siege is linked to Vol)
        List<Siege> sieges = siegeRepository.findByVol_IdVol(volId);

        // Use the new repository method to get prices for this vol
        List<com.fly.andco.model.prix.PrixVol> prixForVol = prixVolRepository.findByVol_IdVol(volId);

        // Group seats by class
        java.util.Map<String, Long> seatsByClass = sieges.stream()
                .collect(java.util.stream.Collectors.groupingBy(Siege::getClasse, java.util.stream.Collectors.counting()));

        java.util.List<com.fly.andco.dto.RevenueDetail> details = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, Long> entry : seatsByClass.entrySet()) {
            String classe = entry.getKey();
            Long count = entry.getValue();

            // Find price for ADULTE
            double prixAdulte = prixForVol.stream()
                    .filter(p -> p.getClasse().equalsIgnoreCase(classe)
                    && "ADULTE".equalsIgnoreCase(p.getTypePassager()))
                    .findFirst()
                    .map(com.fly.andco.model.prix.PrixVol::getPrix)
                    .orElse(0.0);

            // Find price for ENFANT
            double prixEnfant = prixForVol.stream()
                    .filter(p -> p.getClasse().equalsIgnoreCase(classe)
                    && "ENFANT".equalsIgnoreCase(p.getTypePassager()))
                    .findFirst()
                    .map(com.fly.andco.model.prix.PrixVol::getPrix)
                    .orElse(0.0);

            details.add(new com.fly.andco.dto.RevenueDetail(classe, prixAdulte, prixEnfant, count));
        }

        return details;
    }

    // Lister les sièges d'un vol
    public List<Siege> getSiegesByVol(Long volId) {
        return siegeRepository.findByVol_IdVol(volId);
    }

    // Calculer le prix réel basé sur les réservations
    public List<RealRevenueDetail> calculateRealRevenue(Long volId) {
        List<Reservation> reservations = reservationRepository.findByVolInstance_Vol_IdVol(volId);

        // Group reservations by class and type passager
        java.util.Map<String, java.util.Map<String, Long>> reservationsByClassAndType = reservations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getPrixVol().getClasse(),
                        java.util.stream.Collectors.groupingBy(
                                r -> r.getPrixVol().getTypePassager() != null ? r.getPrixVol().getTypePassager() : "ADULTE",
                                java.util.stream.Collectors.counting()
                        )
                ));

        List<com.fly.andco.model.prix.PrixVol> prixForVol = prixVolRepository.findByVol_IdVol(volId);

        java.util.List<RealRevenueDetail> details = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, java.util.Map<String, Long>> classEntry : reservationsByClassAndType.entrySet()) {
            String classe = classEntry.getKey();

            for (java.util.Map.Entry<String, Long> typeEntry : classEntry.getValue().entrySet()) {
                String typePassager = typeEntry.getKey();
                Long count = typeEntry.getValue();

                // Find price for this class and type
                double prix = prixForVol.stream()
                        .filter(p -> p.getClasse().equalsIgnoreCase(classe)
                        && typePassager.equalsIgnoreCase(p.getTypePassager()))
                        .findFirst()
                        .map(com.fly.andco.model.prix.PrixVol::getPrix)
                        .orElse(0.0);

                details.add(new RealRevenueDetail(classe, typePassager, prix, count));
            }
        }

        return details;
    }

}
