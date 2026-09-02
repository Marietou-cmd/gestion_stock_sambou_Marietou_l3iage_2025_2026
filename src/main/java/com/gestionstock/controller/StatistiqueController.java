package com.gestionstock.controller;

import com.gestionstock.service.StatistiqueService;
import com.gestionstock.service.StatistiqueServiceImpl;
import com.gestionstock.service.StatistiquesResultat;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Locale;
import java.util.TreeSet;

public class StatistiqueController {

    @FXML
    private DatePicker datePickerDebut;
    @FXML
    private DatePicker datePickerFin;

    @FXML
    private Label labelValeurStock;
    @FXML
    private Label labelProduitPlusMouvemente;
    @FXML
    private Label labelCategoriePlusForteValeur;
    @FXML
    private Label labelFournisseurPlusDeProduits;
    @FXML
    private Label labelRupturesEvitees;

    @FXML
    private BarChart<String, Number> graphiqueBarres;
    @FXML
    private CategoryAxis axeXBarres;
    @FXML
    private PieChart graphiqueCamembert;

    private final StatistiqueService statistiqueService = new StatistiqueServiceImpl();

    @FXML
    public void initialize() {
        chargerStatistiques(null, null);
    }

    @FXML
    private void appliquerFiltre() {
        chargerStatistiques(datePickerDebut.getValue(), datePickerFin.getValue());
    }

    @FXML
    private void reinitialiserFiltre() {
        datePickerDebut.setValue(null);
        datePickerFin.setValue(null);
        chargerStatistiques(null, null);
    }

    private void chargerStatistiques(LocalDate dateDebut, LocalDate dateFin) {
        StatistiquesResultat resultat = statistiqueService.calculerStatistiques(dateDebut, dateFin);

        remplirCartes(resultat);
        remplirGraphiqueBarres(resultat);
        remplirGraphiqueCamembert(resultat);
    }

    private void remplirCartes(StatistiquesResultat resultat) {
        labelValeurStock.setText(String.format(Locale.FRANCE, "%,.0f", resultat.valeurTotaleStock));

        labelProduitPlusMouvemente.setText(
                resultat.nomProduitPlusMouvemente + " (" + resultat.quantiteProduitPlusMouvemente + ")");

        labelCategoriePlusForteValeur.setText(
                resultat.nomCategoriePlusForteValeur + " ("
                        + String.format(Locale.FRANCE, "%,.0f", resultat.valeurCategoriePlusForteValeur) + ")");

        labelFournisseurPlusDeProduits.setText(
                resultat.nomFournisseurPlusDeProduits + " (" + resultat.nbProduitsFournisseurPlusActif + ")");

        labelRupturesEvitees.setText(String.valueOf(resultat.nombreRupturesEviteesDeJustesse));
    }

    private void remplirGraphiqueBarres(StatistiquesResultat resultat) {
        graphiqueBarres.getData().clear();

        // On récupère tous les mois présents (entrées OU sorties), triés chronologiquement,
        // pour que les deux séries partagent exactement les mêmes catégories sur l'axe X.
        TreeSet<String> tousLesMois = new TreeSet<>();
        tousLesMois.addAll(resultat.entreesParMois.keySet());
        tousLesMois.addAll(resultat.sortiesParMois.keySet());

        axeXBarres.setCategories(FXCollections.observableArrayList(tousLesMois));

        XYChart.Series<String, Number> serieEntrees = new XYChart.Series<>();
        serieEntrees.setName("Entrées");
        for (String mois : tousLesMois) {
            serieEntrees.getData().add(new XYChart.Data<>(mois, resultat.entreesParMois.getOrDefault(mois, 0)));
        }

        XYChart.Series<String, Number> serieSorties = new XYChart.Series<>();
        serieSorties.setName("Sorties");
        for (String mois : tousLesMois) {
            serieSorties.getData().add(new XYChart.Data<>(mois, resultat.sortiesParMois.getOrDefault(mois, 0)));
        }

        graphiqueBarres.getData().addAll(serieEntrees, serieSorties);
    }

    private void remplirGraphiqueCamembert(StatistiquesResultat resultat) {
        graphiqueCamembert.getData().clear();

        resultat.valeurStockParCategorie.forEach((nomCategorie, valeur) ->
                graphiqueCamembert.getData().add(new PieChart.Data(nomCategorie, valeur))
        );
    }
}