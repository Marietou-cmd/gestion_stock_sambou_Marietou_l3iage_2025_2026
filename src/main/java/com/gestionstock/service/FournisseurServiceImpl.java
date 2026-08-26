package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class FournisseurServiceImpl implements FournisseurService {

    @Override
    public List<Fournisseur> findAllFournisseurs() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT f FROM Fournisseur f ORDER BY f.nom",
                    Fournisseur.class
            ).getResultList();
        }
    }

    @Override
    public void addFournisseur(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout du fournisseur", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void updateFournisseur(Fournisseur fournisseur) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification du fournisseur", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteFournisseur(int id) {
        long nbProduits = compterProduitsParFournisseur(id);
        if (nbProduits > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer ce fournisseur : " + nbProduits +
                            " produit(s) y sont encore rattaché(s)."
            );
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Fournisseur fournisseur = em.find(Fournisseur.class, id);
            if (fournisseur != null) {
                em.remove(fournisseur);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression du fournisseur", e);
        } finally {
            em.close();
        }
    }

    @Override
    public long compterProduitsParFournisseur(int fournisseurId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.fournisseur.id = :fournisseurId", Long.class)
                    .setParameter("fournisseurId", fournisseurId)
                    .getSingleResult();
        }
    }
}