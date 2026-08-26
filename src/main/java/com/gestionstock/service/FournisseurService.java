package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;

import java.util.List;

public interface FournisseurService {
    List<Fournisseur> findAllFournisseurs();
    void addFournisseur(Fournisseur fournisseur);
    void updateFournisseur(Fournisseur fournisseur);
    void deleteFournisseur(int id);
    long compterProduitsParFournisseur(int fournisseurId);
}