package com.gestionstock.service;

import com.gestionstock.model.Categorie;

import java.util.List;

public interface CategorieService {
    List<Categorie> findAllCategories();
    void addCategorie(Categorie categorie);
    void updateCategorie(Categorie categorie);
    void deleteCategorie(int id);
    long compterProduitsParCategorie(int categorieId);
}