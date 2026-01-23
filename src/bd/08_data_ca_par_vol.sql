-- ============================================================
-- Script de données de test pour le CA par Vol
-- FlyAndCo - Jeu de données pour les vols TNR -> Nosy Be
-- Date: 2026-01-23
-- ============================================================
-- 
-- Jeu de données selon l'exercice:
-- - Créer une destination TNR - Nosy Be
-- - Créer un avion ATR - 045
-- - Créer les vols à destination de TNR - Nosy Be, effectué par l'avion TR - 045:
--   * 20 janvier 2026 - 10h
--   * 21 janvier 2026 - 10h
--   * 21 janvier 2026 - 15h
--
-- | Date/Heure              | Billets                      | Diffusions Pub        |
-- |-------------------------|------------------------------|----------------------|
-- | 20 janvier 2026 - 10h   | Billet adulte économique x40 | Pub Vaniala 1, Lewis 1|
-- | 21 janvier 2026 - 10h   | Billet adulte économique x30 | Pub Socobis 2, Jejoo 1|
-- | 21 janvier 2026 - 15h   | Billet adulte économique x50 | 0 pub                |
--
-- Rappel: Billet adulte économique = 800 000 Ar
-- ============================================================

\c pg10;

-- ============================================================
-- 1. CRÉATION DE L'AVION ATR-045
-- ============================================================
INSERT INTO avion (modele, capacite, numero_immatriculation)
VALUES ('ATR-045', 70, 'TR-045')
ON CONFLICT (numero_immatriculation) DO UPDATE SET modele = EXCLUDED.modele;

-- Configuration des places pour ATR-045 (70 places: 10 première classe, 60 économique)
INSERT INTO avion_place (id_avion, id_type_place, nombre_places)
SELECT a.id_avion, 1, 10 FROM avion a WHERE a.numero_immatriculation = 'TR-045'
ON CONFLICT (id_avion, id_type_place) DO UPDATE SET nombre_places = EXCLUDED.nombre_places;

INSERT INTO avion_place (id_avion, id_type_place, nombre_places)
SELECT a.id_avion, 2, 60 FROM avion a WHERE a.numero_immatriculation = 'TR-045'
ON CONFLICT (id_avion, id_type_place) DO UPDATE SET nombre_places = EXCLUDED.nombre_places;

-- ============================================================
-- 2. MISE À JOUR DU TARIF ÉCONOMIQUE TNR -> NOSY BE À 800 000 AR
-- ============================================================
-- Vol 1 = TNR -> Nosy Be (d'après 02_data.sql)
UPDATE tarif_vol SET prix = 800000 WHERE id_vol = 1 AND id_type_place = 2;

-- ============================================================
-- 3. CRÉATION DES VOLS PROGRAMMÉS
-- ============================================================
-- Récupérer l'ID de l'avion ATR-045
DO $$
DECLARE
    v_id_avion INT;
    v_id_vol INT := 1;  -- Vol TNR -> Nosy Be
    v_id_vp_1 INT;
    v_id_vp_2 INT;
    v_id_vp_3 INT;
BEGIN
    SELECT id_avion INTO v_id_avion FROM avion WHERE numero_immatriculation = 'TR-045';

    -- Vol 1: 20 janvier 2026 - 10h
    INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut)
    VALUES (v_id_vol, v_id_avion, '2026-01-20 10:00:00', '2026-01-20 11:30:00', 800000, 'prévu')
    RETURNING id_vol_programme INTO v_id_vp_1;
    
    -- Vol 2: 21 janvier 2026 - 10h
    INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut)
    VALUES (v_id_vol, v_id_avion, '2026-01-21 10:00:00', '2026-01-21 11:30:00', 800000, 'prévu')
    RETURNING id_vol_programme INTO v_id_vp_2;
    
    -- Vol 3: 21 janvier 2026 - 15h
    INSERT INTO vol_programme (id_vol, id_avion, date_heure_depart, date_heure_arrivee, prix, statut)
    VALUES (v_id_vol, v_id_avion, '2026-01-21 15:00:00', '2026-01-21 16:30:00', 800000, 'prévu')
    RETURNING id_vol_programme INTO v_id_vp_3;

    RAISE NOTICE 'Vols créés: % (20/01 10h), % (21/01 10h), % (21/01 15h)', v_id_vp_1, v_id_vp_2, v_id_vp_3;
