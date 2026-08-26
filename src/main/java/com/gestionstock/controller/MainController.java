package com.gestionstock.controller;

import com.gestionstock.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/*
    -@FXML: Annotation qui connecte un attribut Java à un composant déclaré dans le fichier XML via son fx:id
    -initialize(): méthode spéciale appelée automatiquement par JavaFx après le chargement du FXML
 */
public class MainController {
    @FXML
    private StackPane contenuPrincipale;

    @FXML
    private Label labelUtilisateurConnecte;
    @FXML
    private Button boutonDeconnexion;

    @FXML
    public void initialize() {
        afficherUtilisateurConnecte();
        afficherDashboard();
    }

    private void afficherUtilisateurConnecte() {
        var utilisateur = SessionUtilisateur.getInstance().getUtilisateurConnecte();
        if (utilisateur != null) {
            labelUtilisateurConnecte.setText(utilisateur.getNom() + " (" + utilisateur.getRole() + ")");
        }
    }

    @FXML
    private void seDeconnecter() {
        SessionUtilisateur.getInstance().deconnecter();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/LoginView.fxml"));
            Parent racine = loader.load();

            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/gestionstock/style.css").toExternalForm());

            Stage stage = (Stage) boutonDeconnexion.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion Stock IAGE — Connexion");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void afficherDashboard() {
        contenuPrincipale.getChildren().clear();
        contenuPrincipale.getChildren().add(new Label("Dashboard"));
    }

    @FXML
    private void afficherProduits() {
        chargerVue("/com/gestionstock/produits.fxml");
    }

    @FXML
    private void afficherCategories() {
        chargerVue("/com/gestionstock/categories.fxml");
    }

    @FXML
    private void afficherFournisseurs() {
        chargerVue("/com/gestionstock/fournisseurs.fxml");
    }

    private void chargerVue(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(cheminFxml)
            );
            Node vue = loader.load();
            contenuPrincipale.getChildren().clear();
            contenuPrincipale.getChildren().add(vue);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}