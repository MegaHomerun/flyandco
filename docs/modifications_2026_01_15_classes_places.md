# Documentation des Modifications - Classes de Places et Tarification
**Date:** 2026-01-15

---

## 1. AFFICHAGE (Interface Utilisateur)

### 1.1 Page: Valeur Maximale par Avion
**URL:** `/valeur-max`

**Description:** Cette page affiche le revenu potentiel maximum qu'un avion peut générer pour un vol donné, basé sur la configuration des places (première classe/économique) et les tarifs associés.

#### Formulaire de Filtres

| Champ | Type | Description | Source des données |
|-------|------|-------------|-------------------|
| Route (Vol) | Liste déroulante | Sélection d'une route (ex: TNR → NOS) | Table `Vol` via `VolService.getAll()` → affiche `vol.displayName` |
| Avion | Liste déroulante | Sélection d'un avion | Table `Avion` via `AvionService.getAllAvions()` → affiche `avion.displayName` |

#### Boutons

| Bouton | Action | Fonction appelée | Tables utilisées |
|--------|--------|------------------|-----------------|
| **Filtrer** | Soumet le formulaire GET vers `/valeur-max` avec les paramètres `idVol` et `idAvion` | `ValeurMaxController.valeurMax()` | `avion`, `avion_place`, `type_place`, `vol`, `tarif_vol`, `aeroport` |
| **Réinitialiser** | Redirige vers `/valeur-max` sans paramètres | - | - |

#### Tableau des Résultats

| Colonne | Description | Champ DTO |
|---------|-------------|-----------|
| Avion | Modèle + immatriculation (ex: "Boeing 737-800 (5R-MJB)") | `ValeurMaxAvionVol.avionDisplay` |
| Route | Départ → Arrivée (ex: "TNR → NOS") | `ValeurMaxAvionVol.route` |
| Valeur Maximale | Montant formaté (ex: "54 200 000 Ar") | `ValeurMaxAvionVol.valeurMaxFormatee` |

#### Flux de Navigation

```
Sidebar → Gestion → Valeur Max Avions → Page /valeur-max
                                            ↓
                                    Sélection filtres (optionnel)
                                            ↓
                                    Clic "Filtrer"
                                            ↓
                                    Affichage résultats filtrés
```

---

## 2. BASE DE DONNÉES

### 2.1 Nouvelles Tables

#### Table `type_place`
**Description:** Types de places disponibles (Première classe, Économique)

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id_type_place` | SERIAL | PRIMARY KEY | Identifiant unique |
| `nom` | VARCHAR(50) | NOT NULL, UNIQUE | Nom du type (ex: "Première classe") |
| `description` | VARCHAR(200) | - | Description du type |

#### Table `avion_place`
**Description:** Configuration du nombre de places par type pour chaque avion

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id_avion_place` | SERIAL | PRIMARY KEY | Identifiant unique |
| `id_avion` | INT | FK → Avion, NOT NULL | Référence à l'avion |
| `id_type_place` | INT | FK → type_place, NOT NULL | Référence au type de place |
| `nombre_places` | INT | NOT NULL | Nombre de places de ce type |

**Contrainte:** UNIQUE(`id_avion`, `id_type_place`)

#### Table `tarif_vol`
**Description:** Tarifs par type de place pour chaque route (vol)

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id_tarif_vol` | SERIAL | PRIMARY KEY | Identifiant unique |
| `id_vol` | INT | FK → Vol, NOT NULL | Référence à la route |
| `id_type_place` | INT | FK → type_place, NOT NULL | Référence au type de place |
| `prix` | NUMERIC(12,2) | NOT NULL | Prix en Ariary |

**Contrainte:** UNIQUE(`id_vol`, `id_type_place`)

### 2.2 Vue

#### Vue `v_valeur_max_avion_vol`
**Description:** Calcule la valeur maximale pour chaque combinaison avion/vol

```sql
SELECT 
    a.id_avion, a.modele, a.numero_immatriculation,
    v.id_vol, ad.code_iata AS depart, aa.code_iata AS arrivee,
    SUM(ap.nombre_places * tv.prix) AS valeur_max
