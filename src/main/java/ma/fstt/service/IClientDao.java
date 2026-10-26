package ma.fstt.service;

import java.util.List;

import ma.fstt.entities.Client;

public interface IClientDao extends IDao<Client> {

    /** Recherche par nom, prénom ou email (mot-clé partiel). */
    List<Client> search(String motCle);
}
