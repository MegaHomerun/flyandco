# À FAIRE GLOBAL - FlyAndCo
## Système de Gestion de Compagnie Aérienne
**Date de création:** 30 janvier 2026

---

# SOMMAIRE

1. [MCD Complet](#1-mcd-complet)
2. [Liste des Classes Complètes](#2-liste-des-classes-complètes)
3. [Fonctionnalités Détaillées](#3-fonctionnalités-détaillées)
   - F1: Réservation de billets
   - F2: Gestion des classes de places et tarifs
   - F3: Valeur maximale d'un avion pour un vol
   - F4: Tarification par catégorie (Adulte/Enfant/Bébé)
   - F5: Calcul du CA par vol programmé
   - F6: Diffusion publicitaire
   - F7: Gestion des produits extra
   - F8: CA mensuel global

---

# 1. MCD COMPLET

## 1.1 Diagramme Entité-Association

```
┌─────────────────┐                      ┌─────────────────┐
│   Utilisateur   │                      │    Aeroport     │
├─────────────────┤                      ├─────────────────┤
│ id_utilisateur  │                      │ id_aeroport     │
│ username        │                      │ nom             │
│ mot_de_passe    │                      │ ville           │
│ role            │                      │ pays            │
└─────────────────┘                      │ code_iata       │
                                         │ code_icao       │
                                         └────────┬────────┘
                                                  │
                        ┌─────────────────────────┼─────────────────────────┐
                        │ (0,n)                   │                   (0,n) │
                        ▼                         │                         ▼
              id_aeroport_depart                  │            id_aeroport_arrivee
                        │                         │                         │
                        └─────────────┬───────────┴─────────────────────────┘
                                      │
                                      ▼
                             ┌─────────────────┐
                             │       Vol       │ (Route)
                             ├─────────────────┤
                             │ id_vol          │
                             │ id_aeroport_dep │
                             │ id_aeroport_arr │
                             └────────┬────────┘
                                      │ (1,1)
                     ┌────────────────┼────────────────────────┐
                     │                │                        │
                     ▼ (0,n)          ▼ (0,n)                  ▼ (0,n)
           ┌─────────────────┐ ┌─────────────────┐   ┌─────────────────┐
           │   tarif_vol     │ │ tarif_categorie │   │ vol_programme   │
           ├─────────────────┤ ├─────────────────┤   ├─────────────────┤
           │ id_tarif_vol    │ │ id_tarif_categ  │   │ id_vol_programme│
           │ id_vol (FK)     │ │ id_vol (FK)     │   │ id_vol (FK)     │
           │ id_type_place   │ │ id_type_place   │   │ id_avion (FK)   │
           │ prix            │ │ id_categ_pass   │   │ date_heure_dep  │
           └────────┬────────┘ │ prix/pourcentage│   │ date_heure_arr  │
                    │          └────────┬────────┘   │ statut          │
                    │                   │            └────────┬────────┘
                    ▼ (1,1)             ▼ (1,1)               │
           ┌─────────────────┐ ┌─────────────────┐            │
           │   type_place    │ │categorie_passag │            │
           ├─────────────────┤ ├─────────────────┤            │
           │ id_type_place   │ │ id_categ_pass   │            │
           │ nom             │ │ nom             │            │
           │ description     │ │ age_min/age_max │            │
           └────────┬────────┘ └─────────────────┘            │
                    │                                         │
                    ▼ (0,n)                                   │
           ┌─────────────────┐                                │
           │   avion_place   │◄───────────────────────────────┤
           ├─────────────────┤                                │
           │ id_avion_place  │                                │
           │ id_avion (FK)   │                                ▼ (0,n)
           │ id_type_place   │                       ┌─────────────────┐
           │ nombre_places   │                       │   Reservation   │
           └────────┬────────┘                       ├─────────────────┤
                    │                                │ id_reservation  │
                    ▼ (1,1)                          │ id_client (FK)  │
           ┌─────────────────┐                       │ id_vol_prog (FK)│
           │      Avion      │                       │ nombre_places   │
           ├─────────────────┤                       │ date_reservation│
           │ id_avion        │                       │ statut          │
           │ modele          │                       └────────┬────────┘
           │ capacite        │                                │ (1,n)
           │ numero_immat    │                                ▼
           └─────────────────┘                       ┌─────────────────┐
                                                     │detail_reservation│
                                                     ├─────────────────┤
                                                     │ id_detail_res   │
                     ┌─────────────────┐             │ id_reservation  │
                     │     Client      │◄────────────│ id_type_place   │
                     ├─────────────────┤             │ id_categ_pass   │
                     │ id_client       │             │ prix_paye       │
                     │ nom             │             └─────────────────┘
                     │ prenom          │
                     │ email           │
                     │ telephone       │
                     └─────────────────┘


═══════════════════════════════════════════════════════════════════════════════
                          PARTIE DIFFUSIONS PUBLICITAIRES
═══════════════════════════════════════════════════════════════════════════════

┌─────────────────────┐                    ┌─────────────────────┐
│ societe_diffuseur   │────────────────────│  tarif_diffusion    │
├─────────────────────┤ (1,n)       (0,n)  ├─────────────────────┤
│ id_societe_diffuseur│                    │ id_tarif_diffusion  │
│ nom                 │                    │ id_societe (FK,NULL)│
│ email               │                    │ id_vol (FK,NULL)    │
│ telephone           │                    │ prix_unitaire       │
│ adresse             │                    │ actif               │
└─────────┬───────────┘                    └─────────────────────┘
          │ (1,n)
          ▼
┌─────────────────────┐
│      facture        │
├─────────────────────┤
│ id_facture          │
│ numero_facture      │
│ id_societe (FK)     │
│ date_facture        │
│ montant             │
│ statut              │
└─────────┬───────────┘
          │ (1,n)
          ├──────────────────────────────┐
          ▼                              ▼
┌─────────────────────┐        ┌─────────────────────┐
│   detail_facture    │        │     paiement        │
├─────────────────────┤        ├─────────────────────┤
│ id_detail_facture   │        │ id_paiement         │
│ id_facture (FK)     │        │ id_facture (FK)     │
│ id_vol_programme    │        │ date_paiement       │
│ nombre_diffusions   │        │ montant_paye        │
│ prix_unitaire       │        │ mode_paiement       │
│ montant_ligne       │        │ reference_paiement  │
│ montant_paye        │        └─────────────────────┘
└─────────────────────┘


═══════════════════════════════════════════════════════════════════════════════
                            PARTIE PRODUITS EXTRA
═══════════════════════════════════════════════════════════════════════════════

┌─────────────────────┐
│ categorie_produit   │
├─────────────────────┤
│ id_categorie_produit│
│ nom                 │
│ description         │
│ actif               │
└─────────┬───────────┘
          │ (1,n)
          ▼
┌─────────────────────┐                    ┌─────────────────────┐
│   produit_extra     │────────────────────│   vente_produit     │
├─────────────────────┤ (1,n)       (0,n)  ├─────────────────────┤
│ id_produit_extra    │                    │ id_vente_produit    │
│ id_categorie (FK)   │                    │ id_vol_programme    │
│ code_produit        │                    │ id_produit_extra    │
│ nom                 │                    │ quantite            │
│ prix_unitaire       │                    │ prix_unitaire       │
│ actif               │                    │ montant_total       │
└─────────────────────┘                    │ date_vente          │
                                           └─────────────────────┘
                                                     │
                                                     ▼ via facturation
┌─────────────────────┐
│  facture_produit    │
├─────────────────────┤
│ id_facture_produit  │
│ numero_facture      │
│ date_facture        │
│ montant             │
│ statut              │
└─────────┬───────────┘
          │ (1,n)
          ├──────────────────────────────┐
          ▼                              ▼
┌────────────────────────┐    ┌─────────────────────┐
│detail_facture_produit  │    │  paiement_produit   │
├────────────────────────┤    ├─────────────────────┤
│ id_detail_fact_produit │    │ id_paiement_produit │
│ id_facture_produit (FK)│    │ id_facture_prod (FK)│
│ id_vente_produit       │    │ date_paiement       │
│ quantite               │    │ montant_paye        │
│ montant_ligne          │    │ mode_paiement       │
│ montant_paye           │    └─────────────────────┘
└────────────────────────┘
```

## 1.2 Cardinalités Principales

| Relation | Cardinalité | Description |
|----------|-------------|-------------|
| Vol - Aeroport (départ) | N:1 | Un vol part d'un aéroport |
| Vol - Aeroport (arrivée) | N:1 | Un vol arrive à un aéroport |
| Vol - vol_programme | 1:N | Une route peut avoir plusieurs instances |
| vol_programme - Avion | N:1 | Un vol programmé utilise un avion |
| vol_programme - Reservation | 1:N | Un vol programmé peut avoir plusieurs réservations |
| Reservation - Client | N:1 | Une réservation appartient à un client |
| Reservation - detail_reservation | 1:N | Une réservation contient plusieurs passagers |
| Avion - avion_place | 1:N | Un avion a plusieurs configurations de places |
| Vol - tarif_vol | 1:N | Un vol a des tarifs par type de place |
| societe_diffuseur - facture | 1:N | Une société peut avoir plusieurs factures |
| facture - detail_facture | 1:N | Une facture contient plusieurs lignes |
| facture - paiement | 1:N | Une facture peut avoir plusieurs paiements |
| categorie_produit - produit_extra | 1:N | Une catégorie contient plusieurs produits |
| produit_extra - vente_produit | 1:N | Un produit peut être vendu plusieurs fois |

---

# 2. LISTE DES CLASSES COMPLÈTES

## 2.1 Couche Model (Entités JPA)

### Package `com.fly.andco.model`

| Sous-package | Classe | Table correspondante |
|--------------|--------|---------------------|
| `aeroports` | `Aeroport` | `aeroport` |
| `avions` | `Avion` | `avion` |
| `vols` | `Vol` | `vol` |
| `vols` | `VolProgramme` | `vol_programme` |
| `clients` | `Client` | `client` |
| `reservations` | `Reservation` | `reservation` |
| `reservations` | `DetailReservation` | `detail_reservation` |
| `places` | `TypePlace` | `type_place` |
| `places` | `AvionPlace` | `avion_place` |
| `places` | `TarifVol` | `tarif_vol` |
| `places` | `CategoriePassager` | `categorie_passager` |
| `places` | `TarifCategorie` | `tarif_categorie` |
| `places` | `ValeurMaxAvionVol` | Vue `v_valeur_max_avion_vol` (DTO) |
| `diffusions` | `SocieteDiffuseur` | `societe_diffuseur` |
| `diffusions` | `TarifDiffusion` | `tarif_diffusion` |
| `diffusions` | `Facture` | `facture` |
| `diffusions` | `DetailFacture` | `detail_facture` |
| `diffusions` | `Paiement` | `paiement` |
| `produits` | `CategorieProduit` | `categorie_produit` |
| `produits` | `ProduitExtra` | `produit_extra` |
| `produits` | `VenteProduit` | `vente_produit` |
| `produits` | `FactureProduit` | `facture_produit` |
| `produits` | `DetailFactureProduit` | `detail_facture_produit` |
| `produits` | `PaiementProduit` | `paiement_produit` |
| `utilisateurs` | `Utilisateur` | `utilisateur` |

## 2.2 Couche Repository

### Package `com.fly.andco.repository`

| Sous-package | Classe | Entité gérée |
|--------------|--------|--------------|
| `aeroports` | `AeroportRepository` | `Aeroport` |
| `avions` | `AvionRepository` | `Avion` |
| `vols` | `VolRepository` | `Vol` |
| `vols` | `VolProgrammeRepository` | `VolProgramme` |
| `clients` | `ClientRepository` | `Client` |
| `reservations` | `ReservationRepository` | `Reservation` |
| `reservations` | `DetailReservationRepository` | `DetailReservation` |
| `places` | `TypePlaceRepository` | `TypePlace` |
| `places` | `AvionPlaceRepository` | `AvionPlace` |
| `places` | `TarifVolRepository` | `TarifVol` |
| `places` | `CategoriePassagerRepository` | `CategoriePassager` |
| `places` | `TarifCategorieRepository` | `TarifCategorie` |
| `places` | `ValeurMaxAvionVolRepository` | `ValeurMaxAvionVol` |
| `diffusions` | `SocieteDiffuseurRepository` | `SocieteDiffuseur` |
| `diffusions` | `TarifDiffusionRepository` | `TarifDiffusion` |
| `diffusions` | `FactureRepository` | `Facture` |
| `diffusions` | `DetailFactureRepository` | `DetailFacture` |
| `diffusions` | `PaiementRepository` | `Paiement` |
| `produits` | `CategorieProduitRepository` | `CategorieProduit` |
| `produits` | `ProduitExtraRepository` | `ProduitExtra` |
| `produits` | `VenteProduitRepository` | `VenteProduit` |
| `produits` | `FactureProduitRepository` | `FactureProduit` |
| `produits` | `DetailFactureProduitRepository` | `DetailFactureProduit` |
| `produits` | `PaiementProduitRepository` | `PaiementProduit` |

## 2.3 Couche Service

### Package `com.fly.andco.service`

| Sous-package | Classe |
|--------------|--------|
| `aeroports` | `AeroportService` |
| `avions` | `AvionService` |
| `vols` | `VolService` |
| `vols` | `VolProgrammeService` |
| `clients` | `ClientService` |
| `reservations` | `ReservationService` |
| `places` | `TypePlaceService` |
| `places` | `TarifService` |
| `places` | `ValeurMaxService` |
| `diffusions` | `SocieteDiffuseurService` |
| `diffusions` | `DiffusionService` |
| `diffusions` | `FactureService` |
| `diffusions` | `PaiementService` |
| `produits` | `CategorieProduitService` |
| `produits` | `ProduitExtraService` |
| `produits` | `VenteProduitService` |
| `produits` | `FactureProduitService` |
| `ca` | `CAMensuelService` |

## 2.4 Couche Controller

### Package `com.fly.andco.controller`

| Sous-package | Classe | URL de base |
|--------------|--------|-------------|
| `home` | `HomeController` | `/` |
| `booking` | `BookingController` | `/booking` |
| `avions` | `AvionController` | `/avions` |
| `vols` | `VolController` | `/vols` |
| `places` | `ValeurMaxController` | `/valeur-max` |
| `tarifs` | `TarifController` | `/tarifs` |
| `diffusions` | `DiffusionController` | `/diffusions` |
| `produits` | `ProduitController` | `/produits` |
| `produits` | `VenteProduitController` | `/ventes-produits` |
| `ca` | `CAMensuelController` | `/ca-mensuel` |
| `api` | `TarifApiController` | `/api/tarifs` |

## 2.5 Couche DTO

### Package `com.fly.andco.dto`

| Classe | Usage |
|--------|-------|
| `ValeurMaxAvionVol` | Résultat vue valeur max |
| `CAVolProgrammeDTO` | CA par vol programmé |
| `CAMensuelDTO` | CA mensuel agrégé |
| `CAMensuelDetailDTO` | Détail CA mensuel |
| `ReservationFormDTO` | Formulaire réservation |
| `PaiementFormDTO` | Formulaire paiement |

---

# 3. FONCTIONNALITÉS DÉTAILLÉES

---

## F1: RÉSERVATION DE BILLETS

### 3.1.1 Dessin d'écran

#### Écran: Recherche de vols `/booking`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  RECHERCHE DE VOLS                                    │
│            │                                                         │
│            │  Aéroport de départ*:  [▼ Sélectionner         ]      │
│            │  Aéroport d'arrivée*:  [▼ Sélectionner         ]      │
│            │  Date de voyage*:      [📅 __/__/____          ]      │
│            │                                                         │
│            │  [Rechercher]                                          │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Résultats recherche `/booking/search`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  VOLS DISPONIBLES - TNR → NOS - 12/01/2026            │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Heure  │ Avion      │ Places dispo │ Action    │   │
│            │  │────────│────────────│──────────────│───────────│   │
│            │  │ 08:00  │ Boeing 737 │ 45 places    │ [Réserver]│   │
│            │  │ 12:00  │ ATR 72-600 │ 22 places    │ [Réserver]│   │
│            │  │ 16:00  │ Airbus A320│ 89 places    │ [Réserver]│   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [← Nouvelle recherche]                                │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Formulaire réservation `/booking/reserve/{id}`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  RÉSERVATION                                          │
│            │                                                         │
│            │  ┌──────────────────┐  ┌──────────────────────────┐   │
│            │  │ DÉTAILS DU VOL   │  │ VOS INFORMATIONS         │   │
│            │  │                  │  │                          │   │
│            │  │ TNR → NOS        │  │ Nom*:     [____________] │   │
│            │  │ 12/01/2026       │  │ Prénom*:  [____________] │   │
│            │  │ 12:00 - 13:30    │  │ Email*:   [____________] │   │
│            │  │ ATR 72-600       │  │ Tél:      [____________] │   │
│            │  │                  │  │                          │   │
│            │  │ 22 places dispo  │  │ Type place*: [▼ Éco   ] │   │
│            │  │                  │  │ Catégorie*:  [▼ Adulte] │   │
│            │  │                  │  │ Nb places*:  [▼ 1     ] │   │
│            │  │                  │  │                          │   │
│            │  └──────────────────┘  │ TOTAL: 700 000 Ar       │   │
│            │                        │                          │   │
│            │                        │ [Annuler] [Confirmer]    │   │
│            │                        └──────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.1.2 Signatures des fonctions

#### BookingController (`controller/booking/BookingController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `showSearchForm(Model model)` | GET `/booking` | Affiche formulaire recherche |
| `searchFlights(Long idDepart, Long idArrivee, LocalDate date, Model model)` | GET `/booking/search` | Recherche vols disponibles |
| `showReserveForm(Long idVolProgramme, Model model)` | GET `/booking/reserve/{id}` | Affiche formulaire réservation |
| `reserve(ReservationFormDTO form, RedirectAttributes attr)` | POST `/booking/reserve` | Effectue la réservation |
| `showMesReservations(String email, Model model)` | GET `/booking/mes-reservations` | Liste réservations client |

#### VolProgrammeService (`service/vols/VolProgrammeService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findByVolAndDate(Long idVol, LocalDate date)` | `List<VolProgramme>` | Vols d'une route à une date |
| `findById(Long id)` | `Optional<VolProgramme>` | Récupère un vol programmé |
| `getPlacesDisponibles(Long idVolProgramme)` | `Integer` | Calcul places restantes |
| `getPlacesDisponiblesParType(Long idVolProgramme)` | `Map<TypePlace, Integer>` | Places par type |

#### ReservationService (`service/reservations/ReservationService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `effectuerReservation(ReservationFormDTO form)` | `Reservation` | Crée réservation complète |
| `findByClientEmail(String email)` | `List<Reservation>` | Réservations d'un client |
| `findById(Long id)` | `Optional<Reservation>` | Détail réservation |

#### TarifService (`service/places/TarifService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `calculerPrixPassager(Long idVol, Long idTypePlace, Long idCategorie)` | `BigDecimal` | Prix d'un passager |
| `getTarifAdulte(Long idVol, Long idTypePlace)` | `BigDecimal` | Tarif de base adulte |

### 3.1.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `vol_programme` | Vols disponibles |
| `vol` | Routes (départ/arrivée) |
| `aeroport` | Noms aéroports |
| `avion` | Modèle et capacité |
| `client` | Informations client |
| `reservation` | Réservation mère |
| `detail_reservation` | Passagers détaillés |
| `type_place` | Types de places |
| `categorie_passager` | Catégories âge |
| `tarif_vol` | Prix de base |
| `tarif_categorie` | Remises catégories |

### 3.1.4 Vues utilisées

**Aucune vue spécifique** - Les données sont récupérées via requêtes JPA avec jointures.

---

## F2: GESTION DES CLASSES DE PLACES ET TARIFS

### 3.2.1 Dessin d'écran

#### Écran: Liste des tarifs `/tarifs`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  TARIFICATION PAR VOL                                 │
│            │                                                         │
│            │  Filtrer par vol: [▼ Tous les vols          ]         │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Route       │ Type place     │ Prix       │ Act │   │
│            │  │─────────────│────────────────│────────────│─────│   │
│            │  │ TNR → NOS   │ Première classe│ 1 200 000  │ [✏️] │   │
│            │  │ TNR → NOS   │ Économique     │ 700 000    │ [✏️] │   │
│            │  │ TNR → NOS   │ Premium        │ 1 000 000  │ [✏️] │   │
│            │  │ TNR → TMM   │ Première classe│ 300 000    │ [✏️] │   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [+ Nouveau tarif]                                     │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Configuration places avion `/avions/{id}/places`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CONFIGURATION PLACES - ATR 72-600 (5R-MJA)           │
│            │                                                         │
│            │  Capacité totale: 70 places                            │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Type de place    │ Nombre places │ Actions     │   │
│            │  │──────────────────│───────────────│─────────────│   │
│            │  │ Première classe  │ 10            │ [✏️] [🗑️]   │   │
│            │  │ Économique       │ 60            │ [✏️] [🗑️]   │   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [+ Ajouter type de place]                             │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2.2 Signatures des fonctions

#### TarifController (`controller/tarifs/TarifController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `listTarifs(Long idVol, Model model)` | GET `/tarifs` | Liste tous les tarifs |
| `showForm(Long id, Model model)` | GET `/tarifs/edit/{id}` | Formulaire édition |
| `saveTarif(TarifVol tarif, RedirectAttributes attr)` | POST `/tarifs/save` | Enregistre tarif |

#### AvionController (`controller/avions/AvionController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `showPlaces(Long idAvion, Model model)` | GET `/avions/{id}/places` | Config places avion |
| `savePlace(AvionPlace place, RedirectAttributes attr)` | POST `/avions/places/save` | Enregistre config |

#### TypePlaceService (`service/places/TypePlaceService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<TypePlace>` | Tous les types |
| `findById(Long id)` | `Optional<TypePlace>` | Un type |

#### TarifVolRepository (`repository/places/TarifVolRepository.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<TarifVol>` | Tous les tarifs |
| `findByVolIdVol(Long idVol)` | `List<TarifVol>` | Tarifs d'un vol |
| `findByVolIdVolAndTypePlaceIdTypePlace(Long idVol, Long idTypePlace)` | `Optional<TarifVol>` | Tarif spécifique |

### 3.2.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `type_place` | Types (Première, Éco, Premium) |
| `avion_place` | Nb places par type par avion |
| `tarif_vol` | Prix par route et type |
| `vol` | Routes |
| `avion` | Avions |

### 3.2.4 Vues utilisées

**Aucune vue spécifique**

---

## F3: VALEUR MAXIMALE D'UN AVION POUR UN VOL

### 3.3.1 Dessin d'écran

#### Écran: Valeur max `/valeur-max`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  VALEUR MAXIMALE PAR AVION                            │
│            │                                                         │
│            │  Filtres:                                               │
│            │  Route: [▼ Toutes les routes    ]                      │
│            │  Avion: [▼ Tous les avions      ]                      │
│            │                                                         │
│            │  [Filtrer] [Réinitialiser]                             │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Avion                │ Route     │ Valeur Max   │   │
│            │  │──────────────────────│───────────│──────────────│   │
│            │  │ Boeing 737 (5R-MJB)  │ TNR → NOS │ 136 000 000  │   │
│            │  │ ATR 72-600 (5R-MJA)  │ TNR → NOS │ 54 000 000   │   │
│            │  │ Airbus A320 (5R-MJC) │ TNR → NOS │ 113 000 000  │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3.2 Signatures des fonctions

#### ValeurMaxController (`controller/places/ValeurMaxController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `valeurMax(Long idVol, Long idAvion, Model model)` | GET `/valeur-max` | Affiche valeurs max |

#### ValeurMaxService (`service/places/ValeurMaxService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<ValeurMaxAvionVol>` | Toutes les combinaisons |
| `findByVol(Long idVol)` | `List<ValeurMaxAvionVol>` | Filtré par vol |
| `findByAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | Filtré par avion |
| `findByVolAndAvion(Long idVol, Long idAvion)` | `List<ValeurMaxAvionVol>` | Double filtre |

#### ValeurMaxAvionVolRepository (`repository/places/ValeurMaxAvionVolRepository.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<ValeurMaxAvionVol>` | Requête native sur vue |
| `findByIdVol(Long idVol)` | `List<ValeurMaxAvionVol>` | Filtre vol |
| `findByIdAvion(Long idAvion)` | `List<ValeurMaxAvionVol>` | Filtre avion |

### 3.3.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `avion` | Info avion |
| `avion_place` | Configuration places |
| `type_place` | Types de places |
| `vol` | Routes |
| `tarif_vol` | Prix par type |
| `aeroport` | Codes IATA |

### 3.3.4 Vue utilisée: `v_valeur_max_avion_vol`

#### Colonnes de la vue

| Colonne | Type | Description |
|---------|------|-------------|
| `id_avion` | INT | ID de l'avion |
| `modele` | VARCHAR | Modèle de l'avion |
| `numero_immatriculation` | VARCHAR | Immatriculation |
| `id_vol` | INT | ID de la route |
| `depart` | CHAR(3) | Code IATA départ |
| `arrivee` | CHAR(3) | Code IATA arrivée |
| `valeur_max` | NUMERIC | Valeur maximale calculée |

#### Table principale
`avion`

#### Tables annexes

| Table | Type liaison | Colonnes de liaison |
|-------|--------------|---------------------|
| `avion_place` | INNER JOIN | `avion.id_avion = avion_place.id_avion` |
| `type_place` | INNER JOIN | `avion_place.id_type_place = type_place.id_type_place` |
| `vol` | CROSS JOIN | Aucune (produit cartésien) |
| `tarif_vol` | INNER JOIN | `vol.id_vol = tarif_vol.id_vol AND type_place.id_type_place = tarif_vol.id_type_place` |
| `aeroport` (départ) | INNER JOIN | `vol.id_aeroport_depart = aeroport.id_aeroport` |
| `aeroport` (arrivée) | INNER JOIN | `vol.id_aeroport_arrivee = aeroport.id_aeroport` |

#### Calcul
```sql
SUM(avion_place.nombre_places * tarif_vol.prix) AS valeur_max
GROUP BY avion, vol
```

---

## F4: TARIFICATION PAR CATÉGORIE (ADULTE/ENFANT/BÉBÉ)

### 3.4.1 Dessin d'écran

#### Écran: Tarifs catégories `/tarifs/categories`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  TARIFICATION PAR CATÉGORIE                           │
│            │                                                         │
│            │  Route: [▼ TNR → NOS                       ]          │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Type place  │ Adulte    │ Enfant    │ Bébé     │   │
│            │  │─────────────│───────────│───────────│──────────│   │
│            │  │ Première cl │ 1 200 000 │ 1 000 000 │ 120 000  │   │
│            │  │ Économique  │ 700 000   │ 500 000   │ 70 000   │   │
│            │  │ Premium     │ 1 000 000 │ 800 000   │ 100 000  │   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [✏️ Modifier tarifs]                                   │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.4.2 Signatures des fonctions

#### TarifController (`controller/tarifs/TarifController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `showTarifsCategories(Long idVol, Model model)` | GET `/tarifs/categories` | Affiche grille tarifs |
| `saveTarifCategorie(TarifCategorie tarif, RedirectAttributes attr)` | POST `/tarifs/categories/save` | Enregistre tarif catégorie |

#### TarifService (`service/places/TarifService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `calculerPrixPassager(Long idVol, Long idTypePlace, Long idCategorie)` | `BigDecimal` | Prix selon logique |
| `getTarifCategorie(Long idVol, Long idTypePlace, Long idCategorie)` | `Optional<TarifCategorie>` | Tarif spécifique |
| `getTarifsParVol(Long idVol)` | `List<TarifCategorie>` | Tous tarifs d'un vol |

#### CategoriePassagerService (`service/places/CategoriePassagerService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<CategoriePassager>` | Toutes catégories |
| `findById(Long id)` | `Optional<CategoriePassager>` | Une catégorie |

### 3.4.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `categorie_passager` | Définition catégories (Adulte, Enfant, Bébé) |
| `tarif_categorie` | Tarifs spécifiques par catégorie |
| `tarif_vol` | Tarifs de base (adulte par défaut) |
| `vol` | Routes |
| `type_place` | Types de places |

### 3.4.4 Logique de calcul du prix

```
SI tarif_categorie.prix IS NOT NULL:
    prix = tarif_categorie.prix
SINON SI tarif_categorie.pourcentage IS NOT NULL:
    prix = tarif_vol.prix × (pourcentage / 100)
    SI tarif_categorie.frais_reduction IS NOT NULL:
        prix = prix - frais_reduction
SINON:
    prix = tarif_vol.prix (tarif adulte par défaut)

RETOURNER MAX(prix, 0)
```

---

## F5: CALCUL DU CA PAR VOL PROGRAMMÉ

### 3.5.1 Dessin d'écran

#### Écran: CA par vol `/vols/ca/{id}`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CHIFFRE D'AFFAIRES - VOL                             │
│            │                                                         │
│            │  ┌──────────────────────────────────────────┐          │
│            │  │ Route: TNR → NOS                         │          │
│            │  │ Date: 12/01/2026 12:00                   │          │
│            │  │ Avion: ATR 72-600 (5R-MJA)               │          │
│            │  └──────────────────────────────────────────┘          │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Type place  │ Catégorie│ Nb rés │ Prix  │ Total│   │
│            │  │─────────────│──────────│────────│───────│──────│   │
│            │  │ Économique  │ Adulte   │ 15     │700 000│10.5M │   │
│            │  │ Économique  │ Enfant   │ 5      │500 000│2.5M  │   │
│            │  │ Première cl │ Adulte   │ 3      │1.2M   │3.6M  │   │
│            │  │─────────────│──────────│────────│───────│──────│   │
│            │  │ TOTAL       │          │ 23     │       │16.6M │   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [← Retour liste vols]                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.5.2 Signatures des fonctions

#### VolController (`controller/vols/VolController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `showCA(Long idVolProgramme, Model model)` | GET `/vols/ca/{id}` | Affiche CA détaillé |

#### CAVolProgrammeService (`service/ca/CAVolProgrammeService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `getCAByVolProgramme(Long id)` | `CAVolProgrammeDTO` | CA détaillé |
| `getCATotalByVolProgramme(Long id)` | `BigDecimal` | CA total |
| `getDetailsByVolProgramme(Long id)` | `List<CADetailDTO>` | Détail par catégorie |

### 3.5.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `vol_programme` | Vol analysé |
| `reservation` | Réservations du vol |
| `detail_reservation` | Passagers et prix payés |
| `type_place` | Types de places |
| `categorie_passager` | Catégories passagers |
| `avion` | Info avion |
| `vol` | Route |
| `aeroport` | Codes IATA |

### 3.5.4 Vue utilisée: `v_ca_vol_programme`

#### Colonnes de la vue

| Colonne | Type | Description |
|---------|------|-------------|
| `id_vol_programme` | INT | ID vol programmé |
| `id_vol` | INT | ID route |
| `id_avion` | INT | ID avion |
| `avion_modele` | VARCHAR | Modèle avion |
| `numero_immatriculation` | VARCHAR | Immat avion |
| `depart` | CHAR(3) | Code IATA départ |
| `arrivee` | CHAR(3) | Code IATA arrivée |
| `date_heure_depart` | TIMESTAMP | Date/heure départ |
| `id_type_place` | INT | Type de place |
| `type_place` | VARCHAR | Nom type place |
| `id_categorie_passager` | INT | ID catégorie |
| `categorie` | VARCHAR | Nom catégorie |
| `nb_reservations` | INT | Nombre passagers |
| `ca_total` | NUMERIC | CA de cette combinaison |

#### Table principale
`vol_programme`

#### Tables annexes

| Table | Type liaison | Colonnes de liaison |
|-------|--------------|---------------------|
| `avion` | INNER JOIN | `vol_programme.id_avion = avion.id_avion` |
| `vol` | INNER JOIN | `vol_programme.id_vol = vol.id_vol` |
| `aeroport` (départ) | INNER JOIN | `vol.id_aeroport_depart = aeroport.id_aeroport` |
| `aeroport` (arrivée) | INNER JOIN | `vol.id_aeroport_arrivee = aeroport.id_aeroport` |
| `reservation` | LEFT JOIN | `vol_programme.id_vol_programme = reservation.id_vol_programme AND statut='confirmée'` |
| `detail_reservation` | LEFT JOIN | `reservation.id_reservation = detail_reservation.id_reservation` |
| `type_place` | LEFT JOIN | `detail_reservation.id_type_place = type_place.id_type_place` |
| `categorie_passager` | LEFT JOIN | `detail_reservation.id_categorie_passager = categorie_passager.id_categorie_passager` |

#### Agrégation
```sql
GROUP BY vol_programme, avion, vol, aeroport, type_place, categorie_passager
SUM(detail_reservation.prix_paye) AS ca_total
COUNT(detail_reservation.id) AS nb_reservations
```

---

## F6: DIFFUSION PUBLICITAIRE

### 3.6.1 Dessin d'écran

#### Écran: Liste sociétés `/diffusions`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  SOCIÉTÉS DIFFUSEURS                                  │
│            │                                                         │
│            │  [+ Nouvelle société]                                  │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Société    │ Email          │ Diffusions │ Act │   │
│            │  │────────────│────────────────│────────────│─────│   │
│            │  │ Vaniala    │ vaniala@mg.com │ 20         │ [👁️] │   │
│            │  │ Lewis      │ lewis@mg.com   │ 10         │ [👁️] │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Commande diffusions `/diffusions/commande`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  COMMANDER DIFFUSIONS PUBLICITAIRES                   │
│            │                                                         │
│            │  Société*:        [▼ Sélectionner               ]     │
│            │  Date facture*:   [📅 30/01/2026                ]     │
│            │                                                         │
│            │  LIGNES DE DIFFUSION:                                  │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Vol programmé      │ Nb diff │ Prix U │ Total  │   │
│            │  │────────────────────│─────────│────────│────────│   │
│            │  │ [▼ TNR→NOS 12:00 ] │ [10   ] │400 000 │4 000 000│  │
│            │  │ [▼ TNR→MJN 08:00 ] │ [5    ] │400 000 │2 000 000│  │
│            │  └─────────────────────────────────────────────────┘   │
│            │  [+ Ajouter ligne]                                     │
│            │                                                         │
│            │  TOTAL: 6 000 000 Ar                                   │
│            │                                                         │
│            │  [Annuler] [Valider commande]                          │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: CA Diffusions `/diffusions/ca`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CA DIFFUSIONS PUBLICITAIRES                          │
│            │                                                         │
│            │  Filtres:                                               │
│            │  Société:    [▼ Toutes                    ]            │
│            │  Date début: [📅 ____________]                         │
│            │  Date fin:   [📅 ____________]                         │
│            │                                                         │
│            │  [Calculer] [Réinitialiser]                            │
│            │                                                         │
│            │  ┌──────────────────────────────────┐                  │
│            │  │ Total Diffusions: 30             │                  │
│            │  │ Total CA: 12 000 000 Ar          │                  │
│            │  └──────────────────────────────────┘                  │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Société    │ Nb Diffusions │ CA Société       │   │
│            │  │────────────│───────────────│──────────────────│   │
│            │  │ Vaniala    │ 20            │ 8 000 000 Ar     │   │
│            │  │ Lewis      │ 10            │ 4 000 000 Ar     │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.6.2 Signatures des fonctions

#### DiffusionController (`controller/diffusions/DiffusionController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `listSocietes(Model model)` | GET `/diffusions` | Liste sociétés |
| `showCommandeForm(Model model)` | GET `/diffusions/commande` | Formulaire commande |
| `validerCommande(CommandeDiffusionDTO cmd, RedirectAttributes attr)` | POST `/diffusions/commande` | Crée facture |
| `afficherCA(Long idSociete, LocalDate debut, LocalDate fin, Model model)` | GET `/diffusions/ca` | CA diffusions |

#### SocieteDiffuseurService (`service/diffusions/SocieteDiffuseurService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<SocieteDiffuseur>` | Toutes sociétés |
| `findById(Long id)` | `Optional<SocieteDiffuseur>` | Une société |
| `save(SocieteDiffuseur s)` | `SocieteDiffuseur` | Enregistre |

#### FactureService (`service/diffusions/FactureService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `creerFacture(Long idSociete, List<DetailFactureDTO> lignes)` | `Facture` | Crée facture complète |
| `findBySociete(Long idSociete)` | `List<Facture>` | Factures société |
| `getResteAPayer(Long idFacture)` | `BigDecimal` | Reste à payer |

#### PaiementService (`service/diffusions/PaiementService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `enregistrerPaiement(Long idFacture, PaiementFormDTO form)` | `Paiement` | Enregistre paiement |
| `repartirProrata(Long idFacture, BigDecimal montant)` | `void` | Répartit sur lignes |

#### DiffusionService (`service/diffusions/DiffusionService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `getCAParPeriode(LocalDate debut, LocalDate fin)` | `BigDecimal` | CA total période |
| `getCAParSociete(Long idSociete, LocalDate debut, LocalDate fin)` | `BigDecimal` | CA société |
| `getTarifApplicable(Long idSociete, Long idVol)` | `BigDecimal` | Tarif applicable |

### 3.6.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `societe_diffuseur` | Sociétés clientes |
| `tarif_diffusion` | Tarifs (par défaut ou spécifiques) |
| `facture` | Factures émises |
| `detail_facture` | Lignes de facture |
| `paiement` | Paiements reçus |
| `vol_programme` | Vols concernés |

### 3.6.4 Vue utilisée: `v_resume_paiement_societe`

#### Colonnes de la vue

| Colonne | Type | Description |
|---------|------|-------------|
| `id_societe_diffuseur` | INT | ID société |
| `societe` | VARCHAR | Nom société |
| `total_diffusions` | INT | Nombre total diffusions |
| `total_facture` | NUMERIC | Total facturé |
| `total_paye` | NUMERIC | Total payé |
| `reste_a_payer` | NUMERIC | Reste à payer |

#### Table principale
`societe_diffuseur`

#### Tables annexes

| Table | Type liaison | Colonnes de liaison |
|-------|--------------|---------------------|
| `facture` | LEFT JOIN | `societe_diffuseur.id_societe_diffuseur = facture.id_societe_diffuseur AND statut!='annulée'` |
| `detail_facture` | LEFT JOIN | `facture.id_facture = detail_facture.id_facture` |
| `paiement` (sous-requête) | - | `facture.id_facture = paiement.id_facture` |

---

## F7: GESTION DES PRODUITS EXTRA

### 3.7.1 Dessin d'écran

#### Écran: Liste produits `/produits`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  GESTION DES PRODUITS EXTRA                           │
│            │                                                         │
│            │  [+ Nouveau Produit]                                   │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Code     │ Nom            │ Catégorie │ Prix    │   │
│            │  │──────────│────────────────│───────────│─────────│   │
│            │  │CHOCO-001 │ Tablette choco │ Snacks    │ 5 000   │   │
│            │  │SODA-001  │ Coca-Cola 33cl │ Boissons  │ 3 000   │   │
│            │  │ECRTR-001 │ Écouteurs      │ Accessoir │ 15 000  │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Formulaire produit `/produits/nouveau`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  NOUVEAU PRODUIT                                      │
│            │                                                         │
│            │  Code produit*:   [________________]                   │
│            │  Nom*:            [________________]                   │
│            │  Description:     [________________]                   │
│            │  Catégorie*:      [▼ Sélectionner      ]              │
│            │  Prix unitaire*:  [________] Ar                       │
│            │  Actif:           [✓]                                  │
│            │                                                         │
│            │  [Annuler] [Enregistrer]                               │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Ventes produits `/ventes-produits`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  VENTES PRODUITS À BORD                               │
│            │                                                         │
│            │  Filtres: Date début [____] Date fin [____] [Filtrer] │
│            │                                                         │
│            │  [+ Nouvelle Vente]                                    │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Date     │ Vol        │ Produit   │ Qté │Montant│   │
│            │  │──────────│────────────│───────────│─────│───────│   │
│            │  │20/01/26  │TNR→NOS 12h │Choco      │ 15  │75 000 │   │
│            │  │21/01/26  │TNR→NOS 12h │Choco      │ 20  │100 000│   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  Total: 35 produits | 175 000 Ar                       │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.7.2 Signatures des fonctions

#### ProduitController (`controller/produits/ProduitController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `list(Model model)` | GET `/produits` | Liste produits |
| `showForm(Model model)` | GET `/produits/nouveau` | Form création |
| `showEditForm(Long id, Model model)` | GET `/produits/edit/{id}` | Form édition |
| `save(ProduitExtra produit, RedirectAttributes attr)` | POST `/produits/save` | Enregistre |

#### VenteProduitController (`controller/produits/VenteProduitController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `list(LocalDate debut, LocalDate fin, Model model)` | GET `/ventes-produits` | Liste ventes |
| `showForm(Model model)` | GET `/ventes-produits/nouveau` | Form vente |
| `save(VenteProduitDTO form, RedirectAttributes attr)` | POST `/ventes-produits/save` | Enregistre vente |

#### ProduitExtraService (`service/produits/ProduitExtraService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<ProduitExtra>` | Tous produits |
| `findAllActifs()` | `List<ProduitExtra>` | Produits actifs |
| `findById(Long id)` | `Optional<ProduitExtra>` | Un produit |
| `save(ProduitExtra p)` | `ProduitExtra` | Enregistre |

#### VenteProduitService (`service/produits/VenteProduitService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `findAll()` | `List<VenteProduit>` | Toutes ventes |
| `findByPeriode(LocalDate debut, LocalDate fin)` | `List<VenteProduit>` | Ventes période |
| `findByVolProgramme(Long idVp)` | `List<VenteProduit>` | Ventes d'un vol |
| `save(VenteProduit v)` | `VenteProduit` | Enregistre vente |
| `getTotalByPeriode(LocalDate debut, LocalDate fin)` | `BigDecimal` | CA période |

### 3.7.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `categorie_produit` | Catégories de produits |
| `produit_extra` | Produits à vendre |
| `vente_produit` | Ventes enregistrées |
| `vol_programme` | Vol concerné |
| `facture_produit` | Facturation |
| `detail_facture_produit` | Lignes facture |
| `paiement_produit` | Paiements |

### 3.7.4 Vue utilisée: `v_ca_produits_vol`

#### Colonnes de la vue

| Colonne | Type | Description |
|---------|------|-------------|
| `id_vol_programme` | INT | ID vol |
| `id_vol` | INT | ID route |
| `avion_modele` | VARCHAR | Modèle avion |
| `numero_immatriculation` | VARCHAR | Immat |
| `route` | VARCHAR | "TNR → NOS" |
| `date_heure_depart` | TIMESTAMP | Date/heure |
| `id_produit_extra` | INT | ID produit |
| `produit` | VARCHAR | Nom produit |
| `categorie` | VARCHAR | Catégorie produit |
| `quantite_vendue` | INT | Quantité totale |
| `ca_produits` | NUMERIC | CA produits |

#### Table principale
`vol_programme`

#### Tables annexes

| Table | Type liaison | Colonnes de liaison |
|-------|--------------|---------------------|
| `avion` | INNER JOIN | `vol_programme.id_avion = avion.id_avion` |
| `vol` | INNER JOIN | `vol_programme.id_vol = vol.id_vol` |
| `aeroport` (départ) | INNER JOIN | `vol.id_aeroport_depart = aeroport.id_aeroport` |
| `aeroport` (arrivée) | INNER JOIN | `vol.id_aeroport_arrivee = aeroport.id_aeroport` |
| `vente_produit` | LEFT JOIN | `vol_programme.id_vol_programme = vente_produit.id_vol_programme` |
| `produit_extra` | LEFT JOIN | `vente_produit.id_produit_extra = produit_extra.id_produit_extra` |
| `categorie_produit` | LEFT JOIN | `produit_extra.id_categorie_produit = categorie_produit.id_categorie_produit` |

---

## F8: CA MENSUEL GLOBAL

### 3.8.1 Dessin d'écran

#### Écran: CA Mensuel `/ca-mensuel`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CHIFFRE D'AFFAIRES MENSUEL                           │
│            │                                                         │
│            │  Mois: [▼ Janvier   ] Année: [2026]  [Afficher]       │
│            │                                                         │
│            │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│            │  │🎫Tickets │ │📺Diffus. │ │🍫Produits│ │💰 TOTAL  │   │
│            │  │96M Ar    │ │12M Ar    │ │300K Ar   │ │108.3M Ar │   │
│            │  │120 rés.  │ │30 diff.  │ │60 prods  │ │210 ops   │   │
│            │  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ TYPE           │ NOMBRE    │ MONTANT    │ 👁️   │   │
│            │  │────────────────│───────────│────────────│──────│   │
│            │  │ Tickets        │ 120       │ 96 000 000 │ [👁️] │   │
│            │  │ Diffusions     │ 30        │ 12 000 000 │ [👁️] │   │
│            │  │ Produits Extra │ 60        │ 300 000    │ [👁️] │   │
│            │  │────────────────│───────────│────────────│──────│   │
│            │  │ TOTAL          │ 210       │108 300 000 │      │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

#### Écran: Détail CA Produits `/ca-mensuel/produits`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CA PRODUITS - JANVIER 2026                           │
│            │                                                         │
│            │  [← Retour CA Mensuel]                                 │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Date   │ Vol        │ Produit  │ Qté │ Montant  │   │
│            │  │────────│────────────│──────────│─────│──────────│   │
│            │  │20/01   │TNR→NOS 12h │ Choco    │ 15  │ 75 000   │   │
│            │  │21/01   │TNR→NOS 12h │ Choco    │ 20  │ 100 000  │   │
│            │  │21/01   │TNR→NOS 15h │ Soda     │ 25  │ 75 000   │   │
│            │  │────────│────────────│──────────│─────│──────────│   │
│            │  │ TOTAL  │            │          │ 60  │ 300 000  │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.8.2 Signatures des fonctions

#### CAMensuelController (`controller/ca/CAMensuelController.java`)

| Signature | Type | Description |
|-----------|------|-------------|
| `showCAMensuel(Integer annee, Integer mois, Model model)` | GET `/ca-mensuel` | Page CA global |
| `detailTickets(Integer annee, Integer mois, Model model)` | GET `/ca-mensuel/tickets` | Détail tickets |
| `detailDiffusions(Integer annee, Integer mois, Model model)` | GET `/ca-mensuel/diffusions` | Détail diffusions |
| `detailProduits(Integer annee, Integer mois, Model model)` | GET `/ca-mensuel/produits` | Détail produits |

#### CAMensuelService (`service/ca/CAMensuelService.java`)

| Signature | Retour | Description |
|-----------|--------|-------------|
| `getCAMensuel(int annee, int mois)` | `CAMensuelDTO` | CA agrégé du mois |
| `getCAMensuelDetail(int annee, int mois)` | `CAMensuelDetailDTO` | CA détaillé |
| `getDetailTickets(int annee, int mois)` | `List<DetailTicketDTO>` | Tickets du mois |
| `getDetailDiffusions(int annee, int mois)` | `List<DetailDiffusionDTO>` | Diffusions du mois |
| `getDetailProduits(int annee, int mois)` | `List<VenteProduit>` | Ventes produits mois |
| `getMoisDisponibles()` | `List<YearMonth>` | Mois avec données |

### 3.8.3 Tables utilisées

| Table | Usage |
|-------|-------|
| `detail_reservation` | CA tickets (prix_paye) |
| `reservation` | Lien vol programmé |
| `vol_programme` | Date du vol |
| `detail_facture` | CA diffusions |
| `facture` | Statut facture |
| `vente_produit` | CA produits |

### 3.8.4 Vue utilisée: `v_ca_mensuel_detail`

#### Colonnes de la vue

| Colonne | Type | Description |
|---------|------|-------------|
| `mois` | TIMESTAMP | Premier jour du mois |
| `nb_tickets` | INT | Nombre de réservations |
| `ca_tickets` | NUMERIC | CA tickets |
| `nb_diffusions` | INT | Nombre de diffusions |
| `ca_diffusions` | NUMERIC | CA diffusions |
| `nb_produits` | INT | Quantité produits vendus |
| `ca_produits` | NUMERIC | CA produits |
| `ca_total` | NUMERIC | CA total du mois |

#### Tables/CTEs utilisées

La vue utilise 3 CTEs (Common Table Expressions):

**CTE `ca_tickets`:**
| Table source | Table cible | Clé FK |
|--------------|-------------|--------|
| `vol_programme` | - | Table principale |
| `reservation` | `vol_programme` | `id_vol_programme` |
| `detail_reservation` | `reservation` | `id_reservation` |

**CTE `ca_diffusions`:**
| Table source | Table cible | Clé FK |
|--------------|-------------|--------|
| `facture` | - | Table principale |
| `detail_facture` | `facture` | `id_facture` |

**CTE `ca_produits`:**
| Table source | Table cible | Clé FK |
|--------------|-------------|--------|
| `vente_produit` | - | Table principale (date_vente) |

**Jointure finale:**
Les 3 CTEs sont combinées avec `FULL OUTER JOIN` sur la colonne `mois`.

---

# 4. RÉCAPITULATIF DES VUES SQL

| Vue | Table principale | Tables annexes | Fonction |
|-----|------------------|----------------|----------|
| `v_valeur_max_avion_vol` | `avion` | `avion_place`, `type_place`, `vol`, `tarif_vol`, `aeroport` | Valeur max potentielle |
| `v_ca_vol_programme` | `vol_programme` | `avion`, `vol`, `aeroport`, `reservation`, `detail_reservation`, `type_place`, `categorie_passager` | CA détaillé par vol |
| `v_ca_total_vol_programme` | `vol_programme` | `avion`, `vol`, `aeroport`, `reservation`, `detail_reservation` | CA total par vol |
| `v_resume_paiement_societe` | `societe_diffuseur` | `facture`, `detail_facture`, `paiement` | Résumé paiements sociétés |
| `v_ca_produits_vol` | `vol_programme` | `avion`, `vol`, `aeroport`, `vente_produit`, `produit_extra`, `categorie_produit` | CA produits par vol |
| `v_ca_mensuel` | Union de sources | `detail_reservation`, `reservation`, `vol_programme`, `detail_facture`, `facture`, `vente_produit` | CA mensuel par type |
| `v_ca_mensuel_detail` | CTEs combinées | Idem `v_ca_mensuel` | CA mensuel détaillé |

---

# 5. RÉCAPITULATIF DES FONCTIONS SQL

| Fonction | Type | Description |
|----------|------|-------------|
| `get_prix_passager(id_vol, id_type_place, id_categorie)` | FUNCTION | Calcule prix selon catégorie |
| `update_facture_statut()` | TRIGGER | Met à jour statut facture après paiement |
| `repartir_paiement_prorata(id_facture, montant)` | FUNCTION | Répartit paiement sur lignes |
| `update_facture_produit_statut()` | TRIGGER | Met à jour statut facture produit |
| `repartir_paiement_produit_prorata(id_facture, montant)` | FUNCTION | Répartit paiement produit |

---

# 6. RÈGLES DE GESTION

## 6.1 Avions et Vols
- Un avion a une capacité totale fixe
- Un avion possède des places de différents types (Première classe, Économique, Premium)
- Un vol (route) est défini par son aéroport de départ et d'arrivée
- Un vol peut être effectué par plusieurs avions
- Un vol peut être effectué plusieurs fois dans la journée
- Un vol peut être programmé sur plusieurs jours

## 6.2 Tarification
- Chaque route a un tarif de base par type de place (table `tarif_vol`)
- Le tarif adulte = tarif de base
- Les enfants et bébés peuvent avoir des remises (table `tarif_categorie`)
- La remise peut être: prix fixe OU pourcentage du tarif adulte OU réduction en montant

## 6.3 Réservations
- Places disponibles = Capacité avion - Places déjà réservées
- Une réservation peut inclure plusieurs passagers (table `detail_reservation`)
- Chaque passager a son type de place et sa catégorie

## 6.4 Diffusions publicitaires
- Coût par défaut: 400 000 Ar / diffusion
- Tarifs spécifiques possibles par société
- Paiements multiples autorisés
- Répartition au prorata sur les lignes de facture

## 6.5 Produits extra
- Vente à bord des avions
- Facturation possible avec paiements multiples
- Même logique de prorata que les diffusions

## 6.6 CA Mensuel
- Agrège 3 sources: Tickets + Diffusions + Produits
- Tickets = somme des prix_paye dans detail_reservation
- Diffusions = somme des montant_ligne dans detail_facture (hors annulées)
- Produits = somme des montant_total dans vente_produit

---

**Document généré le 30 janvier 2026**
**Version: 1.0**
