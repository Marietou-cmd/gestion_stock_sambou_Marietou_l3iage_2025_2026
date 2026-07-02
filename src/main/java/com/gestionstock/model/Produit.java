package com.gestionstock.model;

public class Produit {
    private int id;
    private String nom;
    private int quantiteStock;
    private int quantiteMin;
    private double prix;
    private int categorieId;
    private int fournisseurId;
    private String categorie_nom;
    private String fournisseur_nom;

    public Produit() {
    }

    public Produit(String nom, int quantiteStock, int quantiteMin, double prix, String categorie_nom, String fournisseur_nom) {
        this.nom = nom;
        this.quantiteStock = quantiteStock;
        this.quantiteMin = quantiteMin;
        this.prix = prix;
        this.categorie_nom = categorie_nom;
        this.fournisseur_nom = fournisseur_nom;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public void setQuantiteMin(int quantiteMin) {
        this.quantiteMin = quantiteMin;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setCategorie_nom(String categorie_nom) {
        this.categorie_nom = categorie_nom;
    }

    public void setFournisseur_nom(String fournisseur_nom) {
        this.fournisseur_nom = fournisseur_nom;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getNom() {
        return nom;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public int getQuantiteMin() {
        return quantiteMin;
    }

    public double getPrix() {
        return prix;
    }

    public String getCategorie_nom() {
        return categorie_nom;
    }

    public String getFournisseur_nom() {
        return fournisseur_nom;
    }
}