FROM avion a
JOIN avion_place ap ON a.id_avion = ap.id_avion
JOIN type_place tp ON ap.id_type_place = tp.id_type_place
CROSS JOIN vol v
JOIN tarif_vol tv ON v.id_vol = tv.id_vol AND tp.id_type_place = tv.id_type_place
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport
GROUP BY a.id_avion, a.modele, a.numero_immatriculation, v.id_vol, ad.code_iata, aa.code_iata
```

### 2.3 Données Initiales

#### Types de places
| ID | Nom | Description |
|----|-----|-------------|
| 1 | Première classe | Sièges spacieux avec service premium |
| 2 | Économique | Sièges standards |

#### Configuration des avions

| Avion | Première classe | Économique | Total |
|-------|-----------------|------------|-------|
| ATR 72-600 | 10 | 60 | 70 |
| Boeing 737-800 | 20 | 160 | 180 |
| Airbus A320 | 16 | 134 | 150 |
| ATR 42-500 | 8 | 40 | 48 |
| Embraer E190 | 12 | 88 | 100 |

#### Tarifs TNR ↔ Nosy Be

| Type | Prix |
|------|------|
| Première classe | 1 200 000 Ar |
| Économique | 700 000 Ar |

### 2.4 Script SQL
**Fichier:** `src/bd/03_classes_places.sql`

---

## 3. MÉTIER (Backend)

### 3.1 Classes (Entités)

#### `TypePlace`
**Package:** `com.fly.andco.model.places`  
**Table:** `type_place`

| Attribut | Type | Colonne |
|----------|------|---------|
| idTypePlace | Long | id_type_place |
| nom | String | nom |
| description | String | description |

#### `AvionPlace`
**Package:** `com.fly.andco.model.places`  
**Table:** `avion_place`

| Attribut | Type | Colonne |
|----------|------|---------|
| idAvionPlace | Long | id_avion_place |
| avion | Avion | id_avion (FK) |
| typePlace | TypePlace | id_type_place (FK) |
| nombrePlaces | Integer | nombre_places |

#### `TarifVol`
**Package:** `com.fly.andco.model.places`  
**Table:** `tarif_vol`

| Attribut | Type | Colonne |
|----------|------|---------|
| idTarifVol | Long | id_tarif_vol |
| vol | Vol | id_vol (FK) |
| typePlace | TypePlace | id_type_place (FK) |
| prix | BigDecimal | prix |

**Méthodes utiles:**
- `getPrixFormate(): String` → Retourne le prix formaté (ex: "1 200 000 Ar")

#### `ValeurMaxAvionVol` (DTO)
**Package:** `com.fly.andco.model.places`  
**Table:** Aucune (résultat de requête SQL)

| Attribut | Type |
|----------|------|
| idAvion | Long |
| modele | String |
| numeroImmatriculation | String |
| idVol | Long |
| depart | String |
| arrivee | String |
| valeurMax | BigDecimal |

**Méthodes utiles:**
- `getRoute(): String` → "TNR → NOS"
- `getValeurMaxFormatee(): String` → "54 200 000 Ar"
- `getAvionDisplay(): String` → "Boeing 737-800 (5R-MJB)"

### 3.2 Repositories

#### `TypePlaceRepository`
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Retour | Tables |
|---------|-----------|--------|--------|
| findAll | `findAll()` | `List<TypePlace>` | type_place |

#### `AvionPlaceRepository`
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Retour | Tables |
|---------|-----------|--------|--------|
| findByAvionIdAvion | `findByAvionIdAvion(Long idAvion)` | `List<AvionPlace>` | avion_place |
| findPlacesByAvion | `findPlacesByAvion(Long idAvion)` | `List<AvionPlace>` | avion_place, type_place |

#### `TarifVolRepository`
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Retour | Tables |
|---------|-----------|--------|--------|
| findByVolIdVol | `findByVolIdVol(Long idVol)` | `List<TarifVol>` | tarif_vol |
| findTarifsByVol | `findTarifsByVol(Long idVol)` | `List<TarifVol>` | tarif_vol, type_place |

#### `ValeurMaxAvionVolRepository`
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Retour | Tables |
|---------|-----------|--------|--------|
| findAll | `findAll()` | `List<ValeurMaxAvionVol>` | avion, avion_place, type_place, vol, tarif_vol, aeroport |
| findByVol | `findByVol(Long idVol)` | `List<ValeurMaxAvionVol>` | avion, avion_place, type_place, vol, tarif_vol, aeroport |
| findByAvion | `findByAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | avion, avion_place, type_place, vol, tarif_vol, aeroport |
| findByAvionAndVol | `findByAvionAndVol(Long idAvion, Long idVol)` | `ValeurMaxAvionVol` | avion, avion_place, type_place, vol, tarif_vol, aeroport |

