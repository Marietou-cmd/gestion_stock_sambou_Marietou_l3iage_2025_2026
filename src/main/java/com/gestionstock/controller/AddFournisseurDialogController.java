package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class AddFournisseurDialogController {

    // Format email simple : quelque chose@quelque chose.quelque chose
    private static final Pattern MOTIF_EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    // 9 chiffres, débutant par 77, 78, 75, 76 ou 70 (règle exacte du sujet)
    private static final Pattern MOTIF_TELEPHONE = Pattern.compile("^(77|78|75|76|70)\\d{7}$");

    @FXML
    private Label labelTitre;
    @FXML
    private TextField champNom;
    @FXML
    private TextField champEmail;
    @FXML
    private TextField champTel;
    @FXML
    private Label labelErreur;
    @FXML
    private Button boutonAnnuler;
    @FXML
    private Button boutonEnregistrer;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private Fournisseur fournisseurAModifier = null;
    private boolean fournisseurEnregistre = false;

    public void setFournisseurAModifier(Fournisseur fournisseur) {
        this.fournisseurAModifier = fournisseur;
        labelTitre.setText("Modifier le fournisseur");
        champNom.setText(fournisseur.getNom());
        champEmail.setText(fournisseur.getEmail());
        champTel.setText(fournisseur.getTel());
    }

    public boolean isFournisseurEnregistre() {
        return fournisseurEnregistre;
    }

    @FXML
    private void enregistrer() {
        String erreur = validerFormulaire();
        if (erreur != null) {
            labelErreur.setText(erreur);
            return;
        }

        String nom = champNom.getText().trim();
        String email = champEmail.getText() == null ? "" : champEmail.getText().trim();
        String tel = champTel.getText() == null ? "" : champTel.getText().trim();

        try {
            if (fournisseurAModifier == null) {
                Fournisseur nouveauFournisseur = new Fournisseur();
                nouveauFournisseur.setNom(nom);
                nouveauFournisseur.setEmail(email.isEmpty() ? null : email);
                nouveauFournisseur.setTel(tel.isEmpty() ? null : tel);
                fournisseurService.addFournisseur(nouveauFournisseur);
            } else {
                fournisseurAModifier.setNom(nom);
                fournisseurAModifier.setEmail(email.isEmpty() ? null : email);
                fournisseurAModifier.setTel(tel.isEmpty() ? null : tel);
                fournisseurService.updateFournisseur(fournisseurAModifier);
            }
        } catch (Exception e) {
            labelErreur.setText("Erreur lors de l'enregistrement : " + e.getMessage());
            return;
        }

        fournisseurEnregistre = true;
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

    private String validerFormulaire() {
        String nom = champNom.getText() == null ? "" : champNom.getText().trim();
        if (nom.length() < 2) {
            return "Le nom doit contenir au moins 2 caractères.";
        }

        String email = champEmail.getText() == null ? "" : champEmail.getText().trim();
        if (!email.isEmpty() && !MOTIF_EMAIL.matcher(email).matches()) {
            return "Le format de l'email n'est pas valide.";
        }

        String tel = champTel.getText() == null ? "" : champTel.getText().trim();
        if (!tel.isEmpty() && !MOTIF_TELEPHONE.matcher(tel).matches()) {
            return "Le téléphone doit contenir 9 chiffres et débuter par 77, 78, 75, 76 ou 70.";
        }

        return null;
    }
}