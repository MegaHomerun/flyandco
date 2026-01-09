# Documentation des Modifications - FlyAndCo

## Date: 9 janvier 2026

Ce document décrit les modifications apportées au système de gestion de compagnie aérienne FlyAndCo.

---

## 1. AFFICHAGES

### 1.1 Nouvelles pages créées

| Page | URL | Description |
|------|-----|-------------|
| Recherche de vols | `/booking` | Formulaire de recherche par aéroport et date |
| Résultats | `/booking/search` | Liste des vols disponibles avec places |
| Réservation | `/booking/reserve/{id}` | Formulaire d'achat de billets |
| Mes réservations | `/booking/mes-reservations` | Affichage des billets achetés |

### 1.2 Navigation (Sidebar)

Menu mis à jour avec les sections :
- **Accueil** : Page d'accueil
- **Réservation** :
  - Rechercher un vol
  - Mes réservations
- **Administration** :
  - Gestion des avions
- **Déconnexion**

### 1.3 Fonctionnalités d'affichage

1. **Recherche de vols**
   - Sélection aéroport départ/arrivée via liste déroulante
   - Sélection date avec input date
   - Affichage des vols avec horaires, prix, places disponibles

2. **Réservation**
   - Résumé du vol sélectionné
   - Formulaire : nom, prénom, email, téléphone
   - Choix du nombre de places
   - Calcul automatique du total

3. **Mes réservations**
   - Recherche par email
   - Tableau des réservations avec détails complets

---

## 2. BASE DE DONNÉES

### 2.1 Nouveau schéma

Le script SQL a été complètement revu pour respecter les nouvelles règles de gestion.

#### Tables conservées (modifiées)
- `Utilisateur` : authentification admin
- `Aeroport` : liste des aéroports
- `Avion` : avions disponibles (sans liaison compagnie)

#### Nouvelles tables
- `Vol` : définit une route (départ → arrivée)
- `VolProgramme` : instance d'un vol (date, heure, avion, prix)
- `Client` : informations client pour réservation
- `Reservation` : achat de places

#### Tables supprimées
- `Compagnie` : plus nécessaire
- `Pilote`, `Equipage` : hors scope
- `Passager` : remplacé par Client
- `PrixVol` : intégré dans VolProgramme

### 2.2 Structure des tables principales

```sql
-- Vol = Route entre 2 aéroports
Vol (id_vol, id_aeroport_depart, id_aeroport_arrivee)

-- VolProgramme = Instance d'un vol
VolProgramme (id_vol_programme, id_vol, id_avion, 
              date_heure_depart, date_heure_arrivee, prix, statut)

-- Reservation = Achat
Reservation (id_reservation, id_client, id_vol_programme, 
             nombre_places, date_reservation, statut)
```

### 2.3 Données de test

- 5 aéroports de Madagascar (TNR, Nosy Be, Toamasina, Fort Dauphin, Mahajanga)
- 5 avions (ATR, Boeing, Airbus, Embraer)
- 8 routes (liaisons inter-aéroports)
- 11 vols programmés pour janvier 2026
- Vol spécifique : TNR → Nosy Be le 12 janvier 2026 à 12h00

---

## 3. MÉTIER (Règles de gestion)

### 3.1 Règles implémentées

1. **Avions**
   - Chaque avion a sa propre capacité
   - Un avion n'est plus lié à une compagnie

2. **Vols (Routes)**
   - Un vol est défini par un aéroport de départ et d'arrivée
   - Unicité : une seule route par combinaison départ/arrivée

3. **Vols Programmés**
   - Un vol peut être effectué par plusieurs avions différents
   - Un vol peut être effectué plusieurs fois par jour
   - Un vol peut être programmé sur plusieurs jours
   - Chaque instance a son propre prix

4. **Réservations**
   - Un client peut acheter plusieurs places en une réservation
   - Vérification de la disponibilité avant réservation
   - Places disponibles = Capacité avion - Places réservées

### 3.2 Flux de réservation

```
1. Client recherche un vol (aéroport + date)
2. Système affiche les vols disponibles avec places restantes
3. Client sélectionne un vol
4. Client remplit ses informations + nombre de places
5. Système vérifie disponibilité
6. Système crée/récupère le client par email
7. Système crée la réservation
8. Client consulte ses réservations par email
```

### 3.3 Calculs

- **Places disponibles** = `avion.capacite - SUM(reservations.nombrePlaces)`
- **Montant total** = `volProgramme.prix × reservation.nombrePlaces`

### 3.4 Classes Java

| Couche | Classes |
|--------|---------|
| Model | `Vol`, `VolProgramme`, `Avion`, `Aeroport`, `Client`, `Reservation` |
| Repository | `VolRepository`, `VolProgrammeRepository`, `AeroportRepository`, `ClientRepository`, `ReservationRepository` |
| Service | `VolService`, `VolProgrammeService`, `AeroportService`, `ClientService`, `ReservationService` |
| Controller | `BookingController` |

---

## Fichiers modifiés/créés

### SQL
- `src/bd/00_script_new.sql` - Nouveau script de base de données

### Java - Models
- `model/vols/Vol.java` - Route (départ/arrivée)
- `model/vols/VolProgramme.java` - Instance de vol (NOUVEAU)
- `model/avions/Avion.java` - Sans compagnie
- `model/aeroports/Aeroport.java` - Avec getters/setters
- `model/clients/Client.java` - Client (NOUVEAU)
- `model/reservations/Reservation.java` - Modifié

### Java - Repositories
- `repository/vols/VolProgrammeRepository.java` (NOUVEAU)
- `repository/clients/ClientRepository.java` (NOUVEAU)
- `repository/reservations/ReservationRepository.java` - Modifié

### Java - Services
- `service/vols/VolProgrammeService.java` (NOUVEAU)
- `service/clients/ClientService.java` (NOUVEAU)
- `service/reservations/ReservationService.java` - Modifié

### Java - Controllers
- `controller/booking/BookingController.java` (NOUVEAU)

### Templates
- `templates/views/booking/search.html` (NOUVEAU)
- `templates/views/booking/reserve.html` (NOUVEAU)
- `templates/views/booking/reservations.html` (NOUVEAU)
- `templates/fragments/sidebar.html` - Modifié
