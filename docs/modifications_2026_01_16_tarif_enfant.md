# Modifications 2026-01-16 - Tarification Enfant et Calcul CA

## Description Fonctionnelle
- **Remise enfant** : Pour le vol Tana → Nosy Be, les enfants bénéficient d'un tarif réduit de 500 000 Ar (au lieu de 700 000 Ar) en classe économique.
- **Calcul du CA** : Permettre de calculer le Chiffre d'Affaires généré par un avion pour un vol programmé donné.

## Architecture des Tarifs

### ⚠️ IMPORTANT: Séparation des responsabilités

**1. Table `tarif_vol` (02_data.sql)**
- Contient les **tarifs de BASE** pour tous les vols et types de place
- Ces tarifs s'appliquent par défaut aux adultes
- **Toujours obligatoire** pour chaque (vol, type_place)

**2. Table `tarif_categorie` (06_tarif_enfant_ca.sql)**
- Contient **UNIQUEMENT les réductions** ou tarifs spéciaux
- Ne remplir que pour les cas avec remise (Enfant, Bébé)
- ❌ **Ne PAS dupliquer** les tarifs adulte (déjà dans tarif_vol)

### Logique de calcul du prix

```
Si tarif_categorie existe pour (vol, type_place, categorie):
    Si prix fixe défini → utiliser ce prix
    Sinon si pourcentage → tarif_adulte × pourcentage / 100
    Sinon si frais_reduction → soustraire les frais
Sinon:
    Utiliser tarif_vol (tarif adulte standard)
```

### Exemple concret

Pour TNR → Nosy Be, Économique :

| Catégorie | Où mettre ? | Valeur | Résultat |
|-----------|-------------|--------|----------|
| Adulte | `tarif_vol` uniquement | 700 000 Ar | 700 000 Ar |
| Enfant | `tarif_categorie` | prix = 500 000 Ar | 500 000 Ar (remise spéciale) |
| Bébé | `tarif_categorie` | pourcentage = 10% | 70 000 Ar (10% de 700 000) |

✅ Avantages :
- Pas de duplication de données
- Plus simple à maintenir
- Si pas de réduction → pas d'entrée nécessaire

---

# PARTIE 1 : AFFICHAGE

## 1.1 Page de Réservation de Billets (`/booking/reserve/{id}`)

### Accès
- **URL** : `/booking/reserve/{idVolProgramme}`
- **Méthode** : GET (affichage) / POST (soumission)
- **Page précédente** : `/booking/search` (Recherche de vols)
- **Page suivante** : `/booking/mes-reservations?email={email}` (Liste des réservations)

### Panneau Gauche - Détails du Vol (lecture seule)
| Champ | Type | Source |
|-------|------|--------|
| Route (codes IATA) | Texte | `VolProgramme.vol.aeroportDepart.codeIata` → `VolProgramme.vol.aeroportArrivee.codeIata` |
| Ville départ | Texte | `VolProgramme.vol.aeroportDepart.ville` |
| Ville arrivée | Texte | `VolProgramme.vol.aeroportArrivee.ville` |
| Date | Date formatée | `VolProgramme.dateHeureDepart` (format dd/MM/yyyy) |
| Heure départ | Heure | `VolProgramme.dateHeureDepart` (format HH:mm) |
| Heure arrivée | Heure | `VolProgramme.dateHeureArrivee` (format HH:mm) |
| Modèle avion | Texte | `VolProgramme.avion.modele` |
| Places disponibles | Badge numérique | Calculé via `VolProgrammeService.getPlacesDisponibles()` |

### Panneau Droit - Formulaire de Réservation

#### Champs du formulaire
| Champ | Type HTML | Obligatoire | Validation |
|-------|-----------|-------------|------------|
| Nom | `input[type=text]` | Oui | Non vide |
| Prénom | `input[type=text]` | Oui | Non vide |
| Email | `input[type=email]` | Oui | Format email valide |
| Téléphone | `input[type=tel]` | Non | - |
| Type de place | `select` (liste déroulante) | Oui | - |
| Catégorie passager | `select` (liste déroulante) | Oui | - |
| Nombre de places | `select` (liste déroulante) | Oui | 1 à min(10, placesDisponibles) |
| Total à payer | Affichage calculé | - | Mise à jour dynamique JavaScript |

#### Détail des listes déroulantes

