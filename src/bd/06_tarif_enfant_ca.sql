-- ============================================================
-- Script de modification: Tarification Enfant et Calcul CA
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-16
-- ============================================================

\c pg10;

-- ============================================================
-- NOTE DE REFACTO:
-- Les tables, vues, fonctions et données de base sont désormais
-- centralisées dans 01_table.sql (DDL) et 02_data.sql (DML).
-- Ce script garde uniquement les données de test spécifiques.
-- ============================================================

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
