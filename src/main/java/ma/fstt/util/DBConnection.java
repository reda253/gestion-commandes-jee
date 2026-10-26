package ma.fstt.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Fournit les connexions JDBC vers MySQL.
 * Bean CDI unique pour toute l'application (@ApplicationScoped),
 * les paramètres sont lus depuis src/main/resources/db.properties.
 */
@ApplicationScoped
public class DBConnection {

    private final String url;
    private final String user;
    private final String password;

    public DBConnection() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties introuvable dans le classpath");
            }
            props.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Impossible d'initialiser la connexion MySQL", e);
        }
        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password", "");
    }

    /** Ouvre une nouvelle connexion ; à fermer par l'appelant (try-with-resources). */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
