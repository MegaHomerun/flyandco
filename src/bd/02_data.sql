-- ============================================================
-- Script d'insertion des données
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-16
-- ============================================================

\c pg10;

-- ============================================================
-- INSERTION: UTILISATEURS
-- ============================================================
INSERT INTO Utilisateur (username, mot_de_passe, role)
VALUES ('admin', 'admin', 'admin');

-- ============================================================
-- INSERTION: AÉROPORTS
-- ============================================================
INSERT INTO Aeroport (nom, ville, pays, code_iata, code_icao) VALUES
('Aéroport International d''Ivato', 'Antananarivo', 'Madagascar', 'TNR', 'FMMI'),
('Aéroport de Fascene', 'Nosy Be', 'Madagascar', 'NOS', 'FMNN'),
('Aéroport de Toamasina', 'Toamasina', 'Madagascar', 'TMM', 'FMMT'),
('Aéroport de Tolagnaro', 'Fort Dauphin', 'Madagascar', 'FTU', 'FMSD'),
('Aéroport de Mahajanga', 'Mahajanga', 'Madagascar', 'MJN', 'FMNM');

-- ============================================================
-- INSERTION: AVIONS
-- ============================================================
INSERT INTO Avion (modele, capacite, numero_immatriculation) VALUES
('ATR 72-600', 70, '5R-MJA'),
('Boeing 737-800', 180, '5R-MJB'),
('Airbus A320', 150, '5R-MJC'),
('ATR 42-500', 48, '5R-MJD'),
('Embraer E190', 100, '5R-MJE'),
('Airbus A321neo', 120, '5R-MJF');

-- ============================================================
-- INSERTION: ROUTES (VOLS)
-- ============================================================
INSERT INTO Vol (id_aeroport_depart, id_aeroport_arrivee) VALUES
(1, 2),  -- TNR -> Nosy Be
(2, 1),  -- Nosy Be -> TNR
(1, 3),  -- TNR -> Toamasina
(3, 1),  -- Toamasina -> TNR
(1, 4),  -- TNR -> Fort Dauphin
(4, 1),  -- Fort Dauphin -> TNR
(1, 5),  -- TNR -> Mahajanga
(5, 1);  -- Mahajanga -> TNR

-- ============================================================
-- INSERTION: PROGRAMMES DE VOLS (Instances)
-- ============================================================
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

-- ============================================================
-- INSERTION: CLIENTS
-- ============================================================
INSERT INTO Client (nom, prenom, email, telephone) VALUES
('Rakoto', 'Jean', 'jean.rakoto@email.com', '034 12 345 67'),
('Rasoa', 'Marie', 'marie.rasoa@email.com', '033 98 765 43');

-- ============================================================
-- INSERTION: RÉSERVATIONS
-- ============================================================
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut) VALUES
(1, 1, 2, 'confirmée');

-- ============================================================
-- INSERTION: TYPES DE PLACES
-- ============================================================
INSERT INTO type_place (nom, description) VALUES
('Première classe', 'Sièges spacieux avec service premium'),
('Économique', 'Sièges standards'),
('Premium', 'Sièges confortables avec services améliorés');

-- ============================================================
-- INSERTION: CONFIGURATION DES PLACES PAR AVION
-- ============================================================

-- ATR 72-600 (70 places total): 10 première classe, 60 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(1, 1, 10),   -- ATR 72-600: 10 première classe
(1, 2, 60);   -- ATR 72-600: 60 économique

-- Boeing 737-800 (180 places total): 20 première classe, 160 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(2, 1, 20),   -- Boeing 737-800: 20 première classe
(2, 2, 160);  -- Boeing 737-800: 160 économique

-- Airbus A320 (150 places total): 16 première classe, 134 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(3, 1, 16),   -- Airbus A320: 16 première classe
(3, 2, 134);  -- Airbus A320: 134 économique

-- ATR 42-500 (48 places total): 8 première classe, 40 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(4, 1, 8),    -- ATR 42-500: 8 première classe
(4, 2, 40);   -- ATR 42-500: 40 économique

-- Embraer E190 (100 places total): 12 première classe, 88 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(5, 1, 12),   -- Embraer E190: 12 première classe
(5, 2, 88);   -- Embraer E190: 88 économique

-- Airbus A321neo (120 places total): 30 première classe, 40 premium, 50 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(6, 1, 30),   -- Airbus A321neo: 30 première classe
(6, 3, 40),   -- Airbus A321neo: 40 premium
(6, 2, 50);   -- Airbus A321neo: 50 économique

-- ============================================================
-- INSERTION: TARIFS PAR VOL ET TYPE DE PLACE
-- ============================================================

-- Vol 1: TNR -> Nosy Be
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(1, 1, 1200000),  -- Première classe: 1 200 000 Ar
(1, 2, 700000),   -- Économique: 700 000 Ar
(1, 3, 1000000);  -- Premium: 1 000 000 Ar

-- Vol 2: Nosy Be -> TNR
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(2, 1, 1200000),  -- Première classe: 1 200 000 Ar
(2, 2, 700000),   -- Économique: 700 000 Ar
(2, 3, 1000000);  -- Premium: 1 000 000 Ar

-- Vol 3: TNR -> Toamasina
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(3, 1, 300000),   -- Première classe: 300 000 Ar
(3, 2, 150000),   -- Économique: 150 000 Ar
(3, 3, 200000);   -- Premium: 200 000 Ar

-- Vol 4: Toamasina -> TNR
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(4, 1, 300000),   -- Première classe: 300 000 Ar
(4, 2, 150000),   -- Économique: 150 000 Ar
(4, 3, 200000);   -- Premium: 200 000 Ar

-- Vol 5: TNR -> Fort Dauphin
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(5, 1, 900000),   -- Première classe: 900 000 Ar
(5, 2, 450000),   -- Économique: 450 000 Ar
(5, 3, 650000);   -- Premium: 650 000 Ar

-- Vol 6: Fort Dauphin -> TNR
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(6, 1, 900000),   -- Première classe: 900 000 Ar
(6, 2, 450000),   -- Économique: 450 000 Ar
(6, 3, 650000);   -- Premium: 650 000 Ar

-- Vol 7: TNR -> Mahajanga
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(7, 1, 560000),   -- Première classe: 560 000 Ar
(7, 2, 280000),   -- Économique: 280 000 Ar
(7, 3, 400000);   -- Premium: 400 000 Ar

-- Vol 8: Mahajanga -> TNR
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(8, 1, 560000),   -- Première classe: 560 000 Ar
(8, 2, 280000),   -- Économique: 280 000 Ar
(8, 3, 400000);   -- Premium: 400 000 Ar
