package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.Utilisateur;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class MouvementServiceImpl implements MouvementService {

    @Override
    public List<Mouvement> findAllMouvements() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT m FROM Mouvement m ORDER BY m.dateMouvement DESC",
                    Mouvement.class
            ).getResultList();
        }
    }

    @Override
    public void enregistrerMouvement(int produitId, TypeMouvement type, int quantite, String motif, Utilisateur utilisateur) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive.");
        }
        if (type == TypeMouvement.SORTIE && (motif == null || motif.isBlank())) {
            throw new IllegalArgumentException("Le motif est obligatoire pour une sortie de stock.");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // On charge le produit AVEC ce même em, pour que la mise à jour de son stock
            // fasse partie de la même transaction que l'enregistrement du mouvement.
            Produit produit = em.find(Produit.class, produitId);
            if (produit == null) {
                throw new IllegalArgumentException("Produit introuvable.");
            }

            if (type == TypeMouvement.ENTREE) {
                produit.setQuantiteStock(produit.getQuantiteStock() + quantite);
            } else { // SORTIE
                if (quantite > produit.getQuantiteStock()) {
                    throw new IllegalArgumentException(
                            "Stock insuffisant : " + produit.getQuantiteStock() + " disponible(s), " +
                                    quantite + " demandé(s)."
                    );
                }
                produit.setQuantiteStock(produit.getQuantiteStock() - quantite);
            }
            em.merge(produit);

            Mouvement mouvement = new Mouvement();
            mouvement.setProduit(produit);
            mouvement.setType(type);
            mouvement.setQuantite(quantite);
            mouvement.setMotif(motif);
            mouvement.setDateMouvement(LocalDateTime.now());
            mouvement.setUtilisateur(utilisateur);
            em.persist(mouvement);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            // On relance telle quelle une erreur métier (message déjà clair pour l'utilisateur),
            // sinon on enveloppe dans un message générique.
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Erreur lors de l'enregistrement du mouvement.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Mouvement> rechercherMouvements(Integer produitId, TypeMouvement type, LocalDate dateDebut, LocalDate dateFin) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            StringBuilder jpql = new StringBuilder("SELECT m FROM Mouvement m WHERE 1=1");

            if (produitId != null) {
                jpql.append(" AND m.produit.id = :produitId");
            }
            if (type != null) {
                jpql.append(" AND m.type = :type");
            }
            if (dateDebut != null) {
                jpql.append(" AND m.dateMouvement >= :dateDebut");
            }
            if (dateFin != null) {
                jpql.append(" AND m.dateMouvement <= :dateFin");
            }
            jpql.append(" ORDER BY m.dateMouvement DESC");

            TypedQuery<Mouvement> requete = em.createQuery(jpql.toString(), Mouvement.class);

            if (produitId != null) {
                requete.setParameter("produitId", produitId);
            }
            if (type != null) {
                requete.setParameter("type", type);
            }
            if (dateDebut != null) {
                requete.setParameter("dateDebut", dateDebut.atStartOfDay());
            }
            if (dateFin != null) {
                requete.setParameter("dateFin", LocalDateTime.of(dateFin, LocalTime.MAX));
            }

            return requete.getResultList();
        }
    }
}