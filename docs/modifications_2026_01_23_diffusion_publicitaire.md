# Fonctionnalité : Diffusion Vidéos Publicitaires
**Date : 2026-01-23**

---

## 📋 RÉSUMÉ

Les sociétés peuvent diffuser des vidéos publicitaires sur les écrans des avions. La diffusion est payante avec un système de facturation flexible supportant les paiements multiples et les échéanciers.

**Contexte initial :**
- Coût standard : 400 000 Ar / diffusion
- Vaniala : 20 diffusions en décembre 2025
- Lewis : 10 diffusions en décembre 2025

---

# PARTIE 1 : AFFICHAGE (IHM)

## 1.1 Page : Chiffre d'Affaires Diffusions

**URL :** `/diffusions/ca`

**Titre de la page :** "Chiffre d'Affaires - Diffusions Publicitaires"

### Formulaire de Filtres

| Champ | Type | Obligatoire | Source des données |
|-------|------|-------------|-------------------|
| Société | Liste déroulante | Non | Table `societe_diffuseur`, fonction `SocieteDiffuseurRepository.findAll()` |
| Date début | Date (input date) | Non | - |
| Date fin | Date (input date) | Non | - |

**Valeur par défaut :**
- Société : "Toutes les sociétés" (valeur vide = inclure tout)
- Date début : vide
- Date fin : vide

### Boutons

| Bouton | Libellé | Action | Fonction appelée | Tables utilisées |
|--------|---------|--------|------------------|------------------|
| Calculer | "Calculer CA" | Soumet le formulaire GET | `DiffusionController.afficherCA()` | `facture`, `detail_facture`, `societe_diffuseur` |
| Réinitialiser | "Réinitialiser" | Recharge la page sans filtres | Redirect vers `/diffusions/ca` | - |

### Zone de Résultats

**Bloc Résumé Global :**
| Élément | Description |
|---------|-------------|
| Total Diffusions | Nombre total de diffusions pour la période |
| Total CA | Montant total en Ariary (formaté avec séparateurs milliers) |

**Tableau Détails par Société :**
| Colonne | Type | Description |
|---------|------|-------------|
| Société | Texte | Nom de la société (`societe_diffuseur.nom`) |
| Nombre Diffusions | Nombre | Total des diffusions de cette société |
| CA Société | Montant | Somme des montants HT des factures |

### Flux de Navigation

```
[Page Accueil] 
     │
     ▼
[Sidebar → Menu "Diffusions"] 
     │
     ▼
[/diffusions/ca - Page CA Diffusions]
     │
     ├──[Sélection filtres]──▶ [Clic "Calculer CA"]──▶ [Affichage résultats]
     │
     └──[Clic "Réinitialiser"]──▶ [Rechargement page vierge]
```

---

# PARTIE 2 : BASE DE DONNÉES

## 2.1 Nouvelles Tables

### Table `societe_diffuseur`
Sociétés clientes qui diffusent des publicités.

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_societe_diffuseur | SERIAL | PRIMARY KEY | Identifiant unique |
| nom | VARCHAR(100) | NOT NULL | Nom de la société |
| email | VARCHAR(150) | UNIQUE | Email de contact |
| telephone | VARCHAR(30) | - | Téléphone de contact |
| adresse | VARCHAR(255) | - | Adresse postale |
| date_creation | TIMESTAMP | DEFAULT NOW() | Date de création |

### Table `tarif_diffusion`
Tarifs de diffusion (par défaut ou spécifiques par société).

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_tarif_diffusion | SERIAL | PRIMARY KEY | Identifiant unique |
| id_societe_diffuseur | INT | FK, NULLABLE | NULL = tarif par défaut |
| id_vol | INT | FK, NULLABLE | NULL = tous les vols |
| id_type_place | INT | FK, NULLABLE | NULL = toutes les classes |
| prix_unitaire | NUMERIC(12,2) | NOT NULL | Prix par diffusion |
| actif | BOOLEAN | DEFAULT TRUE | Tarif actif ou non |
| date_debut_validite | DATE | - | Début de validité |
| date_fin_validite | DATE | - | Fin de validité |

**Logique de tarification :**
1. Chercher tarif spécifique société + vol + type_place
2. Sinon tarif société + vol
3. Sinon tarif société seul
4. Sinon tarif par défaut (id_societe_diffuseur = NULL)

### Table `facture`
Factures mères (regroupement de diffusions).

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_facture | SERIAL | PRIMARY KEY | Identifiant unique |
| numero_facture | VARCHAR(50) | UNIQUE, NOT NULL | Numéro formaté (ex: FAC-2025-001) |
| id_societe_diffuseur | INT | FK, NOT NULL | Société facturée |
| date_facture | DATE | NOT NULL | Date d'émission |
| date_debut_periode | DATE | NOT NULL | Début période facturée |
| date_fin_periode | DATE | NOT NULL | Fin période facturée |
| montant_ht | NUMERIC(12,2) | NOT NULL | Montant HT total |
| taux_tva | NUMERIC(5,2) | DEFAULT 0 | Taux TVA (%) |
| montant_tva | NUMERIC(12,2) | DEFAULT 0 | Montant TVA |
| montant_ttc | NUMERIC(12,2) | NOT NULL | Montant TTC |
| statut | VARCHAR(30) | DEFAULT 'émise' | émise, partiellement_payée, payée, annulée |
| notes | TEXT | - | Remarques |

