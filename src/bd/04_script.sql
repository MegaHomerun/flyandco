DROP DATABASE IF EXISTS pg10;
CREATE DATABASE pg10;
\c pg10;

-- =========================
-- UTILISATEUR (ADMIN)
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin'
);

-- =========================
-- COMPAGNIE
-- =========================
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    pays VARCHAR(50),
    code_iata CHAR(2) UNIQUE,
    code_icao CHAR(3) UNIQUE
);

-- =========================
-- AEROPORT
-- =========================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville VARCHAR(50),
    pays VARCHAR(50),
    code_iata CHAR(3) UNIQUE NOT NULL,
    code_icao CHAR(4) UNIQUE
);

-- =========================
-- AVION
-- =========================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    modele VARCHAR(50) NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    numero_immatriculation VARCHAR(20) UNIQUE NOT NULL,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- VOL (trajet théorique)
-- =========================
CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    id_aeroport_depart INT NOT NULL,
    id_aeroport_arrivee INT NOT NULL,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie),
    FOREIGN KEY (id_aeroport_depart) REFERENCES aeroport(id_aeroport),
    FOREIGN KEY (id_aeroport_arrivee) REFERENCES aeroport(id_aeroport),
    CHECK (id_aeroport_depart <> id_aeroport_arrivee)
);

-- =========================
-- VOL INSTANCE (date réelle)
-- =========================
CREATE TABLE vol_instance (
    id_vol_instance SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    id_avion INT NOT NULL,
    date_depart TIMESTAMP NOT NULL,
    date_arrivee TIMESTAMP NOT NULL,
    statut VARCHAR(20) DEFAULT 'PREVU',
    FOREIGN KEY (id_vol) REFERENCES vol(id_vol),
    FOREIGN KEY (id_avion) REFERENCES avion(id_avion)
);

-- =========================
-- PRIX PAR CLASSE (MODIFIE POUR PRIX ADULTE/ENFANT)
-- =========================
CREATE TABLE prix_vol (
    id_prix SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,
    classe VARCHAR(20) NOT NULL CHECK (classe IN ('ECONOMY','PREMIUM','FIRST')),
    type_passager VARCHAR(10) DEFAULT 'ADULTE' CHECK (type_passager IN ('ADULTE', 'ENFANT')),
    prix NUMERIC(10,2) NOT NULL CHECK (prix > 0),
    date_maj TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (id_vol) REFERENCES vol(id_vol),
    UNIQUE (id_vol, classe, type_passager)
);

-- =========================
-- PASSAGER (MODIFIE POUR TYPE PASSAGER)
-- =========================
CREATE TABLE passager (
    id_passager SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    date_naissance DATE,
    email VARCHAR(100) UNIQUE,
    type_passager VARCHAR(10) DEFAULT 'ADULTE' CHECK (type_passager IN ('ADULTE', 'ENFANT'))
);

-- =========================
-- RESERVATION
-- =========================
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_passager INT NOT NULL,
    id_vol_instance INT NOT NULL,
    id_prix INT NOT NULL,
    siege VARCHAR(5),
    date_reservation TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'CONFIRMEE',
    FOREIGN KEY (id_passager) REFERENCES passager(id_passager),
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_prix) REFERENCES prix_vol(id_prix),
    UNIQUE (id_passager, id_vol_instance)
);

-- =========================
-- PILOTE
-- =========================
CREATE TABLE pilote (
    id_pilote SERIAL PRIMARY KEY,
    id_compagnie INT NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    licence_num VARCHAR(20) UNIQUE NOT NULL,
    experience INT DEFAULT 0,
    FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- EQUIPAGE
-- =========================
CREATE TABLE equipage (
    id_equipage SERIAL PRIMARY KEY,
    id_vol_instance INT NOT NULL,
    id_pilote INT NOT NULL,
    role VARCHAR(50) DEFAULT 'PILOTE',
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_pilote) REFERENCES pilote(id_pilote),
    UNIQUE (id_vol_instance, id_pilote)
);


-- =========================
-- MODE PAIEMENT
-- =========================
CREATE TABLE moyen_paiement (
    id_moyen_paiement SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- PAIEMENT
-- =========================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_moyen_paiement INT NOT NULL,
    montant NUMERIC(10,2) NOT NULL,
    date_paiement TIMESTAMP DEFAULT NOW(),
    statut VARCHAR(20) DEFAULT 'OK',
    FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation),
    FOREIGN KEY (id_moyen_paiement) REFERENCES moyen_paiement(id_moyen_paiement)
);

