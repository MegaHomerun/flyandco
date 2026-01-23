-- ============================================================
-- Script de création des tables pour la Diffusion Publicitaire
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-23
-- ============================================================

\c pg10;

-- ============================================================
-- TABLE SOCIETE_DIFFUSEUR
-- Sociétés clientes qui diffusent des vidéos publicitaires
-- ============================================================
CREATE TABLE societe_diffuseur (
    id_societe_diffuseur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    telephone VARCHAR(30),
    adresse VARCHAR(255),
    date_creation TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE TARIF_DIFFUSION
-- Tarifs de diffusion (par défaut ou spécifiques par société)
-- Logique: société spécifique > vol spécifique > par défaut
-- ============================================================
CREATE TABLE tarif_diffusion (
    id_tarif_diffusion SERIAL PRIMARY KEY,
    id_societe_diffuseur INT REFERENCES societe_diffuseur(id_societe_diffuseur) ON DELETE CASCADE,
    id_vol INT REFERENCES Vol(id_vol) ON DELETE CASCADE,
    id_type_place INT REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_debut_validite DATE,
    date_fin_validite DATE
);

-- Index pour recherche rapide des tarifs
CREATE INDEX idx_tarif_diffusion_societe ON tarif_diffusion(id_societe_diffuseur);
CREATE INDEX idx_tarif_diffusion_vol ON tarif_diffusion(id_vol);

-- ============================================================
-- TABLE FACTURE
-- Factures mères (regroupement de diffusions pour une période)
-- ============================================================
CREATE TABLE facture (
    id_facture SERIAL PRIMARY KEY,
    numero_facture VARCHAR(50) UNIQUE NOT NULL,
    id_societe_diffuseur INT NOT NULL REFERENCES societe_diffuseur(id_societe_diffuseur),
    date_facture DATE NOT NULL,
    date_debut_periode DATE NOT NULL,
    date_fin_periode DATE NOT NULL,
    montant_ht NUMERIC(12,2) NOT NULL,
    taux_tva NUMERIC(5,2) DEFAULT 0,
    montant_tva NUMERIC(12,2) DEFAULT 0,
    montant_ttc NUMERIC(12,2) NOT NULL,
    statut VARCHAR(30) DEFAULT 'émise' CHECK (statut IN ('émise', 'partiellement_payée', 'payée', 'annulée')),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

-- Index pour recherche par société et date
CREATE INDEX idx_facture_societe ON facture(id_societe_diffuseur);
CREATE INDEX idx_facture_date ON facture(date_facture);
CREATE INDEX idx_facture_periode ON facture(date_debut_periode, date_fin_periode);

-- ============================================================
-- TABLE DETAIL_FACTURE
-- Lignes de facture (factures filles - détails des diffusions)
-- ============================================================
CREATE TABLE detail_facture (
    id_detail_facture SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    id_vol_programme INT REFERENCES vol_programme(id_vol_programme),
    id_type_place INT REFERENCES type_place(id_type_place),
    description VARCHAR(255),
    nombre_diffusions INT NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    montant_ligne NUMERIC(12,2) NOT NULL
);

-- Index pour recherche par facture
CREATE INDEX idx_detail_facture_facture ON detail_facture(id_facture);

-- ============================================================
-- TABLE PLAN_ECHEANCIER
-- Plans de paiement échelonnés
-- ============================================================
CREATE TABLE plan_echeancier (
    id_plan_echeancier SERIAL PRIMARY KEY,
    id_facture INT NOT NULL UNIQUE REFERENCES facture(id_facture) ON DELETE CASCADE,
    montant_total NUMERIC(12,2) NOT NULL,
    nombre_echeances INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin_prevue DATE NOT NULL,
    statut VARCHAR(30) DEFAULT 'actif' CHECK (statut IN ('actif', 'terminé', 'annulé')),
    date_creation TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE ECHEANCE
-- Échéances individuelles d'un plan de paiement
-- ============================================================
CREATE TABLE echeance (
    id_echeance SERIAL PRIMARY KEY,
    id_plan_echeancier INT NOT NULL REFERENCES plan_echeancier(id_plan_echeancier) ON DELETE CASCADE,
    numero_echeance INT NOT NULL,
    date_echeance_prevue DATE NOT NULL,
    montant_prevu NUMERIC(12,2) NOT NULL,
    date_paiement_reel DATE,
    montant_paye NUMERIC(12,2) DEFAULT 0,
    statut VARCHAR(30) DEFAULT 'en_attente' CHECK (statut IN ('en_attente', 'payée', 'en_retard')),
    UNIQUE(id_plan_echeancier, numero_echeance)
);

-- Index pour recherche par plan
CREATE INDEX idx_echeance_plan ON echeance(id_plan_echeancier);

-- ============================================================
-- TABLE PAIEMENT
-- Paiements reçus (supporte paiements multiples par facture)
-- ============================================================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    id_echeance INT REFERENCES echeance(id_echeance),
    date_paiement DATE NOT NULL,
    montant_paye NUMERIC(12,2) NOT NULL,
    mode_paiement VARCHAR(50) CHECK (mode_paiement IN ('virement', 'espèces', 'chèque', 'mobile_money', 'carte')),
    reference_paiement VARCHAR(100),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

-- Index pour recherche par facture
CREATE INDEX idx_paiement_facture ON paiement(id_facture);
CREATE INDEX idx_paiement_date ON paiement(date_paiement);

-- ============================================================
-- VUE: CA Diffusions par Société
-- ============================================================
CREATE OR REPLACE VIEW v_ca_diffusions AS
SELECT 
    sd.id_societe_diffuseur,
    sd.nom AS societe,
    f.date_facture,
    f.date_debut_periode,
    f.date_fin_periode,
    SUM(df.nombre_diffusions) AS total_diffusions,
    SUM(df.montant_ligne) AS total_ca
FROM societe_diffuseur sd
LEFT JOIN facture f ON sd.id_societe_diffuseur = f.id_societe_diffuseur AND f.statut != 'annulée'
LEFT JOIN detail_facture df ON f.id_facture = df.id_facture
GROUP BY sd.id_societe_diffuseur, sd.nom, f.date_facture, f.date_debut_periode, f.date_fin_periode;

-- ============================================================
-- VUE: Résumé CA Diffusions par Période
-- ============================================================
CREATE OR REPLACE VIEW v_ca_diffusions_resume AS
SELECT 
    sd.id_societe_diffuseur,
    sd.nom AS societe,
    COALESCE(SUM(df.nombre_diffusions), 0) AS total_diffusions,
    COALESCE(SUM(f.montant_ht), 0) AS total_ca_ht,
    COALESCE(SUM(f.montant_ttc), 0) AS total_ca_ttc
FROM societe_diffuseur sd
LEFT JOIN facture f ON sd.id_societe_diffuseur = f.id_societe_diffuseur AND f.statut != 'annulée'
LEFT JOIN detail_facture df ON f.id_facture = df.id_facture
GROUP BY sd.id_societe_diffuseur, sd.nom;

-- ============================================================
-- FONCTION: Obtenir le tarif applicable pour une diffusion
-- Logique de priorité: société+vol+type > société+vol > société > défaut
-- ============================================================
CREATE OR REPLACE FUNCTION get_tarif_diffusion(
    p_id_societe INT,
    p_id_vol INT DEFAULT NULL,
    p_id_type_place INT DEFAULT NULL
) RETURNS NUMERIC(12,2) AS $$
DECLARE
    v_prix NUMERIC(12,2);
BEGIN
    -- 1. Tarif spécifique: société + vol + type_place
    IF p_id_vol IS NOT NULL AND p_id_type_place IS NOT NULL THEN
        SELECT prix_unitaire INTO v_prix
        FROM tarif_diffusion
        WHERE id_societe_diffuseur = p_id_societe 
          AND id_vol = p_id_vol 
          AND id_type_place = p_id_type_place
          AND actif = TRUE
          AND (date_debut_validite IS NULL OR date_debut_validite <= CURRENT_DATE)
          AND (date_fin_validite IS NULL OR date_fin_validite >= CURRENT_DATE)
        LIMIT 1;
        IF v_prix IS NOT NULL THEN RETURN v_prix; END IF;
    END IF;
    
    -- 2. Tarif société + vol
    IF p_id_vol IS NOT NULL THEN
        SELECT prix_unitaire INTO v_prix
        FROM tarif_diffusion
        WHERE id_societe_diffuseur = p_id_societe 
          AND id_vol = p_id_vol 
          AND id_type_place IS NULL
          AND actif = TRUE
          AND (date_debut_validite IS NULL OR date_debut_validite <= CURRENT_DATE)
          AND (date_fin_validite IS NULL OR date_fin_validite >= CURRENT_DATE)
        LIMIT 1;
        IF v_prix IS NOT NULL THEN RETURN v_prix; END IF;
    END IF;
    
    -- 3. Tarif société seul
    SELECT prix_unitaire INTO v_prix
    FROM tarif_diffusion
    WHERE id_societe_diffuseur = p_id_societe 
      AND id_vol IS NULL 
      AND id_type_place IS NULL
      AND actif = TRUE
      AND (date_debut_validite IS NULL OR date_debut_validite <= CURRENT_DATE)
      AND (date_fin_validite IS NULL OR date_fin_validite >= CURRENT_DATE)
    LIMIT 1;
    IF v_prix IS NOT NULL THEN RETURN v_prix; END IF;
    
    -- 4. Tarif par défaut (id_societe_diffuseur = NULL)
    SELECT prix_unitaire INTO v_prix
    FROM tarif_diffusion
    WHERE id_societe_diffuseur IS NULL 
      AND id_vol IS NULL 
      AND id_type_place IS NULL
      AND actif = TRUE
      AND (date_debut_validite IS NULL OR date_debut_validite <= CURRENT_DATE)
      AND (date_fin_validite IS NULL OR date_fin_validite >= CURRENT_DATE)
    LIMIT 1;
    
    RETURN COALESCE(v_prix, 400000); -- Fallback: 400 000 Ar
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- FONCTION: Calculer le montant restant à payer pour une facture
-- ============================================================
CREATE OR REPLACE FUNCTION get_montant_restant_facture(p_id_facture INT)
RETURNS NUMERIC(12,2) AS $$
DECLARE
    v_montant_ttc NUMERIC(12,2);
    v_total_paye NUMERIC(12,2);
BEGIN
    SELECT montant_ttc INTO v_montant_ttc FROM facture WHERE id_facture = p_id_facture;
    SELECT COALESCE(SUM(montant_paye), 0) INTO v_total_paye FROM paiement WHERE id_facture = p_id_facture;
    RETURN v_montant_ttc - v_total_paye;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TRIGGER: Mettre à jour le statut de la facture après paiement
-- ============================================================
CREATE OR REPLACE FUNCTION update_facture_statut()
RETURNS TRIGGER AS $$
DECLARE
    v_montant_restant NUMERIC(12,2);
BEGIN
    v_montant_restant := get_montant_restant_facture(NEW.id_facture);
    
    IF v_montant_restant <= 0 THEN
        UPDATE facture SET statut = 'payée' WHERE id_facture = NEW.id_facture;
    ELSIF v_montant_restant < (SELECT montant_ttc FROM facture WHERE id_facture = NEW.id_facture) THEN
        UPDATE facture SET statut = 'partiellement_payée' WHERE id_facture = NEW.id_facture;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_facture_statut
AFTER INSERT OR UPDATE ON paiement
FOR EACH ROW EXECUTE FUNCTION update_facture_statut();

-- ============================================================
-- DONNÉES INITIALES
-- ============================================================

-- Sociétés diffuseurs
INSERT INTO societe_diffuseur (nom, email, telephone, adresse) VALUES
('Vaniala', 'contact@vaniala.mg', '+261 34 00 000 01', 'Antananarivo, Madagascar'),
('Lewis', 'info@lewis.mg', '+261 34 00 000 02', 'Antananarivo, Madagascar');

-- Tarif par défaut: 400 000 Ar / diffusion
INSERT INTO tarif_diffusion (id_societe_diffuseur, id_vol, id_type_place, prix_unitaire, actif) VALUES
(NULL, NULL, NULL, 400000, TRUE);

-- ============================================================
-- FACTURES DÉCEMBRE 2025
-- ============================================================

-- Facture Vaniala - Décembre 2025 (20 diffusions)
INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant_ht, taux_tva, montant_tva, montant_ttc, statut)
VALUES ('FAC-2025-001', 1, '2025-12-31', '2025-12-01', '2025-12-31', 8000000, 0, 0, 8000000, 'émise');

-- Détail facture Vaniala
INSERT INTO detail_facture (id_facture, description, nombre_diffusions, prix_unitaire, montant_ligne)
VALUES (1, 'Diffusions publicitaires - Décembre 2025', 20, 400000, 8000000);

-- Facture Lewis - Décembre 2025 (10 diffusions)
INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant_ht, taux_tva, montant_tva, montant_ttc, statut)
VALUES ('FAC-2025-002', 2, '2025-12-31', '2025-12-01', '2025-12-31', 4000000, 0, 0, 4000000, 'émise');

-- Détail facture Lewis
INSERT INTO detail_facture (id_facture, description, nombre_diffusions, prix_unitaire, montant_ligne)
VALUES (2, 'Diffusions publicitaires - Décembre 2025', 10, 400000, 4000000);

-- ============================================================
-- VÉRIFICATION: CA Décembre 2025
-- ============================================================
-- SELECT * FROM v_ca_diffusions WHERE date_debut_periode = '2025-12-01';
-- Attendu: Vaniala = 8 000 000 Ar, Lewis = 4 000 000 Ar, Total = 12 000 000 Ar
