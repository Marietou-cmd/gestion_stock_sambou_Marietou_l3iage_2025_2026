package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;

import java.util.List;

public interface FournisseurService {
    List<Fournisseur> findAllFournisseurs();
}