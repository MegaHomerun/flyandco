# Modifications 2026-01-29 : Produits Extra et CA Mensuel

## Résumé
- Ajout de la gestion des produits extra vendus à bord
- Calcul du CA mensuel avec détail par type (Tickets, Diffusions, Produits)
- Facturation des produits avec logique prorata pour paiements partiels

---

# 1. AFFICHAGES

## 1.1 Page Liste Produits Extra

### Écran: `/produits`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  GESTION DES PRODUITS EXTRA                           │
│            │                                                         │
│            │  [+ Nouveau Produit]                                   │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Code    │ Nom           │ Catégorie │ Prix     │   │
│            │  │─────────│───────────────│───────────│──────────│   │
│            │  │CHOCO-001│Tablette choco │ Snacks    │ 5 000 Ar │   │
│            │  │         │               │           │ [Éditer] │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### Champs de la liste:
| Champ | Type | Source |
|-------|------|--------|
| Code | Texte | `produit_extra.code_produit` |
| Nom | Texte | `produit_extra.nom` |
| Catégorie | Texte | `categorie_produit.nom` via JOIN |
| Prix | Nombre formaté | `produit_extra.prix_unitaire` |

### Actions:
- **[+ Nouveau Produit]** → Ouvre formulaire création → `/produits/nouveau`
- **[Éditer]** → Ouvre formulaire édition → `/produits/edit/{id}`

---

## 1.2 Formulaire Produit Extra

### Écran: `/produits/nouveau` ou `/produits/edit/{id}`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  NOUVEAU PRODUIT / MODIFIER PRODUIT                   │
│            │                                                         │
│            │  Code produit*:    [___________]  (texte, max 20)     │
│            │  Nom*:             [___________]  (texte, max 100)    │
│            │  Description:      [___________]  (texte, max 255)    │
│            │  Catégorie*:       [▼ Sélectionner ]                   │
│            │  Prix unitaire*:   [___________] Ar  (nombre)         │
│            │  Actif:            [✓]              (checkbox)         │
│            │                                                         │
│            │  [Enregistrer]  [Annuler]                              │
└─────────────────────────────────────────────────────────────────────┘
```

### Champs du formulaire:
| Champ | Type | Obligatoire | Source liste déroulante |
|-------|------|-------------|-------------------------|
| Code produit | Input texte | Oui | - |
| Nom | Input texte | Oui | - |
| Description | Input texte | Non | - |
| Catégorie | Select | Oui | `CategorieProduitRepository.findAllActives()` → `categorie_produit WHERE actif=true` |
| Prix unitaire | Input nombre | Oui | - |
| Actif | Checkbox | Non | Défaut: true |

### Actions:
- **[Enregistrer]** → `ProduitService.saveProduit(produit)` → INSERT/UPDATE `produit_extra` → Redirige `/produits`
- **[Annuler]** → Redirige `/produits`

---

## 1.3 Page Ventes Produits par Vol

### Écran: `/ventes-produits`
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
│            │  │20/01/26  │TNR→NOS 10h │Choco      │ 15  │75 000 │   │
│            │  │21/01/26  │TNR→NOS 10h │Choco      │ 20  │100 000│   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  Total: 35 produits | 175 000 Ar                       │
└─────────────────────────────────────────────────────────────────────┘
```

### Actions:
- **[+ Nouvelle Vente]** → Ouvre formulaire → `/ventes-produits/nouveau`

---

## 1.4 Formulaire Vente Produit

