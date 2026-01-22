-- ============================================================
-- Script d'ajout : Type de place Premium
-- Date: 2026-01-15
-- Description: Ajout d'un nouveau type de place "Premium" avec
--              un nouvel avion et configuration tarifaire
-- ============================================================

-- ============================================================
-- NOTE DE REFACTO:
-- Les tables et données de base sont maintenant centralisées dans
-- 01_table.sql (DDL) et 02_data.sql (DML). Ce script est conservé
-- pour historique et n'est plus nécessaire dans le flux principal.
-- ============================================================

\c pg10;

-- ============================================================
-- AJOUT DU TYPE DE PLACE PREMIUM
-- ============================================================
INSERT INTO type_place (nom, description) VALUES
('Premium', 'Sièges confortables avec services améliorés');

-- ============================================================
-- AJOUT D'UN NOUVEL AVION
-- ============================================================
INSERT INTO Avion (modele, capacite, numero_immatriculation) VALUES
('Airbus A321neo', 120, '5R-MJF');

-- ============================================================
-- CONFIGURATION DES PLACES DU NOUVEL AVION
-- Airbus A321neo (120 places total)
-- - 30 première classe
-- - 40 premium
-- - 50 économique
-- ============================================================
INSERT INTO avion_place (id_avion, id_type_place, nombre_places) VALUES
(6, 1, 30),  -- Airbus A321neo: 30 première classe
(6, 3, 40),  -- Airbus A321neo: 40 premium (id_type_place = 3)
(6, 2, 50);  -- Airbus A321neo: 50 économique

-- ============================================================
-- AJOUT DES TARIFS PREMIUM POUR LES VOLS TNR <-> NOSY BE
-- ============================================================
-- Vol TNR -> Nosy Be (id_vol = 1)
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(1, 3, 1000000);  -- Premium: 1 000 000 Ar

-- Vol Nosy Be -> TNR (id_vol = 2)
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(2, 3, 1000000);  -- Premium: 1 000 000 Ar

-- ============================================================
-- AJOUT DES TARIFS PREMIUM POUR LES AUTRES VOLS
-- ============================================================
INSERT INTO tarif_vol (id_vol, id_type_place, prix) VALUES
(3, 3, 200000),   -- TNR -> Toamasina: Premium
(4, 3, 200000),   -- Toamasina -> TNR: Premium
(5, 3, 650000),   -- TNR -> Fort Dauphin: Premium
(6, 3, 650000),   -- Fort Dauphin -> TNR: Premium
(7, 3, 400000),   -- TNR -> Mahajanga: Premium
(8, 3, 400000);   -- Mahajanga -> TNR: Premium

-- ============================================================
-- VÉRIFICATION
-- ============================================================
-- Afficher le nouvel avion
SELECT * FROM Avion WHERE numero_immatriculation = '5R-MJF';

-- Afficher la configuration des places
SELECT 
    a.modele,
    a.numero_immatriculation,
    tp.nom AS type_place,
    ap.nombre_places
FROM avion_place ap
JOIN Avion a ON ap.id_avion = a.id_avion
JOIN type_place tp ON ap.id_type_place = tp.id_type_place
WHERE a.numero_immatriculation = '5R-MJF'
ORDER BY tp.nom;

-- Afficher les tarifs premium pour TNR -> Nosy Be
SELECT 
    v.id_vol,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    tp.nom AS type_place,
    tv.prix
FROM tarif_vol tv
JOIN Vol v ON tv.id_vol = v.id_vol
JOIN type_place tp ON tv.id_type_place = tp.id_type_place
JOIN Aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN Aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
WHERE tp.nom = 'Premium' AND ad.code_iata = 'TNR' AND aa.code_iata = 'NOS';

-- Afficher la valeur maximale du nouvel avion sur TNR -> Nosy Be
SELECT * FROM v_valeur_max_avion_vol 
WHERE numero_immatriculation = '5R-MJF' 
AND depart = 'TNR' AND arrivee = 'NOS';
