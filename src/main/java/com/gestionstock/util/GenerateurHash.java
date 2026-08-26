package com.gestionstock.util;

import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;

/**
 * Classe temporaire, à exécuter une seule fois pour générer les hash BCrypt
 * des utilisateurs de test à insérer dans init_db.sql / init_db_postgres.sql.
 * Peut être supprimée une fois les hash copiés dans les scripts SQL.
 */
public class GenerateurHash {
    public static void main(String[] args) {
        UtilisateurService utilisateurService = new UtilisateurServiceImpl();

        System.out.println("admin123 -> " + utilisateurService.hacherMotDePasse("admin123"));
        System.out.println("gestion123 -> " + utilisateurService.hacherMotDePasse("gestion123"));
    }
}