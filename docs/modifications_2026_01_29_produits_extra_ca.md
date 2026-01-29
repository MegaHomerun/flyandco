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
| Catégorie | Select | Oui | `CategorieProductRepository.findAll()` → `categorie_produit` |
| Prix unitaire | Input nombre | Oui | - |
| Actif | Checkbox | Non | Défaut: true |

### Actions:
- **[Enregistrer]** → `ProduitExtraService.save(produit)` → INSERT/UPDATE `produit_extra` → Redirige `/produits`
- **[Annuler]** → Redirige `/produits`

---

## 1.3 Page Ventes Produits par Vol

### Écran: `/ventes-produits`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  VENTES PRODUITS À BORD                               │
│            │                                                         │
│            │  Filtres: Vol [▼ Tous]  Date [____] à [____]          │
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
| Vol programmé | Select | Oui | `VolProgrammeService.findAll()` → `vol_programme` + `aeroport` |
| Produit | Select | Oui | `ProduitExtraService.findAllActifs()` → `produit_extra WHERE actif=true` |
| Quantité | Input nombre | Oui | - |
| Prix unitaire | Affichage | - | Auto: `produit_extra.prix_unitaire` |
| Montant total | Affichage | - | Calculé: quantité × prix_unitaire |
| Notes | Input texte | Non | - |

### Actions:
- **[Enregistrer]** → `VenteProductService.save(vente)` → INSERT `vente_produit` → Redirige `/ventes-produits`

---

## 1.5 Page CA Mensuel

### Écran: `/ca-mensuel`
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Sidebar]  │  CHIFFRE D'AFFAIRES MENSUEL                           │
│            │                                                         │
│            │  Mois: [▼ Janvier 2026 ]  [Filtrer]                   │
│            │                                                         │
│            │  ┌─────────────────────────────────────────────────┐   │
│            │  │ TYPE           │ NOMBRE    │ MONTANT            │   │
│            │  │────────────────│───────────│────────────────────│   │
│            │  │ 🎫 Tickets     │ 120       │ 96 000 000 Ar      │   │
│            │  │ 📺 Diffusions  │ 5         │ 2 000 000 Ar       │   │
│            │  │ 🍫 Produits    │ 60        │ 300 000 Ar         │   │
│            │  │────────────────│───────────│────────────────────│   │
│            │  │ TOTAL          │ 185       │ 98 300 000 Ar      │   │
│            │  └─────────────────────────────────────────────────┘   │
│            │                                                         │
│            │  [Voir détail Tickets] [Voir détail Diffusions]       │
│            │  [Voir détail Produits]                                │
└─────────────────────────────────────────────────────────────────────┘
```

### Champs de la liste:
| Champ | Type | Source |
|-------|------|--------|
| Type | Texte | Constantes (Tickets/Diffusions/Produits) |
| Nombre | Entier | Vue `v_ca_mensuel_detail` colonnes nb_* |
| Montant | Nombre formaté | Vue `v_ca_mensuel_detail` colonnes ca_* |

### Actions:
- **[Filtrer]** → `CAMensuelService.getByMonth(mois)` → Vue `v_ca_mensuel_detail`
- **[Voir détail Tickets]** → `/ca-mensuel/tickets?mois=2026-01`
- **[Voir détail Diffusions]** → `/ca-mensuel/diffusions?mois=2026-01`
- **[Voir détail Produits]** → `/ca-mensuel/produits?mois=2026-01`

---

## 1.6 Page Détail CA Produits

### Écran: `/ca-mensuel/produits?mois=2026-01`
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
       ▼                 ▼                 ├──► /ca-mensuel/tickets
┌──────────────┐  ┌──────────────┐         ├──► /ca-mensuel/diffusions
│ Form Produit │  │ Form Vente   │         └──► /ca-mensuel/produits
│/produits/new │  │/ventes/new   │
└──────────────┘  └──────────────┘
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
CA produits par vol programmé avec détail par produit.

### `v_ca_mensuel`
CA agrégé par mois et par type (Tickets, Diffusions, Produits).

### `v_ca_mensuel_detail`
CA mensuel détaillé avec colonnes séparées:
- nb_tickets, ca_tickets
- nb_diffusions, ca_diffusions
- nb_produits, ca_produits
- ca_total

---

## 2.3 Nouvelles Fonctions

### `update_facture_produit_statut()`
Trigger après INSERT/UPDATE sur `paiement_produit`.
Met à jour automatiquement le statut de `facture_produit`.

### `repartir_paiement_produit_prorata(id_facture, montant)`
Répartit un paiement au prorata sur les lignes de `detail_facture_produit`.

---

## 2.4 Script SQL
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

## 4.2 Backend (Java)
- [ ] Créer entités: `CategorieProduit`, `ProduitExtra`, `VenteProduit`
- [ ] Créer entités: `FactureProduit`, `DetailFactureProduit`, `PaiementProduit`
- [ ] Créer repositories correspondants
- [ ] Créer `ProduitExtraService` avec CRUD
- [ ] Créer `VenteProduitService` avec filtres
- [ ] Créer `CAMensuelService` avec requêtes vues
- [ ] Créer `FactureProduitService` avec logique prorata
- [ ] Créer controllers avec endpoints

## 4.3 Frontend (Thymeleaf)
- [ ] Créer template `views/produits/list.html`
- [ ] Créer template `views/produits/form.html`
- [ ] Créer template `views/ventes-produits/list.html`
- [ ] Créer template `views/ventes-produits/form.html`
- [ ] Créer template `views/ca-mensuel/index.html`
- [ ] Créer template `views/ca-mensuel/detail-produits.html`
- [ ] Ajouter liens sidebar

## 4.4 Tests
- [ ] Tester CRUD produits
- [ ] Tester enregistrement ventes
- [ ] Tester calcul CA mensuel
- [ ] Tester paiement prorata factures
