package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategorieServiceImpl implements CategorieService {

    @Override
    public List<Categorie> findAllCategories() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Categorie c ORDER BY c.nom",
                    Categorie.class
            ).getResultList();
        }
    }

    @Override
    public void addCategorie(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de l'ajout de la catégorie", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void updateCategorie(Categorie categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification de la catégorie", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteCategorie(int id) {
        long nbProduits = compterProduitsParCategorie(id);
        if (nbProduits > 0) {
            throw new IllegalStateException(
                    "Impossible de supprimer cette catégorie : " + nbProduits +
                            " produit(s) y sont encore rattaché(s)."
            );
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie categorie = em.find(Categorie.class, id);
            if (categorie != null) {
                em.remove(categorie);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la suppression de la catégorie", e);
        } finally {
            em.close();
        }
    }

    @Override
    public long compterProduitsParCategorie(int categorieId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT COUNT(p) FROM Produit p WHERE p.categorie.id = :categorieId", Long.class)
                    .setParameter("categorieId", categorieId)
                    .getSingleResult();
        }
    }
}