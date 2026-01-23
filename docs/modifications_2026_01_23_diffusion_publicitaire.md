# Fonctionnalité : Diffusion Vidéos Publicitaires
**Date : 2026-01-23**

---

## 📋 RÉSUMÉ

Les sociétés peuvent diffuser des vidéos publicitaires sur les écrans des avions. La diffusion est payante avec un système de facturation flexible supportant les paiements multiples avec **répartition au prorata proportionnel**.

**Contexte initial :**
- Coût standard : 400 000 Ar / diffusion
- Vaniala : 20 diffusions en décembre 2025
- Lewis : 10 diffusions en décembre 2025

---

# PARTIE 1 : AFFICHAGE (IHM)

## 1.1 Page : Commande de Diffusions (NOUVEAU)

**URL :** `/diffusions/commande`

**Titre de la page :** "Commander des Diffusions Publicitaires"

### Formulaire de Commande

| Champ | Type | Obligatoire | Source des données |
|-------|------|-------------|-------------------|
| Société | Liste déroulante | Oui | Table `societe_diffuseur` |
| Date facture | Date | Oui | Défaut: aujourd'hui |
| Lignes de diffusion | Tableau dynamique | Oui (min 1 ligne) | - |

### Tableau dynamique : Lignes de Diffusion

| Colonne | Type | Source | Calcul |
|---------|------|--------|--------|
| Vol programmé | Liste déroulante | Table `vol_programme` | - |
| Nombre diffusions | Nombre (min 1) | Saisie | - |
| Prix unitaire | Montant (lecture seule) | Table `tarif_diffusion` | Auto |
| Sous-total | Montant (lecture seule) | - | nb × prix unitaire |

### Boutons

| Bouton | Action |
|--------|--------|
| ➕ Ajouter une ligne | Ajoute une nouvelle ligne de diffusion |
| 🗑️ Supprimer | Supprime une ligne |
| Valider la commande | Crée la facture (statut 'émise') et redirige vers paiements |

### Logique Métier

Une fois validée :
1. Une facture est créée avec statut 'émise'
2. Chaque ligne devient un `detail_facture` avec `montant_paye = 0`
3. La facture est **clôturée** (non modifiable)
4. Redirection vers la page de paiements

---

## 1.2 Page : Chiffre d'Affaires Diffusions

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

### Table `facture` (Simplifiée)
Factures mères (regroupement de diffusions) - **clôturées après création**.

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_facture | SERIAL | PRIMARY KEY | Identifiant unique |
| numero_facture | VARCHAR(50) | UNIQUE, NOT NULL | Numéro formaté (ex: FAC-2026-001) |
| id_societe_diffuseur | INT | FK, NOT NULL | Société facturée |
| date_facture | DATE | NOT NULL | Date d'émission |
| date_debut_periode | DATE | NOT NULL | Début période facturée |
| date_fin_periode | DATE | NOT NULL | Fin période facturée |
| montant | NUMERIC(12,2) | NOT NULL | Montant total |
| statut | VARCHAR(30) | DEFAULT 'émise' | émise, partiellement_payée, payée, annulée |
| notes | TEXT | - | Remarques |

### Table `detail_facture` (Avec suivi paiement prorata)
Lignes de facture - **chaque ligne suit son propre montant payé**.

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
| **montant_paye** | NUMERIC(12,2) | DEFAULT 0 | **Montant payé sur cette ligne (prorata)** |

### Table `paiement`
Paiements reçus (supporte paiements multiples par facture).

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| id_paiement | SERIAL | PRIMARY KEY | Identifiant unique |
| id_facture | INT | FK, NOT NULL | Facture payée |
| date_paiement | DATE | NOT NULL | Date du paiement |
| montant_paye | NUMERIC(12,2) | NOT NULL | Montant payé |
| mode_paiement | VARCHAR(50) | - | virement, espèces, chèque, mobile_money, carte |
| reference_paiement | VARCHAR(100) | - | Référence transaction |
| notes | TEXT | - | Remarques |

---

## 2.2 Logique de Répartition Prorata Proportionnel

### Principe

Quand un paiement est effectué sur une facture contenant plusieurs lignes, le montant est réparti **proportionnellement** sur chaque ligne.

### Exemple Concret

**Facture avec 3 lignes :**
| Ligne | Vol | Montant | Part (%) |
|-------|-----|---------|----------|
| 1 | TNR-NOS | 200 Ar | 20% |
| 2 | TNR-MJN | 200 Ar | 20% |
| 3 | TNR-DIE | 600 Ar | 60% |
| **Total** | | **1 000 Ar** | **100%** |

**Paiement de 200 Ar (20% du total) :**
| Ligne | Montant | % du total | Paiement reçu |
|-------|---------|------------|---------------|
| 1 | 200 Ar | 20% | 200 × 20% = **40 Ar** |
| 2 | 200 Ar | 20% | 200 × 20% = **40 Ar** |
| 3 | 600 Ar | 60% | 200 × 60% = **120 Ar** |
| **Total** | | | **200 Ar** ✓ |

