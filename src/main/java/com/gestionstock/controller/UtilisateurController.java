package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;

public class UtilisateurController {

    @FXML
    private TableView<Utilisateur> tableUtilisateurs;
    @FXML
    private TableColumn<Utilisateur, String> colonneNom;
    @FXML
    private TableColumn<Utilisateur, String> colonneEmail;
    @FXML
    private TableColumn<Utilisateur, String> colonneRole;
    @FXML
    private TableColumn<Utilisateur, String> colonneStatut;
    @FXML
    private TableColumn<Utilisateur, Void> colonneActions;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    public void initialize() {
        // Cet écran est réservé aux ADMIN : accès direct via l'URL/le code n'existe pas ici,
        // mais on revérifie quand même par sécurité (même logique que pour les suppressions).
        if (!SessionUtilisateur.getInstance().estAdmin()) {
            tableUtilisateurs.setPlaceholder(new Label("Accès réservé aux administrateurs."));
            return;
        }

        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colonneRole.setCellValueFactory(donnees ->
                new javafx.beans.property.SimpleStringProperty(donnees.getValue().getRole().toString()));

        colonneStatut.setCellValueFactory(donnees ->
                new javafx.beans.property.SimpleStringProperty(donnees.getValue().isActif() ? "Actif" : "Désactivé"));

        colonneActions.setCellFactory(fabriquerCelluleActions());
    }

    private Callback<TableColumn<Utilisateur, Void>, TableCell<Utilisateur, Void>> fabriquerCelluleActions() {
        return colonne -> new TableCell<>() {
            private final Button boutonBasculer = new Button();
            private final HBox conteneur = new HBox(8.0, boutonBasculer);

            {
                boutonBasculer.setOnAction(e -> {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    basculerStatut(utilisateur);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                if (vide) {
                    setGraphic(null);
                } else {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    boutonBasculer.setText(utilisateur.isActif() ? "Désactiver" : "Activer");
                    setGraphic(conteneur);
                }
            }
        };
    }

    private void chargerDonnees() {
        List<Utilisateur> utilisateurs = utilisateurService.findAllUtilisateurs();
        tableUtilisateurs.setItems(FXCollections.observableArrayList(utilisateurs));
    }

    private void basculerStatut(Utilisateur utilisateur) {
        boolean nouveauStatut = !utilisateur.isActif();

        String action = nouveauStatut ? "activer" : "désactiver";
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment " + action + " le compte de \"" + utilisateur.getNom() + "\" ?");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == javafx.scene.control.ButtonType.OK) {
                try {
                    utilisateurService.changerStatutActif(utilisateur.getId(), nouveauStatut);
                    chargerDonnees();
                } catch (Exception e) {
                    Alert alerteErreur = new Alert(Alert.AlertType.ERROR);
                    alerteErreur.setTitle("Erreur");
                    alerteErreur.setHeaderText(null);
                    alerteErreur.setContentText("Erreur lors du changement de statut.");
                    alerteErreur.showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }
}