### Écran: `/ventes-produits/nouveau`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  ENREGISTRER VENTE PRODUIT                            │
│            │                                                         │
│            │  Vol programmé*:  [▼ Sélectionner vol ]                │
│            │  Produit*:        [▼ Sélectionner produit ]            │
│            │  Quantité*:       [___]                                │
│            │  Prix unitaire:   5 000 Ar (auto-rempli)               │
│            │  Montant total:   25 000 Ar (calculé auto)             │
│            │  Notes:           [___________]                        │
│            │                                                         │
│            │  [Enregistrer]  [Annuler]                              │
└─────────────────────────────────────────────────────────────────────┘
```

### Champs du formulaire:
| Champ | Type | Obligatoire | Source liste déroulante |
|-------|------|-------------|-------------------------|
| Vol programmé | Select | Oui | `ProduitService.getAllVolsProgrammes()` → `vol_programme` + `aeroport` |
| Produit | Select | Oui | `ProduitService.getProduitsActifs()` → `produit_extra WHERE actif=true` |
| Quantité | Input nombre | Oui | - |
| Prix unitaire | Affichage | - | Auto: `produit_extra.prix_unitaire` |
| Montant total | Affichage | - | Calculé JS: quantité × prix_unitaire |
| Notes | Input texte | Non | - |

### Actions:
- **[Enregistrer]** → `ProduitService.creerVente(idVol, idProduit, qte, notes)` → INSERT `vente_produit` → Redirige `/ventes-produits`

---

## 1.5 Page CA Mensuel

### Écran: `/ca-mensuel`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CHIFFRE D'AFFAIRES MENSUEL                           │
│            │                                                         │
│            │  Mois: [▼ Janvier] Année: [2026]  [Afficher]          │
│            │                                                         │
│            │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│            │  │🎫Tickets │ │📺Diffus. │ │🍫Produits│ │💰 TOTAL  │   │
│            │  │96M Ar    │ │2M Ar     │ │300K Ar   │ │98.3M Ar  │   │
│            │  │120 rés.  │ │5 diff.   │ │60 prods  │ │185 ops   │   │
│            │  └──────────┘ └──────────┘ └[Détail]──┘ └──────────┘   │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ TYPE           │ NOMBRE    │ MONTANT    │ 👁️   │   │
│            │  │────────────────│───────────│────────────│──────│   │
│            │  │ Tickets        │ 120       │ 96 000 000 │  👁️  │   │
│            │  │ Diffusions     │ 5         │ 2 000 000  │  👁️  │   │
│            │  │ Produits Extra │ 60        │ 300 000    │  👁️  │   │
│            │  │ TOTAL          │ 185       │ 98 300 000 │      │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

### Champs de la liste:
| Champ | Type | Source |
|-------|------|--------|
| Type | Texte | Constantes (Tickets/Diffusions/Produits) |
| Nombre | Entier | `CAMensuelService.getCAMensuel()` |
| Montant | Nombre formaté | `CAMensuelService.getCAMensuel()` |

### Actions:
- **[Afficher]** → `CAMensuelService.getCAMensuel(annee, mois)` → Rafraîchit page
- **[👁️ Produits]** → `/ca-mensuel/produits?annee=2026&mois=1`

---

## 1.6 Page Détail CA Produits

### Écran: `/ca-mensuel/produits?annee=2026&mois=1`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CA PRODUITS - JANVIER 2026                           │
│            │                                                         │
│            │  [← Retour CA Mensuel]                                 │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ Date   │ Vol       │ Produit │ Qté │ Montant    │   │
│            │  │────────│───────────│─────────│─────│────────────│   │
│            │  │20/01   │TNR→NOS 10h│ Choco   │ 15  │ 75 000 Ar  │   │
│            │  │21/01   │TNR→NOS 10h│ Choco   │ 20  │ 100 000 Ar │   │
│            │  │21/01   │TNR→NOS 15h│ Choco   │ 25  │ 125 000 Ar │   │
│            │  │────────│───────────│─────────│─────│────────────│   │
│            │  │ TOTAL  │           │         │ 60  │ 300 000 Ar │   │
│            │  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 1.7 Flux des pages

```
┌──────────────┐
│  Dashboard   │
└──────┬───────┘
       │
       ├─────────────────┬─────────────────┬─────────────────┐
       ▼                 ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Produits   │  │Ventes Produit│  │  CA Mensuel  │  │  (autres)    │
│   /produits  │  │/ventes-produ │  │  /ca-mensuel │  │              │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────────────┘
       │                 │                 │
       ▼                 ▼                 ├──► /ca-mensuel/produits
┌──────────────┐  ┌──────────────┐         └──► /ca-mensuel/factures-produits
│ Form Produit │  │ Form Vente   │                      │
│/produits/new │  │/ventes/new   │                      ▼
└──────────────┘  └──────────────┘              ┌──────────────┐
                                               │Détail Facture│
                                               │  + Paiement  │
                                               └──────────────┘
