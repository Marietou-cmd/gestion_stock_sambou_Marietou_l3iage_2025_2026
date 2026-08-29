package com.gestionstock.controller;

import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import com.gestionstock.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AddMouvementDialogController {

    @FXML
    private ComboBox<Produit> comboProduit;
    @FXML
    private RadioButton radioEntree;
    @FXML
    private RadioButton radioSortie;
    @FXML
    private TextField champQuantite;
    @FXML
    private TextField champMotif;
    @FXML
    private Label labelApercu;
    @FXML
    private Label labelErreur;
    @FXML
    private Button boutonAnnuler;
    @FXML
    private Button boutonEnregistrer;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    private boolean mouvementEnregistre = false;

    @FXML
    public void initialize() {
        comboProduit.setItems(FXCollections.observableArrayList(produitService.findAllProduits()));
        comboProduit.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produit produit) {
                return produit == null ? "" : produit.getNom() + " (stock: " + produit.getQuantiteStock() + ")";
            }

            @Override
            public Produit fromString(String s) {
                return null;
            }
        });

        radioEntree.setSelected(true);

        // Met à jour l'aperçu du stock résultant à chaque changement pertinent
        comboProduit.valueProperty().addListener((obs, ancien, nouveau) -> mettreAJourApercu());
        radioEntree.selectedProperty().addListener((obs, ancien, nouveau) -> mettreAJourApercu());
        champQuantite.textProperty().addListener((obs, ancien, nouveau) -> mettreAJourApercu());
    }

    public boolean isMouvementEnregistre() {
        return mouvementEnregistre;
    }

    private void mettreAJourApercu() {
        Produit produit = comboProduit.getValue();
        Integer quantite = lireQuantite();

        if (produit == null || quantite == null) {
            labelApercu.setText("");
            return;
        }

        int stockActuel = produit.getQuantiteStock();
        int stockResultant = radioEntree.isSelected() ? stockActuel + quantite : stockActuel - quantite;

        labelApercu.setText("Stock actuel : " + stockActuel + "  →  Stock après mouvement : " + stockResultant);
    }

    private Integer lireQuantite() {
        try {
            return Integer.parseInt(champQuantite.getText().trim());
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void enregistrer() {
        Produit produit = comboProduit.getValue();
        if (produit == null) {
            labelErreur.setText("Veuillez choisir un produit.");
            return;
        }

        Integer quantite = lireQuantite();
        if (quantite == null || quantite <= 0) {
            labelErreur.setText("La quantité doit être un entier strictement positif.");
            return;
        }

        TypeMouvement type = radioEntree.isSelected() ? TypeMouvement.ENTREE : TypeMouvement.SORTIE;
        String motif = champMotif.getText() == null ? "" : champMotif.getText().trim();

        if (type == TypeMouvement.SORTIE && motif.isEmpty()) {
            labelErreur.setText("Le motif est obligatoire pour une sortie de stock.");
            return;
        }

        try {
            mouvementService.enregistrerMouvement(
                    produit.getId(), type, quantite, motif.isEmpty() ? null : motif,
                    SessionUtilisateur.getInstance().getUtilisateurConnecte()
            );
        } catch (IllegalArgumentException e) {
            labelErreur.setText(e.getMessage());
            return;
        } catch (Exception e) {
            labelErreur.setText("Erreur inattendue lors de l'enregistrement.");
            e.printStackTrace();
            return;
        }

        mouvementEnregistre = true;
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