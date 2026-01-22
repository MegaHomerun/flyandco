-- ============================================================
-- Script de modification : Classes de places et tarification
-- Date: 2026-01-15
-- Description: Ajout des types de places (première/économique),
--              configuration par avion et tarification par vol
-- ============================================================

-- ============================================================
-- NOTE DE REFACTO:
-- Les tables et données de base sont maintenant centralisées dans
-- 01_table.sql (DDL) et 02_data.sql (DML). Ce script est conservé
-- pour historique et n'est plus nécessaire dans le flux principal.
-- ============================================================

\c pg10;

-- ============================================================
-- TABLE TYPE_PLACE (Première classe, Économique, etc.)
-- ============================================================
CREATE TABLE type_place (
    id_type_place SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- ============================================================
-- TABLE AVION_PLACE (Configuration des places par avion)
-- Un avion a X places de type Y
-- ============================================================
CREATE TABLE avion_place (
    id_avion_place SERIAL PRIMARY KEY,
    id_avion INT NOT NULL,
    id_type_place INT NOT NULL,
    nombre_places INT NOT NULL,
    FOREIGN KEY (id_avion) REFERENCES Avion(id_avion) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    UNIQUE (id_avion, id_type_place)
);

-- ============================================================
-- TABLE TARIF_VOL (Prix par type de place pour chaque route)
-- Un vol (route) a un tarif différent selon le type de place
-- ============================================================
CREATE TABLE tarif_vol (
    id_tarif_vol SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_type_place INT NOT NULL,
    prix NUMERIC(12,2) NOT NULL,
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    UNIQUE (id_vol, id_type_place)
);

-- ============================================================
-- VUE: Valeur maximale par avion pour un vol
-- Calcule le revenu potentiel max d'un avion sur une route
-- ============================================================
CREATE OR REPLACE VIEW v_valeur_max_avion_vol AS
SELECT 
    a.id_avion,
    a.modele,
    a.numero_immatriculation,
    v.id_vol,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    SUM(ap.nombre_places * tv.prix) AS valeur_max
FROM avion a
JOIN avion_place ap ON a.id_avion = ap.id_avion
JOIN type_place tp ON ap.id_type_place = tp.id_type_place
CROSS JOIN vol v
JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata;

-- ============================================================
-- INSERTION DES DONNÉES
-- ============================================================

-- Types de places
INSERT INTO type_place (nom, description) VALUES
('Première classe', 'Sièges spacieux avec service premium'),
('Économique', 'Sièges standards');

-- Configuration des places par avion
-- ATR 72-600 (70 places total): 10 première classe, 60 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(1, 1, 10),  -- ATR 72-600: 10 première classe
(1, 2, 60);  -- ATR 72-600: 60 économique

-- Boeing 737-800 (180 places total): 20 première classe, 160 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(2, 1, 20),  -- Boeing 737-800: 20 première classe
(2, 2, 160); -- Boeing 737-800: 160 économique

-- Airbus A320 (150 places total): 16 première classe, 134 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(3, 1, 16),  -- Airbus A320: 16 première classe
(3, 2, 134); -- Airbus A320: 134 économique

-- ATR 42-500 (48 places total): 8 première classe, 40 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(4, 1, 8),   -- ATR 42-500: 8 première classe
(4, 2, 40);  -- ATR 42-500: 40 économique

-- Embraer E190 (100 places total): 12 première classe, 88 économique
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(5, 1, 12),  -- Embraer E190: 12 première classe
(5, 2, 88);  -- Embraer E190: 88 économique

-- Tarifs pour le vol TNR -> Nosy Be (id_vol = 1)
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(1, 1, 1200000),  -- Première classe: 1 200 000 Ar
(1, 2, 700000);   -- Économique: 700 000 Ar

-- Tarifs pour le vol Nosy Be -> TNR (id_vol = 2)
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(2, 1, 1200000),  -- Première classe: 1 200 000 Ar
(2, 2, 700000);   -- Économique: 700 000 Ar

-- Tarifs pour les autres vols (TNR -> Toamasina, etc.)
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(3, 1, 300000),   -- TNR -> Toamasina: Première classe
(3, 2, 150000),   -- TNR -> Toamasina: Économique
(4, 1, 300000),   -- Toamasina -> TNR: Première classe
(4, 2, 150000),   -- Toamasina -> TNR: Économique
(5, 1, 900000),   -- TNR -> Fort Dauphin: Première classe
(5, 2, 450000),   -- TNR -> Fort Dauphin: Économique
(6, 1, 900000),   -- Fort Dauphin -> TNR: Première classe
(6, 2, 450000),   -- Fort Dauphin -> TNR: Économique
(7, 1, 560000),   -- TNR -> Mahajanga: Première classe
(7, 2, 280000),   -- TNR -> Mahajanga: Économique
(8, 1, 560000),   -- Mahajanga -> TNR: Première classe
(8, 2, 280000);   -- Mahajanga -> TNR: Économique

-- ============================================================
-- TEST: Afficher la valeur maximale pour chaque avion sur TNR-NOS
-- ============================================================
-- SELECT * FROM v_valeur_max_avion_vol WHERE depart = 'TNR' AND arrivee = 'NOS';
