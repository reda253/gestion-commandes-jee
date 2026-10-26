package ma.fstt.service;

import java.util.List;

import ma.fstt.entities.Commande;

public interface ICommandeDao extends IDao<Commande> {

    List<Commande> findByClient(Long clientId);
}
