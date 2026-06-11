package com.gestionstock.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/*
    -@FXML: Annotation qui connecte un attribut Java à un composant déclaré dans le fichier XML via son fx:id
    -initialize(): méthode spéciale appelée automatiquement par JavaFx après le chargement du FXML
 */
public class MainController {
    @FXML
    private StackPane contenuPrincipale;

    @FXML
    public void initialize() { afficherDashboard();}

    @FXML
    private void afficherDashboard() {
        contenuPrincipale.getChildren().clear();
        contenuPrincipale.getChildren().add(new Label("Dashboard"));
    }

    @FXML
    private void afficherProduits() {
        contenuPrincipale.getChildren().clear();
        contenuPrincipale.getChildren().add(new Label("Produits"));
    }

    @FXML
    private void afficherCategories() {
        contenuPrincipale.getChildren().clear();
            contenuPrincipale.getChildren().add(new Label("Categories"));
    }

    @FXML
    private void afficherFournisseurs() {
        contenuPrincipale.getChildren().clear();
        contenuPrincipale.getChildren().add(new Label("Fournisseurs"));
    }

}