```

---

# 2. BASE DE DONNÉES

## 2.1 Nouvelles Tables

### `categorie_produit`
| Colonne | Type | Description |
|---------|------|-------------|
| id_categorie_produit | SERIAL PK | ID auto |
| nom | VARCHAR(100) UNIQUE | Nom catégorie |
| description | VARCHAR(255) | Description |
| actif | BOOLEAN | Actif ou non |

### `produit_extra`
| Colonne | Type | Description |
|---------|------|-------------|
| id_produit_extra | SERIAL PK | ID auto |
| id_categorie_produit | INT FK | Réf catégorie |
| code_produit | VARCHAR(20) UNIQUE | Code unique |
| nom | VARCHAR(100) | Nom produit |
| description | VARCHAR(255) | Description |
| prix_unitaire | NUMERIC(12,2) | Prix en Ar |
| actif | BOOLEAN | Actif ou non |

### `vente_produit`
| Colonne | Type | Description |
|---------|------|-------------|
| id_vente_produit | SERIAL PK | ID auto |
| id_vol_programme | INT FK | Vol concerné |
| id_produit_extra | INT FK | Produit vendu |
| quantite | INT | Quantité vendue |
| prix_unitaire | NUMERIC(12,2) | Prix au moment vente |
| montant_total | NUMERIC(12,2) | quantite × prix |
| date_vente | TIMESTAMP | Date/heure vente |

### `facture_produit`
| Colonne | Type | Description |
|---------|------|-------------|
| id_facture_produit | SERIAL PK | ID auto |
| numero_facture | VARCHAR(50) UNIQUE | Numéro facture |
| date_facture | DATE | Date émission |
| date_debut_periode | DATE | Début période |
| date_fin_periode | DATE | Fin période |
| montant | NUMERIC(12,2) | Montant total |
| statut | VARCHAR(30) | émise/partiellement_payée/payée/annulée |

### `detail_facture_produit`
| Colonne | Type | Description |
|---------|------|-------------|
| id_detail_facture_produit | SERIAL PK | ID auto |
| id_facture_produit | INT FK | Réf facture mère |
| id_vente_produit | INT FK | Réf vente |
| id_vol_programme | INT FK | Vol concerné |
| id_produit_extra | INT FK | Produit |
| quantite | INT | Quantité ligne |
| prix_unitaire | NUMERIC(12,2) | Prix unitaire |
| montant_ligne | NUMERIC(12,2) | Total ligne |
| montant_paye | NUMERIC(12,2) | Montant payé (prorata) |

### `paiement_produit`
| Colonne | Type | Description |
|---------|------|-------------|
| id_paiement_produit | SERIAL PK | ID auto |
| id_facture_produit | INT FK | Réf facture |
| date_paiement | DATE | Date paiement |
| montant_paye | NUMERIC(12,2) | Montant |
| mode_paiement | VARCHAR(50) | virement/espèces/chèque/mobile_money/carte |
| reference_paiement | VARCHAR(100) | Référence |

---

## 2.2 Nouvelles Vues

### `v_ca_produits_vol`
**Objectif:** Calculer le CA des produits vendus par vol programmé.

**Tables utilisées:**
```
vente_produit (vp)
    └── vol_programme (volp) via vp.id_vol_programme
           ├── vol (v) via volp.id_vol
           ├── aeroport depart (ad) via volp.id_aeroport_depart
           └── aeroport arrivee (aa) via volp.id_aeroport_arrivee
    └── produit_extra (pe) via vp.id_produit_extra
           └── categorie_produit (cp) via pe.id_categorie_produit
```

**Jointures:**
| Table source | Table cible | Clé FK |
|--------------|-------------|--------|
| `vente_produit` | `vol_programme` | `id_vol_programme` |
| `vol_programme` | `aeroport` (départ) | `id_aeroport_depart` |
| `vol_programme` | `aeroport` (arrivée) | `id_aeroport_arrivee` |
| `vente_produit` | `produit_extra` | `id_produit_extra` |
| `produit_extra` | `categorie_produit` | `id_categorie_produit` |

**Opérations:**
- GROUP BY `id_vol_programme`, `date_vol`
- SUM `quantite` → total quantité vendue par vol
- SUM `montant_total` → CA total par vol

---

### `v_ca_mensuel`
**Objectif:** Agréger le CA total par mois en combinant tickets, diffusions et produits.

**Tables utilisées:**
```
1. CA Tickets:
   detail_reservation (dr)
       └── reservation (r) via dr.id_reservation
              └── vol_programme (vp) via r.id_vol_programme

