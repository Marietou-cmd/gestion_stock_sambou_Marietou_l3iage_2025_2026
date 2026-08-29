package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FournisseurController {

    @FXML
    private TableView<Fournisseur> tableFournisseurs;
    @FXML
    private TableColumn<Fournisseur, String> colonneNom;
    @FXML
    private TableColumn<Fournisseur, String> colonneEmail;
    @FXML
    private TableColumn<Fournisseur, String> colonneTel;
    @FXML
    private TableColumn<Fournisseur, Integer> colonneNbProduits;
    @FXML
    private TableColumn<Fournisseur, Void> colonneActions;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private final Map<Integer, Long> compteProduitsParFournisseur = new HashMap<>();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneTel.setCellValueFactory(new PropertyValueFactory<>("tel"));

        colonneNbProduits.setCellValueFactory(donnees -> {
            int id = donnees.getValue().getId();
            long compte = compteProduitsParFournisseur.getOrDefault(id, 0L);
            return new javafx.beans.property.SimpleObjectProperty<>((int) compte);
        });

        colonneActions.setCellFactory(fabriquerCelluleActions());
    }

    private Callback<TableColumn<Fournisseur, Void>, TableCell<Fournisseur, Void>> fabriquerCelluleActions() {
        return colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox conteneur = new HBox(8.0, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> {
                    Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                    ouvrirDialog(fournisseur);
                });
                boutonSupprimer.setOnAction(e -> {
                    Fournisseur fournisseur = getTableView().getItems().get(getIndex());
                    supprimerFournisseur(fournisseur);
                });
                boolean estAdmin = com.gestionstock.util.SessionUtilisateur.getInstance().estAdmin();
                boutonSupprimer.setVisible(estAdmin);
                boutonSupprimer.setManaged(estAdmin);
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : conteneur);
            }
        };
    }

    private void chargerDonnees() {
        List<Fournisseur> fournisseurs = fournisseurService.findAllFournisseurs();

        compteProduitsParFournisseur.clear();
        for (Fournisseur fournisseur : fournisseurs) {
            long compte = fournisseurService.compterProduitsParFournisseur(fournisseur.getId());
            compteProduitsParFournisseur.put(fournisseur.getId(), compte);
        }

        ObservableList<Fournisseur> donneesObservables = FXCollections.observableArrayList(fournisseurs);
        tableFournisseurs.setItems(donneesObservables);
        tableFournisseurs.refresh();
    }

    @FXML
    private void ajouterFournisseur() {
        ouvrirDialog(null);
    }

    private void ouvrirDialog(Fournisseur fournisseurAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddFournisseurDialog.fxml"));
            Parent racine = loader.load();

            AddFournisseurDialogController controleurDialog = loader.getController();
            if (fournisseurAModifier != null) {
                controleurDialog.setFournisseurAModifier(fournisseurAModifier);
            }

            Stage fenetreDialog = new Stage();
            fenetreDialog.setTitle(fournisseurAModifier == null ? "Nouveau fournisseur" : "Modifier le fournisseur");
            fenetreDialog.initModality(Modality.APPLICATION_MODAL);
            fenetreDialog.setScene(new Scene(racine));
            fenetreDialog.showAndWait();

            if (controleurDialog.isFournisseurEnregistre()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir le formulaire de fournisseur.");
            e.printStackTrace();
        }
    }

    private void supprimerFournisseur(Fournisseur fournisseur) {
        if (!com.gestionstock.util.SessionUtilisateur.getInstance().estAdmin()) {
            afficherErreur("Seul un administrateur peut supprimer un fournisseur.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer le fournisseur \"" + fournisseur.getNom() + "\" ?");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == javafx.scene.control.ButtonType.OK) {
                try {
                    fournisseurService.deleteFournisseur(fournisseur.getId());
                    chargerDonnees();
                } catch (IllegalStateException e) {
                    afficherErreur(e.getMessage());
                } catch (Exception e) {
                    afficherErreur("Erreur inattendue lors de la suppression.");
                    e.printStackTrace();
                }
            }
        });
    }

    private void afficherErreur(String message) {
        Alert alerteErreur = new Alert(Alert.AlertType.ERROR);
        alerteErreur.setTitle("Erreur");
        alerteErreur.setHeaderText(null);
        alerteErreur.setContentText(message);
        alerteErreur.showAndWait();
    }
}