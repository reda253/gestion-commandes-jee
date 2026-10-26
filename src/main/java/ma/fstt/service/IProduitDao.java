package ma.fstt.service;

import java.util.List;

import ma.fstt.entities.Produit;

public interface IProduitDao extends IDao<Produit> {

    /** Recherche par nom de produit (mot-clé partiel). */
    List<Produit> search(String motCle);
}
