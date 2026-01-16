-- ============================================================
-- Script: Mise à jour des tarifs TNR - Nosy Be
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-16
-- ============================================================
-- 
-- Objectif: Mettre à jour les tarifs et créer des réservations pour TNR - Nosy Be
-- avec les données spécifiques par classe et catégorie de passager
-- ============================================================

\c pg10;

-- ============================================================
-- ÉTAPE 1: MISE À JOUR DES TARIFS VOL (TARIFS ADULTE)
-- Vol 1: TNR → Nosy Be
-- ============================================================

-- Mise à jour des tarifs adulte pour TNR-Nosy Be
UPDATE tarif_vol SET prix = 2000000 WHERE id_vol = 1 AND id_type_place = 1;  -- 1ère classe
UPDATE tarif_vol SET prix = 1000000 WHERE id_vol = 1 AND id_type_place = 3;  -- Premium
UPDATE tarif_vol SET prix = 800000 WHERE id_vol = 1 AND id_type_place = 2;   -- Économique

-- ============================================================
-- ÉTAPE 2: INSERTION DES TARIFS CATÉGORIES (ENFANTS ET BÉBÉS)
-- Vol 1: TNR → Nosy Be
-- ============================================================

-- 1ère classe
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(1, 1, 2, 800000, NULL, NULL),    -- Enfant 1ère classe: 800 000 Ar (prix fixe)
(1, 1, 3, NULL, 10, NULL)         -- Bébé 1ère classe: 10% du tarif adulte = 200 000 Ar
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage;

-- Premium
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(1, 3, 2, 700000, NULL, NULL),    -- Enfant Premium: 700 000 Ar (prix fixe)
(1, 3, 3, NULL, 10, NULL)         -- Bébé Premium: 10% du tarif adulte = 100 000 Ar
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage;

-- Économique
INSERT INTO tarif_categorie (id_vol, id_type_place, id_categorie_passager, prix, pourcentage, frais_reduction) VALUES
(1, 2, 2, 600000, NULL, NULL),    -- Enfant Économique: 600 000 Ar (prix fixe)
(1, 2, 3, NULL, 10, NULL)         -- Bébé Économique: 10% du tarif adulte = 80 000 Ar
ON CONFLICT (id_vol, id_type_place, id_categorie_passager) 
DO UPDATE SET prix = EXCLUDED.prix, pourcentage = EXCLUDED.pourcentage;

-- ============================================================
-- ÉTAPE 3: CRÉATION DES CLIENTS POUR LES RÉSERVATIONS
-- ============================================================

-- Clients pour les réservations de test
INSERT INTO Client (nom, prenom, email, telephone) VALUES
('Rakoto', 'Olivier', 'olivier.rakoto@email.com', '034 11 111 11'),
('Randrianampoinimerina', 'Sophie', 'sophie.randria@email.com', '033 22 222 22'),
('Zafimanela', 'Marc', 'marc.zafimanela@email.com', '034 33 333 33')
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- ÉTAPE 4: CRÉATION DES RÉSERVATIONS POUR LE VOL 1
-- (TNR → Nosy Be du 12/01/2026)
-- 
-- Réservation 1ère classe : 2 bébés + 4 enfants + 10 adultes = 16 passagers
-- Réservation Premium :     4 bébés + 5 enfants + 20 adultes = 29 passagers
-- Réservation Économique :  4 bébés + 10 enfants + 30 adultes = 44 passagers
-- TOTAL: 89 passagers
-- ============================================================

-- Réservation 1: Groupe 1ère classe (16 passagers)
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 1, 16, 'confirmée'
FROM Client c WHERE c.email = 'olivier.rakoto@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE r.id_client = c.id_client AND r.id_vol_programme = 1);

-- Détails réservation 1ère classe: 2 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = 1),
    1, 3, 200000;

-- Détails réservation 1ère classe: 2ème bébé
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = 1),
    1, 3, 200000;

-- Détails réservation 1ère classe: 4 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = 1),
    1, 2, 800000
FROM generate_series(1, 4);

-- Détails réservation 1ère classe: 10 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = 1),
    1, 1, 2000000
FROM generate_series(1, 10);

-- Réservation 2: Groupe Premium (29 passagers)
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 1, 29, 'confirmée'
FROM Client c WHERE c.email = 'sophie.randria@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE r.id_client = c.id_client AND r.id_vol_programme = 1);

-- Détails réservation Premium: 4 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = 1),
    3, 3, 100000
FROM generate_series(1, 4);

-- Détails réservation Premium: 5 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = 1),
    3, 2, 700000
FROM generate_series(1, 5);

-- Détails réservation Premium: 20 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = 1),
    3, 1, 1000000