### 3.3 Services

#### `PlaceService`
**Package:** `com.fly.andco.service.places`

| Méthode | Signature | Retour | Description |
|---------|-----------|--------|-------------|
| getAllTypePlaces | `getAllTypePlaces()` | `List<TypePlace>` | Tous les types de places |
| getPlacesByAvion | `getPlacesByAvion(Long idAvion)` | `List<AvionPlace>` | Configuration places d'un avion |
| getTarifsByVol | `getTarifsByVol(Long idVol)` | `List<TarifVol>` | Tarifs d'une route |
| getAllValeurMax | `getAllValeurMax()` | `List<ValeurMaxAvionVol>` | Valeur max tous avions/vols |
| getValeurMaxByVol | `getValeurMaxByVol(Long idVol)` | `List<ValeurMaxAvionVol>` | Valeur max pour un vol |
| getValeurMaxByAvion | `getValeurMaxByAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | Valeur max pour un avion |
| getValeurMaxByAvionAndVol | `getValeurMaxByAvionAndVol(Long idAvion, Long idVol)` | `ValeurMaxAvionVol` | Valeur max avion+vol spécifique |

### 3.4 Contrôleurs

#### `ValeurMaxController`
**Package:** `com.fly.andco.controller.places`

| Endpoint | Méthode HTTP | Signature | Vue | Services utilisés |
|----------|--------------|-----------|-----|-------------------|
| `/valeur-max` | GET | `valeurMax(Long idVol, Long idAvion, Model)` | views/places/valeur-max | PlaceService, AvionService, VolService |

**Paramètres GET:**
- `idVol` (optionnel): Filtre par route
- `idAvion` (optionnel): Filtre par avion

**Attributs du modèle:**
- `resultats`: Liste de ValeurMaxAvionVol
- `avions`: Liste d'Avion (pour le filtre)
- `vols`: Liste de Vol (pour le filtre)
- `idVolSelected`: ID vol sélectionné
- `idAvionSelected`: ID avion sélectionné

---

## 4. FICHIERS CRÉÉS/MODIFIÉS

### Nouveaux fichiers

| Fichier | Type |
|---------|------|
| `src/bd/03_classes_places.sql` | Script SQL |
| `src/main/java/com/fly/andco/model/places/TypePlace.java` | Entité |
| `src/main/java/com/fly/andco/model/places/AvionPlace.java` | Entité |
| `src/main/java/com/fly/andco/model/places/TarifVol.java` | Entité |
| `src/main/java/com/fly/andco/model/places/ValeurMaxAvionVol.java` | DTO |
| `src/main/java/com/fly/andco/repository/places/TypePlaceRepository.java` | Repository |
| `src/main/java/com/fly/andco/repository/places/AvionPlaceRepository.java` | Repository |
| `src/main/java/com/fly/andco/repository/places/TarifVolRepository.java` | Repository |
| `src/main/java/com/fly/andco/repository/places/ValeurMaxAvionVolRepository.java` | Repository |
| `src/main/java/com/fly/andco/service/places/PlaceService.java` | Service |
| `src/main/java/com/fly/andco/controller/places/ValeurMaxController.java` | Contrôleur |
| `src/main/resources/templates/views/places/valeur-max.html` | Template Thymeleaf |

### Fichiers modifiés

| Fichier | Modification |
|---------|--------------|
| `src/main/resources/templates/fragments/sidebar.html` | Ajout lien "Valeur Max Avions" |
