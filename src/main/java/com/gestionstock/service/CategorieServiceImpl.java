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
}