2. CA Diffusions:
   detail_facture (df)
       └── facture (f) via df.id_facture
       └── diffusion (d) via df.id_diffusion
              └── vol_programme (vp) via d.id_vol_programme

3. CA Produits:
   vente_produit (vp)
       └── vol_programme (volp) via vp.id_vol_programme
```

**Jointures pour CA Tickets:**
| Table source | Table cible | Clé FK | Opération |
|--------------|-------------|--------|-----------|
| `detail_reservation` | `reservation` | `id_reservation` | INNER JOIN |
| `reservation` | `vol_programme` | `id_vol_programme` | INNER JOIN |

**Jointures pour CA Diffusions:**
| Table source | Table cible | Clé FK | Opération |
|--------------|-------------|--------|-----------|
| `detail_facture` | `facture` | `id_facture` | INNER JOIN |
| `detail_facture` | `diffusion` | `id_diffusion` | INNER JOIN |
| `diffusion` | `vol_programme` | `id_vol_programme` | INNER JOIN |

**Jointures pour CA Produits:**
| Table source | Table cible | Clé FK | Opération |
|--------------|-------------|--------|-----------|
| `vente_produit` | `vol_programme` | `id_vol_programme` | INNER JOIN |

**Opérations:**
- EXTRACT(YEAR/MONTH FROM date_vol) → groupement par mois
- COUNT(*) → nombre d'opérations par type
- SUM(montant) → CA par type
- UNION ALL des 3 sources avec type='TICKET'|'DIFFUSION'|'PRODUIT'

---

### `v_ca_mensuel_detail`
**Objectif:** Fournir un résumé mensuel avec colonnes séparées pour chaque type de CA.

**Dérivée de:** `v_ca_mensuel`

**Structure résultat:**
| Colonne | Calcul |
|---------|--------|
| `annee` | EXTRACT(YEAR FROM date_vol) |
| `mois` | EXTRACT(MONTH FROM date_vol) |
| `nb_tickets` | COUNT WHERE type='TICKET' |
| `ca_tickets` | SUM(montant) WHERE type='TICKET' |
| `nb_diffusions` | COUNT WHERE type='DIFFUSION' |
| `ca_diffusions` | SUM(montant) WHERE type='DIFFUSION' |
| `nb_produits` | COUNT WHERE type='PRODUIT' |
| `ca_produits` | SUM(montant) WHERE type='PRODUIT' |
| `ca_total` | SUM(ca_tickets + ca_diffusions + ca_produits) |

**Opérations:**
- Pivotage avec FILTER (WHERE type = ...) 
- GROUP BY annee, mois
- COALESCE pour gérer les NULL → 0

---

## 2.3 Nouvelles Fonctions

### `update_facture_produit_statut()`
**Type:** Trigger AFTER INSERT/UPDATE sur `paiement_produit`

**Logique:**
1. Calcule la somme des paiements pour la facture concernée
2. Compare avec le montant total de la facture
3. Met à jour le statut:
   - `payée` si somme_paiements >= montant
   - `partiellement_payée` si 0 < somme_paiements < montant
   - `émise` si aucun paiement

```sql
-- Pseudocode
total_paye = SELECT SUM(montant_paye) FROM paiement_produit WHERE id_facture = NEW.id_facture;
montant_facture = SELECT montant FROM facture_produit WHERE id_facture = NEW.id_facture;

IF total_paye >= montant_facture THEN
    UPDATE facture_produit SET statut = 'payée';
ELSIF total_paye > 0 THEN
    UPDATE facture_produit SET statut = 'partiellement_payée';
END IF;
```

---

### `repartir_paiement_produit_prorata(id_facture, montant)`
**Type:** Fonction appelée lors d'un paiement partiel

**Principe du prorata:**
Quand une facture contient plusieurs lignes (plusieurs ventes de produits), un paiement partiel est réparti proportionnellement au montant de chaque ligne.

**Exemple:**
```
Facture: 100 000 Ar
├── Ligne 1 (chocolats): 60 000 Ar (60% du total)
└── Ligne 2 (boissons): 40 000 Ar (40% du total)

