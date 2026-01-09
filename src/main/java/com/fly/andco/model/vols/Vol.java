package com.fly.andco.model.vols;

import com.fly.andco.model.aeroports.Aeroport;
import jakarta.persistence.*;

@Entity
@Table(name = "Vol", uniqueConstraints = @UniqueConstraint(columnNames = {"id_aeroport_depart", "id_aeroport_arrivee"}))
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol")
    private Long idVol;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_depart", nullable = false)
    private Aeroport aeroportDepart;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_arrivee", nullable = false)
    private Aeroport aeroportArrivee;

    // Constructeurs
    public Vol() {}

    public Vol(Aeroport aeroportDepart, Aeroport aeroportArrivee) {
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
    }

    // Getters & Setters
    public Long getIdVol() {
        return idVol;
    }

    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }

    public Aeroport getAeroportDepart() {
        return aeroportDepart;
    }

    public void setAeroportDepart(Aeroport aeroportDepart) {
        this.aeroportDepart = aeroportDepart;
    }

    public Aeroport getAeroportArrivee() {
        return aeroportArrivee;
    }

    public void setAeroportArrivee(Aeroport aeroportArrivee) {
        this.aeroportArrivee = aeroportArrivee;
    }

    // Méthode utile pour affichage
    public String getDisplayName() {
        return aeroportDepart.getCodeIata() + " → " + aeroportArrivee.getCodeIata();
    }
}
