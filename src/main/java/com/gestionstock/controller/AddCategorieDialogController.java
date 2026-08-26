package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCategorieDialogController {

    @FXML
    private Label labelTitre;
    @FXML
    private TextField champNom;
    @FXML
    private TextArea champDescription;
    @FXML
    private Label labelErreur;
    @FXML
    private Button boutonAnnuler;
    @FXML
    private Button boutonEnregistrer;

    private final CategorieService categorieService = new CategorieServiceImpl();

    // null => on est en mode "ajout" ; non-null => on est en mode "modification"
    private Categorie categorieAModifier = null;

    private boolean categorieEnregistree = false;

    /**
     * Appelée par CategorieController AVANT d'afficher le dialog, uniquement en cas de modification.
     * Pré-remplit le formulaire avec les valeurs actuelles de la catégorie.
     */
    public void setCategorieAModifier(Categorie categorie) {
        this.categorieAModifier = categorie;
        labelTitre.setText("Modifier la catégorie");
        champNom.setText(categorie.getNom());
        champDescription.setText(categorie.getDescription());
    }

    public boolean isCategorieEnregistree() {
        return categorieEnregistree;
    }

    @FXML
    private void enregistrer() {
        String nom = champNom.getText() == null ? "" : champNom.getText().trim();
        if (nom.length() < 2) {
            labelErreur.setText("Le nom doit contenir au moins 2 caractères.");
            return;
        }

        String description = champDescription.getText() == null ? "" : champDescription.getText().trim();

        try {
            if (categorieAModifier == null) {
                Categorie nouvelleCategorie = new Categorie();
                nouvelleCategorie.setNom(nom);
                nouvelleCategorie.setDescription(description);
                categorieService.addCategorie(nouvelleCategorie);
            } else {
                categorieAModifier.setNom(nom);
                categorieAModifier.setDescription(description);
                categorieService.updateCategorie(categorieAModifier);
            }
        } catch (Exception e) {
            labelErreur.setText("Erreur lors de l'enregistrement : " + e.getMessage());
            return;
        }

        categorieEnregistree = true;
        fermerFenetre();
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) boutonAnnuler.getScene().getWindow();
        stage.close();
    }
}