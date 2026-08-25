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
}