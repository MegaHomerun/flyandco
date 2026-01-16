\c pg10;

UPDATE tarif_vol 
SET prix = 800000  -- Nouveau prix: 1 100 000 Ar
WHERE id_vol IN (1, 2) AND id_type_place = 2;

-- 1-1ere classe
-- 2-econom
-- 3-premium