### Formule

```
montant_ligne_payé = paiement × (montant_ligne / montant_total_facture)
```

### Algorithme

```java
// Pour chaque paiement
BigDecimal pourcentagePaiement = montantPaiement.divide(facture.getMontant(), 4, RoundingMode.HALF_UP);

for (DetailFacture ligne : facture.getDetails()) {
    BigDecimal partLigne = ligne.getMontantLigne().multiply(pourcentagePaiement);
    ligne.setMontantPaye(ligne.getMontantPaye().add(partLigne));
}
```

---

## 2.3 Données Initiales

| Société | Diffusions Dec 2025 | Prix Unitaire | CA |
|---------|---------------------|---------------|-----|
| Vaniala | 20 | 400 000 Ar | 8 000 000 Ar |
| Lewis | 10 | 400 000 Ar | 4 000 000 Ar |
| **TOTAL** | **30** | - | **12 000 000 Ar** |

**Paiement Vaniala :** 1 000 000 Ar le 15/12/2025 → Reste 7 000 000 Ar

---

# PARTIE 3 : MÉTIER (BACKEND)

## 3.1 Classes (Entités)

### Package `com.fly.andco.model.diffusions`

| Classe | Table | Description |
|--------|-------|-------------|
| `SocieteDiffuseur` | societe_diffuseur | Entité société cliente |
| `TarifDiffusion` | tarif_diffusion | Entité tarif diffusion |
| `Facture` | facture | Entité facture (simplifiée sans TVA) |
| `DetailFacture` | detail_facture | Entité ligne de facture avec montant_paye |
| `Paiement` | paiement | Entité paiement |

### DTO (Data Transfer Objects)

| Classe | Description |
|--------|-------------|
| `CADiffusionDTO` | Résultat du calcul CA (totalDiffusions, totalCA) |
| `CAParSocieteDTO` | Détail CA par société (nomSociete, nbDiffusions, ca) |
| `ResumePaiementDTO` | Résumé paiements (totalFacturé, totalPayé, reste) |
| `CommandeDiffusionDTO` | Commande de diffusion (société, lignes) |
| `LigneDiffusionDTO` | Ligne de commande (vol, nbDiffusions) |

## 3.2 Repositories

### Package `com.fly.andco.repository.diffusions`

| Interface | Méthodes clés |
|-----------|---------------|
| `SocieteDiffuseurRepository` | `findAll()`, `findById()`, `findAllOrderByNom()` |
| `TarifDiffusionRepository` | `getTarifApplicable()` |
| `FactureRepository` | `findBySociete()`, `calculerCA*()`, `findLastNumeroFactureForYear()` |
| `DetailFactureRepository` | `findByFacture()`, `compterDiffusions*()` |
| `PaiementRepository` | `findByFacture()`, `sumMontantPayeByFacture()` |

## 3.3 Services

### `DiffusionService` - Méthodes principales

| Méthode | Description |
|---------|-------------|
| `creerCommande(CommandeDiffusionDTO)` | Crée une facture avec plusieurs lignes |
| `ajouterPaiement(idFacture, montant, ...)` | Ajoute un paiement avec **répartition prorata** |
| `calculerCA(idSociete, dateDebut, dateFin)` | Calcule le CA filtré |
| `getResumePaiementSociete(idSociete)` | Résumé paiements d'une société |

## 3.4 Contrôleurs

### `DiffusionController`

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/diffusions/commande` | GET | Formulaire de commande |
| `/diffusions/commande` | POST | Crée la facture |
| `/diffusions/ca` | GET | Page CA avec filtres |
| `/diffusions/paiements` | GET | Page gestion paiements |
| `/diffusions/paiements/ajouter` | POST | Enregistre un paiement |

---

# CHECKLIST DÉVELOPPEUR

## Base de données
- [x] Créer le script `07_diffusion_publicitaire.sql`
- [x] Tables : societe_diffuseur, tarif_diffusion, facture, detail_facture, paiement
- [x] Vue v_resume_paiement_societe
- [x] Données initiales (Vaniala, Lewis)
- [ ] **Ajouter colonne `montant_paye` à `detail_facture`**

## Backend Java
- [x] Entités JPA (SocieteDiffuseur, Facture, DetailFacture, Paiement, TarifDiffusion)
- [x] Repositories avec requêtes
- [x] DiffusionService (CA, paiements)
- [ ] **Ajouter méthode creerCommande()**
- [ ] **Implémenter répartition prorata dans ajouterPaiement()**

## Frontend
- [x] ca.html (page CA)
- [x] paiements.html (gestion paiements)
- [ ] **commande.html (nouvelle commande)**
- [x] Lien sidebar "Diffusions Pub"