END $$;

-- ============================================================
-- 4. CRÉATION DES SOCIÉTÉS PUBLICITAIRES
-- ============================================================
INSERT INTO societe_diffuseur (nom, email, telephone, adresse) VALUES
('Socobis', 'contact@socobis.mg', '+261 34 00 000 03', 'Antananarivo, Madagascar'),
('Jejoo', 'info@jejoo.mg', '+261 34 00 000 04', 'Antananarivo, Madagascar')
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 5. CRÉATION DES RÉSERVATIONS (BILLETS)
-- ============================================================
-- Client pour les tests
INSERT INTO client (nom, prenom, email, telephone)
VALUES ('Test', 'CA Vol', 'testcavol@email.com', '034 00 000 00')
ON CONFLICT (email) DO NOTHING;

DO $$
DECLARE
    v_id_client INT;
    v_id_vp_1 INT;
    v_id_vp_2 INT;
    v_id_vp_3 INT;
    v_id_reservation INT;
    i INT;
BEGIN
    -- Récupérer le client
    SELECT id_client INTO v_id_client FROM client WHERE email = 'testcavol@email.com';
    
    -- Récupérer les vols programmés (triés par date)
    SELECT id_vol_programme INTO v_id_vp_1 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND vp.date_heure_depart = '2026-01-20 10:00:00';
    
    SELECT id_vol_programme INTO v_id_vp_2 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND vp.date_heure_depart = '2026-01-21 10:00:00';
    
    SELECT id_vol_programme INTO v_id_vp_3 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND vp.date_heure_depart = '2026-01-21 15:00:00';

    -- ==========================================
    -- Vol 1 (20 janvier 10h): 40 billets adulte économique = 40 × 800 000 = 32 000 000 Ar
    -- ==========================================
    INSERT INTO reservation (id_client, id_vol_programme, nombre_places, statut)
    VALUES (v_id_client, v_id_vp_1, 40, 'confirmée')
    RETURNING id_reservation INTO v_id_reservation;
    
    -- 40 détails de réservation (adulte économique 800 000 Ar)
    FOR i IN 1..40 LOOP
        INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
        VALUES (v_id_reservation, 2, 1, 800000);  -- type 2=économique, catégorie 1=adulte
    END LOOP;
    
    -- ==========================================
    -- Vol 2 (21 janvier 10h): 30 billets adulte économique = 30 × 800 000 = 24 000 000 Ar
    -- ==========================================
    INSERT INTO reservation (id_client, id_vol_programme, nombre_places, statut)
    VALUES (v_id_client, v_id_vp_2, 30, 'confirmée')
    RETURNING id_reservation INTO v_id_reservation;
    
    FOR i IN 1..30 LOOP
        INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
        VALUES (v_id_reservation, 2, 1, 800000);
    END LOOP;
    
    -- ==========================================
    -- Vol 3 (21 janvier 15h): 50 billets adulte économique = 50 × 800 000 = 40 000 000 Ar
    -- ==========================================
    INSERT INTO reservation (id_client, id_vol_programme, nombre_places, statut)
    VALUES (v_id_client, v_id_vp_3, 50, 'confirmée')
    RETURNING id_reservation INTO v_id_reservation;
    
    FOR i IN 1..50 LOOP
        INSERT INTO detail_reservation (id_reservation, id_type_place, id_categorie_passager, prix_paye)
        VALUES (v_id_reservation, 2, 1, 800000);
    END LOOP;

    RAISE NOTICE 'Réservations créées pour les 3 vols';
