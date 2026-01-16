-- =========================
-- SCRIPT 05: VUES POUR PRIX DIFFERENCIES
-- =========================

-- =========================
-- VUE POUR AFFICHER LES PRIX AVEC TYPE DE PASSAGER
-- =========================
CREATE OR REPLACE VIEW view_prix_detailles AS
SELECT 
    pv.id_prix,
    v.id_vol,
    c.nom AS compagnie,
    ad.ville AS ville_depart,
    aa.ville AS ville_arrivee,
    pv.classe,
    pv.type_passager,
    pv.prix,
    pv.date_maj
FROM prix_vol pv
JOIN vol v ON pv.id_vol = v.id_vol
JOIN compagnie c ON v.id_compagnie = c.id_compagnie
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
ORDER BY v.id_vol, pv.classe, pv.type_passager;

-- =========================
-- VUE POUR CALCULER LE PRIX REEL D'UN VOL
-- =========================
-- Cette vue calcule le prix total d'un vol instance en sommant
-- les prix de tous les sièges occupés en tenant compte du type de passager
CREATE OR REPLACE VIEW view_prix_reel_vol AS
SELECT 
    vi.id_vol_instance,
    v.id_vol,
    c.nom AS compagnie,
    ad.ville AS ville_depart,
    aa.ville AS ville_arrivee,
    vi.date_depart,
    vi.date_arrivee,
    COUNT(r.id_reservation) AS nombre_passagers,
    COUNT(CASE WHEN p.type_passager = 'ADULTE' THEN 1 END) AS nombre_adultes,
    COUNT(CASE WHEN p.type_passager = 'ENFANT' THEN 1 END) AS nombre_enfants,
    SUM(pv.prix) AS prix_total_vol
FROM vol_instance vi
JOIN vol v ON vi.id_vol = v.id_vol
JOIN compagnie c ON v.id_compagnie = c.id_compagnie
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
LEFT JOIN reservation r ON vi.id_vol_instance = r.id_vol_instance
LEFT JOIN passager p ON r.id_passager = p.id_passager
LEFT JOIN prix_vol pv ON r.id_prix = pv.id_prix
GROUP BY vi.id_vol_instance, v.id_vol, c.nom, ad.ville, aa.ville, vi.date_depart, vi.date_arrivee
ORDER BY vi.date_depart, v.id_vol;
