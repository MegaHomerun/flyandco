-- ============================================================
-- Script: Produits Extra
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-29
-- Description: Gestion des produits vendus à bord (snacks, etc.)
-- ============================================================

\c pg10;

-- ============================================================
-- TABLE CATEGORIE_PRODUIT (Snacks, Boissons, Accessoires, etc.)
-- ============================================================
CREATE TABLE IF NOT EXISTS categorie_produit (
    id_categorie_produit SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT NOW()
);

-- ============================================================
-- TABLE PRODUIT_EXTRA (Produits vendus à bord)
-- ============================================================
CREATE TABLE IF NOT EXISTS produit_extra (
    id_produit_extra SERIAL PRIMARY KEY,
    id_categorie_produit INT NOT NULL REFERENCES categorie_produit(id_categorie_produit),
    code_produit VARCHAR(20) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    prix_unitaire NUMERIC(12,2) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_produit_categorie ON produit_extra(id_categorie_produit);
CREATE INDEX IF NOT EXISTS idx_produit_actif ON produit_extra(actif);

-- ============================================================
-- TABLE VENTE_PRODUIT (Ventes de produits à bord d'un vol)
-- ============================================================
CREATE TABLE IF NOT EXISTS vente_produit (
    id_vente_produit SERIAL PRIMARY KEY,
    id_vol_programme INT NOT NULL REFERENCES vol_programme(id_vol_programme),
    id_produit_extra INT NOT NULL REFERENCES produit_extra(id_produit_extra),
    quantite INT NOT NULL CHECK (quantite > 0),
    prix_unitaire NUMERIC(12,2) NOT NULL,
    montant_total NUMERIC(12,2) NOT NULL,
    date_vente TIMESTAMP DEFAULT NOW(),
    notes VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_vente_vol ON vente_produit(id_vol_programme);
CREATE INDEX IF NOT EXISTS idx_vente_produit ON vente_produit(id_produit_extra);
CREATE INDEX IF NOT EXISTS idx_vente_date ON vente_produit(date_vente);

-- ============================================================
-- TABLE FACTURE_PRODUIT (Factures pour ventes de produits)
-- Même logique que facture diffusion avec paiements multiples
-- ============================================================
CREATE TABLE IF NOT EXISTS facture_produit (
    id_facture_produit SERIAL PRIMARY KEY,
    numero_facture VARCHAR(50) UNIQUE NOT NULL,
    date_facture DATE NOT NULL,
    date_debut_periode DATE NOT NULL,
    date_fin_periode DATE NOT NULL,
    montant NUMERIC(12,2) NOT NULL,
    statut VARCHAR(30) DEFAULT 'émise' CHECK (statut IN ('émise', 'partiellement_payée', 'payée', 'annulée')),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_facture_produit_date ON facture_produit(date_facture);
CREATE INDEX IF NOT EXISTS idx_facture_produit_periode ON facture_produit(date_debut_periode, date_fin_periode);

-- ============================================================
-- TABLE DETAIL_FACTURE_PRODUIT (Lignes de facture produit)
-- Avec suivi paiement prorata comme detail_facture
-- ============================================================
CREATE TABLE IF NOT EXISTS detail_facture_produit (
    id_detail_facture_produit SERIAL PRIMARY KEY,
    id_facture_produit INT NOT NULL REFERENCES facture_produit(id_facture_produit) ON DELETE CASCADE,
    id_vente_produit INT REFERENCES vente_produit(id_vente_produit),
    id_vol_programme INT REFERENCES vol_programme(id_vol_programme),
    id_produit_extra INT REFERENCES produit_extra(id_produit_extra),
    description VARCHAR(255),
    quantite INT NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    montant_ligne NUMERIC(12,2) NOT NULL,
    montant_paye NUMERIC(12,2) DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_detail_facture_produit ON detail_facture_produit(id_facture_produit);

-- ============================================================
-- TABLE PAIEMENT_PRODUIT (Paiements factures produits)
-- ============================================================
CREATE TABLE IF NOT EXISTS paiement_produit (
    id_paiement_produit SERIAL PRIMARY KEY,
    id_facture_produit INT NOT NULL REFERENCES facture_produit(id_facture_produit) ON DELETE CASCADE,
    date_paiement DATE NOT NULL,
    montant_paye NUMERIC(12,2) NOT NULL,
    mode_paiement VARCHAR(50) CHECK (mode_paiement IN ('virement', 'espèces', 'chèque', 'mobile_money', 'carte')),
    reference_paiement VARCHAR(100),
    notes TEXT,
    date_creation TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_paiement_produit_facture ON paiement_produit(id_facture_produit);
CREATE INDEX IF NOT EXISTS idx_paiement_produit_date ON paiement_produit(date_paiement);

-- ============================================================
-- VUE: CA Produits par Vol Programmé
-- ============================================================
CREATE OR REPLACE VIEW v_ca_produits_vol AS
SELECT 
    vp.id_vol_programme,
    vp.id_vol,
    a.modele AS avion_modele,
    a.numero_immatriculation,
    ad.code_iata || ' → ' || aa.code_iata AS route,
    vp.date_heure_depart,
    pe.id_produit_extra,
    pe.nom AS produit,
    cp.nom AS categorie,
    COALESCE(SUM(vpr.quantite), 0) AS quantite_vendue,
    COALESCE(SUM(vpr.montant_total), 0) AS ca_produits
FROM vol_programme vp
JOIN avion a ON vp.id_avion = a.id_avion
JOIN vol v ON vp.id_vol = v.id_vol
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
LEFT JOIN vente_produit vpr ON vp.id_vol_programme = vpr.id_vol_programme
LEFT JOIN produit_extra pe ON vpr.id_produit_extra = pe.id_produit_extra
LEFT JOIN categorie_produit cp ON pe.id_categorie_produit = cp.id_categorie_produit
GROUP BY vp.id_vol_programme, vp.id_vol, a.modele, a.numero_immatriculation,
         ad.code_iata, aa.code_iata, vp.date_heure_depart, 
         pe.id_produit_extra, pe.nom, cp.nom;

-- ============================================================
-- VUE: CA Mensuel Global (Tickets + Diffusions + Produits)
-- ============================================================
CREATE OR REPLACE VIEW v_ca_mensuel AS
SELECT 
    DATE_TRUNC('month', date_ref) AS mois,
    type_ca,
    COUNT(*) AS nombre,
    SUM(montant) AS ca_total
FROM (
    -- CA Tickets (réservations)
    SELECT 
        vp.date_heure_depart AS date_ref,
        'Tickets' AS type_ca,
        dr.prix_paye AS montant
    FROM detail_reservation dr
    JOIN reservation r ON dr.id_reservation = r.id_reservation
    JOIN vol_programme vp ON r.id_vol_programme = vp.id_vol_programme
    WHERE r.statut = 'confirmée'
    
    UNION ALL
    
    -- CA Diffusions (pub)
    SELECT 
        f.date_facture AS date_ref,
        'Diffusions' AS type_ca,
        df.montant_ligne AS montant
    FROM detail_facture df
    JOIN facture f ON df.id_facture = f.id_facture
    WHERE f.statut != 'annulée'
    
    UNION ALL
    
    -- CA Produits Extra
    SELECT 
        vpr.date_vente AS date_ref,
        'Produits Extra' AS type_ca,
        vpr.montant_total AS montant
    FROM vente_produit vpr
) AS ca_sources
GROUP BY DATE_TRUNC('month', date_ref), type_ca
ORDER BY mois DESC, type_ca;

-- ============================================================
-- VUE: Détail CA Mensuel avec totaux
-- ============================================================
CREATE OR REPLACE VIEW v_ca_mensuel_detail AS
WITH ca_tickets AS (
    SELECT 
        DATE_TRUNC('month', vp.date_heure_depart) AS mois,
        COUNT(dr.id_detail_reservation) AS nb_tickets,
        COALESCE(SUM(dr.prix_paye), 0) AS ca_tickets
    FROM vol_programme vp
    LEFT JOIN reservation r ON vp.id_vol_programme = r.id_vol_programme AND r.statut = 'confirmée'
    LEFT JOIN detail_reservation dr ON r.id_reservation = dr.id_reservation
    GROUP BY DATE_TRUNC('month', vp.date_heure_depart)
),
ca_diffusions AS (
    SELECT 
        DATE_TRUNC('month', f.date_facture) AS mois,
        COALESCE(SUM(df.nombre_diffusions), 0) AS nb_diffusions,
        COALESCE(SUM(df.montant_ligne), 0) AS ca_diffusions
    FROM facture f
    LEFT JOIN detail_facture df ON f.id_facture = df.id_facture
    WHERE f.statut != 'annulée'
    GROUP BY DATE_TRUNC('month', f.date_facture)
),
ca_produits AS (
    SELECT 
        DATE_TRUNC('month', vpr.date_vente) AS mois,
        COALESCE(SUM(vpr.quantite), 0) AS nb_produits,
        COALESCE(SUM(vpr.montant_total), 0) AS ca_produits
    FROM vente_produit vpr
    GROUP BY DATE_TRUNC('month', vpr.date_vente)
)
SELECT 
    COALESCE(t.mois, d.mois, p.mois) AS mois,
    COALESCE(t.nb_tickets, 0) AS nb_tickets,
    COALESCE(t.ca_tickets, 0) AS ca_tickets,
    COALESCE(d.nb_diffusions, 0) AS nb_diffusions,
    COALESCE(d.ca_diffusions, 0) AS ca_diffusions,
    COALESCE(p.nb_produits, 0) AS nb_produits,
    COALESCE(p.ca_produits, 0) AS ca_produits,
    COALESCE(t.ca_tickets, 0) + COALESCE(d.ca_diffusions, 0) + COALESCE(p.ca_produits, 0) AS ca_total
FROM ca_tickets t
FULL OUTER JOIN ca_diffusions d ON t.mois = d.mois
FULL OUTER JOIN ca_produits p ON COALESCE(t.mois, d.mois) = p.mois
ORDER BY mois DESC;

-- ============================================================
-- FONCTION: Mettre à jour statut facture produit après paiement
-- ============================================================
CREATE OR REPLACE FUNCTION update_facture_produit_statut()
RETURNS TRIGGER AS $$
DECLARE
    v_montant_facture NUMERIC(12,2);
    v_total_paye NUMERIC(12,2);
BEGIN
    SELECT montant INTO v_montant_facture FROM facture_produit WHERE id_facture_produit = NEW.id_facture_produit;
    SELECT COALESCE(SUM(montant_paye), 0) INTO v_total_paye FROM paiement_produit WHERE id_facture_produit = NEW.id_facture_produit;
    
    IF v_total_paye >= v_montant_facture THEN
        UPDATE facture_produit SET statut = 'payée' WHERE id_facture_produit = NEW.id_facture_produit;
    ELSIF v_total_paye > 0 THEN
        UPDATE facture_produit SET statut = 'partiellement_payée' WHERE id_facture_produit = NEW.id_facture_produit;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_facture_produit_statut ON paiement_produit;
CREATE TRIGGER trg_update_facture_produit_statut
AFTER INSERT OR UPDATE ON paiement_produit
FOR EACH ROW EXECUTE FUNCTION update_facture_produit_statut();

-- ============================================================
-- FONCTION: Répartition prorata paiement sur lignes facture produit
-- ============================================================
CREATE OR REPLACE FUNCTION repartir_paiement_produit_prorata(p_id_facture_produit INT, p_montant_paiement NUMERIC)
RETURNS VOID AS $$
DECLARE
    v_montant_total NUMERIC(12,2);
    v_ligne RECORD;
    v_part_ligne NUMERIC(12,2);
BEGIN
    SELECT montant INTO v_montant_total FROM facture_produit WHERE id_facture_produit = p_id_facture_produit;
    
    FOR v_ligne IN SELECT * FROM detail_facture_produit WHERE id_facture_produit = p_id_facture_produit
    LOOP
        v_part_ligne := p_montant_paiement * (v_ligne.montant_ligne / v_montant_total);
        
        UPDATE detail_facture_produit 
        SET montant_paye = COALESCE(montant_paye, 0) + v_part_ligne
        WHERE id_detail_facture_produit = v_ligne.id_detail_facture_produit;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- DONNÉES: Catégories de produits
-- ============================================================
INSERT INTO categorie_produit (nom, description) VALUES
('Snacks', 'Produits alimentaires (chocolat, biscuits, etc.)'),
('Boissons', 'Boissons (eau, jus, soda, etc.)'),
('Accessoires', 'Accessoires de voyage (coussins, masques, etc.)')
ON CONFLICT (nom) DO NOTHING;

-- ============================================================
-- DONNÉES: Produits Extra (Tablette de chocolat)
-- ============================================================
INSERT INTO produit_extra (id_categorie_produit, code_produit, nom, description, prix_unitaire) VALUES
(1, 'CHOCO-001', 'Tablette de chocolat', 'Tablette de chocolat au lait 100g', 5000)
ON CONFLICT (code_produit) DO NOTHING;

-- ============================================================
-- DONNÉES DE TEST: Ventes de produits sur vols existants
-- ============================================================
DO $$
DECLARE
    v_id_produit INT;
    v_id_vp INT;
BEGIN
    SELECT id_produit_extra INTO v_id_produit FROM produit_extra WHERE code_produit = 'CHOCO-001';
    
    -- Ventes sur le vol 20/01 10h (TR-045)
    SELECT vp.id_vol_programme INTO v_id_vp 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND DATE(vp.date_heure_depart) = '2026-01-20'
    AND EXTRACT(HOUR FROM vp.date_heure_depart) = 10;
    
    IF v_id_vp IS NOT NULL AND NOT EXISTS (SELECT 1 FROM vente_produit WHERE id_vol_programme = v_id_vp) THEN
        INSERT INTO vente_produit (id_vol_programme, id_produit_extra, quantite, prix_unitaire, montant_total, notes)
        VALUES (v_id_vp, v_id_produit, 15, 5000, 75000, 'Vente chocolat vol 20/01 10h');
    END IF;
    
    -- Ventes sur le vol 21/01 10h
    SELECT vp.id_vol_programme INTO v_id_vp 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND DATE(vp.date_heure_depart) = '2026-01-21'
    AND EXTRACT(HOUR FROM vp.date_heure_depart) = 10;
    
    IF v_id_vp IS NOT NULL AND NOT EXISTS (SELECT 1 FROM vente_produit WHERE id_vol_programme = v_id_vp) THEN
        INSERT INTO vente_produit (id_vol_programme, id_produit_extra, quantite, prix_unitaire, montant_total, notes)
        VALUES (v_id_vp, v_id_produit, 20, 5000, 100000, 'Vente chocolat vol 21/01 10h');
    END IF;
    
    -- Ventes sur le vol 21/01 15h
    SELECT vp.id_vol_programme INTO v_id_vp 
    FROM vol_programme vp
    JOIN avion a ON vp.id_avion = a.id_avion
    WHERE a.numero_immatriculation = 'TR-045' 
    AND DATE(vp.date_heure_depart) = '2026-01-21'
    AND EXTRACT(HOUR FROM vp.date_heure_depart) = 15;
    
    IF v_id_vp IS NOT NULL AND NOT EXISTS (SELECT 1 FROM vente_produit WHERE id_vol_programme = v_id_vp) THEN
        INSERT INTO vente_produit (id_vol_programme, id_produit_extra, quantite, prix_unitaire, montant_total, notes)
        VALUES (v_id_vp, v_id_produit, 25, 5000, 125000, 'Vente chocolat vol 21/01 15h');
    END IF;
END $$;