Paiement de 50 000 Ar → répartition:
├── Ligne 1: 50 000 × 60% = 30 000 Ar
└── Ligne 2: 50 000 × 40% = 20 000 Ar
```

**Logique:**
```sql
-- Pour chaque ligne de détail de la facture:
UPDATE detail_facture_produit
SET montant_paye = montant_paye + (montant_paiement * (montant_ligne / montant_total_facture))
WHERE id_facture_produit = id_facture;
```

---

## 2.4 Requêtes SQL du Service CA Mensuel

### Requête: Calcul CA Tickets
```sql
SELECT 
    EXTRACT(YEAR FROM vp.date_vol) AS annee,
    EXTRACT(MONTH FROM vp.date_vol) AS mois,
    COUNT(DISTINCT r.id_reservation) AS nb_tickets,
    COALESCE(SUM(dr.prix_paye), 0) AS ca_tickets
FROM detail_reservation dr
INNER JOIN reservation r ON dr.id_reservation = r.id_reservation
INNER JOIN vol_programme vp ON r.id_vol_programme = vp.id_vol_programme
WHERE vp.date_vol BETWEEN :dateDebut AND :dateFin
GROUP BY EXTRACT(YEAR FROM vp.date_vol), EXTRACT(MONTH FROM vp.date_vol)
```

### Requête: Calcul CA Diffusions
```sql
SELECT 
    EXTRACT(YEAR FROM vp.date_vol) AS annee,
    EXTRACT(MONTH FROM vp.date_vol) AS mois,
    COUNT(DISTINCT d.id_diffusion) AS nb_diffusions,
    COALESCE(SUM(df.montant_paye), 0) AS ca_diffusions
FROM detail_facture df
INNER JOIN facture f ON df.id_facture = f.id_facture
INNER JOIN diffusion d ON df.id_diffusion = d.id_diffusion
INNER JOIN vol_programme vp ON d.id_vol_programme = vp.id_vol_programme
WHERE vp.date_vol BETWEEN :dateDebut AND :dateFin
GROUP BY EXTRACT(YEAR FROM vp.date_vol), EXTRACT(MONTH FROM vp.date_vol)
```

### Requête: Calcul CA Produits
```sql
SELECT 
    EXTRACT(YEAR FROM vp.date_vol) AS annee,
    EXTRACT(MONTH FROM vp.date_vol) AS mois,
    COUNT(*) AS nb_produits,
    COALESCE(SUM(vente.montant_total), 0) AS ca_produits