-- =========================
-- SIEGE
-- =========================
CREATE TABLE siege (
    id_siege SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,    
    numero VARCHAR(5) NOT NULL,           
    classe VARCHAR(20) NOT NULL CHECK (classe IN ('ECONOMY','PREMIUM','FIRST')),
    FOREIGN KEY (id_vol) REFERENCES vol(id_vol)   
);


CREATE TABLE siege_vol (
    id_siege_vol SERIAL PRIMARY KEY,
    id_vol_instance INT NOT NULL,
    id_siege INT NOT NULL,
    statut VARCHAR(20) DEFAULT 'LIBRE',
    FOREIGN KEY (id_vol_instance) REFERENCES vol_instance(id_vol_instance),
    FOREIGN KEY (id_siege) REFERENCES siege(id_siege)
);

-- =========================
-- UTILISATEUR
-- =========================
INSERT INTO utilisateur (username, mot_de_passe, role)
VALUES
('admin', 'admin', 'admin');

-- =========================
-- AEROPORTS
-- =========================
INSERT INTO aeroport (nom, ville, pays, code_iata, code_icao)
VALUES
('Ivato International Airport', 'Antananarivo', 'Madagascar', 'TNR', 'FMMI'),
('Fascene Airport', 'Nosy Be', 'Madagascar', 'NOS', 'FMNN'),
('Arrachart Airport', 'Antsiranana', 'Madagascar', 'DIE', 'FMNA'),
('Toliara Airport', 'Toliara', 'Madagascar', 'TLE', 'FMST'),
('Marillac Airport', 'Fort Dauphin', 'Madagascar', 'FTU', 'FMSD');

-- =========================
-- COMPAGNIES
-- =========================
INSERT INTO compagnie (nom, pays, code_iata, code_icao)
VALUES
('Air Madagascar', 'Madagascar', 'MD', 'MDG'),
('Tsaradia', 'Madagascar', 'TZ', 'TSD'),
('Madagascar Airlines', 'Madagascar', 'MA', 'MDA'),
('Ewa Air', 'Comores', 'ZW', 'EWR');

-- =========================
-- AVIONS
-- =========================
INSERT INTO avion (id_compagnie, modele, capacite, numero_immatriculation)
VALUES
(1, 'ATR 72', 70, '5R-MJG'),
(2, 'ATR 42', 48, '5R-TSA'),
(3, 'Boeing 737', 140, '5R-MDL'),
(1, 'Dash 8 Q400', 78, '5R-DQ4'),
(4, 'Embraer 190', 100, 'D2-EWA');

-- =========================
-- VOLS (trajets)
-- =========================
INSERT INTO vol (id_compagnie, id_aeroport_depart, id_aeroport_arrivee)
VALUES
(1, 1, 2),
(2, 1, 2),
(1, 1, 3);

-- =========================
-- VOL INSTANCES
-- =========================
INSERT INTO vol_instance (id_vol, id_avion, date_depart, date_arrivee)
VALUES
(1, 1, '2026-01-12 12:00', '2026-01-12 13:30'),
(2, 2, '2026-01-12 12:00', '2026-01-12 13:25'),
(3, 3, '2026-01-15 02:00', '2026-01-15 08:25');

-- =========================
-- PRIX PAR CLASSE (AVEC PRIX ADULTE ET ENFANT POUR ECONOMY)
-- =========================
-- Vol 1 - Prix ADULTE
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(1, 'ECONOMY', 'ADULTE', 800000),
(1, 'FIRST', 'ADULTE', 1200000),
(1, 'PREMIUM', 'ADULTE', 1000000);

-- Vol 1 - Prix ENFANT (uniquement pour ECONOMY, 40% de réduction)
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(1, 'ECONOMY', 'ENFANT', 480000);

-- Vol 2 - Prix ADULTE
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(2, 'ECONOMY', 'ADULTE', 330000),
(2, 'FIRST', 'ADULTE', 1200000),
(2, 'PREMIUM', 'ADULTE', 1000000);

-- Vol 2 - Prix ENFANT (uniquement pour ECONOMY, 40% de réduction)
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(2, 'ECONOMY', 'ENFANT', 198000);

