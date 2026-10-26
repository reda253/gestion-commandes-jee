package ma.fstt.service;

import java.util.List;

import ma.fstt.entities.LigneCommande;

public interface ILigneCommandeDao extends IDao<LigneCommande> {

    List<LigneCommande> findByCommande(Long commandeId);
}
