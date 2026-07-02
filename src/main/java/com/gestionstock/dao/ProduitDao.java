package com.gestionstock.dao;

import com.gestionstock.model.Produit;

import java.util.List;
import java.util.Optional;

public interface ProduitDao {
    List<Produit> findAllProduits();
    Optional<Produit> findById(int id);
    void addProduit(Produit p);
    void updateProduit(Produit p);
    void deleteProduit(int id);
}
