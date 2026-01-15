# Modifications - Gestion des places et valeurs maximales
**Date:** 15 janvier 2026

---

## 1. AFFICHAGES

### 1.1 Page Liste des avions
**URL:** `/avions`  
**Template:** `views/avions/list.html`

#### Tableau principal
| Colonne | Type | Source |
|---------|------|--------|
| ID | Texte | `Avion.idAvion` |
| Modèle | Texte | `Avion.modele` |
| Immatriculation | Texte | `Avion.numeroImmatriculation` |
| Première classe | Nombre | `ConfigurationAvion.nombrePlaces` filtré par `TypePlace.nom = 'Première classe'` |
| Économique | Nombre | `ConfigurationAvion.nombrePlaces` filtré par `TypePlace.nom = 'Économique'` |
| Capacité totale | Nombre | `Avion.capacite` |
| Actions | Bouton | Lien vers détail valeur maximale |

#### Boutons
| Bouton | Action | URL cible |
|--------|--------|-----------|
| Valeurs maximales | Affiche toutes les valeurs maximales | `/avions/valeurs-maximales` |
| Valeur max (par ligne) | Affiche la valeur max pour un avion | `/avions/{id}/valeur-maximale` |

#### Données chargées
- `AvionController.listAvions()` → `AvionService.getAllAvions()`
- `PlaceService.getAllConfigurations()`

---

### 1.2 Page Valeur maximale d'un avion
**URL:** `/avions/{id}/valeur-maximale`  
**Template:** `views/avions/valeur-maximale.html`

#### Section Configuration des places
Affiche des cartes avec le nombre de places par type:
- Titre: `TypePlace.nom`
- Valeur: `ConfigurationAvion.nombrePlaces`

#### Tableau Valeur maximale par route
| Colonne | Type | Source |
|---------|------|--------|
| Route | Texte | `ValeurMaxAvionVol.depart` + " → " + `ValeurMaxAvionVol.arrivee` |
| Valeur maximale | Badge | `ValeurMaxAvionVol.valeurMaximaleFormatee` (format: "X XXX XXX Ar") |

#### Boutons
| Bouton | Action | URL cible |
|--------|--------|-----------|
| Retour à la liste | Retour liste avions | `/avions` |

#### Données chargées
- `AvionController.valeurMaximale(id)`:
  - `AvionService.getAvionById(id)`
  - `AvionService.getConfigurationAvion(id)`
  - `AvionService.getValeursMaximalesByAvion(id)`

---

### 1.3 Page Toutes les valeurs maximales
**URL:** `/avions/valeurs-maximales`  
**Template:** `views/avions/valeurs-maximales.html`

#### Tableau principal
| Colonne | Type | Source |
|---------|------|--------|
| Avion | Texte | `ValeurMaxAvionVol.modele` |
| Immatriculation | Texte | `ValeurMaxAvionVol.numeroImmatriculation` |
| Route | Texte | `ValeurMaxAvionVol.route` |
| Valeur maximale | Badge | `ValeurMaxAvionVol.valeurMaximaleFormatee` |

#### Boutons
| Bouton | Action | URL cible |
|--------|--------|-----------|
| Retour à la liste | Retour liste avions | `/avions` |

#### Données chargées
- `AvionController.toutesValeursMaximales()`:
  - `AvionService.getAllValeursMaximales()`

---

### 1.4 Flux des pages

```
/avions (Liste des avions)
    │
    ├──> /avions/valeurs-maximales (Toutes les valeurs max)
    │         │
    │         └──> /avions (Retour)
    │
    └──> /avions/{id}/valeur-maximale (Valeur max d'un avion)
              │
              └──> /avions (Retour)
```

---

## 2. BASE DE DONNÉES

### 2.1 Nouvelles tables

#### Table `type_place`
| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_type_place | SERIAL | PRIMARY KEY | Identifiant unique |
| nom | VARCHAR(50) | NOT NULL, UNIQUE | Nom du type (Première classe, Économique) |
| description | VARCHAR(200) | | Description détaillée |

#### Table `configuration_avion`
| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_configuration | SERIAL | PRIMARY KEY | Identifiant unique |
| id_avion | INT | NOT NULL, FK → Avion | Référence vers l'avion |
| id_type_place | INT | NOT NULL, FK → type_place | Référence vers le type de place |
| nombre_places | INT | NOT NULL | Nombre de places de ce type |

**Contrainte unique:** (id_avion, id_type_place)

