-- ============================================================
-- Script de modification: Tarification Enfant et Calcul CA
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-16
-- ============================================================

\c pg10;

-- ============================================================
-- TABLE CATEGORIE_PASSAGER (Adulte, Enfant, Bébé)
-- Permet de différencier les tarifs selon l'âge du passager
-- ============================================================
CREATE TABLE IF NOT EXISTS categorie_passager (
    id_categorie_passager SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    age_min INT DEFAULT 0,
    age_max INT DEFAULT 999
);

-- ============================================================
-- TABLE TARIF_CATEGORIE (Prix par vol, type de place et catégorie)
-- Permet des tarifs différenciés avec prix fixe, pourcentage ou réduction
-- ============================================================
CREATE TABLE IF NOT EXISTS tarif_categorie (
    id_tarif_categorie SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_type_place INT NOT NULL,
    id_categorie_passager INT NOT NULL,
    prix NUMERIC(12,2),                     -- Prix fixe (NULL = utiliser pourcentage)
    pourcentage NUMERIC(5,2),               -- % du tarif adulte (ex: 10 pour bébé)
    frais_reduction NUMERIC(12,2),          -- Frais à soustraire (optionnel)
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    FOREIGN KEY (id_categorie_passager) REFERENCES categorie_passager(id_categorie_passager) ON DELETE CASCADE,
    UNIQUE (id_vol, id_type_place, id_categorie_passager)
);

-- ============================================================
-- TABLE DETAIL_RESERVATION (Détail de chaque passager réservé)
-- Stocke le prix effectivement payé pour chaque passager
-- ============================================================
CREATE TABLE IF NOT EXISTS detail_reservation (
    id_detail_reservation SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_type_place INT NOT NULL,
    id_categorie_passager INT NOT NULL,
    prix_paye NUMERIC(12,2) NOT NULL,
    FOREIGN KEY (id_reservation) REFERENCES Reservation(id_reservation) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place),
    FOREIGN KEY (id_categorie_passager) REFERENCES categorie_passager(id_categorie_passager)
);

-- ============================================================
-- INSERTION: CATÉGORIES DE PASSAGERS
-- ============================================================
INSERT INTO categorie_passager (nom, description, age_min, age_max) VALUES
('Adulte', 'Passager de 12 ans et plus', 12, 999),
('Enfant', 'Passager de 2 à 11 ans', 2, 11),
('Bébé', 'Passager de moins de 2 ans (sur genoux)', 0, 1)
ON CONFLICT (nom) DO NOTHING;

-- ============================================================
-- INSERTION: TARIFS PAR CATÉGORIE (SEULEMENT LES RÉDUCTIONS)
-- ⚠️ IMPORTANT: Ne mettre QUE les tarifs différents du tarif adulte standard
-- Les tarifs adulte sont déjà dans tarif_vol (02_data.sql)
-- ============================================================
-- 
-- ARCHITECTURE:
-- 1. tarif_vol       → Tarifs de BASE (adulte) pour tous les vols
-- 2. tarif_categorie → SEULEMENT les réductions (enfant, bébé)
--
-- Si pas d'entrée dans tarif_categorie → utilise automatiquement tarif_vol
-- ============================================================

