package com.gestionstock.service;

import java.time.LocalDate;

public interface StatistiqueService {
    /**
     * Calcule tous les indicateurs de l'écran Statistiques.
     * dateDebut/dateFin filtrent uniquement les indicateurs liés aux mouvements
     * (produit le plus mouvementé, ruptures évitées, graphique entrées/sorties par mois).
     * Les indicateurs d'état courant (valeur du stock, catégorie la plus forte valeur,
     * fournisseur le plus référencé) reflètent toujours l'état actuel, non filtré par période.
     */
    StatistiquesResultat calculerStatistiques(LocalDate dateDebut, LocalDate dateFin);
}