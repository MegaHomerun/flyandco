-- Script de création de la base de données FlyAndCo
-- Nouvelle structure respectant les règles de gestion

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
    premiere_classe INT NOT NULL,
    classe_economique INT NOT NULL,
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
-- INSERTION DES DONNÉES
-- ============================================================

-- Compte admin par défaut
INSERT INTO Utilisateur (username, mot_de_passe, role)
VALUES ('admin', 'admin', 'admin');

-- Aéroports de Madagascar
INSERT INTO Aeroport (nom, ville, pays, code_iata, code_icao) VALUES
('Aéroport International d''Ivato', 'Antananarivo', 'Madagascar', 'TNR', 'FMMI'),
('Aéroport de Fascene', 'Nosy Be', 'Madagascar', 'NOS', 'FMNN'),
('Aéroport de Toamasina', 'Toamasina', 'Madagascar', 'TMM', 'FMMT'),
('Aéroport de Tolagnaro', 'Fort Dauphin', 'Madagascar', 'FTU', 'FMSD'),
('Aéroport de Mahajanga', 'Mahajanga', 'Madagascar', 'MJN', 'FMNM');

-- Avions disponibles
INSERT INTO Avion (
    modele,
    capacite,
    premiere_classe,
    classe_economique,
    numero_immatriculation
) VALUES
-- ATR 72-600 (souvent 100% éco ou très peu de business)
('ATR 72-600', 70, 4, 66, '5R-MJA'),

-- Boeing 737-800
('Boeing 737-800', 180, 16, 164, '5R-MJB'),

-- Airbus A320
('Airbus A320', 150, 12, 138, '5R-MJC'),

-- ATR 42-500
('ATR 42-500', 48, 4, 44, '5R-MJD'),

-- Embraer E190
('Embraer E190', 100, 8, 92, '5R-MJE');


-- Vols (routes) - définition des liaisons
INSERT INTO Vol (id_aeroport_depart, id_aeroport_arrivee) VALUES
(1, 2),  -- TNR -> Nosy Be
(2, 1),  -- Nosy Be -> TNR
(1, 3),  -- TNR -> Toamasina
(3, 1),  -- Toamasina -> TNR
(1, 4),  -- TNR -> Fort Dauphin
(4, 1),  -- Fort Dauphin -> TNR
(1, 5),  -- TNR -> Mahajanga
(5, 1);  -- Mahajanga -> TNR

-- Programmes de vols pour janvier 2026 (TNR <-> Nosy Be)
-- Vol TNR -> Nosy Be le 12 janvier 2026 à 12h00
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

-- Client exemple
INSERT INTO Client (nom, prenom, email, telephone) VALUES
('Rakoto', 'Jean', 'jean.rakoto@email.com', '034 12 345 67'),
('Rasoa', 'Marie', 'marie.rasoa@email.com', '033 98 765 43');

-- Réservation exemple
INSERT INTO Reservation (id_client, id_vol_programme, nombre_places, statut) VALUES
(1, 1, 2, 'confirmée');
