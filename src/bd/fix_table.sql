-- Script de correction pour la table vol_programme
-- À exécuter dans psql après connexion à la base pg10

\c pg10;

-- Supprimer les tables dépendantes
DROP TABLE IF EXISTS Reservation CASCADE;
DROP TABLE IF EXISTS VolProgramme CASCADE;
DROP TABLE IF EXISTS vol_programme CASCADE;

-- Recréer la table avec le bon nom
CREATE TABLE vol_programme (
    id_vol_programme SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_avion INT NOT NULL,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    prix NUMERIC(10,2) NOT NULL,
    statut VARCHAR(20) DEFAULT 'prévu',
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol),
    FOREIGN KEY (id_avion) REFERENCES Avion(id_avion)
);

-- Recréer la table Reservation
CREATE TABLE Reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    id_vol_programme INT NOT NULL,
    nombre_places INT NOT NULL DEFAULT 1,
    date_reservation TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'confirmée',
    FOREIGN KEY (id_client) REFERENCES Client(id_client),
    FOREIGN KEY (id_vol_programme) REFERENCES vol_programme(id_vol_programme)
);

-- Réinsérer les données de vols programmés
INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut) VALUES
-- 12 janvier 2026 - Vol TNR -> Nosy Be
(1, 1, '2026-01-12 12:00:00', '2026-01-12 13:30:00', 350000, 'prévu'),
(1, 2, '2026-01-12 08:00:00', '2026-01-12 09:30:00', 380000, 'prévu'),
(1, 3, '2026-01-12 16:00:00', '2026-01-12 17:30:00', 360000, 'prévu'),

-- 12 janvier 2026 - Vol Nosy Be -> TNR
(2, 1, '2026-01-12 14:30:00', '2026-01-12 16:00:00', 350000, 'prévu'),
(2, 2, '2026-01-12 10:30:00', '2026-01-12 12:00:00', 380000, 'prévu'),

-- 13 janvier 2026 - Vol TNR -> Nosy Be
(1, 1, '2026-01-13 12:00:00', '2026-01-13 13:30:00', 350000, 'prévu'),
(1, 4, '2026-01-13 07:00:00', '2026-01-13 08:45:00', 320000, 'prévu'),

-- Autres vols
(3, 5, '2026-01-12 09:00:00', '2026-01-12 09:45:00', 150000, 'prévu'),
(4, 5, '2026-01-12 15:00:00', '2026-01-12 15:45:00', 150000, 'prévu'),
(5, 2, '2026-01-15 06:00:00', '2026-01-15 07:30:00', 450000, 'prévu'),
(7, 3, '2026-01-14 11:00:00', '2026-01-14 12:00:00', 280000, 'prévu');

-- Réinsérer une réservation exemple
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut) VALUES
(1, 1, 2, 'confirmée');

-- Vérification
SELECT 'Vols programmés créés:' as message, COUNT(*) as count FROM vol_programme;
SELECT 'Réservations créées:' as message, COUNT(*) as count FROM Reservation;
