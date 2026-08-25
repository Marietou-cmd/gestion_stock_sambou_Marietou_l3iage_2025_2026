package com.gestionstock.service;

import com.gestionstock.model.Categorie;

import java.util.List;

public interface CategorieService {
    List<Categorie> findAllCategories();
}