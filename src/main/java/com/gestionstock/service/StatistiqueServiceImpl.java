package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiqueServiceImpl implements StatistiqueService {

    private static final DateTimeFormatter FORMAT_MOIS = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    @Override
    public StatistiquesResultat calculerStatistiques(LocalDate dateDebut, LocalDate dateFin) {
        StatistiquesResultat resultat = new StatistiquesResultat();

        List<Produit> tousLesProduits = produitService.findAllProduits();

        calculerIndicateursEtatActuel(tousLesProduits, resultat);
        calculerIndicateursMouvements(tousLesProduits, dateDebut, dateFin, resultat);

        return resultat;
    }

    /**
     * Indicateurs "instantané" : reflètent l'état actuel de la base, indépendamment
     * de la période choisie (une catégorie ne devient pas "moins riche" parce qu'on
     * change de période de filtre).
     */
    private void calculerIndicateursEtatActuel(List<Produit> produits, StatistiquesResultat resultat) {
        // Valeur totale du stock = somme(quantiteStock * prix) sur tous les produits
        resultat.valeurTotaleStock = produits.stream()
                .mapToDouble(p -> p.getQuantiteStock() * p.getPrix())
                .sum();

        // Regroupement par catégorie : nom de catégorie -> valeur totale de son stock
        Map<String, Double> valeurParCategorie = new HashMap<>();
        for (Produit p : produits) {
            Categorie categorie = p.getCategorie();
            if (categorie == null) continue;
            double valeur = p.getQuantiteStock() * p.getPrix();
            valeurParCategorie.merge(categorie.getNom(), valeur, Double::sum);
        }
        resultat.valeurStockParCategorie = valeurParCategorie;

        // Catégorie représentant la plus forte valeur de stock = le max de la map ci-dessus
        valeurParCategorie.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entree -> {
                    resultat.nomCategoriePlusForteValeur = entree.getKey();
                    resultat.valeurCategoriePlusForteValeur = entree.getValue();
                });

        // Regroupement par fournisseur : nom de fournisseur -> nombre de produits qu'il fournit
        Map<String, Long> nbProduitsParFournisseur = new HashMap<>();
        for (Produit p : produits) {
            Fournisseur fournisseur = p.getFournisseur();
            if (fournisseur == null) continue;
            nbProduitsParFournisseur.merge(fournisseur.getNom(), 1L, Long::sum);
        }

        nbProduitsParFournisseur.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entree -> {
                    resultat.nomFournisseurPlusDeProduits = entree.getKey();
                    resultat.nbProduitsFournisseurPlusActif = entree.getValue();
                });
    }
        /**
         * Indicateurs liés à l'activité (mouvements), filtrés par la période choisie.
         */
        private void calculerIndicateursMouvements(List<Produit> tousLesProduits, LocalDate dateDebut, LocalDate dateFin,
                StatistiquesResultat resultat) {
            List<Mouvement> mouvementsPeriode = mouvementService.rechercherMouvements(null, null, dateDebut, dateFin);

            // Produit le plus mouvementé (en quantité totale, entrées + sorties confondues) sur la période
            Map<String, Integer> quantiteParProduit = new HashMap<>();
            for (Mouvement m : mouvementsPeriode) {
                if (m.getProduit() == null) continue;
                quantiteParProduit.merge(m.getProduit().getNom(), m.getQuantite(), Integer::sum);
            }
            quantiteParProduit.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .ifPresent(entree -> {
                        resultat.nomProduitPlusMouvemente = entree.getKey();
                        resultat.quantiteProduitPlusMouvemente = entree.getValue();
                    });

            // Graphique barres : quantité entrée / sortie par mois (clé triée "yyyy-MM")
            Map<String, Integer> entreesParMois = new java.util.TreeMap<>();
            Map<String, Integer> sortiesParMois = new java.util.TreeMap<>();
            for (Mouvement m : mouvementsPeriode) {
                String mois = m.getDateMouvement().format(FORMAT_MOIS);
                if (m.getType() == TypeMouvement.ENTREE) {
                    entreesParMois.merge(mois, m.getQuantite(), Integer::sum);
                } else {
                    sortiesParMois.merge(mois, m.getQuantite(), Integer::sum);
                }
            }
            resultat.entreesParMois = entreesParMois;
            resultat.sortiesParMois = sortiesParMois;

            resultat.nombreRupturesEviteesDeJustesse = compterRupturesEviteesDeJustesse(tousLesProduits, dateDebut, dateFin);
        }

        /**
         * Compte les SORTIE qui ont fait passer le stock d'un produit sous (ou à) son seuil minimum,
         * SANS toutefois le vider complètement (stock résultant > 0) : c'est ça, une "rupture évitée
         * de justesse". On ne stocke pas l'état du stock à chaque instant du passé, donc on le
         * RECONSTITUE : on part du stock ACTUEL, on soustrait l'effet net de tous les mouvements
         * connus pour retrouver le stock INITIAL, puis on rejoue chaque mouvement dans l'ordre
         * chronologique pour connaître le stock après chacun d'eux.
         */
        private long compterRupturesEviteesDeJustesse(List<Produit> tousLesProduits, LocalDate dateDebut, LocalDate dateFin) {
            List<Mouvement> tousLesMouvements = mouvementService.findAllMouvements();

            Map<Integer, List<Mouvement>> mouvementsParProduit = new HashMap<>();
            for (Mouvement m : tousLesMouvements) {
                if (m.getProduit() == null) continue;
                mouvementsParProduit
                        .computeIfAbsent(m.getProduit().getId(), k -> new java.util.ArrayList<>())
                        .add(m);
            }

            long compteur = 0;

            for (Produit produit : tousLesProduits) {
                List<Mouvement> mouvementsDuProduit = mouvementsParProduit.get(produit.getId());
                if (mouvementsDuProduit == null || mouvementsDuProduit.isEmpty()) continue;

                mouvementsDuProduit.sort(Comparator.comparing(Mouvement::getDateMouvement));

                int effetNet = 0;
                for (Mouvement m : mouvementsDuProduit) {
                    effetNet += (m.getType() == TypeMouvement.ENTREE) ? m.getQuantite() : -m.getQuantite();
                }
                int stockCourant = produit.getQuantiteStock() - effetNet;

                for (Mouvement m : mouvementsDuProduit) {
                    if (m.getType() == TypeMouvement.ENTREE) {
                        stockCourant += m.getQuantite();
                    } else {
                        stockCourant -= m.getQuantite();
                    }

                    boolean dansLaPeriode = estDansLaPeriode(m.getDateMouvement().toLocalDate(), dateDebut, dateFin);

                    if (m.getType() == TypeMouvement.SORTIE && dansLaPeriode
                            && stockCourant > 0 && stockCourant <= produit.getQuantiteMin()) {
                        compteur++;
                    }
                }
            }

            return compteur;
        }

        private boolean estDansLaPeriode(LocalDate date, LocalDate debut, LocalDate fin) {
            if (debut != null && date.isBefore(debut)) return false;
            if (fin != null && date.isAfter(fin)) return false;
            return true;
        }
    }