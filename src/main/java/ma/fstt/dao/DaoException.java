package ma.fstt.dao;

/** Exception non vérifiée levée par la couche DAO (enveloppe les SQLException). */
public class DaoException extends RuntimeException {

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