-- Vol 3 - Prix ADULTE
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(3, 'FIRST', 'ADULTE', 1200000),
(3, 'ECONOMY', 'ADULTE', 700000),
(3, 'PREMIUM', 'ADULTE', 1000000);

-- Vol 3 - Prix ENFANT (uniquement pour ECONOMY, 40% de réduction)
INSERT INTO prix_vol (id_vol, classe, type_passager, prix)
VALUES
(3, 'ECONOMY', 'ENFANT', 420000);

-- =========================
-- INSERTION DES MODES DE PAIEMENT
-- =========================
INSERT INTO moyen_paiement (libelle)
VALUES
('CB'),        -- Carte Bancaire
('Virement'),  -- Virement bancaire
('Paypal'),    -- Paiement en ligne via Paypal
('Espèces'),   -- Paiement en espèces
('Chèque');    -- Paiement par chèque

-- =========================
-- FIRST CLASS pour le vol 1 (30 sièges)
-- =========================
INSERT INTO siege (id_vol, numero, classe) VALUES
(1,'1A','FIRST'), (1,'1B','FIRST'),
(1,'2A','FIRST'), (1,'2B','FIRST'),
(1,'3A','FIRST'), (1,'3B','FIRST'),
(1,'4A','FIRST'), (1,'4B','FIRST'),
(1,'5A','FIRST'), (1,'5B','FIRST'),
(1,'6A','FIRST'), (1,'6B','FIRST'),
(1,'7A','FIRST'), (1,'7B','FIRST'),
(1,'8A','FIRST'), (1,'8B','FIRST'),
(1,'9A','FIRST'), (1,'9B','FIRST'),
(1,'10A','FIRST'), (1,'10B','FIRST'),
(1,'11A','FIRST'), (1,'11B','FIRST'),
(1,'12A','FIRST'), (1,'12B','FIRST'),
(1,'13A','FIRST'), (1,'13B','FIRST'),
(1,'14A','FIRST'), (1,'14B','FIRST'),
(1,'15A','FIRST'), (1,'15B','FIRST');

-- =========================
-- PREMIUM CLASS pour le vol 1 (40 sièges)
-- =========================
INSERT INTO siege (id_vol, numero, classe) VALUES
(1,'16A','PREMIUM'), (1,'16B','PREMIUM'),
(1,'17A','PREMIUM'), (1,'17B','PREMIUM'),
(1,'18A','PREMIUM'), (1,'18B','PREMIUM'),
(1,'19A','PREMIUM'), (1,'19B','PREMIUM'),
(1,'20A','PREMIUM'), (1,'20B','PREMIUM'),
(1,'21A','PREMIUM'), (1,'21B','PREMIUM'),
(1,'22A','PREMIUM'), (1,'22B','PREMIUM'),
(1,'23A','PREMIUM'), (1,'23B','PREMIUM'),
(1,'24A','PREMIUM'), (1,'24B','PREMIUM'),
(1,'25A','PREMIUM'), (1,'25B','PREMIUM'),
(1,'26A','PREMIUM'), (1,'26B','PREMIUM'),
(1,'27A','PREMIUM'), (1,'27B','PREMIUM'),
(1,'28A','PREMIUM'), (1,'28B','PREMIUM'),
(1,'29A','PREMIUM'), (1,'29B','PREMIUM'),
(1,'30A','PREMIUM'), (1,'30B','PREMIUM'),
(1,'31A','PREMIUM'), (1,'31B','PREMIUM'),
(1,'32A','PREMIUM'), (1,'32B','PREMIUM'),
(1,'33A','PREMIUM'), (1,'33B','PREMIUM'),
(1,'34A','PREMIUM'), (1,'34B','PREMIUM'),
(1,'35A','PREMIUM'), (1,'35B','PREMIUM');