| Liste | Source Table | Fonction d'obtention | Valeurs |
|-------|--------------|---------------------|---------|
| Type de place | `type_place` | `TypePlaceService.getAll()` | id_type_place / nom |
| Catégorie passager | `categorie_passager` | `CategoriePassagerService.getAll()` | id_categorie_passager / nom |
| Nombre de places | Généré (1 à N) | Calculé côté template | Entiers de 1 à max |

### Boutons d'action

| Bouton | Action | Fonction appelée | Tables impactées |
|--------|--------|------------------|------------------|
| **Annuler** | Retour recherche | Redirect vers `/booking` | Aucune |
| **Confirmer la réservation** | Soumettre le formulaire | `POST /booking/reserve` → `BookingController.reserve()` → `ReservationService.effectuerReservation()` | `client`, `reservation`, `detail_reservation` |

### Calcul dynamique du prix (JavaScript)
- **Déclencheur** : Changement de Type de place, Catégorie passager, ou Nombre de places
- **Fonction** : `updateTotal()`
- **Appel AJAX** : `GET /api/tarifs/calculer?idVol={id}&idTypePlace={id}&idCategorie={id}`
- **Service appelé** : `TarifService.calculerPrixPassager(idVol, idTypePlace, idCategoriePassager)`

### Flux de navigation
```
[Recherche de vols] → [Résultats (clic "Réserver")] → [Page Réservation] → [Confirmation] → [Mes réservations]
     /booking              /booking/search            /booking/reserve/{id}              /booking/mes-reservations
```

---

## 1.2 Page Calcul du CA par Vol Programmé (`/vols/ca/{id}`)

### Accès
- **URL** : `/vols/ca/{idVolProgramme}`
- **Méthode** : GET
- **Accès depuis** : Liste des vols programmés, bouton "Voir CA"

### Informations affichées

| Section | Champs | Source |
|---------|--------|--------|
| En-tête vol | Route, Date, Avion | `VolProgramme` |
| **CA par type de place** | Tableau détaillé | `v_ca_vol_programme` (Vue SQL) |
| **Total CA** | Somme | Agrégation de la vue |

### Tableau CA détaillé
| Colonne | Description |
|---------|-------------|
| Type de place | Nom du type (Première classe, Économique, Premium) |
| Catégorie | Nom de la catégorie (Adulte, Enfant) |
| Nb réservations | Nombre de places réservées |
| Prix unitaire | Tarif appliqué |
| **Sous-total** | Nb × Prix |

### Boutons
| Bouton | Action |
|--------|--------|
| Retour | Redirect `/vols/programmes` |
| Exporter PDF | Génère PDF du CA (optionnel) |

---

# PARTIE 2 : BASE DE DONNÉES

## 2.1 Nouvelles Tables

### Table `categorie_passager`
```sql
CREATE TABLE categorie_passager (
    id_categorie_passager SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,        -- 'Adulte', 'Enfant', 'Bébé'
    description VARCHAR(200),
    age_min INT DEFAULT 0,                  -- Âge minimum (inclus)
    age_max INT DEFAULT 999                 -- Âge maximum (inclus)
);
```

### Table `tarif_categorie` (remises par catégorie de passager)
```sql
CREATE TABLE tarif_categorie (
    id_tarif_categorie SERIAL PRIMARY KEY,
    id_vol INT NOT NULL,                    -- Route concernée
    id_type_place INT NOT NULL,             -- Type de place
    id_categorie_passager INT NOT NULL,     -- Catégorie (Adulte/Enfant/Bébé)
    prix NUMERIC(12,2),                     -- Prix fixe (NULL = utiliser pourcentage)
    pourcentage NUMERIC(5,2),               -- % du tarif adulte (ex: 71.43 pour enfant, 10 pour bébé)
    frais_reduction NUMERIC(12,2),          -- Frais fixes à soustraire (optionnel)
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    FOREIGN KEY (id_categorie_passager) REFERENCES categorie_passager(id_categorie_passager) ON DELETE CASCADE,
    UNIQUE (id_vol, id_type_place, id_categorie_passager)
);
```

#### Logique de calcul du prix
1. Si `prix` est défini (NOT NULL) → utiliser le prix fixe
2. Sinon, si `pourcentage` est défini → calculer : `tarif_adulte * pourcentage / 100`
3. Si `frais_reduction` est défini → soustraire : `prix_calculé - frais_reduction`
4. Le prix minimum est 0 (jamais négatif)