END $$;

-- ============================================================
-- 6. CRÉATION DES FACTURES DE DIFFUSION
-- ============================================================
DO $$
DECLARE
    v_id_vp_1 INT;
    v_id_vp_2 INT;
    v_id_societe_vaniala INT;
    v_id_societe_lewis INT;
    v_id_societe_socobis INT;
    v_id_societe_jejoo INT;
    v_id_facture INT;
BEGIN
    -- Récupérer les vols programmés
    SELECT id_vol_programme INTO v_id_vp_1 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND vp.date_heure_depart = '2026-01-20 10:00:00';
    
    SELECT id_vol_programme INTO v_id_vp_2 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND vp.date_heure_depart = '2026-01-21 10:00:00';
    
    -- Récupérer les sociétés
    SELECT id_societe_diffuseur INTO v_id_societe_vaniala FROM societe_diffuseur WHERE nom = 'Vaniala';
    SELECT id_societe_diffuseur INTO v_id_societe_lewis FROM societe_diffuseur WHERE nom = 'Lewis';
    SELECT id_societe_diffuseur INTO v_id_societe_socobis FROM societe_diffuseur WHERE nom = 'Socobis';
    SELECT id_societe_diffuseur INTO v_id_societe_jejoo FROM societe_diffuseur WHERE nom = 'Jejoo';

    -- ==========================================
    -- FACTURE VANIALA - 1 diffusion sur vol 20/01 10h (400 000 Ar)
    -- ==========================================
    INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
    VALUES ('FAC-2026-V001', v_id_societe_vaniala, '2026-01-20', '2026-01-20', '2026-01-20', 400000, 'payée')
    RETURNING id_facture INTO v_id_facture;
    
    INSERT INTO detail_facture (id_facture, id_vol_programme, description, nombre_diffusions, prix_unitaire, montant_ligne, montant_paye)
    VALUES (v_id_facture, v_id_vp_1, 'Diffusion pub vol TNR-NOS 20/01', 1, 400000, 400000, 400000);
    
    INSERT INTO paiement (id_facture, date_paiement, montant_paye, mode_paiement, reference_paiement)
    VALUES (v_id_facture, '2026-01-20', 400000, 'virement', 'VIR-2026-V001');

    -- ==========================================
    -- FACTURE LEWIS - 1 diffusion sur vol 20/01 10h (400 000 Ar)
    -- ==========================================
    INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
    VALUES ('FAC-2026-L001', v_id_societe_lewis, '2026-01-20', '2026-01-20', '2026-01-20', 400000, 'payée')
    RETURNING id_facture INTO v_id_facture;
    
    INSERT INTO detail_facture (id_facture, id_vol_programme, description, nombre_diffusions, prix_unitaire, montant_ligne, montant_paye)
    VALUES (v_id_facture, v_id_vp_1, 'Diffusion pub vol TNR-NOS 20/01', 1, 400000, 400000, 400000);
    
    INSERT INTO paiement (id_facture, date_paiement, montant_paye, mode_paiement, reference_paiement)
    VALUES (v_id_facture, '2026-01-20', 400000, 'virement', 'VIR-2026-L001');

    -- ==========================================
    -- FACTURE SOCOBIS - 2 diffusions sur vol 21/01 10h (800 000 Ar)
    -- ==========================================
    INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
    VALUES ('FAC-2026-S001', v_id_societe_socobis, '2026-01-21', '2026-01-21', '2026-01-21', 800000, 'partiellement_payée')
    RETURNING id_facture INTO v_id_facture;
    
    INSERT INTO detail_facture (id_facture, id_vol_programme, description, nombre_diffusions, prix_unitaire, montant_ligne, montant_paye)
    VALUES (v_id_facture, v_id_vp_2, 'Diffusion pub vol TNR-NOS 21/01 10h', 2, 400000, 800000, 400000);
    
    -- Paiement partiel 400 000 Ar (50%)
    INSERT INTO paiement (id_facture, date_paiement, montant_paye, mode_paiement, reference_paiement)
    VALUES (v_id_facture, '2026-01-21', 400000, 'virement', 'VIR-2026-S001');

    -- ==========================================
    -- FACTURE JEJOO - 1 diffusion sur vol 21/01 10h (400 000 Ar)
    -- ==========================================
    INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
    VALUES ('FAC-2026-J001', v_id_societe_jejoo, '2026-01-21', '2026-01-21', '2026-01-21', 400000, 'émise')
    RETURNING id_facture INTO v_id_facture;
    
    INSERT INTO detail_facture (id_facture, id_vol_programme, description, nombre_diffusions, prix_unitaire, montant_ligne, montant_paye)
    VALUES (v_id_facture, v_id_vp_2, 'Diffusion pub vol TNR-NOS 21/01 10h', 1, 400000, 400000, 0);
    -- Pas de paiement pour Jejoo

    RAISE NOTICE 'Factures de diffusion créées';
