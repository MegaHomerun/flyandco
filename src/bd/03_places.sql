-- ============================================================
-- Script de création des tables pour la gestion des places
-- et des prix par type de place et par route
-- ============================================================

-- ============================================================
-- TABLE TYPE_PLACE (Première classe, Économique, etc.)
-- ============================================================
CREATE TABLE type_place (
    id_type_place SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- ============================================================
-- TABLE CONFIGURATION_AVION (Lie un avion à ses types de places)
-- Permet de définir combien de places de chaque type un avion possède
-- ============================================================
CREATE TABLE configuration_avion (
    id_configuration SERIAL PRIMARY KEY,
    id_avion INT NOT NULL,
    id_type_place INT NOT NULL,
    nombre_places INT NOT NULL,
    FOREIGN KEY (id_avion) REFERENCES Avion(id_avion),
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place),
    UNIQUE (id_avion, id_type_place)
);

-- ============================================================
-- TABLE PRIX_VOL (Prix par type de place et par vol)
-- Permet de définir le prix de chaque type de place pour chaque vol
-- ============================================================
CREATE TABLE prix_vol (
    id_prix_vol SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_type_place INT NOT NULL,
    prix NUMERIC(12,2) NOT NULL,
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol),
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place),
    UNIQUE (id_vol, id_type_place)
);

-- ============================================================
-- INSERTION DES TYPES DE PLACES
-- ============================================================
INSERT INTO type_place (nom, description) VALUES
('Première classe', 'Places avec plus d''espace, repas premium, service prioritaire'),
('Économique', 'Places standard avec services de base');

-- ============================================================
-- CONFIGURATION DES AVIONS (Places par type)
-- ============================================================
-- ATR 72-600 (70 places): 10 première classe, 60 économique
INSERT INTO configuration_avion (id_avion, id_type_place, nombre_places) VALUES
(1, 1, 10),  -- Première classe
(1, 2, 60);  -- Économique

-- Boeing 737-800 (180 places): 20 première classe, 160 économique
INSERT INTO configuration_avion (id_avion, id_type_place, nombre_places) VALUES
(2, 1, 20),
(2, 2, 160);

-- Airbus A320 (150 places): 16 première classe, 134 économique
INSERT INTO configuration_avion (id_avion, id_type_place, nombre_places) VALUES
(3, 1, 16),
(3, 2, 134);

-- ATR 42-500 (48 places): 6 première classe, 42 économique
INSERT INTO configuration_avion (id_avion, id_type_place, nombre_places) VALUES
(4, 1, 6),
(4, 2, 42);

-- Embraer E190 (100 places): 12 première classe, 88 économique
INSERT INTO configuration_avion (id_avion, id_type_place, nombre_places) VALUES
(5, 1, 12),
(5, 2, 88);

-- ============================================================
-- PRIX PAR VOL (TNR <-> Nosy Be comme exemple principal)
-- ============================================================
-- Vol 1: TNR -> Nosy Be
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(1, 1, 1200000),  -- Première classe: 1 200 000 Ar
(1, 2, 700000);   -- Économique: 700 000 Ar

-- Vol 2: Nosy Be -> TNR
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(2, 1, 1200000),
(2, 2, 700000);

-- Vol 3: TNR -> Toamasina
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(3, 1, 300000),
(3, 2, 150000);

-- Vol 4: Toamasina -> TNR
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(4, 1, 300000),
(4, 2, 150000);

-- Vol 5: TNR -> Fort Dauphin
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(5, 1, 900000),
(5, 2, 450000);

-- Vol 6: Fort Dauphin -> TNR
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(6, 1, 900000),
(6, 2, 450000);

-- Vol 7: TNR -> Mahajanga
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(7, 1, 560000),
(7, 2, 280000);

-- Vol 8: Mahajanga -> TNR
INSERT INTO prix_vol (id_vol, id_type_place, prix) VALUES
(8, 1, 560000),
(8, 2, 280000);

-- ============================================================
-- VUE: Valeur maximale qu'un avion peut générer pour un vol
-- ============================================================
CREATE OR REPLACE VIEW v_valeur_max_avion_vol AS
SELECT 
    a.id_avion,
    a.modele,
    a.numero_immatriculation,
    v.id_vol,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    SUM(ca.nombre_places * pr.prix) AS valeur_maximale
FROM avion a
JOIN configuration_avion ca ON a.id_avion = ca.id_avion
JOIN type_place tp ON ca.id_type_place = tp.id_type_place
JOIN vol v ON 1=1
JOIN prix_vol pr ON pr.id_vol = v.id_vol AND pr.id_type_place = tp.id_type_place
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata;

-- ============================================================
-- VUE: Configuration détaillée des avions
-- ============================================================
CREATE OR REPLACE VIEW v_configuration_avion AS
SELECT 
    a.id_avion,
    a.modele,
    a.numero_immatriculation,
    a.capacite AS capacite_totale,
    tp.id_type_place,
    tp.nom AS type_place,
    ca.nombre_places
FROM avion a
JOIN configuration_avion ca ON a.id_avion = ca.id_avion
JOIN type_place tp ON ca.id_type_place = tp.id_type_place
ORDER BY a.id_avion, tp.id_type_place;
