-- ============================================================
-- Script: Vol 21h avec tarifs modifiés TNR - Nosy Be
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-16
-- ============================================================
-- 
-- Objectif: Créer un vol à 21h avec tarif adulte économique augmenté à 900 000 Ar
-- et insérer les réservations avec les nouveaux prix
-- ============================================================

\c pg10;

-- ============================================================
-- ÉTAPE 1: MISE À JOUR DU TARIF ADULTE ÉCONOMIQUE
-- Passage de 800 000 Ar à 900 000 Ar
-- ============================================================

UPDATE tarif_vol SET prix = 900000 WHERE id_vol = 1 AND id_type_place = 2;

-- ============================================================
-- ÉTAPE 2: MISE À JOUR DU TARIF BÉBÉ ÉCONOMIQUE
-- Bébé = 10% du tarif adulte → 10% × 900 000 = 90 000 Ar
-- ============================================================

UPDATE tarif_categorie SET pourcentage = 10 WHERE id_vol = 1 AND id_type_place = 2 AND id_categorie_passager = 3;

-- ============================================================
-- ÉTAPE 3: CRÉATION DU NOUVEAU VOL À 21:00
-- (TNR → Nosy Be du 12/01/2026 à 21:00)
-- ============================================================

INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut)
VALUES (1, 1, '2026-01-12 21:00:00', '2026-01-12 22:30:00', 360000, 'prévu');

-- ============================================================
-- ÉTAPE 4: CRÉATION DES RÉSERVATIONS POUR LE VOL À 21:00
-- (TNR → Nosy Be du 12/01/2026 à 21:00)
-- Avec les NOUVEAUX tarifs:
-- - Adulte économique: 900 000 Ar (au lieu de 800 000)
-- - Enfant économique: 600 000 Ar (inchangé)
-- - Bébé économique: 90 000 Ar (10% × 900 000)
-- ============================================================

-- Réservation 1: Groupe 1ère classe (16 passagers) - VOL 21:00
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

-- Réservation 2: Groupe Premium (29 passagers) - VOL 21:00
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

-- Réservation 3: Groupe Économique (44 passagers) - VOL 21:00 - AVEC NOUVEAUX PRIX
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut)
SELECT c.id_client, 
       (SELECT MAX(id_vol_programme) FROM vol_programme), 
       44, 'confirmée'
FROM Client c WHERE c.email = 'marc.zafimanela@email.com'
AND NOT EXISTS (SELECT 1 FROM Reservation r 
                WHERE r.id_client = c.id_client 
                AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme));

-- Détails réservation Économique: 4 bébés - NOUVEAU PRIX: 90 000 Ar
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 3, 90000
FROM generate_series(1, 4);

-- Détails réservation Économique: 10 enfants - PRIX INCHANGÉ: 600 000 Ar
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 2, 600000
FROM generate_series(1, 10);

-- Détails réservation Économique: 30 adultes - NOUVEAU PRIX: 900 000 Ar
INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
SELECT 
    (SELECT MAX(r.id_reservation) FROM Reservation r 
     WHERE r.id_client = (SELECT id_client FROM Client WHERE email = 'marc.zafimanela@email.com')
       AND r.id_vol_programme = (SELECT MAX(id_vol_programme) FROM vol_programme)),
    2, 1, 900000
FROM generate_series(1, 30);

-- ============================================================
-- VÉRIFICATION: Afficher les CA par vol
-- ============================================================

SELECT 'CA généré par tous les vols TNR-Nosy Be du 12/01:' as titre;
SELECT 
    vp.date_heure_depart,
    a.modele as avion,
    COUNT(dr.id_detail_reservation) as nb_passagers,
    SUM(dr.prix_paye) as ca_total
FROM vol_programme vp
JOIN avion a ON vp.id_avion = a.id_avion
LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
WHERE vp.id_vol = 1 AND DATE(vp.date_heure_depart) = '2026-01-12'
GROUP BY vp.id_vol_programme, vp.date_heure_depart, a.modele
ORDER BY vp.date_heure_depart;

SELECT 'TOTAL CA 12/01:' as titre;
SELECT 
    COUNT(dr.id_detail_reservation) as nb_total_passagers,
    SUM(dr.prix_paye) as ca_total_jour
FROM vol_programme vp
LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
WHERE vp.id_vol = 1 AND DATE(vp.date_heure_depart) = '2026-01-12';

-- ============================================================
-- COMPARAISON: Prix économique avant/après
-- ============================================================

SELECT 'CHANGEMENTS DE TARIFS ÉCONOMIQUE:' as titre;
SELECT 
    'Adulte' as categorie,
    '800 000' as ancien_prix,
    '900 000' as nouveau_prix,
    '+100 000 Ar' as difference;

SELECT 
    'Enfant' as categorie,
    '600 000' as ancien_prix,
    '600 000' as nouveau_prix,
    'Inchangé' as difference;

SELECT 
    'Bébé' as categorie,
    '80 000' as ancien_prix,
    '90 000' as nouveau_prix,
    '+10 000 Ar (10% du tarif adulte)' as difference;
