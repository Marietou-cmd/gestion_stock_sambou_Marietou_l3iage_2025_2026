package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
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

public class CategorieController {

    @FXML
    private TableView<Categorie> tableCategories;
    @FXML
    private TableColumn<Categorie, String> colonneNom;
    @FXML
    private TableColumn<Categorie, String> colonneDescription;
    @FXML
    private TableColumn<Categorie, Integer> colonneNbProduits;
    @FXML
    private TableColumn<Categorie, Void> colonneActions;

    private final CategorieService categorieService = new CategorieServiceImpl();

    // Recalculé à chaque chargement : id catégorie -> nombre de produits rattachés.
    // Évite de refaire une requête SQL pour chaque ligne du tableau.
    private final Map<Integer, Long> compteProduitsParCategorie = new HashMap<>();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colonneNbProduits.setCellValueFactory(donnees -> {
            int id = donnees.getValue().getId();
            long compte = compteProduitsParCategorie.getOrDefault(id, 0L);
            return new javafx.beans.property.SimpleObjectProperty<>((int) compte);
        });

        // Colonne "Actions" : pas de donnée à afficher, juste des boutons par ligne.
        colonneActions.setCellFactory(fabriquerCelluleActions());
    }

    private Callback<TableColumn<Categorie, Void>, TableCell<Categorie, Void>> fabriquerCelluleActions() {
        return colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox conteneur = new HBox(8.0, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> {
                    Categorie categorie = getTableView().getItems().get(getIndex());
                    ouvrirDialogModification(categorie);
                });
                boutonSupprimer.setOnAction(e -> {
                    Categorie categorie = getTableView().getItems().get(getIndex());
                    supprimerCategorie(categorie);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : conteneur);
            }
        };
    }

    private void chargerDonnees() {
        List<Categorie> categories = categorieService.findAllCategories();

        compteProduitsParCategorie.clear();
        for (Categorie categorie : categories) {
            long compte = categorieService.compterProduitsParCategorie(categorie.getId());
            compteProduitsParCategorie.put(categorie.getId(), compte);
        }

        ObservableList<Categorie> donneesObservables = FXCollections.observableArrayList(categories);
        tableCategories.setItems(donneesObservables);
        tableCategories.refresh();
    }

    @FXML
    private void ajouterCategorie() {
        ouvrirDialog(null);
    }

    private void ouvrirDialogModification(Categorie categorie) {
        ouvrirDialog(categorie);
    }

    /**
     * Ouvre le formulaire d'ajout/modification. Si categorieAModifier est null, le formulaire
     * s'ouvre en mode "ajout" ; sinon en mode "modification" pré-rempli.
     */
    private void ouvrirDialog(Categorie categorieAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddCategorieDialog.fxml"));
            Parent racine = loader.load();

            AddCategorieDialogController controleurDialog = loader.getController();
            if (categorieAModifier != null) {
                controleurDialog.setCategorieAModifier(categorieAModifier);
            }

            Stage fenetreDialog = new Stage();
            fenetreDialog.setTitle(categorieAModifier == null ? "Nouvelle catégorie" : "Modifier la catégorie");
            fenetreDialog.initModality(Modality.APPLICATION_MODAL);
            fenetreDialog.setScene(new Scene(racine));
            fenetreDialog.showAndWait();

            if (controleurDialog.isCategorieEnregistree()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir le formulaire de catégorie.");
            e.printStackTrace();
        }
    }

    private void supprimerCategorie(Categorie categorie) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer la catégorie \"" + categorie.getNom() + "\" ?");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == javafx.scene.control.ButtonType.OK) {
                try {
                    categorieService.deleteCategorie(categorie.getId());
                    chargerDonnees();
                } catch (IllegalStateException e) {
                    // Cas attendu : des produits sont encore rattachés (message clair pour l'utilisateur)
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