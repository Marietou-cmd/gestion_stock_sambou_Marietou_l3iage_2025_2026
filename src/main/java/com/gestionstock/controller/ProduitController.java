package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import com.gestionstock.model.Produit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProduitController {
    @FXML
    TableView<Produit> tableProduits;
    @FXML
    TableColumn<Produit, Integer> colonneNom;
    @FXML
    TableColumn<Produit, Double> colonnePrix;
    @FXML
    TableColumn<Produit, Double> colonnePrixPromo;
    @FXML
    TableColumn<Produit, Integer> colonneStock;
    @FXML
    TableColumn<Produit, Integer> colonneStockMin;
    @FXML
    TableColumn<Produit, String> colonneCategorie;
    @FXML
    TableColumn<Produit, String> colonneFournisseur;
    @FXML
    TableColumn<Produit, Void> colonneActions;
    @FXML
    TextField champRecherche;
    @FXML
    Button boutonSupprimer;
    @FXML
    ComboBox<Categorie> comboFiltreCategorie;
    @FXML
    ComboBox<Fournisseur> comboFiltreFournisseur;
    @FXML
    CheckBox checkStockBas;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    // Liste complète chargée depuis la base, utilisée comme référence pour la recherche et les filtres
    private ObservableList<Produit> listeProduits;

    @FXML
    public void initialize() {
        configurerColones();
        configurerFiltres();
        chargerDonnees();

        boolean estAdmin = com.gestionstock.util.SessionUtilisateur.getInstance().estAdmin();
        boutonSupprimer.setVisible(estAdmin);
        boutonSupprimer.setManaged(estAdmin);
    }

    private void configurerColones() {
        colonneNom.setCellValueFactory( new PropertyValueFactory<>("nom"));
        colonnePrix.setCellValueFactory( new PropertyValueFactory<>("prix"));
        colonnePrixPromo.setCellValueFactory( new PropertyValueFactory<>("prixPromo"));
        colonneStock.setCellValueFactory( new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory( new PropertyValueFactory<>("quantiteMin"));
        colonneCategorie.setCellValueFactory( data -> {
            Categorie cat = data.getValue().getCategorie();
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        colonneFournisseur.setCellValueFactory( data -> {
            Fournisseur fournisseur = data.getValue().getFournisseur();
            return new SimpleStringProperty(fournisseur != null ? fournisseur.getNom() : "");
        });
        colonneActions.setCellFactory(fabriquerCelluleActions());
    }

    private Callback<TableColumn<Produit, Void>, TableCell<Produit, Void>> fabriquerCelluleActions() {
        return colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final HBox conteneur = new HBox(8.0, boutonModifier);

            {
                boutonModifier.setOnAction(e -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    ouvrirDialog(produit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : conteneur);
            }
        };
    }

    private void configurerFiltres() {
        List<Categorie> categories = categorieService.findAllCategories();
        comboFiltreCategorie.setItems(FXCollections.observableArrayList(categories));
        comboFiltreCategorie.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie == null ? "Toutes" : categorie.getNom();
            }

            @Override
            public Categorie fromString(String s) {
                return null;
            }
        });

        List<Fournisseur> fournisseurs = fournisseurService.findAllFournisseurs();
        comboFiltreFournisseur.setItems(FXCollections.observableArrayList(fournisseurs));
        comboFiltreFournisseur.setConverter(new StringConverter<>() {
            @Override
            public String toString(Fournisseur fournisseur) {
                return fournisseur == null ? "Tous" : fournisseur.getNom();
            }

            @Override
            public Fournisseur fromString(String s) {
                return null;
            }
        });
    }

    private void chargerDonnees() {
        List<Produit> produits = produitService.findAllProduits();
        listeProduits = FXCollections.observableArrayList(produits);
        tableProduits.setItems(listeProduits);
    }

    @FXML
    private void rechercherProduits() {
        appliquerFiltres();
    }

    @FXML
    private void appliquerFiltres() {
        String recherche = champRecherche.getText() == null ? "" : champRecherche.getText().trim().toLowerCase();
        Categorie categorieChoisie = comboFiltreCategorie.getValue();
        Fournisseur fournisseurChoisi = comboFiltreFournisseur.getValue();
        boolean stockBasUniquement = checkStockBas.isSelected();

        ObservableList<Produit> resultats = listeProduits.filtered(produit -> {
            boolean correspondRecherche = recherche.isEmpty()
                    || (produit.getNom() != null && produit.getNom().toLowerCase().contains(recherche));

            boolean correspondCategorie = categorieChoisie == null
                    || (produit.getCategorie() != null && produit.getCategorie().getId() == categorieChoisie.getId());

            boolean correspondFournisseur = fournisseurChoisi == null
                    || (produit.getFournisseur() != null && produit.getFournisseur().getId() == fournisseurChoisi.getId());

            boolean correspondStockBas = !stockBasUniquement
                    || produit.getQuantiteStock() <= produit.getQuantiteMin();

            return correspondRecherche && correspondCategorie && correspondFournisseur && correspondStockBas;
        });

        tableProduits.setItems(resultats);
    }

    @FXML
    private void reinitialiserFiltres() {
        champRecherche.clear();
        comboFiltreCategorie.setValue(null);
        comboFiltreFournisseur.setValue(null);
        checkStockBas.setSelected(false);
        tableProduits.setItems(listeProduits);
    }

    @FXML
    private void ajouterProduit() {
        ouvrirDialog(null);
    }

    private void ouvrirDialog(Produit produitAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddProduitDialog.fxml"));
            Parent racine = loader.load();

            AddProduitDialogController controleurDialog = loader.getController();
            if (produitAModifier != null) {
                controleurDialog.setProduitAModifier(produitAModifier);
            }

            Stage fenetreDialog = new Stage();
            fenetreDialog.setTitle(produitAModifier == null ? "Nouveau produit" : "Modifier le produit");
            fenetreDialog.initModality(Modality.APPLICATION_MODAL);
            fenetreDialog.setScene(new Scene(racine));
            fenetreDialog.showAndWait();

            if (controleurDialog.isProduitAjoute()) {
                chargerDonnees();
            }
        } catch (IOException e) {
            Alert alerteErreur = new Alert(Alert.AlertType.ERROR);
            alerteErreur.setTitle("Erreur");
            alerteErreur.setHeaderText(null);
            alerteErreur.setContentText("Impossible d'ouvrir le formulaire de produit.");
            alerteErreur.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerProduit() {
        if (!com.gestionstock.util.SessionUtilisateur.getInstance().estAdmin()) {
            Alert alerteAcces = new Alert(Alert.AlertType.ERROR);
            alerteAcces.setTitle("Accès refusé");
            alerteAcces.setHeaderText(null);
            alerteAcces.setContentText("Seul un administrateur peut supprimer un produit.");
            alerteAcces.showAndWait();
            return;
        }

        Produit produitSelectionne = tableProduits.getSelectionModel().getSelectedItem();

        if (produitSelectionne == null) {
            Alert alerteInfo = new Alert(Alert.AlertType.INFORMATION);
            alerteInfo.setTitle("Aucune sélection");
            alerteInfo.setHeaderText(null);
            alerteInfo.setContentText("Veuillez sélectionner un produit à supprimer.");
            alerteInfo.showAndWait();
            return;
        }

        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmation de suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Voulez-vous vraiment supprimer le produit \"" + produitSelectionne.getNom() + "\" ?");

        Optional<ButtonType> reponse = alerteConfirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            produitService.deleteProduit(produitSelectionne.getId());
            chargerDonnees();
        }
    }
}