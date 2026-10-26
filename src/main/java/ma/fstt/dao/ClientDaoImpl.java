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
import ma.fstt.entities.Client;
import ma.fstt.service.IClientDao;
import ma.fstt.util.DBConnection;

@ApplicationScoped
public class ClientDaoImpl implements IClientDao {

    @Inject
    private DBConnection db;

    @Override
    public Client save(Client c) {
        String sql = "INSERT INTO client (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fill(ps, c);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getLong(1));
            }
            return c;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de l'ajout du client", e);
        }
    }

    @Override
    public Client update(Client c) {
        String sql = "UPDATE client SET nom=?, prenom=?, email=?, telephone=?, adresse=? WHERE id=?";
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            fill(ps, c);
            ps.setLong(6, c.getId());
            ps.executeUpdate();
            return c;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la modification du client", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM client WHERE id=?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la suppression du client", e);
        }
    }

    @Override
    public Client findById(Long id) {
        try (Connection cn = db.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM client WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Erreur lors de la recherche du client", e);
        }
    }

    @Override
    public List<Client> findAll() {
        return query("SELECT * FROM client ORDER BY nom, prenom", null);
    }

    @Override
    public List<Client> search(String motCle) {
        return query("SELECT * FROM client WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ? ORDER BY nom",
                "%" + motCle + "%");
    }

    private List<Client> query(String sql, String param) {
        List<Client> list = new ArrayList<>();
        try (Connection cn = db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param); // nom
                ps.setString(2, param); // prenom
                ps.setString(3, param); // email
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Erreur lors du chargement des clients", e);
        }
    }

    private void fill(PreparedStatement ps, Client c) throws SQLException {
        ps.setString(1, c.getNom());
        ps.setString(2, c.getPrenom());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getTelephone());
        ps.setString(5, c.getAdresse());
    }

    static Client map(ResultSet rs) throws SQLException {
        return new Client(rs.getLong("id"), rs.getString("nom"), rs.getString("prenom"),
                rs.getString("email"), rs.getString("telephone"), rs.getString("adresse"));
    }
}
