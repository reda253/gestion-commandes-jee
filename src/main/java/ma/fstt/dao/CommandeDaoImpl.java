package ma.fstt.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.fstt.entities.Client;
import ma.fstt.entities.Commande;
import ma.fstt.service.ICommandeDao;
import ma.fstt.service.ILigneCommandeDao;
import ma.fstt.util.DBConnection;

@ApplicationScoped
public class CommandeDaoImpl implements ICommandeDao {

    private static final String SELECT =
            "SELECT co.id, co.date_commande, co.statut, "
          + "c.id AS c_id, c.nom, c.prenom, c.email, c.telephone, c.adresse "
          + "FROM commande co JOIN client c ON c.id = co.client_id ";

    @Inject
    private DBConnection db;

    @Inject
    private ILigneCommandeDao ligneDao;

    @Override
    public Commande save(Commande co) {
        String sql = "INSERT INTO commande (date_commande, statut, client_id) VALUES (?, ?, ?)";
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(co.getDateCommande()));
            ps.setString(2, co.getStatut());
            ps.setLong(3, co.getClient().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) co.setId(keys.getLong(1));
            }
            return co;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la création de la commande", e);
        }
    }

    @Override
    public Commande update(Commande co) {
        String sql = "UPDATE commande SET date_commande=?, statut=?, client_id=? WHERE id=?";
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(co.getDateCommande()));
            ps.setString(2, co.getStatut());
            ps.setLong(3, co.getClient().getId());
            ps.setLong(4, co.getId());
            ps.executeUpdate();
            return co;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la modification de la commande", e);
        }
    }

    /** Supprime la commande ; les quantités de ses lignes sont remises en stock. */
    @Override
    public boolean delete(Long id) {
        try (Connection cn = db.getConnection()) {
            cn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE produit p JOIN ligne_commande l ON l.produit_id = p.id "
                      + "SET p.quantite_stock = p.quantite_stock + l.quantite WHERE l.commande_id = ?")) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                int n;
                // Les lignes sont supprimées par ON DELETE CASCADE
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM commande WHERE id = ?")) {
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
            throw new DaoException("Erreur lors de la suppression de la commande", e);
        }
    }

    @Override
    public Commande findById(Long id) {
        List<Commande> res = query(SELECT + "WHERE co.id = ?", id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public List<Commande> findAll() {
        return query(SELECT + "ORDER BY co.date_commande DESC, co.id DESC", null);
    }

    @Override
    public List<Commande> findByClient(Long clientId) {
        return query(SELECT + "WHERE c.id = ? ORDER BY co.date_commande DESC", clientId);
    }

    private List<Commande> query(String sql, Long param) {
        List<Commande> list = new ArrayList<>();
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) ps.setLong(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Client c = new Client(rs.getLong("c_id"), rs.getString("nom"), rs.getString("prenom"),
                            rs.getString("email"), rs.getString("telephone"), rs.getString("adresse"));
                    list.add(new Commande(rs.getLong("id"), rs.getDate("date_commande").toLocalDate(),
                            rs.getString("statut"), c));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors du chargement des commandes", e);
        }
        // Charger les lignes (pour le total et le détail)
        for (Commande co : list) {
            co.setLignes(ligneDao.findByCommande(co.getId()));
        }
        return list;
    }
}