FROM generate_series(1, 20);

-- Réservation 3: Groupe Économique (44 passagers)
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 1, 44, 'confirmée'
FROM Client c WHERE c.email = 'marc.zafimanela@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r WHERE r.id_client = c.id_client AND r.id_vol_programme = 1);

-- Détails réservation Économique: 4 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = 1),
    2, 3, 80000
FROM generate_series(1, 4);

-- Détails réservation Économique: 10 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = 1),
    2, 2, 600000
FROM generate_series(1, 10);

-- Détails réservation Économique: 30 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = 1),
    2, 1, 800000
FROM generate_series(1, 30);

-- ============================================================
-- VÉRIFICATION: Afficher le CA généré
-- ============================================================
SELECT 'CA généré par le vol TNR-Nosy Be du 12/01 à 12:00:' as titre;
SELECT 
    tp.nom as classe,
    cp.nom as categorie,
    COUNT(*) as nb_passagers,
    SUM(dr.prix_paye) as total_ca
FROM detail_reservation dr
JOIN reservation r ON dr.id_reservation = r.id_reservation
JOIN type_place tp ON dr.id_type_place = tp.id_type_place
JOIN categorie_passager cp ON dr.id_categorie_passager = cp.id_categorie_passager
WHERE r.id_vol_programme = 1
GROUP BY tp.nom, cp.nom
ORDER BY tp.nom, cp.nom;

SELECT 'CA TOTAL VOL 1 (12:00):' as titre;
SELECT 
    COUNT(*) as nb_total_passagers,
    SUM(dr.prix_paye) as ca_total
FROM detail_reservation dr
JOIN reservation r ON dr.id_reservation = r.id_reservation
WHERE r.id_vol_programme = 1;

-- ============================================================
-- ÉTAPE 5: CRÉATION D'UN NOUVEAU VOL À 14:00
-- (TNR → Nosy Be du 12/01/2026 à 14:00)
-- ============================================================

INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut)
VALUES (1, 3, '2026-01-12 14:00:00', '2026-01-12 15:30:00', 360000, 'prévu');

-- ============================================================
-- ÉTAPE 6: CRÉATION DES RÉSERVATIONS POUR LE VOL À 14:00
-- (TNR → Nosy Be du 12/01/2026 à 14:00)
-- Vol_programme id = le dernier inséré (nouvellement créé)
-- 
-- Réservation 4: Groupe 1ère classe (16 passagers)
-- Réservation 5: Groupe Premium (29 passagers)
-- Réservation 6: Groupe Économique (44 passagers)
-- TOTAL: 89 passagers
-- ============================================================

-- Réservation 4: Groupe 1ère classe (16 passagers) - VOL 14:00
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 
       (SELECT MAX(id_vol_programme) FROM vol_programme), 
       16, 'confirmée'
FROM Client c WHERE c.email = 'olivier.rakoto@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r 
                WHERE r.id_client = c.id_client 
                AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme));

-- Détails réservation 1ère classe: 2 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    1, 3, 200000;

-- Détails réservation 1ère classe: 2ème bébé
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    1, 3, 200000;

-- Détails réservation 1ère classe: 4 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    1, 2, 800000
FROM generate_series(1, 4);

-- Détails réservation 1ère classe: 10 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'olivier.rakoto@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    1, 1, 2000000
FROM generate_series(1, 10);

-- Réservation 5: Groupe Premium (29 passagers) - VOL 14:00
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 
       (SELECT MAX(id_vol_programme) FROM vol_programme), 
       29, 'confirmée'
FROM Client c WHERE c.email = 'sophie.randria@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r 
                WHERE r.id_client = c.id_client 
                AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme));

-- Détails réservation Premium: 4 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    3, 3, 100000
FROM generate_series(1, 4);

-- Détails réservation Premium: 5 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    3, 2, 700000
FROM generate_series(1, 5);

-- Détails réservation Premium: 20 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'sophie.randria@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    3, 1, 1000000
FROM generate_series(1, 20);

-- Réservation 6: Groupe Économique (44 passagers) - VOL 14:00
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 
       (SELECT MAX(id_vol_programme) FROM vol_programme), 
       44, 'confirmée'
FROM Client c WHERE c.email = 'marc.zafimanela@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r 
                WHERE r.id_client = c.id_client 
                AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme));

-- Détails réservation Économique: 4 bébés
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 3, 80000
FROM generate_series(1, 4);

-- Détails réservation Économique: 10 enfants
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 2, 600000
FROM generate_series(1, 10);

-- Détails réservation Économique: 30 adultes
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 1, 800000
FROM generate_series(1, 30);
