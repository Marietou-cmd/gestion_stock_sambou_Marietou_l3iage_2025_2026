package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    @Override
    public Optional<Utilisateur> authentifier(String email, String motDePasseEnClair) {
        Optional<Utilisateur> utilisateurOptionnel = findByEmail(email);

        if (utilisateurOptionnel.isEmpty()) {
            return Optional.empty(); // email inconnu
        }

        Utilisateur utilisateur = utilisateurOptionnel.get();

        if (!utilisateur.isActif()) {
            return Optional.empty(); // compte désactivé
        }

        boolean motDePasseCorrect = BCrypt.checkpw(motDePasseEnClair, utilisateur.getMotDePasseHash());
        if (!motDePasseCorrect) {
            return Optional.empty();
        }

        return Optional.of(utilisateur);
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Utilisateur utilisateur = em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(utilisateur);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public String hacherMotDePasse(String motDePasseEnClair) {
        // gensalt() génère un "sel" aléatoire à chaque appel : deux utilisateurs avec
        // le même mot de passe auront des hash différents en base (protection supplémentaire).
        return BCrypt.hashpw(motDePasseEnClair, BCrypt.gensalt());
    }
}