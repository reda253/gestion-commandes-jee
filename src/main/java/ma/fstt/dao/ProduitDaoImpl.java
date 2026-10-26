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
import ma.fstt.entities.Produit;
import ma.fstt.service.IProduitDao;
import ma.fstt.util.DBConnection;

@ApplicationScoped
public class ProduitDaoImpl implements IProduitDao {

    @Inject
    private DBConnection db;

    @Override
    public Produit save(Produit p) {
        String sql = "INSERT INTO produit (nom, description, prix, quantite_stock) VALUES (?, ?, ?, ?)";
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, p);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }
            return p;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout du produit", e);
        }
    }

    @Override
    public Produit update(Produit p) {
        String sql = "UPDATE produit SET nom=?, description=?, prix=?, quantite_stock=? WHERE id=?";
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            fill(ps, p);
            ps.setLong(5, p.getId());
            ps.executeUpdate();
            return p;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la modification du produit", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM produit WHERE id=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Contrainte ON DELETE RESTRICT : produit présent dans une commande
            throw new DaoException("Impossible de supprimer ce produit : il figure dans au moins une commande", e);
        }
    }

    @Override
    public Produit findById(Long id) {
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM produit WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la recherche du produit", e);
        }
    }

    @Override
    public List<Produit> findAll() {
        return query("SELECT * FROM produit ORDER BY nom", null);
    }

    @Override
    public List<Produit> search(String motCle) {
        return query("SELECT * FROM produit WHERE nom LIKE ? ORDER BY nom", "%" + motCle + "%");
    }

    private List<Produit> query(String sql, String param) {
        List<Produit> list = new ArrayList<>();
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors du chargement des produits", e);
        }
    }

    private void fill(PreparedStatement ps, Produit p) throws SQLException {
        ps.setString(1, p.getNom());
        ps.setString(2, p.getDescription());
        ps.setDouble(3, p.getPrix());
        ps.setInt(4, p.getQuantiteStock());
    }

    static Produit map(ResultSet rs) throws SQLException {
        return new Produit(rs.getLong("id"), rs.getString("nom"), rs.getString("description"),
                rs.getDouble("prix"), rs.getInt("quantite_stock"));
    }
}
