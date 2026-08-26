package com.gestionstock.util;

import com.gestionstock.model.Utilisateur;

/**
 * Garde en mémoire l'utilisateur actuellement connecté, pendant toute la durée
 * de vie de l'application. Un seul utilisateur peut être connecté à la fois
 * (application de bureau mono-utilisateur), d'où le pattern singleton.
 */
public class SessionUtilisateur {

    private static SessionUtilisateur instance;

    private Utilisateur utilisateurConnecte;

    private SessionUtilisateur() {
    }

    public static SessionUtilisateur getInstance() {
        if (instance == null) {
            instance = new SessionUtilisateur();
        }
        return instance;
    }

    public void connecter(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public void deconnecter() {
        this.utilisateurConnecte = null;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    public boolean estAdmin() {
        return estConnecte() && utilisateurConnecte.getRole() == com.gestionstock.model.enums.RoleUtilisateur.ADMIN;
    }
}