package com.gestionstock.controller;

import com.gestionstock.model.Produit;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProduitController {
    @FXML
    TableView<Produit> tableProduits;
    @FXML
    TableColumn<Produit, Integer> colonneNom;
    @FXML
    TableColumn<Produit, Double> colonnePrix;
    @FXML
    TableColumn<Produit, Integer> colonneStock;
    @FXML
    TableColumn<Produit, String> colonneCategorie;
    @FXML
    TextField champRecherche;

    @FXML
    public void initialize() {
        /*
            - PropertyValueFactory: indique à la colonne d'afficher la valeur retournée par getNom() sur chaque objet Produit
            - ObservableList: C'est une liste spéciale qui permet de mettre à jour automatiquement TableView lorsque
            des éléments sont ajoutés ou supprimés.
            - FXCollections.observableArrayList: crée une ObservableList à partir d'objets

            A RETENIR: PropertyValueFactory<>("nom") appelle automatiquement la méthode getNom()
            de la classe Produit. Il faut donc que les getters soient définis dans la classe modèle
         */
        // Lier chaque colonne à un attribut de la classe Produit
        colonneNom.setCellValueFactory( new PropertyValueFactory<>("nom"));
        colonnePrix.setCellValueFactory( new PropertyValueFactory<>("prix"));
        colonneStock.setCellValueFactory( new PropertyValueFactory<>("quantiteStock"));
        colonneCategorie.setCellValueFactory( new PropertyValueFactory<>("categorie"));

        // Charger des données de test
        ObservableList<Produit> listeProduits = FXCollections.observableArrayList(
                new Produit("Ordinateur Portable", 15, 550000.0, "Informatique"),
                new Produit("Bureau en bois", 8, 87000.0, "Mobilier"),
                new Produit("Stylos (lost de 10)", 1000, 1499.0, "Fournitures")
        );

        tableProduits.setItems(listeProduits);
    }
}
