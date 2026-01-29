-- ============================================================
-- Script d'insertion des données
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-29
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
-- INSERTION: CATÉGORIES DE PASSAGERS
-- ============================================================
INSERT INTO categorie_passager (nom, description, age_min, age_max) VALUES
('Adulte', 'Passager de 12 ans et plus', 12, 999),
('Enfant', 'Passager de 2 à 11 ans', 2, 11),
('Bébé', 'Passager de moins de 2 ans (sur genoux)', 0, 1)
ON CONFLICT (nom) DO NOTHING;

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

-- ============================================================
-- INSERTION: TARIFS PAR CATÉGORIE (SEULEMENT LES RÉDUCTIONS)
-- ⚠️ IMPORTANT: Ne mettre QUE les tarifs différents du tarif adulte standard
-- Les tarifs adulte sont déjà dans tarif_vol
-- ============================================================
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
-- INSERTION: SOCIÉTÉS DIFFUSEURS
-- ============================================================
INSERT INTO societe_diffuseur (nom, email, telephone, adresse) VALUES
('Vaniala', 'contact@vaniala.mg', '+261 34 00 000 01', 'Antananarivo, Madagascar'),
('Lewis', 'info@lewis.mg', '+261 34 00 000 02', 'Antananarivo, Madagascar'),
('Socobis', 'contact@socobis.mg', '+261 34 00 000 03', 'Antananarivo, Madagascar'),
('Jejoo', 'info@jejoo.mg', '+261 34 00 000 04', 'Antananarivo, Madagascar');

-- ============================================================
-- INSERTION: TARIF DIFFUSION PAR DÉFAUT
-- ============================================================
-- Tarif par défaut: 400 000 Ar / diffusion
INSERT INTO tarif_diffusion (id_societe_diffuseur, id_vol, id_type_place, prix_unitaire, actif) VALUES
(NULL, NULL, NULL, 400000, TRUE);