FROM vente_produit vente
INNER JOIN vol_programme vp ON vente.id_vol_programme = vp.id_vol_programme
WHERE vp.date_vol BETWEEN :dateDebut AND :dateFin
GROUP BY EXTRACT(YEAR FROM vp.date_vol), EXTRACT(MONTH FROM vp.date_vol)
```

---

## 2.5 Script SQL
Fichier: `src/bd/10_produits_extra.sql`

---

# 3. MÉTIER (Classes et Fonctions)

## 3.1 Scénario: Gestion des Produits

### Classes
| Classe | Package | Tables utilisées |
|--------|---------|------------------|
| `CategorieProduit` | model | `categorie_produit` |
| `ProduitExtra` | model | `produit_extra` |
| `CategorieProduitRepository` | repository | `categorie_produit` |
| `ProduitExtraRepository` | repository | `produit_extra` |
| `ProduitExtraService` | service | via repositories |
| `ProduitExtraController` | controller | via service |

### Fonctions ProduitExtraService
| Signature | Retour | Tables |
|-----------|--------|--------|
| `findAll()` | `List<ProduitExtra>` | `produit_extra` + `categorie_produit` |
| `findById(Long id)` | `Optional<ProduitExtra>` | `produit_extra` |
| `findAllActifs()` | `List<ProduitExtra>` | `produit_extra WHERE actif=true` |
| `save(ProduitExtra p)` | `ProduitExtra` | INSERT/UPDATE `produit_extra` |
| `delete(Long id)` | `void` | DELETE `produit_extra` |

### Fonctions ProduitExtraController
| Signature | Méthode | Action |
|-----------|---------|--------|
| `list(Model)` | GET `/produits` | Affiche liste |
| `showForm(Model)` | GET `/produits/nouveau` | Formulaire création |
| `showEditForm(Long id, Model)` | GET `/produits/edit/{id}` | Formulaire édition |
| `save(ProduitExtra, RedirectAttr)` | POST `/produits/save` | Enregistre |

---

## 3.2 Scénario: Ventes Produits à Bord

### Classes
| Classe | Package | Tables utilisées |
|--------|---------|------------------|
| `VenteProduit` | model | `vente_produit` |
| `VenteProduitRepository` | repository | `vente_produit` |
| `VenteProduitService` | service | via repositories |
| `VenteProduitController` | controller | via service |

### Fonctions VenteProduitService
| Signature | Retour | Tables |
|-----------|--------|--------|
| `findAll()` | `List<VenteProduit>` | `vente_produit` + JOINs |
| `findByVolProgramme(Long idVp)` | `List<VenteProduit>` | `vente_produit` |
| `findByPeriode(Date debut, Date fin)` | `List<VenteProduit>` | `vente_produit` |
| `save(VenteProduit v)` | `VenteProduit` | INSERT `vente_produit` |
| `getTotalByPeriode(Date d, Date f)` | `BigDecimal` | SUM `vente_produit` |

### Fonctions VenteProduitController
| Signature | Méthode | Action |
|-----------|---------|--------|
| `list(Model, filtres)` | GET `/ventes-produits` | Affiche liste |
| `showForm(Model)` | GET `/ventes-produits/nouveau` | Formulaire vente |
| `save(VenteProduit, RedirectAttr)` | POST `/ventes-produits/save` | Enregistre vente |

---

## 3.3 Scénario: CA Mensuel

### Classes
| Classe | Package | Tables/Vues utilisées |
|--------|---------|----------------------|
| `CAMensuelDTO` | dto | - |
| `CAMensuelDetailDTO` | dto | - |
| `CAMensuelService` | service | Vues `v_ca_mensuel`, `v_ca_mensuel_detail` |
| `CAMensuelController` | controller | via service |

### Fonctions CAMensuelService
| Signature | Retour | Vues/Tables |
|-----------|--------|-------------|
| `getCAMensuel(YearMonth mois)` | `CAMensuelDTO` | `v_ca_mensuel_detail` |
| `getDetailTickets(YearMonth)` | `List<DetailTicketDTO>` | `detail_reservation` + JOINs |
| `getDetailDiffusions(YearMonth)` | `List<DetailDiffusionDTO>` | `detail_facture` + JOINs |
| `getDetailProduits(YearMonth)` | `List<VenteProduit>` | `vente_produit` + JOINs |
| `getAllMoisDisponibles()` | `List<YearMonth>` | DISTINCT sur dates |

### Fonctions CAMensuelController
| Signature | Méthode | Action |
|-----------|---------|--------|
| `showCAMensuel(YearMonth, Model)` | GET `/ca-mensuel` | Page CA global |
| `detailTickets(YearMonth, Model)` | GET `/ca-mensuel/tickets` | Détail tickets |
| `detailDiffusions(YearMonth, Model)` | GET `/ca-mensuel/diffusions` | Détail diffusions |
| `detailProduits(YearMonth, Model)` | GET `/ca-mensuel/produits` | Détail produits |

---

## 3.4 Scénario: Facturation Produits

### Classes
| Classe | Package | Tables utilisées |
|--------|---------|------------------|
| `FactureProduit` | model | `facture_produit` |
| `DetailFactureProduit` | model | `detail_facture_produit` |
| `PaiementProduit` | model | `paiement_produit` |
| `FactureProduitRepository` | repository | `facture_produit` |
| `DetailFactureProduitRepository` | repository | `detail_facture_produit` |
| `PaiementProduitRepository` | repository | `paiement_produit` |
| `FactureProduitService` | service | via repositories |

### Fonctions FactureProduitService
| Signature | Retour | Tables |
|-----------|--------|--------|
| `creerFacture(Date debut, Date fin)` | `FactureProduit` | INSERT `facture_produit` + `detail_facture_produit` |
| `findAll()` | `List<FactureProduit>` | `facture_produit` |
| `findById(Long id)` | `Optional<FactureProduit>` | `facture_produit` + details |
| `enregistrerPaiement(Long idFact, Paiement p)` | `PaiementProduit` | INSERT `paiement_produit` |
| `getResteAPayer(Long idFacture)` | `BigDecimal` | calcul montant - SUM paiements |

---

# 4. TODO DÉVELOPPEUR

## 4.1 Base de données
- [ ] Exécuter `10_produits_extra.sql`
- [ ] Vérifier création tables et vues
- [ ] Vérifier données test insérées

## 4.2 Backend (Java) ✅ TERMINÉ
- [x] Créer entités: `CategorieProduit`, `ProduitExtra`, `VenteProduit`
- [x] Créer entités: `FactureProduit`, `DetailFactureProduit`, `PaiementProduit`
- [x] Créer repositories correspondants
- [x] Créer `ProduitService` avec CRUD
- [x] Créer `CAMensuelService` avec requêtes natives
- [x] Créer `CAMensuelDTO` pour les données
- [x] Créer controllers avec endpoints (`ProduitController`, `VenteProduitController`, `CAMensuelController`)

## 4.3 Frontend (Thymeleaf) ✅ TERMINÉ
- [x] Créer template `views/produits/list.html`
- [x] Créer template `views/produits/form.html`
- [x] Créer template `views/produits/ventes-list.html`
- [x] Créer template `views/produits/vente-form.html`
- [x] Créer template `views/produits/ca-mensuel.html`
- [x] Créer template `views/produits/ca-detail-produits.html`
- [x] Créer template `views/produits/factures-list.html`
- [x] Créer template `views/produits/facture-detail.html`
- [x] Créer template `views/produits/facture-form.html`
- [x] Créer template `views/produits/paiement-form.html`
- [x] Ajouter liens sidebar (menu Produits Extra + lien CA Mensuel)

## 4.4 Tests
- [ ] Tester CRUD produits
- [ ] Tester enregistrement ventes
- [ ] Tester calcul CA mensuel
- [ ] Tester paiement prorata factures

---

# 5. FICHIERS CRÉÉS

## 5.1 Modèles (Entités JPA)
| Fichier | Chemin |
|---------|--------|
| `CategorieProduit.java` | `src/main/java/com/fly/andco/model/produits/` |
| `ProduitExtra.java` | `src/main/java/com/fly/andco/model/produits/` |
| `VenteProduit.java` | `src/main/java/com/fly/andco/model/produits/` |
| `FactureProduit.java` | `src/main/java/com/fly/andco/model/produits/` |
| `DetailFactureProduit.java` | `src/main/java/com/fly/andco/model/produits/` |
| `PaiementProduit.java` | `src/main/java/com/fly/andco/model/produits/` |

## 5.2 Repositories
| Fichier | Chemin |
|---------|--------|
| `CategorieProduitRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |
| `ProduitExtraRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |
| `VenteProduitRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |
| `FactureProduitRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |
| `DetailFactureProduitRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |
| `PaiementProduitRepository.java` | `src/main/java/com/fly/andco/repository/produits/` |