### Table `detail_reservation` (détail des passagers par réservation)
```sql
CREATE TABLE detail_reservation (
    id_detail_reservation SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_type_place INT NOT NULL,
    id_categorie_passager INT NOT NULL,
    prix_paye NUMERIC(12,2) NOT NULL,       -- Prix effectivement payé
    FOREIGN KEY (id_reservation) REFERENCES Reservation(id_reservation) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place),
    FOREIGN KEY (id_categorie_passager) REFERENCES categorie_passager(id_categorie_passager)
);
```

## 2.2 Nouvelle Vue

### Vue `v_ca_vol_programme` (Chiffre d'Affaires par vol programmé)
```sql
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
```

## 2.3 Données insérées

### Catégories de passagers
| Nom | Age min | Age max |
|-----|---------|---------|
| Adulte | 12 | 999 |
| Enfant | 2 | 11 |
| Bébé | 0 | 1 |

### Tarifs de base (dans `tarif_vol` - 02_data.sql)
| Vol | Type place | Prix (Adulte) |
|-----|------------|---------------|
| TNR → NOS | Première classe | 1 200 000 Ar |
| TNR → NOS | Économique | 700 000 Ar |
| TNR → NOS | Premium | 1 000 000 Ar |

### Réductions (dans `tarif_categorie` - 06_tarif_enfant_ca.sql)
| Vol | Type place | Catégorie | Prix fixe | Pourcentage | Prix calculé |
|-----|------------|-----------|-----------|-------------|--------------|
| TNR → NOS | Économique | Enfant | 500 000 Ar | NULL | **500 000 Ar** (remise spéciale) |
| TNR → NOS | Économique | Bébé | NULL | 10% | 70 000 Ar (10% × 700 000) |
| TNR → NOS | Première classe | Enfant | 900 000 Ar | NULL | 900 000 Ar |
| TNR → NOS | Première classe | Bébé | NULL | 10% | 120 000 Ar (10% × 1 200 000) |

**Note** : Les tarifs adulte ne sont PAS dans tarif_categorie, ils sont dans tarif_vol.

#### Exemples de calcul
- **Adulte** : Pas d'entrée dans tarif_categorie → utilise tarif_vol = 700 000 Ar
- **Enfant** : Entrée dans tarif_categorie avec prix fixe = 500 000 Ar
- **Bébé** : Entrée dans tarif_categorie avec pourcentage = 10% → 700 000 × 10% = 70 000 Ar

### Jeu de données test (réservations avec enfants)
Voir script `06_tarif_enfant_ca.sql` pour les données complètes.

---

# PARTIE 3 : MÉTIER (BUSINESS LOGIC)

## 3.1 Nouvelles Classes Model

### `CategoriePassager.java`
```java
@Entity
@Table(name = "categorie_passager")
public class CategoriePassager {
    Long idCategoriePassager;   // PK
    String nom;                 // 'Adulte', 'Enfant', 'Bébé'
    String description;
    Integer ageMin;
    Integer ageMax;
}
```
**Table utilisée** : `categorie_passager`

### `TarifCategorie.java`
```java
@Entity
@Table(name = "tarif_categorie")
public class TarifCategorie {
    Long idTarifCategorie;              // PK
    Vol vol;                            // FK → vol
    TypePlace typePlace;                // FK → type_place
    CategoriePassager categoriePassager; // FK → categorie_passager
    BigDecimal prix;                    // Prix fixe (nullable)
    BigDecimal pourcentage;             // % du tarif adulte (nullable)
    BigDecimal fraisReduction;          // Frais à soustraire (nullable)
}
```
**Table utilisée** : `tarif_categorie`

#### Méthode de calcul du prix effectif
```java
public BigDecimal calculerPrixEffectif(BigDecimal tarifAdulte) {
    // 1. Si prix fixe défini, l'utiliser
    if (prix != null) return prix;
    
    // 2. Sinon calculer avec pourcentage
    BigDecimal prixCalcule = tarifAdulte;
    if (pourcentage != null) {
        prixCalcule = tarifAdulte.multiply(pourcentage).divide(BigDecimal.valueOf(100));
    }
    
    // 3. Soustraire frais de réduction si définis
    if (fraisReduction != null) {
        prixCalcule = prixCalcule.subtract(fraisReduction);
    }
    
    // 4. Prix minimum = 0
    return prixCalcule.max(BigDecimal.ZERO);
}
```

