-- ============================================================
-- Script de création des tables pour la Diffusion Publicitaire
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-23 (Version simplifiée - sans TVA)
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

CREATE INDEX idx_tarif_diffusion_societe ON tarif_diffusion(id_societe_diffuseur);

-- ============================================================
-- TABLE FACTURE (Simplifiée - sans TVA)
-- Paiements multiples autorisés sans échéancier obligatoire
-- ============================================================
CREATE TABLE facture (
    id_facture SERIAL PRIMARY KEY,
    numero_facture VARCHAR(50) UNIQUE NOT NULL,
    id_societe_diffuseur INT NOT NULL REFERENCES societe_diffuseur(id_societe_diffuseur),
    date_facture DATE NOT NULL,
    date_debut_periode DATE NOT NULL,
    date_fin_periode DATE NOT NULL,
    montant NUMERIC(12,2) NOT NULL,
    statut VARCHAR(30) DEFAULT 'émise' CHECK (statut IN ('émise', 'partiellement_payée', 'payée', 'annulée')),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_facture_societe ON facture(id_societe_diffuseur);
CREATE INDEX idx_facture_date ON facture(date_facture);
CREATE INDEX idx_facture_periode ON facture(date_debut_periode, date_fin_periode);

-- ============================================================
-- TABLE DETAIL_FACTURE
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

CREATE INDEX idx_detail_facture_facture ON detail_facture(id_facture);

-- ============================================================
-- TABLE PAIEMENT (Paiements multiples sans échéancier obligatoire)
-- ============================================================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    date_paiement DATE NOT NULL,
    montant_paye NUMERIC(12,2) NOT NULL,
    mode_paiement VARCHAR(50) CHECK (mode_paiement IN ('virement', 'espèces', 'chèque', 'mobile_money', 'carte')),
    reference_paiement VARCHAR(100),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_paiement_facture ON paiement(id_paiement);
CREATE INDEX idx_paiement_date ON paiement(date_paiement);

-- ============================================================
-- VUE: Résumé par société avec reste à payer
-- ============================================================
CREATE OR REPLACE VIEW v_resume_paiement_societe AS
SELECT 
    sd.id_societe_diffuseur,
    sd.nom AS societe,
    COALESCE(SUM(df.nombre_diffusions), 0) AS total_diffusions,
    COALESCE(SUM(f.montant), 0) AS total_facture,
    COALESCE((SELECT SUM(p.montant_paye) FROM paiement p 
              JOIN facture f2 ON p.id_facture = f2.id_facture 
              WHERE f2.id_societe_diffuseur = sd.id_societe_diffuseur AND f2.statut != 'annulée'), 0) AS total_paye,
    COALESCE(SUM(f.montant), 0) - COALESCE((SELECT SUM(p.montant_paye) FROM paiement p 
              JOIN facture f2 ON p.id_facture = f2.id_facture 
              WHERE f2.id_societe_diffuseur = sd.id_societe_diffuseur AND f2.statut != 'annulée'), 0) AS reste_a_payer
FROM societe_diffuseur sd
LEFT JOIN facture f ON sd.id_societe_diffuseur = f.id_societe_diffuseur AND f.statut != 'annulée'
LEFT JOIN detail_facture df ON f.id_facture = df.id_facture
GROUP BY sd.id_societe_diffuseur, sd.nom;

-- ============================================================
-- FONCTION: Mettre à jour le statut de la facture après paiement
-- ============================================================
CREATE OR REPLACE FUNCTION update_facture_statut()
RETURNS TRIGGER AS $$
DECLARE
    v_montant_facture NUMERIC(12,2);
    v_total_paye NUMERIC(12,2);
BEGIN
    SELECT montant INTO v_montant_facture FROM facture WHERE id_facture = NEW.id_facture;
    SELECT COALESCE(SUM(montant_paye), 0) INTO v_total_paye FROM paiement WHERE id_facture = NEW.id_facture;
    
    IF v_total_paye >= v_montant_facture THEN
        UPDATE facture SET statut = 'payée' WHERE id_facture = NEW.id_facture;
    ELSIF v_total_paye > 0 THEN
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

-- Facture Vaniala - Décembre 2025 (20 diffusions x 400 000 = 8 000 000 Ar)
INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
VALUES ('FAC-2025-001', 1, '2025-12-31', '2025-12-01', '2025-12-31', 8000000, 'partiellement_payée');

INSERT INTO detail_facture (id_facture, description, nombre_diffusions, prix_unitaire, montant_ligne)
VALUES (1, 'Diffusions publicitaires - Décembre 2025', 20, 400000, 8000000);

-- Paiement partiel Vaniala - 1 000 000 Ar le 15 décembre 2025
INSERT INTO paiement (id_facture, date_paiement, montant_paye, mode_paiement, reference_paiement, notes)
VALUES (1, '2025-12-15', 1000000, 'virement', 'VIR-2025-001', 'Acompte décembre 2025');

-- Facture Lewis - Décembre 2025 (10 diffusions x 400 000 = 4 000 000 Ar)
INSERT INTO facture (numero_facture, id_societe_diffuseur, date_facture, date_debut_periode, date_fin_periode, montant, statut)
VALUES ('FAC-2025-002', 2, '2025-12-31', '2025-12-01', '2025-12-31', 4000000, 'émise');

INSERT INTO detail_facture (id_facture, description, nombre_diffusions, prix_unitaire, montant_ligne)
VALUES (2, 'Diffusions publicitaires - Décembre 2025', 10, 400000, 4000000);

-- ============================================================
-- VÉRIFICATION
-- ============================================================
-- SELECT * FROM v_resume_paiement_societe;
-- Résultat attendu:
-- Vaniala: 20 diffusions, 8 000 000 Ar facturé, 1 000 000 Ar payé, 7 000 000 Ar reste
-- Lewis: 10 diffusions, 4 000 000 Ar facturé, 0 Ar payé, 4 000 000 Ar reste