#### Table `prix_vol`
| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_prix_vol | SERIAL | PRIMARY KEY | Identifiant unique |
| id_vol | INT | NOT NULL, FK → Vol | Référence vers le vol/route |
| id_type_place | INT | NOT NULL, FK → type_place | Référence vers le type de place |
| prix | NUMERIC(12,2) | NOT NULL | Prix en Ariary |

**Contrainte unique:** (id_vol, id_type_place)

### 2.2 Vues créées

#### Vue `v_valeur_max_avion_vol`
Calcule la valeur maximale qu'un avion peut générer pour chaque vol.

| Colonne | Description |
|---------|-------------|
| id_avion | ID de l'avion |
| modele | Modèle de l'avion |
| numero_immatriculation | Immatriculation |
| id_vol | ID du vol/route |
| depart | Code IATA aéroport départ |
| arrivee | Code IATA aéroport arrivée |
| valeur_maximale | Somme(nombre_places × prix) par type |

#### Vue `v_configuration_avion`
Affiche la configuration détaillée de chaque avion.

| Colonne | Description |
|---------|-------------|
| id_avion | ID de l'avion |
| modele | Modèle de l'avion |
| numero_immatriculation | Immatriculation |
| capacite_totale | Capacité totale |
| id_type_place | ID du type de place |
| type_place | Nom du type de place |
| nombre_places | Nombre de places de ce type |

### 2.3 Données insérées

#### Types de places
| ID | Nom | Description |
|----|-----|-------------|
| 1 | Première classe | Places avec plus d'espace, repas premium |
| 2 | Économique | Places standard |

#### Prix pour TNR ↔ Nosy Be
| Type | Prix |
|------|------|
| Première classe | 1 200 000 Ar |
| Économique | 700 000 Ar |

### 2.4 Script SQL
**Fichier:** `src/bd/03_places.sql`

---

## 3. MÉTIER

### 3.1 Entités (Models)

#### TypePlace
**Package:** `com.fly.andco.model.places`  
**Table:** `type_place`

| Attribut | Type | Description |
|----------|------|-------------|
| idTypePlace | Long | ID (généré) |
| nom | String | Nom du type |
| description | String | Description |

#### ConfigurationAvion
**Package:** `com.fly.andco.model.places`  
**Table:** `configuration_avion`

| Attribut | Type | Description |
|----------|------|-------------|
| idConfiguration | Long | ID (généré) |
| avion | Avion | Relation ManyToOne |
| typePlace | TypePlace | Relation ManyToOne |
| nombrePlaces | int | Nombre de places |

#### PrixVol
**Package:** `com.fly.andco.model.places`  
**Table:** `prix_vol`

| Attribut | Type | Description |
|----------|------|-------------|
| idPrixVol | Long | ID (généré) |
| vol | Vol | Relation ManyToOne |
| typePlace | TypePlace | Relation ManyToOne |
| prix | BigDecimal | Prix en Ariary |

#### ValeurMaxAvionVol (DTO)
**Package:** `com.fly.andco.model.places`

| Attribut | Type | Description |
|----------|------|-------------|
| idAvion | Long | ID de l'avion |
| modele | String | Modèle |
| numeroImmatriculation | String | Immatriculation |
| idVol | Long | ID du vol |
| depart | String | Code IATA départ |
| arrivee | String | Code IATA arrivée |
| valeurMaximale | BigDecimal | Valeur calculée |

**Méthodes utilitaires:**
- `getRoute()` → String: Retourne "depart → arrivee"
- `getValeurMaximaleFormatee()` → String: Retourne "X XXX XXX Ar"

### 3.2 Repositories

#### TypePlaceRepository
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Description |
|---------|-----------|-------------|
| findAll | `List<TypePlace> findAll()` | JPA standard |

#### ConfigurationAvionRepository
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Tables | Description |
|---------|-----------|--------|-------------|
| findByAvionId | `List<ConfigurationAvion> findByAvionId(Long idAvion)` | configuration_avion | Config d'un avion |
| findAllWithDetails | `List<ConfigurationAvion> findAllWithDetails()` | configuration_avion, avion, type_place | Toutes les configs avec détails |

#### PrixVolRepository
**Package:** `com.fly.andco.repository.places`

| Méthode | Signature | Tables | Description |
|---------|-----------|--------|-------------|
| findByVolId | `List<PrixVol> findByVolId(Long idVol)` | prix_vol | Prix d'un vol |
| findAllWithDetails | `List<PrixVol> findAllWithDetails()` | prix_vol, vol, aeroport, type_place | Tous les prix avec détails |

### 3.3 Services

#### PlaceService
**Package:** `com.fly.andco.service.places`