### Table `detail_facture`
Lignes de facture (factures filles - détails des diffusions).

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_detail_facture | SERIAL | PRIMARY KEY | Identifiant unique |
| id_facture | INT | FK, NOT NULL | Facture parente |
| id_vol_programme | INT | FK, NULLABLE | Vol programmé concerné |
| id_type_place | INT | FK, NULLABLE | Classe concernée |
| description | VARCHAR(255) | - | Description libre |
| nombre_diffusions | INT | NOT NULL | Nombre de diffusions |
| prix_unitaire | NUMERIC(12,2) | NOT NULL | Prix unitaire appliqué |
| montant_ligne | NUMERIC(12,2) | NOT NULL | = nombre_diffusions × prix_unitaire |

### Table `paiement`
Paiements reçus (supporte paiements multiples par facture).

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_paiement | SERIAL | PRIMARY KEY | Identifiant unique |
| id_facture | INT | FK, NOT NULL | Facture payée |
| id_echeance | INT | FK, NULLABLE | Échéance associée (si plan) |
| date_paiement | DATE | NOT NULL | Date du paiement |
| montant_paye | NUMERIC(12,2) | NOT NULL | Montant payé |
| mode_paiement | VARCHAR(50) | - | virement, espèces, chèque, mobile |
| reference_paiement | VARCHAR(100) | - | Référence transaction |
| notes | TEXT | - | Remarques |

### Table `plan_echeancier`
Plans de paiement échelonnés.

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_plan_echeancier | SERIAL | PRIMARY KEY | Identifiant unique |
| id_facture | INT | FK, NOT NULL, UNIQUE | Facture concernée |
| montant_total | NUMERIC(12,2) | NOT NULL | Montant total à payer |
| nombre_echeances | INT | NOT NULL | Nombre d'échéances |
| date_debut | DATE | NOT NULL | Date 1ère échéance |
| date_fin_prevue | DATE | NOT NULL | Date dernière échéance prévue |
| statut | VARCHAR(30) | DEFAULT 'actif' | actif, terminé, annulé |

### Table `echeance`
Échéances individuelles d'un plan.

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_echeance | SERIAL | PRIMARY KEY | Identifiant unique |
| id_plan_echeancier | INT | FK, NOT NULL | Plan parent |
| numero_echeance | INT | NOT NULL | Numéro de l'échéance (1, 2, 3...) |
| date_echeance_prevue | DATE | NOT NULL | Date prévue |
| montant_prevu | NUMERIC(12,2) | NOT NULL | Montant prévu |
| date_paiement_reel | DATE | - | Date paiement effectif |
| montant_paye | NUMERIC(12,2) | DEFAULT 0 | Montant effectivement payé |
| statut | VARCHAR(30) | DEFAULT 'en_attente' | en_attente, payée, en_retard |

## 2.2 Vue pour le Calcul du CA

### Vue `v_ca_diffusions`
```sql
-- Agrège le CA des diffusions par société et période
SELECT societe, date_facture, total_diffusions, total_ca
FROM v_ca_diffusions
WHERE date_facture BETWEEN ? AND ?
```

## 2.3 Données Initiales

| Société | Diffusions Dec 2025 | Prix Unitaire | CA |
|---------|---------------------|---------------|-----|
| Vaniala | 20 | 400 000 Ar | 8 000 000 Ar |
| Lewis | 10 | 400 000 Ar | 4 000 000 Ar |
| **TOTAL** | **30** | - | **12 000 000 Ar** |

---

# PARTIE 3 : MÉTIER (BACKEND)

## 3.1 Classes (Entités)

### Package `com.fly.andco.model.diffusions`

| Classe | Table | Description |
|--------|-------|-------------|
| `SocieteDiffuseur` | societe_diffuseur | Entité société cliente |
| `TarifDiffusion` | tarif_diffusion | Entité tarif diffusion |
| `Facture` | facture | Entité facture mère |
| `DetailFacture` | detail_facture | Entité ligne de facture |
| `Paiement` | paiement | Entité paiement |
| `PlanEcheancier` | plan_echeancier | Entité plan de paiement |
| `Echeance` | echeance | Entité échéance |

### DTO (Data Transfer Objects)

| Classe | Description |
|--------|-------------|
| `CADiffusionDTO` | Résultat du calcul CA (totalDiffusions, totalCA) |
| `CAParSocieteDTO` | Détail CA par société (nomSociete, nbDiffusions, ca) |
| `FiltreCADTO` | Critères de filtrage (idSociete, dateDebut, dateFin) |

## 3.2 Repositories

### Package `com.fly.andco.repository.diffusions`

