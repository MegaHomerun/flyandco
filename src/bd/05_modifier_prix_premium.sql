-- ============================================================
-- Script de modification : Prix du vol Premium
-- Date: 2026-01-15
-- Description: Modification du prix des places Premium pour
--              différentes routes
-- ============================================================

\c pg10;

-- ============================================================
-- OPTION 1: Modifier le prix Premium pour TNR -> Nosy Be
-- ============================================================
UPDATE tarif_vol 
SET prix = 1100000  -- Nouveau prix: 1 100 000 Ar
WHERE id_vol = 1 AND id_type_place = 3;

-- ============================================================
-- OPTION 2: Modifier le prix Premium pour Nosy Be -> TNR
-- ============================================================
UPDATE tarif_vol 
SET prix = 1100000  -- Nouveau prix: 1 100 000 Ar
WHERE id_vol = 2 AND id_type_place = 3;

-- ============================================================
-- OPTION 3: Modifier le prix Premium pour tous les vols TNR <-> Nosy Be
-- ============================================================
UPDATE tarif_vol 
SET prix = 1100000  -- Nouveau prix: 1 100 000 Ar
WHERE id_vol IN (1, 2) AND id_type_place = 3;

-- ============================================================
-- OPTION 4: Modifier le prix Premium pour une route spécifique
-- Exemple: TNR -> Mahajanga
-- ============================================================
UPDATE tarif_vol tv
SET prix = 450000  -- Nouveau prix: 450 000 Ar
FROM Vol v
JOIN Aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN Aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
WHERE tv.id_vol = v.id_vol
AND tv.id_type_place = 3
AND ad.code_iata = 'TNR'
AND aa.code_iata = 'MJN';

-- ============================================================
-- OPTION 5: Modifier tous les prix Premium (augmentation de 10%)
-- ============================================================
UPDATE tarif_vol 
SET prix = prix * 1.10  -- Augmentation de 10%
WHERE id_type_place = 3;

-- ============================================================
-- VÉRIFICATION APRÈS MODIFICATION
-- ============================================================
-- Afficher tous les tarifs Premium
SELECT 
    v.id_vol,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    tp.nom AS type_place,
    tv.prix AS prix_actuel
FROM tarif_vol tv
JOIN Vol v ON tv.id_vol = v.id_vol
JOIN type_place tp ON tv.id_type_place = tp.id_type_place
JOIN Aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN Aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
WHERE tp.nom = 'Premium'
ORDER BY ad.code_iata, aa.code_iata;

-- Afficher la nouvelle valeur maximale pour l'Airbus A321neo sur TNR -> Nosy Be
SELECT * FROM v_valeur_max_avion_vol 
WHERE numero_immatriculation = '5R-MJF' 
AND depart = 'TNR' AND arrivee = 'NOS';