| Méthode | Signature | Retour | Tables/Vues | Description |
|---------|-----------|--------|-------------|-------------|
| getAllTypePlaces | `getAllTypePlaces()` | `List<TypePlace>` | type_place | Tous les types de places |
| getAllConfigurations | `getAllConfigurations()` | `List<ConfigurationAvion>` | configuration_avion, avion, type_place | Toutes les configurations |
| getConfigurationByAvion | `getConfigurationByAvion(Long idAvion)` | `List<ConfigurationAvion>` | configuration_avion | Configuration d'un avion |
| getAllPrixVols | `getAllPrixVols()` | `List<PrixVol>` | prix_vol, vol, aeroport, type_place | Tous les prix |
| getPrixByVol | `getPrixByVol(Long idVol)` | `List<PrixVol>` | prix_vol | Prix d'un vol |
| calculerValeurMaximale | `calculerValeurMaximale(Long idAvion, Long idVol)` | `BigDecimal` | configuration_avion, prix_vol | Calcul valeur max |
| getValeursMaximales | `getValeursMaximales()` | `List<ValeurMaxAvionVol>` | avion, vol, configuration_avion, prix_route, aeroport | Toutes les valeurs max |
| getValeursMaximalesByAvion | `getValeursMaximalesByAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | avion, vol, configuration_avion, prix_route, aeroport | Valeurs max d'un avion |

#### AvionService (modifié)
**Package:** `com.fly.andco.service.avions`

| Méthode | Signature | Retour | Description |
|---------|-----------|--------|-------------|
| getAllAvions | `getAllAvions()` | `List<Avion>` | Tous les avions |
| getAvionById | `getAvionById(Long id)` | `Optional<Avion>` | Un avion par ID |
| getConfigurationAvion | `getConfigurationAvion(Long idAvion)` | `List<ConfigurationAvion>` | Configuration d'un avion |
| getValeursMaximalesByAvion | `getValeursMaximalesByAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | Valeurs max d'un avion |
| getAllValeursMaximales | `getAllValeursMaximales()` | `List<ValeurMaxAvionVol>` | Toutes les valeurs max |

### 3.4 Contrôleurs

#### AvionController (modifié)
**Package:** `com.fly.andco.controller.avions`

| Endpoint | Méthode HTTP | Méthode Java | Services appelés | Vue |
|----------|--------------|--------------|------------------|-----|
| /avions | GET | listAvions | AvionService.getAllAvions(), PlaceService.getAllConfigurations() | views/avions/list |
| /avions/{id}/valeur-maximale | GET | valeurMaximale | AvionService.getAvionById(), getConfigurationAvion(), getValeursMaximalesByAvion() | views/avions/valeur-maximale |
| /avions/valeurs-maximales | GET | toutesValeursMaximales | AvionService.getAllValeursMaximales() | views/avions/valeurs-maximales |

---

## 4. TODO POUR LE DÉVELOPPEUR

### Base de données
- [ ] Exécuter le script `src/bd/03_places.sql` après `00_script_new.sql`

### Tests à effectuer
- [ ] Vérifier que la liste des avions affiche bien les places première classe et économique
- [ ] Vérifier que le calcul de valeur maximale est correct:
  - ATR 72-600 sur TNR→NOS: (10 × 1 200 000) + (60 × 700 000) = 54 000 000 Ar
  - Boeing 737-800 sur TNR→NOS: (20 × 1 200 000) + (160 × 700 000) = 136 000 000 Ar
- [ ] Vérifier la navigation entre les pages

### Fichiers créés
- `src/bd/03_places.sql`
- `src/main/java/com/fly/andco/model/places/TypePlace.java`
- `src/main/java/com/fly/andco/model/places/ConfigurationAvion.java`
- `src/main/java/com/fly/andco/model/places/PrixVol.java`
- `src/main/java/com/fly/andco/model/places/ValeurMaxAvionVol.java`
- `src/main/java/com/fly/andco/model/places/ConfigurationAvionDTO.java`
- `src/main/java/com/fly/andco/repository/places/TypePlaceRepository.java`
- `src/main/java/com/fly/andco/repository/places/ConfigurationAvionRepository.java`
- `src/main/java/com/fly/andco/repository/places/PrixVolRepository.java`
- `src/main/java/com/fly/andco/service/places/PlaceService.java`
- `src/main/resources/templates/views/avions/valeur-maximale.html`
- `src/main/resources/templates/views/avions/valeurs-maximales.html`

### Fichiers modifiés
- `src/main/java/com/fly/andco/service/avions/AvionService.java`
- `src/main/java/com/fly/andco/controller/avions/AvionController.java`
- `src/main/resources/templates/views/avions/list.html`
