# Modifications 2026-01-16 - Tarification Enfant et Calcul CA

## Description Fonctionnelle
- **Remise enfant** : Pour le vol Tana → Nosy Be, les enfants bénéficient d'un tarif réduit de 500 000 Ar (au lieu de 700 000 Ar) en classe économique.
- **Calcul du CA** : Permettre de calculer le Chiffre d'Affaires généré par un avion pour un vol programmé donné.

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
    id_categorie_passager INT NOT NULL,     -- Catégorie (Adulte/Enfant)
    prix NUMERIC(12,2) NOT NULL,            -- Prix spécifique
    FOREIGN KEY (id_vol) REFERENCES Vol(id_vol) ON DELETE CASCADE,
    FOREIGN KEY (id_type_place) REFERENCES type_place(id_type_place) ON DELETE CASCADE,
    FOREIGN KEY (id_categorie_passager) REFERENCES categorie_passager(id_categorie_passager) ON DELETE CASCADE,
    UNIQUE (id_vol, id_type_place, id_categorie_passager)
);
```

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

### Tarifs catégories (Vol TNR → Nosy Be, Économique)
| Vol | Type place | Catégorie | Prix |
|-----|------------|-----------|------|
| TNR → NOS | Économique | Adulte | 700 000 Ar |
| TNR → NOS | Économique | Enfant | **500 000 Ar** |
| TNR → NOS | Économique | Bébé | 0 Ar (gratuit) |
| NOS → TNR | Économique | Adulte | 700 000 Ar |
| NOS → TNR | Économique | Enfant | **500 000 Ar** |

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
    BigDecimal prix;
}
```
**Table utilisée** : `tarif_categorie`

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
| getPrix | `BigDecimal getPrix(Long idVol, Long idTypePlace, Long idCategorie)` | Prix applicable | `tarif_categorie` |
| getByVol | `List<TarifCategorie> getByVol(Long idVol)` | Tarifs d'un vol | `tarif_categorie` |

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

## Fichiers modifiés

| Fichier | Modification |
|---------|-------------|
| `ReservationService.java` | Ajout paramètres typePlace et categoriePassager |
| `BookingController.java` | Ajout listes déroulantes et endpoint AJAX |
| `reserve.html` | Ajout champs typePlace et categoriePassager + JS |

---

# TODO LIST DÉVELOPPEUR

- [ ] 1. Exécuter le script SQL `06_tarif_enfant_ca.sql`
- [ ] 2. Créer `CategoriePassager.java` (model)
- [ ] 3. Créer `TarifCategorie.java` (model)
- [ ] 4. Créer `DetailReservation.java` (model)
- [ ] 5. Créer `CAVolProgramme.java` (DTO)
- [ ] 6. Créer les 4 Repository
- [ ] 7. Créer `CategoriePassagerService.java`
- [ ] 8. Créer `TarifCategorieService.java`
- [ ] 9. Créer `DetailReservationService.java`
- [ ] 10. Créer `CAService.java`
- [ ] 11. Modifier `ReservationService.effectuerReservation()`
- [ ] 12. Modifier `BookingController.java`
- [ ] 13. Modifier `reserve.html`
- [ ] 14. Tester la réservation avec tarif enfant
- [ ] 15. Tester le calcul du CA
