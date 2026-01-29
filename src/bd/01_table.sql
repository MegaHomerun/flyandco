-- ============================================================
-- Script de création des tables
-- FlyAndCo - Système de gestion de compagnie aérienne
-- Date: 2026-01-29
-- ============================================================

-- ============================================================
-- CRÉATION DE LA BASE DE DONNÉES
-- ============================================================
DROP DATABASE IF EXISTS pg10;
CREATE DATABASE pg10;
\c pg10;

-- ============================================================
-- TABLE UTILISATEUR (pour l'authentification admin)
-- ============================================================
CREATE TABLE Utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin'
);

-- ============================================================
-- TABLE AEROPORT
-- ============================================================
CREATE TABLE Aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville VARCHAR(50),
    pays VARCHAR(50),
    code_iata CHAR(3) UNIQUE,
    code_icao CHAR(4) UNIQUE
);

-- ============================================================
-- TABLE AVION (Un avion a sa propre capacité)
-- ============================================================
CREATE TABLE Avion (
    id_avion SERIAL PRIMARY KEY,
    modele VARCHAR(50) NOT NULL,
    capacite INT NOT NULL,
    numero_immatriculation VARCHAR(20) UNIQUE NOT NULL
);

-- ============================================================
-- TABLE VOL (Route: définition par aéroport départ et arrivée)
-- Un vol est une route entre deux aéroports
-- ============================================================
CREATE TABLE Vol (
    id_vol SERIAL PRIMARY KEY,
    id_aeroport_depart INT NOT NULL,
    id_aeroport_arrivee INT NOT NULL,
    FOREIGN KEY (id_aeroport_depart) REFERENCES Aeroport(id_aeroport),
    FOREIGN KEY (id_aeroport_arrivee) REFERENCES Aeroport(id_aeroport),
    UNIQUE (id_aeroport_depart, id_aeroport_arrivee)
);

-- ============================================================
-- TABLE VOL_PROGRAMME (Instance d'un vol: date/heure + avion)
-- Un vol peut être effectué plusieurs fois par jour et sur plusieurs jours
-- Un vol peut être effectué par plusieurs avions
-- ============================================================
CREATE TABLE vol_programme (
    id_vol_programme SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_avion INT NOT NULL,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    prix NUMERIC(10,2) NOT NULL,
    statut VARCHAR(20) DEFAULT 'prévu',
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol),
    FOREIGN KEY (id_avion) REFERENCES Avion(id_avion)
);

-- ============================================================
-- TABLE CLIENT (Pour les achats de billets)
-- ============================================================
CREATE TABLE Client (
    id_client SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20)
);

-- ============================================================
-- TABLE RESERVATION (Achat de place)
-- ============================================================
CREATE TABLE Reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_client INT NOT NULL,
    id_vol_programme INT NOT NULL,
    nombre_places INT NOT NULL DEFAULT 1,
    date_reservation TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'confirmée',
    FOREIGN KEY (id_client) REFERENCES Client(id_client),
    FOREIGN KEY (id_vol_programme) REFERENCES vol_programme(id_vol_programme)
);

-- ============================================================
-- TABLE TYPE_PLACE (Première classe, Économique, Premium, etc.)
-- ============================================================
CREATE TABLE type_place (
    id_type_place SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

-- ============================================================
-- TABLE AVION_PLACE (Configuration des places par avion)
-- Un avion a X places de type Y
-- ============================================================
CREATE TABLE avion_place (
    id_avion_place SERIAL PRIMARY KEY,
    id_avion INT NOT NULL,
    id_type_place INT NOT NULL,
    nombre_places INT NOT NULL,
    FOREIGN KEY (id_avion) REFERENCES Avion(id_avion) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    UNIQUE (id_avion, id_type_place)
);

-- ============================================================
-- TABLE TARIF_VOL (Prix par type de place pour chaque route)
-- Un vol (route) a un tarif différent selon le type de place
-- ============================================================
CREATE TABLE tarif_vol (
    id_tarif_vol SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_type_place INT NOT NULL,
    prix NUMERIC(12,2) NOT NULL,
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    UNIQUE (id_vol, id_type_place)
);

-- ============================================================
-- TABLE CATEGORIE_PASSAGER (Adulte, Enfant, Bébé)
-- Permet de différencier les tarifs selon l'âge du passager
-- ============================================================
CREATE TABLE categorie_passager (
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
CREATE TABLE tarif_categorie (
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
CREATE TABLE detail_reservation (
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
-- TABLE DETAIL_FACTURE (avec suivi paiement prorata)
-- ============================================================
CREATE TABLE detail_facture (
    id_detail_facture SERIAL PRIMARY KEY,
    id_facture INT NOT NULL REFERENCES facture(id_facture) ON DELETE CASCADE,
    id_vol_programme INT REFERENCES vol_programme(id_vol_programme),
    id_type_place INT REFERENCES type_place(id_type_place),
    description VARCHAR(255),
    nombre_diffusions INT NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    montant_ligne NUMERIC(12,2) NOT NULL,
    montant_paye NUMERIC(12,2) DEFAULT 0
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
-- VUE: Valeur maximale par avion pour un vol
-- Calcule le revenu potentiel max d'un avion sur une route
-- ============================================================
CREATE OR REPLACE VIEW v_valeur_max_avion_vol AS
SELECT 
    a.id_avion,
    a.modele,
    a.numero_immatriculation,
    v.id_vol,
    ad.code_iata AS depart,
    aa.code_iata AS arrivee,
    SUM(ap.nombre_places * tv.prix) AS valeur_max
FROM avion a
JOIN avion_place ap ON a.id_avion = ap.id_avion
JOIN type_place tp ON ap.id_type_place = tp.id_type_place
CROSS JOIN vol v
JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata;

-- ============================================================
-- VUE: Chiffre d'Affaires par Vol Programmé
-- Calcule le CA généré par avion pour un vol
-- ============================================================
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
-- FONCTION: Répartition prorata d'un paiement sur les lignes
-- ============================================================
CREATE OR REPLACE FUNCTION repartir_paiement_prorata(p_id_facture INT, p_montant_paiement NUMERIC)
RETURNS VOID AS $$
DECLARE
    v_montant_total NUMERIC(12,2);
    v_ligne RECORD;
    v_part_ligne NUMERIC(12,2);
BEGIN
    -- Récupérer le montant total de la facture
    SELECT montant INTO v_montant_total FROM facture WHERE id_facture = p_id_facture;
    
    -- Répartir le paiement au prorata sur chaque ligne
    FOR v_ligne IN SELECT * FROM detail_facture WHERE id_facture = p_id_facture
    LOOP
        -- Calcul prorata: part_ligne = paiement × (montant_ligne / montant_total)
        v_part_ligne := p_montant_paiement * (v_ligne.montant_ligne / v_montant_total);
        
        UPDATE detail_facture 
        SET montant_paye = COALESCE(montant_paye, 0) + v_part_ligne
        WHERE id_detail_facture = v_ligne.id_detail_facture;
    END LOOP;
END;
$$ LANGUAGE plpgsql;