## 5.3 Services
| Fichier | Chemin |
|---------|--------|
| `ProduitService.java` | `src/main/java/com/fly/andco/service/produits/` |
| `CAMensuelService.java` | `src/main/java/com/fly/andco/service/produits/` |
| `CAMensuelDTO.java` | `src/main/java/com/fly/andco/service/produits/` |

## 5.4 Controllers
| Fichier | Chemin |
|---------|--------|
| `ProduitController.java` | `src/main/java/com/fly/andco/controller/produits/` |
| `VenteProduitController.java` | `src/main/java/com/fly/andco/controller/produits/` |
| `CAMensuelController.java` | `src/main/java/com/fly/andco/controller/produits/` |

## 5.5 Templates Thymeleaf
| Fichier | Chemin |
|---------|--------|
| `list.html` | `src/main/resources/templates/views/produits/` |
| `form.html` | `src/main/resources/templates/views/produits/` |
| `ventes-list.html` | `src/main/resources/templates/views/produits/` |
| `vente-form.html` | `src/main/resources/templates/views/produits/` |
| `ca-mensuel.html` | `src/main/resources/templates/views/produits/` |
| `ca-detail-produits.html` | `src/main/resources/templates/views/produits/` |
| `factures-list.html` | `src/main/resources/templates/views/produits/` |
| `facture-detail.html` | `src/main/resources/templates/views/produits/` |
| `facture-form.html` | `src/main/resources/templates/views/produits/` |
| `paiement-form.html` | `src/main/resources/templates/views/produits/` |

## 5.6 Fichiers modifiés
| Fichier | Modification |
|---------|--------------|
| `sidebar.html` | Ajout menu "Produits Extra" et lien "CA Mensuel" |