### `DetailReservation.java`
```java
@Entity
@Table(name = "detail_reservation")
public class DetailReservation {
    Long idDetailReservation;           // PK
    Reservation reservation;            // FK → reservation
    TypePlace typePlace;                // FK → type_place
    CategoriePassager categoriePassager; // FK → categorie_passager
    BigDecimal prixPaye;                // Prix effectif
}
```
**Table utilisée** : `detail_reservation`

### `CAVolProgramme.java` (DTO)
```java
public class CAVolProgramme {
    Long idVolProgramme;
    String avionModele;
    String route;                       // "TNR → NOS"
    LocalDateTime dateHeureDepart;
    String typePlace;
    String categorie;
    Integer nbReservations;
    BigDecimal caTotal;
}
```
**Vue utilisée** : `v_ca_vol_programme`

## 3.2 Nouvelles Classes Service

### `CategoriePassagerService.java`
| Méthode | Signature | Retour | Tables/Vues |
|---------|-----------|--------|-------------|
| getAll | `List<CategoriePassager> getAll()` | Liste catégories | `categorie_passager` |
| getById | `Optional<CategoriePassager> getById(Long id)` | Catégorie | `categorie_passager` |

### `TarifCategorieService.java`
| Méthode | Signature | Retour | Tables/Vues |
|---------|-----------|--------|-------------|
| getAll | `List<TarifCategorie> getAll()` | Liste tarifs | `tarif_categorie` |
| getPrix | `BigDecimal getPrix(Long idVol, Long idTypePlace, Long idCategorie)` | Prix calculé | `tarif_categorie`, `tarif_vol` |
| getByVol | `List<TarifCategorie> getByVol(Long idVol)` | Tarifs d'un vol | `tarif_categorie` |
| getTarifAdulte | `BigDecimal getTarifAdulte(Long idVol, Long idTypePlace)` | Tarif adulte de référence | `tarif_categorie`, `tarif_vol` |
| calculerPrixEffectif | `BigDecimal calculerPrixEffectif(TarifCategorie tarif, BigDecimal tarifAdulte)` | Prix final | Calcul interne |

#### Logique de `getPrix()`
```java
public BigDecimal getPrix(Long idVol, Long idTypePlace, Long idCategoriePassager) {
    // 1. Récupérer le tarif adulte de référence
    BigDecimal tarifAdulte = getTarifAdulte(idVol, idTypePlace);
    
    // 2. Récupérer le tarif catégorie
    Optional<TarifCategorie> tarifCategorie = getTarifSpecifique(idVol, idTypePlace, idCategoriePassager);
    
    if (tarifCategorie.isPresent()) {
        return tarifCategorie.get().calculerPrixEffectif(tarifAdulte);
    }
    
    // 3. Fallback: utiliser tarif adulte
    return tarifAdulte;
}
```

### `DetailReservationService.java`
| Méthode | Signature | Retour | Tables/Vues |
|---------|-----------|--------|-------------|
| save | `DetailReservation save(DetailReservation detail)` | Détail sauvé | `detail_reservation` |
| getByReservation | `List<DetailReservation> getByReservation(Long idReservation)` | Détails | `detail_reservation` |

### `CAService.java` (nouveau service pour le Chiffre d'Affaires)
| Méthode | Signature | Retour | Tables/Vues |
|---------|-----------|--------|-------------|
| getCAByVolProgramme | `List<CAVolProgramme> getCAByVolProgramme(Long idVolProgramme)` | Détails CA | `v_ca_vol_programme` |
| getTotalCA | `BigDecimal getTotalCA(Long idVolProgramme)` | CA total | `v_ca_vol_programme` |
| getCAByAvionEtRoute | `List<CAVolProgramme> getCAByAvionEtRoute(Long idAvion, Long idVol)` | CA par avion/route | `v_ca_vol_programme` |

## 3.3 Modifications de Classes Existantes

### `ReservationService.java` - Méthode modifiée
```java
@Transactional
public Reservation effectuerReservation(
    Long idVolProgramme,
    String email, String nom, String prenom, String telephone,
    Long idTypePlace,              // NOUVEAU
    Long idCategoriePassager,      // NOUVEAU
    int nombrePlaces
) throws Exception
```
**Tables utilisées** : `client`, `reservation`, `detail_reservation`, `tarif_categorie`