-- Vol 1: TNR -> Nosy Be - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
-- Première classe
(1, 1, 2, 900000, NULL, NULL),   -- Enfant: 900 000 Ar (prix fixe, 25% réduction)
(1, 1, 3, NULL, 10, NULL),       -- Bébé: 10% du tarif adulte = 120 000 Ar
-- Économique
(1, 2, 2, 500000, NULL, NULL),   -- Enfant: 500 000 Ar (prix fixe, REMISE SPÉCIALE)
(1, 2, 3, NULL, 10, NULL),       -- Bébé: 10% du tarif adulte = 70 000 Ar
-- Premium
(1, 3, 2, 750000, NULL, NULL),   -- Enfant: 750 000 Ar (prix fixe)
(1, 3, 3, NULL, 10, NULL)        -- Bébé: 10% du tarif adulte = 100 000 Ar
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 2: Nosy Be -> TNR - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(2, 1, 2, 900000, NULL, NULL), (2, 1, 3, NULL, 10, NULL),
(2, 2, 2, 500000, NULL, NULL), (2, 2, 3, NULL, 10, NULL),
(2, 3, 2, 750000, NULL, NULL), (2, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 3: TNR -> Toamasina - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(3, 1, 2, 225000, NULL, NULL), (3, 1, 3, NULL, 10, NULL),
(3, 2, 2, 110000, NULL, NULL), (3, 2, 3, NULL, 10, NULL),
(3, 3, 2, 150000, NULL, NULL), (3, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 4: Toamasina -> TNR - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(4, 1, 2, 225000, NULL, NULL), (4, 1, 3, NULL, 10, NULL),
(4, 2, 2, 110000, NULL, NULL), (4, 2, 3, NULL, 10, NULL),
(4, 3, 2, 150000, NULL, NULL), (4, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 5: TNR -> Fort Dauphin - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(5, 1, 2, 675000, NULL, NULL), (5, 1, 3, NULL, 10, NULL),
(5, 2, 2, 340000, NULL, NULL), (5, 2, 3, NULL, 10, NULL),
(5, 3, 2, 490000, NULL, NULL), (5, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 6: Fort Dauphin -> TNR - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(6, 1, 2, 675000, NULL, NULL), (6, 1, 3, NULL, 10, NULL),
(6, 2, 2, 340000, NULL, NULL), (6, 2, 3, NULL, 10, NULL),
(6, 3, 2, 490000, NULL, NULL), (6, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 7: TNR -> Mahajanga - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(7, 1, 2, 420000, NULL, NULL), (7, 1, 3, NULL, 10, NULL),
(7, 2, 2, 210000, NULL, NULL), (7, 2, 3, NULL, 10, NULL),
(7, 3, 2, 300000, NULL, NULL), (7, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- Vol 8: Mahajanga -> TNR - SEULEMENT les enfants et bébés
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(8, 1, 2, 420000, NULL, NULL), (8, 1, 3, NULL, 10, NULL),
(8, 2, 2, 210000, NULL, NULL), (8, 2, 3, NULL, 10, NULL),
(8, 3, 2, 300000, NULL, NULL), (8, 3, 3, NULL, 10, NULL)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage, frais_reduction = EXCLUDED.frais_reduction;

-- ============================================================
-- JEU DE DONNÉES TEST: Réservations avec enfants pour TNR -> Nosy Be
-- Pour tester le calcul du CA
-- ============================================================

-- Client famille avec enfants
INSERT INTO Client (nom, prenom, email, telephone) VALUES
('Andria', 'Patrick', 'patrick.andria@email.com', '034 55 123 45'),
('Rabe', 'Voahangy', 'voahangy.rabe@email.com', '033 66 789 01')
ON CONFLICT (email) DO NOTHING;

-- Réservation 1: Famille Andria (2 adultes + 2 enfants) - Vol TNR -> Nosy Be du 12/01
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 1, 4, 'confirmée'
FROM Client c WHERE c.email = 'patrick.andria@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE r.id_client = c.id_client AND r.id_vol_programme = 1);

-- Détails de la réservation famille Andria
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 2, 1, 700000  -- Adulte 1 économique
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'patrick.andria@email.com' AND r.id_vol_programme = 1
LIMIT 1;

INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 2, 1, 700000  -- Adulte 2 économique
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'patrick.andria@email.com' AND r.id_vol_programme = 1
LIMIT 1;

INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 2, 2, 500000  -- Enfant 1 économique (tarif réduit)
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'patrick.andria@email.com' AND r.id_vol_programme = 1
LIMIT 1;

INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 2, 2, 500000  -- Enfant 2 économique (tarif réduit)
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'patrick.andria@email.com' AND r.id_vol_programme = 1
LIMIT 1;

-- Réservation 2: Famille Rabe (1 adulte + 1 enfant première classe) - Vol TNR -> Nosy Be du 12/01
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 2, 2, 'confirmée'
FROM Client c WHERE c.email = 'voahangy.rabe@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE r.id_client = c.id_client AND r.id_vol_programme = 2);

-- Détails réservation Rabe
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 1, 1, 1200000  -- Adulte première classe
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'voahangy.rabe@email.com' AND r.id_vol_programme = 2
LIMIT 1;

INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT r.id_reservation, 1, 2, 900000  -- Enfant première classe
FROM Reservation r 
JOIN Client c ON r.id_client = c.id_client 
WHERE c.email = 'voahangy.rabe@email.com' AND r.id_vol_programme = 2
LIMIT 1;

-- ============================================================
-- VUE: Chiffre d'Affaires par Vol Programmé
-- Calcule le CA généré par avion pour un vol
-- ============================================================
DROP VIEW IF EXISTS v_ca_vol_programme;
CREATE OR REPLACE VIEW v_ca_vol_programme AS
SELECT 
    vp.id_vol_programme,
    vp.id_vol,
    vp.id_avion,
    a.modele AS avion_modele,
    a.numero_immatriculation,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    vp.date_heure_depart,
    tp.id_type_place,
    tp.nom AS type_place,
    cp.id_categorie_passager,
    cp.nom AS categorie,
    COUNT(dr.id_detail_reservation) AS nb_reservations,
    COALESCE(SUM(dr.prix_paye), 0) AS ca_total
FROM vol_programme vp
JOIN avion a ON vp.id_avion = a.id_avion
JOIN vol v ON vp.id_vol = v.id_vol
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
LEFT JOIN type_place tp ON dr.id_type_place = tp.id_type_place
LEFT JOIN categorie_passager cp ON dr.id_categorie_passager = cp.id_categorie_passager
GROUP BY vp.id_vol_programme, vp.id_vol, vp.id_avion, a.modele, a.numero_immatriculation,
         ad.code_iata, aa.code_iata, vp.date_heure_depart, tp.id_type_place, tp.nom,
         cp.id_categorie_passager, cp.nom;

-- ============================================================
-- VUE SIMPLIFIÉE: CA Total par Vol Programmé
-- ============================================================
DROP VIEW IF EXISTS v_ca_total_vol_programme;
CREATE OR REPLACE VIEW v_ca_total_vol_programme AS
SELECT 
    vp.id_vol_programme,
    a.modele AS avion_modele,
    a.numero_immatriculation,
    ad.code_iata || ' → ' || aa.code_iata AS route,
    vp.date_heure_depart,
    COUNT(dr.id_detail_reservation) AS nb_passagers,
    COALESCE(SUM(dr.prix_paye), 0) AS ca_total
FROM vol_programme vp
JOIN avion a ON vp.id_avion = a.id_avion
JOIN vol v ON vp.id_vol = v.id_vol
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
GROUP BY vp.id_vol_programme, a.modele, a.numero_immatriculation, 
         ad.code_iata, aa.code_iata, vp.date_heure_depart;

-- ============================================================
-- FONCTION: Obtenir le prix applicable pour un passager
-- Logique: prix fixe > pourcentage du tarif adulte > tarif_vol
-- ============================================================
CREATE OR REPLACE FUNCTION get_prix_passager(
    p_id_vol INT,
    p_id_type_place INT,
    p_id_categorie_passager INT
) RETURNS NUMERIC(12,2) AS $$
DECLARE
    v_prix NUMERIC(12,2);
    v_pourcentage NUMERIC(5,2);
    v_frais_reduction NUMERIC(12,2);
    v_tarif_adulte NUMERIC(12,2);
    v_prix_calcule NUMERIC(12,2);
BEGIN
    -- Chercher le tarif spécifique catégorie
    SELECT prix, pourcentage, frais_reduction 
    INTO v_prix, v_pourcentage, v_frais_reduction
    FROM tarif_categorie
    WHERE id_vol = p_id_vol 
      AND id_type_place = p_id_type_place 
      AND id_categorie_passager = p_id_categorie_passager;
    
    -- 1. Si prix fixe défini, l'utiliser
    IF v_prix IS NOT NULL THEN
        RETURN v_prix;
    END IF;
    
    -- 2. Si pourcentage défini, calculer à partir du tarif adulte
    IF v_pourcentage IS NOT NULL THEN
        -- Récupérer le tarif adulte (catégorie 1)
        SELECT prix INTO v_tarif_adulte
        FROM tarif_categorie
        WHERE id_vol = p_id_vol 
          AND id_type_place = p_id_type_place 
          AND id_categorie_passager = 1;  -- Adulte
        
        -- Fallback sur tarif_vol si pas de tarif adulte en tarif_categorie
        IF v_tarif_adulte IS NULL THEN
            SELECT prix INTO v_tarif_adulte
            FROM tarif_vol
            WHERE id_vol = p_id_vol AND id_type_place = p_id_type_place;
        END IF;
        
        -- Calculer le prix avec pourcentage
        v_prix_calcule := COALESCE(v_tarif_adulte, 0) * v_pourcentage / 100;
        
        -- Soustraire frais de réduction si définis
        IF v_frais_reduction IS NOT NULL THEN
            v_prix_calcule := v_prix_calcule - v_frais_reduction;
        END IF;
        
        -- Prix minimum = 0
        RETURN GREATEST(v_prix_calcule, 0);
    END IF;
    
    -- 3. Fallback: utiliser le tarif standard
    SELECT prix INTO v_prix
    FROM tarif_vol
    WHERE id_vol = p_id_vol AND id_type_place = p_id_type_place;
    
    RETURN COALESCE(v_prix, 0);
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TEST: Vérification des prix
-- ============================================================
-- SELECT get_prix_passager(1, 2, 1);  -- Adulte économique TNR-NOS = 700 000
-- SELECT get_prix_passager(1, 2, 2);  -- Enfant économique TNR-NOS = 500 000
-- SELECT get_prix_passager(1, 2, 3);  -- Bébé économique TNR-NOS = 70 000 (10% de 700 000)
