package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AddProduitDialogController {

    @FXML
    private TextField champNom;
    @FXML
    private ComboBox<Categorie> comboCategorie;
    @FXML
    private ComboBox<Fournisseur> comboFournisseur;
    @FXML
    private TextField champPrix;
    @FXML
    private TextField champPrixPromo;
    @FXML
    private TextField champQuantiteStock;
    @FXML
    private TextField champQuantiteMin;
    @FXML
    private Label labelErreur;
    @FXML
    private Button boutonAnnuler;
    @FXML
    private Button boutonEnregistrer;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    // null => mode "ajout" ; non-null => mode "modification"
    private Produit produitAModifier = null;

    // Indique au contrôleur appelant (ProduitController) si un produit a bien été enregistré,
// pour savoir s'il doit rafraîchir la liste après la fermeture de ce dialog.
    private boolean produitAjoute = false;
    @FXML
    public void initialize() {
        comboCategorie.setItems(FXCollections.observableArrayList(categorieService.findAllCategories()));
        comboFournisseur.setItems(FXCollections.observableArrayList(fournisseurService.findAllFournisseurs()));

        // Par défaut, une ComboBox affiche le toString() de l'objet. On force ici
        // l'affichage du seul champ "nom", plus lisible pour l'utilisateur.
        comboCategorie.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categorie categorie) {
                return categorie == null ? "" : categorie.getNom();
            }

            @Override
            public Categorie fromString(String s) {
                return null; // non utilisé : la ComboBox n'est pas éditable
            }
        });

        comboFournisseur.setConverter(new StringConverter<>() {
            @Override
            public String toString(Fournisseur fournisseur) {
                return fournisseur == null ? "" : fournisseur.getNom();
            }

            @Override
            public Fournisseur fromString(String s) {
                return null;
            }
        });
    }

    public void setProduitAModifier(Produit produit) {
        this.produitAModifier = produit;
        champNom.setText(produit.getNom());
        comboCategorie.setValue(produit.getCategorie());
        comboFournisseur.setValue(produit.getFournisseur());
        champPrix.setText(String.valueOf(produit.getPrix()));
        champPrixPromo.setText(produit.getPrixPromo() == null ? "" : String.valueOf(produit.getPrixPromo()));
        champQuantiteStock.setText(String.valueOf(produit.getQuantiteStock()));
        champQuantiteMin.setText(String.valueOf(produit.getQuantiteMin()));
    }

    public boolean isProduitAjoute() {
        return produitAjoute;
    }

    @FXML
    private void enregistrer() {
        String erreur = validerFormulaire();
        if (erreur != null) {
            labelErreur.setText(erreur);
            return;
        }

        Produit produit = produitAModifier == null ? new Produit() : produitAModifier;
        produit.setNom(champNom.getText().trim());
        produit.setCategorie(comboCategorie.getValue());
        produit.setFournisseur(comboFournisseur.getValue());
        produit.setPrix(Double.parseDouble(champPrix.getText().trim()));
        produit.setQuantiteStock(Integer.parseInt(champQuantiteStock.getText().trim()));
        produit.setQuantiteMin(Integer.parseInt(champQuantiteMin.getText().trim()));

        String prixPromoTexte = champPrixPromo.getText() == null ? "" : champPrixPromo.getText().trim();
        produit.setPrixPromo(prixPromoTexte.isEmpty() ? null : Double.parseDouble(prixPromoTexte));

        try {
            if (produitAModifier == null) {
                produitService.addProduit(produit);
            } else {
                produitService.updateProduit(produit);
            }
        } catch (Exception e) {
            labelErreur.setText("Erreur lors de l'enregistrement : " + e.getMessage());
            return;
        }

        produitAjoute = true;
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

    /**
     * Valide les champs du formulaire selon les règles du sujet (section "Validation des Données").
     * Retourne null si tout est valide, sinon un message d'erreur à afficher.
     */
    private String validerFormulaire() {
        String nom = champNom.getText() == null ? "" : champNom.getText().trim();
        if (nom.length() < 2) {
            return "Le nom doit contenir au moins 2 caractères.";
        }

        if (comboCategorie.getValue() == null) {
            return "Veuillez choisir une catégorie.";
        }

        if (comboFournisseur.getValue() == null) {
            return "Veuillez choisir un fournisseur.";
        }

        double prix;
        try {
            prix = Double.parseDouble(champPrix.getText().trim());
        } catch (Exception e) {
            return "Le prix doit être un nombre valide.";
        }
        if (prix <= 0) {
            return "Le prix doit être strictement positif.";
        }

        String prixPromoTexte = champPrixPromo.getText() == null ? "" : champPrixPromo.getText().trim();
        if (!prixPromoTexte.isEmpty()) {
            double prixPromo;
            try {
                prixPromo = Double.parseDouble(prixPromoTexte);
            } catch (Exception e) {
                return "Le prix promo doit être un nombre valide.";
            }
            if (prixPromo <= 0 || prixPromo >= prix) {
                return "Le prix promo doit être positif et strictement inférieur au prix normal.";
            }
        }

        int quantiteStock;
        try {
            quantiteStock = Integer.parseInt(champQuantiteStock.getText().trim());
        } catch (Exception e) {
            return "La quantité en stock doit être un nombre entier.";
        }
        if (quantiteStock < 0) {
            return "La quantité en stock ne peut pas être négative.";
        }

        int quantiteMin;
        try {
            quantiteMin = Integer.parseInt(champQuantiteMin.getText().trim());
        } catch (Exception e) {
            return "La quantité minimum doit être un nombre entier.";
        }
        if (quantiteMin < 0) {
            return "La quantité minimum ne peut pas être négative.";
        }

        return null;
    }
}