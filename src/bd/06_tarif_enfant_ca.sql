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
-- Permet des tarifs différenciés: ex. Enfant économique = 500 000 Ar
-- ============================================================
CREATE TABLE IF NOT EXISTS tarif_categorie (
    id_tarif_categorie SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_type_place INT NOT NULL,
    id_categorie_passager INT NOT NULL,
    prix NUMERIC(12,2) NOT NULL,
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
-- INSERTION: TARIFS PAR CATÉGORIE POUR VOL TNR -> NOSY BE (Vol 1)
-- Économique Enfant: 500 000 Ar au lieu de 700 000 Ar
-- ============================================================

-- Vol 1: TNR -> Nosy Be
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
-- Première classe
(1, 1, 1, 1200000),  -- Adulte: 1 200 000 Ar
(1, 1, 2, 900000),   -- Enfant: 900 000 Ar (25% de réduction)
(1, 1, 3, 0),        -- Bébé: Gratuit
-- Économique
(1, 2, 1, 700000),   -- Adulte: 700 000 Ar
(1, 2, 2, 500000),   -- Enfant: 500 000 Ar (REMISE SPÉCIALE)
(1, 2, 3, 0),        -- Bébé: Gratuit
-- Premium
(1, 3, 1, 1000000),  -- Adulte: 1 000 000 Ar
(1, 3, 2, 750000),   -- Enfant: 750 000 Ar
(1, 3, 3, 0)         -- Bébé: Gratuit
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 2: Nosy Be -> TNR (tarifs identiques pour le retour)
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(2, 1, 1, 1200000), (2, 1, 2, 900000), (2, 1, 3, 0),
(2, 2, 1, 700000), (2, 2, 2, 500000), (2, 2, 3, 0),
(2, 3, 1, 1000000), (2, 3, 2, 750000), (2, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 3: TNR -> Toamasina
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(3, 1, 1, 300000), (3, 1, 2, 225000), (3, 1, 3, 0),
(3, 2, 1, 150000), (3, 2, 2, 110000), (3, 2, 3, 0),
(3, 3, 1, 200000), (3, 3, 2, 150000), (3, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 4: Toamasina -> TNR
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(4, 1, 1, 300000), (4, 1, 2, 225000), (4, 1, 3, 0),
(4, 2, 1, 150000), (4, 2, 2, 110000), (4, 2, 3, 0),
(4, 3, 1, 200000), (4, 3, 2, 150000), (4, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 5: TNR -> Fort Dauphin
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(5, 1, 1, 900000), (5, 1, 2, 675000), (5, 1, 3, 0),
(5, 2, 1, 450000), (5, 2, 2, 340000), (5, 2, 3, 0),
(5, 3, 1, 650000), (5, 3, 2, 490000), (5, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 6: Fort Dauphin -> TNR
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(6, 1, 1, 900000), (6, 1, 2, 675000), (6, 1, 3, 0),
(6, 2, 1, 450000), (6, 2, 2, 340000), (6, 2, 3, 0),
(6, 3, 1, 650000), (6, 3, 2, 490000), (6, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 7: TNR -> Mahajanga
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(7, 1, 1, 560000), (7, 1, 2, 420000), (7, 1, 3, 0),
(7, 2, 1, 280000), (7, 2, 2, 210000), (7, 2, 3, 0),
(7, 3, 1, 400000), (7, 3, 2, 300000), (7, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

-- Vol 8: Mahajanga -> TNR
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix) VALUES
(8, 1, 1, 560000), (8, 1, 2, 420000), (8, 1, 3, 0),
(8, 2, 1, 280000), (8, 2, 2, 210000), (8, 2, 3, 0),
(8, 3, 1, 400000), (8, 3, 2, 300000), (8, 3, 3, 0)
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) DO UPDATE SET prix = EXCLUDED.prix;

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
-- ============================================================
CREATE OR REPLACE FUNCTION get_prix_passager(
    p_id_vol INT,
    p_id_type_place INT,
    p_id_categorie_passager INT
) RETURNS NUMERIC(12,2) AS $$
DECLARE
    v_prix NUMERIC(12,2);
BEGIN
    -- Chercher le tarif spécifique catégorie
    SELECT prix INTO v_prix
    FROM tarif_categorie
    WHERE id_vol = p_id_vol 
      AND id_type_place = p_id_type_place 
      AND id_categorie_passager = p_id_categorie_passager;
    
    -- Si pas de tarif catégorie, utiliser le tarif standard
    IF v_prix IS NULL THEN
        SELECT prix INTO v_prix
        FROM tarif_vol
        WHERE id_vol = p_id_vol AND id_type_place = p_id_type_place;
    END IF;
    
    RETURN COALESCE(v_prix, 0);
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TEST: Vérification du CA pour vol TNR -> Nosy Be
-- ============================================================
-- SELECT * FROM v_ca_vol_programme WHERE id_vol_programme = 1;
-- SELECT * FROM v_ca_total_vol_programme WHERE id_vol_programme = 1;
-- Résultat attendu pour vol 1: 
-- - 2 adultes × 700 000 = 1 400 000
-- - 2 enfants × 500 000 = 1 000 000
-- - TOTAL = 2 400 000 Ar
