package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML
    private Label labelTotalProduits;
    @FXML
    private Label labelStockBas;
    @FXML
    private Label labelValeurStock;
    @FXML
    private Label labelMouvementsJour;

    @FXML
    private TableView<Produit> tableStockBas;
    @FXML
    private TableColumn<Produit, String> colonneNom;
    @FXML
    private TableColumn<Produit, Integer> colonneStock;
    @FXML
    private TableColumn<Produit, Integer> colonneStockMin;
    @FXML
    private TableColumn<Produit, String> colonneCategorie;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerStatistiques();
    }

    private void configurerColonnes() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneStock.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory(new PropertyValueFactory<>("quantiteMin"));
        colonneCategorie.setCellValueFactory(donnees -> {
            var categorie = donnees.getValue().getCategorie();
            return new javafx.beans.property.SimpleStringProperty(categorie == null ? "" : categorie.getNom());
        });
    }

    /**
     * Recalcule toutes les statistiques depuis la base. Appelée à chaque ouverture
     * de l'écran (initialize()) : les chiffres ne sont donc jamais figés/statiques.
     */
    private void chargerStatistiques() {
        List<Produit> tousLesProduits = produitService.findAllProduits();

        // Carte 1 : nombre total de produits
        labelTotalProduits.setText(String.valueOf(tousLesProduits.size()));

        // Produits en stock bas : quantiteStock <= quantiteMin (règle exacte du sujet)
        List<Produit> produitsEnStockBas = tousLesProduits.stream()
                .filter(p -> p.getQuantiteStock() <= p.getQuantiteMin())
                .toList();

        // Carte 2 : nombre de produits en stock bas
        labelStockBas.setText(String.valueOf(produitsEnStockBas.size()));

        // Carte 3 : valeur totale du stock = somme(quantiteStock * prix) sur tous les produits
        double valeurTotale = tousLesProduits.stream()
                .mapToDouble(p -> p.getQuantiteStock() * p.getPrix())
                .sum();
        labelValeurStock.setText(String.format(Locale.FRANCE, "%,.0f", valeurTotale));

        // Carte 4 : mouvements du jour (entrées + sorties confondues)
        LocalDate aujourdHui = LocalDate.now();
        List<Mouvement> mouvementsDuJour = mouvementService.rechercherMouvements(
                null, null, aujourdHui, aujourdHui
        );
        labelMouvementsJour.setText(String.valueOf(mouvementsDuJour.size()));

        // Tableau d'alerte : liste visuelle des produits en rupture/stock bas
        tableStockBas.setItems(FXCollections.observableArrayList(produitsEnStockBas));
    }
}