-- =========================
-- ECONOMY CLASS pour le vol 1 (50 sièges)
-- =========================
INSERT INTO siege (id_vol, numero, classe) VALUES
(1,'36A','ECONOMY'), (1,'36B','ECONOMY'),
(1,'37A','ECONOMY'), (1,'37B','ECONOMY'),
(1,'38A','ECONOMY'), (1,'38B','ECONOMY'),
(1,'39A','ECONOMY'), (1,'39B','ECONOMY'),
(1,'40A','ECONOMY'), (1,'40B','ECONOMY'),
(1,'41A','ECONOMY'), (1,'41B','ECONOMY'),
(1,'42A','ECONOMY'), (1,'42B','ECONOMY'),
(1,'43A','ECONOMY'), (1,'43B','ECONOMY'),
(1,'44A','ECONOMY'), (1,'44B','ECONOMY'),
(1,'45A','ECONOMY'), (1,'45B','ECONOMY'),
(1,'46A','ECONOMY'), (1,'46B','ECONOMY'),
(1,'47A','ECONOMY'), (1,'47B','ECONOMY'),
(1,'48A','ECONOMY'), (1,'48B','ECONOMY'),
(1,'49A','ECONOMY'), (1,'49B','ECONOMY'),
(1,'50A','ECONOMY'), (1,'50B','ECONOMY'),
(1,'51A','ECONOMY'), (1,'51B','ECONOMY'),
(1,'52A','ECONOMY'), (1,'52B','ECONOMY'),
(1,'53A','ECONOMY'), (1,'53B','ECONOMY'),
(1,'54A','ECONOMY'), (1,'54B','ECONOMY'),
(1,'55A','ECONOMY'), (1,'55B','ECONOMY'),
(1,'56A','ECONOMY'), (1,'56B','ECONOMY'),
(1,'57A','ECONOMY'), (1,'57B','ECONOMY'),
(1,'58A','ECONOMY'), (1,'58B','ECONOMY'),
(1,'59A','ECONOMY'), (1,'59B','ECONOMY'),
(1,'60A','ECONOMY'), (1,'60B','ECONOMY');

-- =========================
-- PASSAGERS (ADULTES ET ENFANTS)
-- =========================
INSERT INTO passager (nom, prenom, date_naissance, email, type_passager)
VALUES
('Rakoto', 'Jean', '1985-03-15', 'jean.rakoto@email.com', 'ADULTE'),
('Rabe', 'Marie', '1990-07-22', 'marie.rabe@email.com', 'ADULTE'),
('Randria', 'Paul', '1978-11-08', 'paul.randria@email.com', 'ADULTE'),
('Rasoa', 'Hery', '2015-05-10', 'hery.rasoa@email.com', 'ENFANT'),
('Rasolofo', 'Tiana', '2018-09-25', 'tiana.rasolofo@email.com', 'ENFANT'),
('Andria', 'Faly', '1982-01-30', 'faly.andria@email.com', 'ADULTE'),
('Razafy', 'Noro', '1995-12-05', 'noro.razafy@email.com', 'ADULTE'),
('Raharison', 'Lova', '2016-04-18', 'lova.raharison@email.com', 'ENFANT'),
('Ramanantsoa', 'Aina', '1988-08-12', 'aina.ramanantsoa@email.com', 'ADULTE'),
('Ratsimba', 'Koto', '2017-02-28', 'koto.ratsimba@email.com', 'ENFANT');

-- =========================
-- RESERVATIONS (POUR VOL 1 - VOL_INSTANCE 1)
-- =========================
-- Réservations ADULTE en ECONOMY (id_prix = 1)
INSERT INTO reservation (id_passager, id_vol_instance, id_prix, siege, statut)
VALUES
(1, 1, 1, '36A', 'CONFIRMEE'),
(2, 1, 1, '36B', 'CONFIRMEE'),
(3, 1, 1, '37A', 'CONFIRMEE'),
(6, 1, 1, '37B', 'CONFIRMEE'),
(7, 1, 1, '38A', 'CONFIRMEE'),
(9, 1, 1, '38B', 'CONFIRMEE');

-- Réservations ENFANT en ECONOMY (id_prix = 4 pour ECONOMY ENFANT)
INSERT INTO reservation (id_passager, id_vol_instance, id_prix, siege, statut)
VALUES
(4, 1, 4, '39A', 'CONFIRMEE'),
(5, 1, 4, '39B', 'CONFIRMEE'),
(8, 1, 4, '40A', 'CONFIRMEE'),
(10, 1, 4, '40B', 'CONFIRMEE');

-- Réservations ADULTE en FIRST (id_prix = 2)
INSERT INTO reservation (id_passager, id_vol_instance, id_prix, siege, statut)
VALUES
(1, 2, 5, '1A', 'CONFIRMEE'),
(2, 2, 5, '1B', 'CONFIRMEE');

-- Réservations ADULTE en PREMIUM (id_prix = 3)
INSERT INTO reservation (id_passager, id_vol_instance, id_prix, siege, statut)
VALUES
(3, 2, 6, '16A', 'CONFIRMEE');
