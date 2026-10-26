package ma.fstt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.fstt.entities.Commande;
import ma.fstt.entities.LigneCommande;
import ma.fstt.entities.Produit;
import ma.fstt.service.ILigneCommandeDao;
import ma.fstt.util.DBConnection;

/**
 * DAO des lignes de commande. L'ajout et la suppression d'une ligne
 * mettent à jour le stock du produit dans la même transaction.
 */
@ApplicationScoped
public class LigneCommandeDaoImpl implements ILigneCommandeDao {

    private static final String SELECT =
            "SELECT l.id, l.quantite, l.prix_unitaire, l.commande_id, "
          + "p.id AS p_id, p.nom AS p_nom, p.description AS p_desc, p.prix AS p_prix, p.quantite_stock AS p_stock "
          + "FROM ligne_commande l JOIN produit p ON p.id = l.produit_id ";

    @Inject
    private DBConnection db;

    @Override
    public LigneCommande save(LigneCommande l) {
        try (Connection cn = db.getConnection()) {
            cn.setAutoCommit(false);
            try {
                // 1. Décrémenter le stock seulement s'il est suffisant
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE produit SET quantite_stock = quantite_stock - ? WHERE id = ? AND quantite_stock >= ?")) {
                    ps.setInt(1, l.getQuantite());
                    ps.setLong(2, l.getProduit().getId());
                    ps.setInt(3, l.getQuantite());
                    if (ps.executeUpdate() == 0) {
                        throw new DaoException("Stock insuffisant pour le produit sélectionné", null);
                    }
                }
                // 2. Insérer la ligne
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, l.getCommande().getId());
                    ps.setLong(2, l.getProduit().getId());
                    ps.setInt(3, l.getQuantite());
                    ps.setDouble(4, l.getPrixUnitaire());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) l.setId(keys.getLong(1));
                    }
                }
                cn.commit();
                return l;
            } catch (SQLException | DaoException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout de la ligne de commande", e);
        }
    }

    @Override
    public LigneCommande update(LigneCommande l) {
        LigneCommande ancienne = findById(l.getId());
        if (ancienne == null) return null;
        int difference = l.getQuantite() - ancienne.getQuantite();
        try (Connection cn = db.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE produit SET quantite_stock = quantite_stock - ? WHERE id = ? AND quantite_stock >= ?")) {
                    ps.setInt(1, difference);
                    ps.setLong(2, ancienne.getProduit().getId());
                    ps.setInt(3, Math.max(difference, 0));
                    if (ps.executeUpdate() == 0) {
                        throw new DaoException("Stock insuffisant pour augmenter la quantité", null);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE ligne_commande SET quantite = ? WHERE id = ?")) {
                    ps.setInt(1, l.getQuantite());
                    ps.setLong(2, l.getId());
                    ps.executeUpdate();
                }
                cn.commit();
                return l;
            } catch (SQLException | DaoException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la modification de la ligne", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection cn = db.getConnection()) {
            cn.setAutoCommit(false);
            try {
                // Remettre la quantité en stock avant de supprimer la ligne
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE produit p JOIN ligne_commande l ON l.produit_id = p.id "
                      + "SET p.quantite_stock = p.quantite_stock + l.quantite WHERE l.id = ?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                int n;
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM ligne_commande WHERE id = ?")) {
                    ps.setLong(1, id);
                    n = ps.executeUpdate();
                }
                cn.commit();
                return n > 0;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression de la ligne", e);
        }
    }

    @Override
    public LigneCommande findById(Long id) {
        List<LigneCommande> res = query(SELECT + "WHERE l.id = ?", id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public List<LigneCommande> findAll() {
        return query(SELECT + "ORDER BY l.commande_id, l.id", null);
    }

    @Override
    public List<LigneCommande> findByCommande(Long commandeId) {
        return query(SELECT + "WHERE l.commande_id = ? ORDER BY l.id", commandeId);
    }

    private List<LigneCommande> query(String sql, Long param) {
        List<LigneCommande> list = new ArrayList<>();
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Produit p = new Produit(rs.getLong("p_id"), rs.getString("p_nom"), rs.getString("p_desc"),
                            rs.getDouble("p_prix"), rs.getInt("p_stock"));
                    Commande c = new Commande();
                    c.setId(rs.getLong("commande_id"));
                    list.add(new LigneCommande(rs.getLong("id"), rs.getInt("quantite"),
                            rs.getDouble("prix_unitaire"), c, p));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors du chargement des lignes de commande", e);
        }
    }
}