### `BookingController.java` - Méthodes modifiées/ajoutées
| Méthode | Route | Action |
|---------|-------|--------|
| `reserveForm` | GET `/booking/reserve/{id}` | Ajoute typePlaces et categories au model |
| `reserve` | POST `/booking/reserve` | Reçoit idTypePlace et idCategoriePassager |
| `calculerPrix` | GET `/api/tarifs/calculer` | AJAX - retourne prix calculé |

## 3.4 Nouvelles Classes Repository

### `CategoriePassagerRepository.java`
```java
public interface CategoriePassagerRepository extends JpaRepository<CategoriePassager, Long> {
    Optional<CategoriePassager> findByNom(String nom);
}
```

### `TarifCategorieRepository.java`
```java
public interface TarifCategorieRepository extends JpaRepository<TarifCategorie, Long> {
    Optional<TarifCategorie> findByVolIdVolAndTypePlaceIdTypePlaceAndCategoriePassagerIdCategoriePassager(
        Long idVol, Long idTypePlace, Long idCategoriePassager);
    List<TarifCategorie> findByVolIdVol(Long idVol);
}
```

### `DetailReservationRepository.java`
```java
public interface DetailReservationRepository extends JpaRepository<DetailReservation, Long> {
    List<DetailReservation> findByReservationIdReservation(Long idReservation);
}
```

### `CAVolProgrammeRepository.java` (pour la vue)
```java
public interface CAVolProgrammeRepository {
    List<CAVolProgramme> findByIdVolProgramme(Long idVolProgramme);
    BigDecimal sumCAByIdVolProgramme(Long idVolProgramme);
}
```

---

# RÉSUMÉ DES FICHIERS À CRÉER/MODIFIER

## Nouveaux fichiers

| Type | Chemin | Description |
|------|--------|-------------|
| SQL | `src/bd/06_tarif_enfant_ca.sql` | Script complet des modifications BD |
| Model | `model/passagers/CategoriePassager.java` | Entité catégorie passager |
| Model | `model/prix/TarifCategorie.java` | Entité tarif par catégorie |
| Model | `model/reservations/DetailReservation.java` | Entité détail réservation |
| DTO | `model/vols/CAVolProgramme.java` | DTO pour le CA |
| Repository | `repository/passagers/CategoriePassagerRepository.java` | Repository catégories |
| Repository | `repository/prix/TarifCategorieRepository.java` | Repository tarifs catégories |
| Repository | `repository/reservations/DetailReservationRepository.java` | Repository détails |
| Service | `service/passagers/CategoriePassagerService.java` | Service catégories |
| Service | `service/prix/TarifCategorieService.java` | Service tarifs catégories |
| Service | `service/reservations/DetailReservationService.java` | Service détails |
| Service | `service/vols/CAService.java` | Service calcul CA |
| Controller | API dans `BookingController.java` | Endpoint calcul prix AJAX |
| Template | `templates/views/booking/ca.html` | Page affichage CA |

## Fichiers modifiés

| Fichier | Modification |
|---------|-------------|
| `ReservationService.java` | Ajout paramètres typePlace et categoriePassager |
| `BookingController.java` | Ajout listes déroulantes et endpoint AJAX |
| `reserve.html` | Ajout champs typePlace et categoriePassager + JS |
| `search.html` | Ajout bouton "CA" pour voir le chiffre d'affaires |
| `TarifVolRepository.java` | Ajout méthode findByVolIdVolAndTypePlaceIdTypePlace |

---

# TODO LIST DÉVELOPPEUR

- [x] 1. Exécuter le script SQL `06_tarif_enfant_ca.sql`
- [x] 2. Créer `CategoriePassager.java` (model)
- [x] 3. Créer `TarifCategorie.java` (model)
- [x] 4. Créer `DetailReservation.java` (model)
- [x] 5. Créer `CAVolProgramme.java` (DTO)
- [x] 6. Créer les 4 Repository
- [x] 7. Créer `CategoriePassagerService.java`
- [x] 8. Créer `TarifCategorieService.java`
- [x] 9. Créer `DetailReservationService.java`
- [x] 10. Créer `CAService.java`
- [x] 11. Modifier `ReservationService.effectuerReservation()`
- [x] 12. Modifier `BookingController.java`
- [x] 13. Modifier `reserve.html`
- [x] 14. Créer `ca.html` (page CA)
- [ ] 15. Tester la réservation avec tarif enfant
- [ ] 16. Tester le calcul du CA

> **Note**: Pour finaliser, exécuter le script SQL sur la base de données PostgreSQL.
