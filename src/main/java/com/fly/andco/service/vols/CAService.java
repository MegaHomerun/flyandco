package com.fly.andco.service.vols;

import com.fly.andco.model.vols.CAVolProgramme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CAService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<CAVolProgramme> caRowMapper = new RowMapper<CAVolProgramme>() {
        @Override
        public CAVolProgramme mapRow(ResultSet rs, int rowNum) throws SQLException {
            CAVolProgramme ca = new CAVolProgramme();
            ca.setIdVolProgramme(rs.getLong("id_vol_programme"));
            ca.setIdVol(rs.getLong("id_vol"));
            ca.setIdAvion(rs.getLong("id_avion"));
            ca.setAvionModele(rs.getString("avion_modele"));
            ca.setNumeroImmatriculation(rs.getString("numero_immatriculation"));
            ca.setDepart(rs.getString("depart"));
            ca.setArrivee(rs.getString("arrivee"));
            ca.setDateHeureDepart(rs.getTimestamp("date_heure_depart").toLocalDateTime());
            
            Long idTypePlace = rs.getLong("id_type_place");
            ca.setIdTypePlace(rs.wasNull() ? null : idTypePlace);
            ca.setTypePlace(rs.getString("type_place"));
            
            Long idCategoriePassager = rs.getLong("id_categorie_passager");
            ca.setIdCategoriePassager(rs.wasNull() ? null : idCategoriePassager);
            ca.setCategorie(rs.getString("categorie"));
            
            ca.setNbReservations(rs.getLong("nb_reservations"));
            ca.setCaTotal(rs.getBigDecimal("ca_total"));
            return ca;
        }
    };

    /**
     * Récupère le détail du CA par type de place et catégorie pour un vol programmé
     */
    public List<CAVolProgramme> getCAByVolProgramme(Long idVolProgramme) {
        String sql = "SELECT * FROM v_ca_vol_programme WHERE id_vol_programme = ? " +
                     "AND type_place IS NOT NULL ORDER BY type_place, categorie";
        return jdbcTemplate.query(sql, caRowMapper, idVolProgramme);
    }

    /**
     * Récupère le CA total pour un vol programmé
     */
    public BigDecimal getTotalCA(Long idVolProgramme) {
        String sql = "SELECT COALESCE(SUM(ca_total), 0) FROM v_ca_vol_programme WHERE id_vol_programme = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, idVolProgramme);
    }

    /**
     * Récupère le nombre total de passagers pour un vol programmé
     */
    public Long getNbPassagers(Long idVolProgramme) {
        String sql = "SELECT COALESCE(SUM(nb_reservations), 0) FROM v_ca_vol_programme WHERE id_vol_programme = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, idVolProgramme);
    }

    /**
     * Récupère le résumé du CA par vol programmé
     */
    public List<CATotalVolProgramme> getCATotalParVol() {
        String sql = "SELECT * FROM v_ca_total_vol_programme ORDER BY date_heure_depart DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CATotalVolProgramme ca = new CATotalVolProgramme();
            ca.setIdVolProgramme(rs.getLong("id_vol_programme"));
            ca.setAvionModele(rs.getString("avion_modele"));
            ca.setNumeroImmatriculation(rs.getString("numero_immatriculation"));
            ca.setRoute(rs.getString("route"));
            ca.setDateHeureDepart(rs.getTimestamp("date_heure_depart").toLocalDateTime());
            ca.setNbPassagers(rs.getLong("nb_passagers"));
            ca.setCaTotal(rs.getBigDecimal("ca_total"));
            return ca;
        });
    }

    /**
     * Classe interne pour le résumé du CA total
     */
    public static class CATotalVolProgramme {
        private Long idVolProgramme;
        private String avionModele;
        private String numeroImmatriculation;
        private String route;
        private LocalDateTime dateHeureDepart;
        private Long nbPassagers;
        private BigDecimal caTotal;

        // Getters & Setters
        public Long getIdVolProgramme() { return idVolProgramme; }
        public void setIdVolProgramme(Long idVolProgramme) { this.idVolProgramme = idVolProgramme; }
        
        public String getAvionModele() { return avionModele; }
        public void setAvionModele(String avionModele) { this.avionModele = avionModele; }
        
        public String getNumeroImmatriculation() { return numeroImmatriculation; }
        public void setNumeroImmatriculation(String numeroImmatriculation) { this.numeroImmatriculation = numeroImmatriculation; }
        
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        
        public LocalDateTime getDateHeureDepart() { return dateHeureDepart; }
        public void setDateHeureDepart(LocalDateTime dateHeureDepart) { this.dateHeureDepart = dateHeureDepart; }
        
        public Long getNbPassagers() { return nbPassagers; }
        public void setNbPassagers(Long nbPassagers) { this.nbPassagers = nbPassagers; }
        
        public BigDecimal getCaTotal() { return caTotal; }
        public void setCaTotal(BigDecimal caTotal) { this.caTotal = caTotal; }

        public String getCaTotalFormate() {
            return String.format("%,.0f Ar", caTotal);
        }

        public String getAvionDisplay() {
            return avionModele + " (" + numeroImmatriculation + ")";
        }
    }
}