| Interface | Méthodes clés | Tables utilisées |
|-----------|---------------|------------------|
| `SocieteDiffuseurRepository` | `findAll()`, `findById()` | societe_diffuseur |
| `TarifDiffusionRepository` | `findTarifApplicable()` | tarif_diffusion |
| `FactureRepository` | `findByFilters()`, `sumMontantHT()` | facture |
| `DetailFactureRepository` | `findByFacture()`, `sumDiffusions()` | detail_facture |
| `PaiementRepository` | `findByFacture()`, `sumPaiements()` | paiement |
| `PlanEcheancierRepository` | `findByFacture()` | plan_echeancier |
| `EcheanceRepository` | `findByPlan()` | echeance |

### Requêtes JPQL importantes

```java
// FactureRepository
@Query("SELECT SUM(f.montantHt) FROM Facture f " +
       "WHERE (:idSociete IS NULL OR f.societeDiffuseur.id = :idSociete) " +
       "AND f.dateFacture BETWEEN :dateDebut AND :dateFin " +
       "AND f.statut != 'annulée'")
BigDecimal calculerCA(@Param("idSociete") Long idSociete, 
                      @Param("dateDebut") LocalDate dateDebut, 
                      @Param("dateFin") LocalDate dateFin);

// DetailFactureRepository
@Query("SELECT SUM(d.nombreDiffusions) FROM DetailFacture d " +
       "JOIN d.facture f " +
       "WHERE (:idSociete IS NULL OR f.societeDiffuseur.id = :idSociete) " +
       "AND f.dateFacture BETWEEN :dateDebut AND :dateFin")
Integer compterDiffusions(@Param("idSociete") Long idSociete,
                          @Param("dateDebut") LocalDate dateDebut,
                          @Param("dateFin") LocalDate dateFin);
```

## 3.3 Services

### Package `com.fly.andco.service.diffusions`

#### `DiffusionService`

| Méthode | Signature | Retour | Tables/Vues |
|---------|-----------|--------|-------------|
| `calculerCA` | `(Long idSociete, LocalDate debut, LocalDate fin)` | `CADiffusionDTO` | facture, detail_facture |
| `calculerCAParSociete` | `(LocalDate debut, LocalDate fin)` | `List<CAParSocieteDTO>` | facture, detail_facture, societe_diffuseur |
| `getAllSocietes` | `()` | `List<SocieteDiffuseur>` | societe_diffuseur |
| `getTarifApplicable` | `(Long idSociete, Long idVol, Long idTypePlace)` | `BigDecimal` | tarif_diffusion |

#### `FactureService`

| Méthode | Signature | Retour | Tables |
|---------|-----------|--------|--------|
| `creerFacture` | `(FactureDTO dto)` | `Facture` | facture, detail_facture |
| `ajouterPaiement` | `(Long idFacture, PaiementDTO dto)` | `Paiement` | paiement, facture |
| `getMontantRestant` | `(Long idFacture)` | `BigDecimal` | facture, paiement |
| `creerPlanEcheancier` | `(Long idFacture, int nbEcheances)` | `PlanEcheancier` | plan_echeancier, echeance |

## 3.4 Contrôleurs

### Package `com.fly.andco.controller.diffusions`

#### `DiffusionController`

| Endpoint | Méthode HTTP | Fonction | Service appelé |
|----------|--------------|----------|----------------|
| `/diffusions/ca` | GET | `afficherCA()` | `DiffusionService.calculerCA()`, `calculerCAParSociete()` |

**Paramètres GET :**
- `idSociete` (Long, optionnel)
- `dateDebut` (LocalDate, optionnel)
- `dateFin` (LocalDate, optionnel)

**Modèle Thymeleaf :**
- `societes` : Liste des sociétés pour le dropdown
- `totalDiffusions` : Nombre total de diffusions
- `totalCA` : CA total formaté
- `detailsParSociete` : Liste des CA par société
- `filtreActif` : Boolean indiquant si un filtre est appliqué

---

# CHECKLIST DÉVELOPPEUR

## Base de données
- [ ] Créer le script `07_diffusion_publicitaire.sql`
- [ ] Ajouter les tables : societe_diffuseur, tarif_diffusion, facture, detail_facture, paiement, plan_echeancier, echeance
- [ ] Créer la vue v_ca_diffusions
- [ ] Insérer les données initiales (Vaniala, Lewis, tarif 400 000 Ar)
- [ ] Créer les factures de décembre 2025

## Backend Java
- [ ] Créer le package `model/diffusions/`
- [ ] Créer les 7 entités JPA
- [ ] Créer les DTOs
- [ ] Créer le package `repository/diffusions/`
- [ ] Créer les 7 repositories avec requêtes JPQL
- [ ] Créer le package `service/diffusions/`
- [ ] Créer `DiffusionService` avec méthodes de calcul CA
- [ ] Créer `FactureService` pour gestion factures
- [ ] Créer le package `controller/diffusions/`
- [ ] Créer `DiffusionController`

## Frontend
- [ ] Créer le dossier `templates/views/diffusions/`
- [ ] Créer `ca.html` (page CA diffusions)
- [ ] Ajouter le lien dans `sidebar.html`

## Tests
- [ ] Vérifier que le CA décembre 2025 = 12 000 000 Ar
- [ ] Vérifier le filtre par société
- [ ] Vérifier le filtre par dates
