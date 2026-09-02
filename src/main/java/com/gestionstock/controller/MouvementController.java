package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MouvementController {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private ComboBox<Produit> comboFiltreProduit;
    @FXML
    private ComboBox<String> comboFiltreType;
    @FXML
    private DatePicker datePickerDebut;
    @FXML
    private DatePicker datePickerFin;

    @FXML
    private TableView<Mouvement> tableMouvements;
    @FXML
    private TableColumn<Mouvement, String> colonneDate;
    @FXML
    private TableColumn<Mouvement, String> colonneProduit;
    @FXML
    private TableColumn<Mouvement, String> colonneType;
    @FXML
    private TableColumn<Mouvement, Integer> colonneQuantite;
    @FXML
    private TableColumn<Mouvement, String> colonneMotif;
    @FXML
    private TableColumn<Mouvement, String> colonneUtilisateur;

    private final MouvementService mouvementService = new MouvementServiceImpl();
    private final ProduitService produitService = new ProduitServiceImpl();

    private static final String FILTRE_TOUTES = "Toutes";
    private static final String FILTRE_ENTREES = "Entrées";
    private static final String FILTRE_SORTIES = "Sorties";

    @FXML
    public void initialize() {
        comboFiltreType.setItems(FXCollections.observableArrayList(FILTRE_TOUTES, FILTRE_ENTREES, FILTRE_SORTIES));
        comboFiltreType.setValue(FILTRE_TOUTES);

        configurerFiltreProduit();
        configurerColonnes();
        chargerDonnees(mouvementService.findAllMouvements());
    }

    private void configurerFiltreProduit() {
        List<Produit> produits = produitService.findAllProduits();
        comboFiltreProduit.setItems(FXCollections.observableArrayList(produits));
        comboFiltreProduit.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produit produit) {
                return produit == null ? "Tous" : produit.getNom();
            }

            @Override
            public Produit fromString(String s) {
                return null;
            }
        });
    }

    private void configurerColonnes() {
        colonneDate.setCellValueFactory(donnees -> {
            var date = donnees.getValue().getDateMouvement();
            String texte = date == null ? "" : date.format(FORMAT_DATE);
            return new javafx.beans.property.SimpleStringProperty(texte);
        });

        colonneProduit.setCellValueFactory(donnees -> {
            var produit = donnees.getValue().getProduit();
            return new javafx.beans.property.SimpleStringProperty(produit == null ? "" : produit.getNom());
        });

        colonneType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colonneQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colonneMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));

        colonneUtilisateur.setCellValueFactory(donnees -> {
            var utilisateur = donnees.getValue().getUtilisateur();
            return new javafx.beans.property.SimpleStringProperty(utilisateur == null ? "—" : utilisateur.getNom());
        });
    }

    private void chargerDonnees(List<Mouvement> mouvements) {
        tableMouvements.setItems(FXCollections.observableArrayList(mouvements));
    }

    @FXML
    private void ajouterMouvement() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddMouvementDialog.fxml"));
            Parent racine = loader.load();

            AddMouvementDialogController controleurDialog = loader.getController();

            Stage fenetreDialog = new Stage();
            fenetreDialog.setTitle("Nouveau mouvement");
            fenetreDialog.initModality(Modality.APPLICATION_MODAL);
            fenetreDialog.setScene(new Scene(racine));
            fenetreDialog.showAndWait();

            if (controleurDialog.isMouvementEnregistre()) {
                appliquerFiltres(); // recharge avec les filtres actuels
            }
        } catch (IOException e) {
            afficherErreur("Impossible d'ouvrir le formulaire de mouvement.");
            e.printStackTrace();
        }
    }

    @FXML
    private void appliquerFiltres() {
        Produit produitChoisi = comboFiltreProduit.getValue();
        Integer produitId = produitChoisi == null ? null : produitChoisi.getId();

        TypeMouvement type = switch (comboFiltreType.getValue()) {
            case FILTRE_ENTREES -> TypeMouvement.ENTREE;
            case FILTRE_SORTIES -> TypeMouvement.SORTIE;
            default -> null; // "Toutes" => pas de filtre sur le type
        };

        LocalDate dateDebut = datePickerDebut.getValue();
        LocalDate dateFin = datePickerFin.getValue();

        List<Mouvement> resultats = mouvementService.rechercherMouvements(produitId, type, dateDebut, dateFin);
        chargerDonnees(resultats);
    }

    @FXML
    private void reinitialiserFiltres() {
        comboFiltreProduit.setValue(null);
        comboFiltreType.setValue(FILTRE_TOUTES);
        datePickerDebut.setValue(null);
        datePickerFin.setValue(null);
        chargerDonnees(mouvementService.findAllMouvements());
    }

    private void afficherErreur(String message) {
        Alert alerteErreur = new Alert(Alert.AlertType.ERROR);
        alerteErreur.setTitle("Erreur");
        alerteErreur.setHeaderText(null);
        alerteErreur.setContentText(message);
        alerteErreur.showAndWait();
    }
}