package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField champEmail;
    @FXML
    private PasswordField champMotDePasse;
    @FXML
    private Label labelErreur;
    @FXML
    private Button boutonConnexion;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    private void seConnecter() {
        String email = champEmail.getText() == null ? "" : champEmail.getText().trim();
        String motDePasse = champMotDePasse.getText() == null ? "" : champMotDePasse.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            labelErreur.setText("Veuillez renseigner votre email et votre mot de passe.");
            return;
        }

        Optional<Utilisateur> utilisateurOptionnel = utilisateurService.authentifier(email, motDePasse);

        if (utilisateurOptionnel.isEmpty()) {
            labelErreur.setText("Email ou mot de passe incorrect, ou compte désactivé.");
            champMotDePasse.clear();
            return;
        }

        SessionUtilisateur.getInstance().connecter(utilisateurOptionnel.get());

        try {
            ouvrirMenuPrincipal();
        } catch (IOException e) {
            labelErreur.setText("Erreur lors du chargement de l'application.");
            e.printStackTrace();
        }
    }

    private void ouvrirMenuPrincipal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/main.fxml"));
        Parent racine = loader.load();

        Scene scene = new Scene(racine);
        scene.getStylesheets().add(getClass().getResource("/com/gestionstock/style.css").toExternalForm());

        Stage stage = (Stage) boutonConnexion.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Gestion Stock IAGE");
    }
}