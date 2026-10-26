package ma.fstt.service;

import java.util.List;

/**
 * Interface générique CRUD implémentée par toutes les classes DAO.
 *
 * @param <T> type de l'entité
 */
public interface IDao<T> {

    T save(T entity);

    T update(T entity);

    boolean delete(Long id);

    T findById(Long id);

    List<T> findAll();
}
