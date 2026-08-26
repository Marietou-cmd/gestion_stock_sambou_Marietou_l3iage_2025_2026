package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;

import java.util.Optional;

public interface UtilisateurService {
    /**
     * Vérifie l'email et le mot de passe fournis contre la base.
     * Retourne l'utilisateur si les identifiants sont valides ET le compte est actif,
     * un Optional vide sinon (mauvais email, mauvais mot de passe, ou compte désactivé).
     */
    Optional<Utilisateur> authentifier(String email, String motDePasseEnClair);

    Optional<Utilisateur> findByEmail(String email);

    String hacherMotDePasse(String motDePasseEnClair);
}