END $$;

-- ============================================================
-- VÉRIFICATION DES DONNÉES
-- ============================================================

-- Résumé attendu:
-- | Date            | Heure | Billets         | CA Billets   | Diff | CA Diff Facturé | CA Diff Payé | Reste    |
-- |-----------------|-------|-----------------|--------------|------|-----------------|--------------|----------|
-- | 20/01/2026      | 10h   | 40 éco adulte   | 32 000 000   | 2    | 800 000         | 800 000      | 0        |
-- | 21/01/2026      | 10h   | 30 éco adulte   | 24 000 000   | 3    | 1 200 000       | 400 000      | 800 000  |
-- | 21/01/2026      | 15h   | 50 éco adulte   | 40 000 000   | 0    | 0               | 0            | 0        |
-- | TOTAL           |       | 120 passagers   | 96 000 000   | 5    | 2 000 000       | 1 200 000    | 800 000  |

SELECT 'VÉRIFICATION DES VOLS PROGRAMMÉS' AS info;
SELECT 
    vp.id_vol_programme,
    ad.code_iata || ' → ' || aa.code_iata AS route,
    av.modele AS avion,
    av.numero_immatriculation,
    vp.date_heure_depart,
    vp.prix
FROM vol_programme vp
JOIN vol v ON vp.id_vol = v.id_vol
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
JOIN avion av ON vp.id_avion = av.id_avion
WHERE av.numero_immatriculation = 'TR-045'
ORDER BY vp.date_heure_depart;

SELECT 'VÉRIFICATION DES RÉSERVATIONS' AS info;
SELECT 
    vp.date_heure_depart,
    COUNT(dr.id_detail_reservation) AS nb_passagers,
    SUM(dr.prix_paye) AS ca_billets
FROM vol_programme vp
JOIN avion av ON vp.id_avion = av.id_avion
LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
WHERE av.numero_immatriculation = 'TR-045'
GROUP BY vp.id_vol_programme, vp.date_heure_depart
ORDER BY vp.date_heure_depart;

SELECT 'VÉRIFICATION DES DIFFUSIONS' AS info;
SELECT 
    vp.date_heure_depart,
    SUM(df.nombre_diffusions) AS nb_diffusions,
    SUM(df.montant_ligne) AS ca_diffusions_facture,
    SUM(df.montant_paye) AS ca_diffusions_paye,
    SUM(df.montant_ligne) - SUM(df.montant_paye) AS reste_a_payer
FROM vol_programme vp
JOIN avion av ON vp.id_avion = av.id_avion
LEFT JOIN detail_facture df ON vp.id_vol_programme = df.id_vol_programme
LEFT JOIN facture f ON df.id_facture = f.id_facture AND f.statut != 'annulée'
WHERE av.numero_immatriculation = 'TR-045'
GROUP BY vp.id_vol_programme, vp.date_heure_depart
ORDER BY vp.date_heure_depart;
