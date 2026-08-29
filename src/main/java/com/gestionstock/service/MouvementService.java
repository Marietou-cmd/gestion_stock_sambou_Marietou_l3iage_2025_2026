package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Utilisateur;
import com.gestionstock.model.enums.TypeMouvement;

import java.time.LocalDate;
import java.util.List;

public interface MouvementService {
    List<Mouvement> findAllMouvements();

    /**
     * Enregistre un mouvement de stock ET met à jour la quantité en stock du produit concerné,
     * dans une seule transaction (les deux réussissent ou échouent ensemble).
     * Lève une IllegalArgumentException si une SORTIE ferait passer le stock sous zéro.
     */
    void enregistrerMouvement(int produitId, TypeMouvement type, int quantite, String motif, Utilisateur utilisateur);

    /**
     * Historique filtré : chaque paramètre est optionnel (null = pas de filtre sur ce critère).
     */
    List<Mouvement> rechercherMouvements(Integer produitId, TypeMouvement type, LocalDate dateDebut, LocalDate dateFin);
}