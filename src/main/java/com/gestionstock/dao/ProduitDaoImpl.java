package com.gestionstock.dao;

import com.gestionstock.model.Produit;
import com.gestionstock.util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProduitDaoImpl implements ProduitDao{

    private static final String SELECT_AVEC_JOINS= """
            SELECT p.id, p.nom, p.prix, p.quantite_stock, p.quantite_min,
            p.categorie_id, p.fournisseur_id,
            c.nom as categorie_nom, f.nom as fournisseur_nom
            FROM produits p
            LEFT JOIN categories c ON p.categorie_id = c.id
            LEFT JOIN fournisseurs f ON p.fournisseur_id = f.id
            """;
    @Override
    public List<Produit> findAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = SELECT_AVEC_JOINS;

        try (Connection conn = DatabaseConfig.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                produits.add(mappingProduit(rs));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Erreur de récupération des produits: " + e.getMessage());
        }
        return  produits;
    }

    @Override
    public Optional<Produit> findById(int id) {
        String sql = SELECT_AVEC_JOINS +  " WHERE p.id = ?";
        try (Connection conn = DatabaseConfig.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mappingProduit(rs));
            }
        }catch (SQLException e) {
            throw new RuntimeException("Erreur findById produit: " + id + " : " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void addProduit(Produit p) {

    }

    @Override
    public void updateProduit(Produit p) {

    }

    @Override
    public void deleteProduit(int id) {
        String sql = "DELETE FROM produits WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de suppression du produit: " + id + " : " + e.getMessage());
        }
    }

    /*
        Le mappage de données (ou data mapping) consiste à associer les champs
        d'une source de données (base, fichier JSON) à ceux d'une destination qui est ici une classe.
        Donc on mappe les attributs de notre table produits avec notre classe Produit
     */
    private Produit mappingProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setQuantiteStock(rs.getInt("quantite_stock"));
        produit.setQuantiteMin(rs.getInt("quantite_min"));
        produit.setCategorie_nom(rs.getString("categorie_nom"));
        produit.setFournisseur_nom(rs.getString("fournisseur_nom"));
        produit.setCategorieId(rs.getInt("categorie_id"));
        produit.setFournisseurId(rs.getInt("fournisseur_id"));

        return produit;
    }
}
