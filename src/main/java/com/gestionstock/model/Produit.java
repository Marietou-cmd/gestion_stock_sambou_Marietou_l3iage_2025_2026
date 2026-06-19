package com.gestionstock.model;

public class Produit {
    private String nom;
    private int quantiteStock;
    private double prix;
    private String categorie;

    public Produit(String nom, int quantiteStock, double prix, String categorie) {
        this.nom = nom;
        this.quantiteStock = quantiteStock;
        this.prix = prix;
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public double getPrix() {
        return prix;
    }

    public String getCategorie() {
        return categorie;
    }
}
