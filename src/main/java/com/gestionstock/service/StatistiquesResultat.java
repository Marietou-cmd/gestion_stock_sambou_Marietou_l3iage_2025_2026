package com.gestionstock.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Regroupe tous les indicateurs calculés pour l'écran Statistiques, en un seul objet
 * retourné par StatistiqueService.calculerStatistiques(). Évite d'avoir une méthode
 * de service par indicateur (5 méthodes + 2 pour les graphiques serait plus lourd
 * à appeler depuis le contrôleur).
 */
public class StatistiquesResultat {

    public double valeurTotaleStock;

    public String nomProduitPlusMouvemente = "-";
    public int quantiteProduitPlusMouvemente = 0;

    public String nomCategoriePlusForteValeur = "-";
    public double valeurCategoriePlusForteValeur = 0;

    public String nomFournisseurPlusDeProduits = "-";
    public long nbProduitsFournisseurPlusActif = 0;

    public long nombreRupturesEviteesDeJustesse = 0;

    // Clé "yyyy-MM" -> quantité totale entrée/sortie ce mois-là (pour le graphique en barres)
    public Map<String, Integer> entreesParMois = new LinkedHashMap<>();
    public Map<String, Integer> sortiesParMois = new LinkedHashMap<>();

    // Nom de catégorie -> valeur totale de stock dans cette catégorie (pour le camembert)
    public Map<String, Double> valeurStockParCategorie = new LinkedHashMap<>();
}