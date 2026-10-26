package ma.fstt.entities;

import java.io.Serializable;

/** Java Bean représentant une ligne d'une commande (un produit + une quantité). */
public class LigneCommande implements Serializable {

    private Long id;
    private int quantite;
    private double prixUnitaire;
    private Commande commande;
    private Produit produit;

    public LigneCommande() {
    }

    public LigneCommande(Long id, int quantite, double prixUnitaire, Commande commande, Produit produit) {
        this.id = id;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.commande = commande;
        this.produit = produit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Commande getCommande() { return commande; }
    public void setCommande(Commande commande) { this.commande = commande; }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public double getSousTotal() { return quantite * prixUnitaire; }

    @Override
    public String toString() {
        return "LigneCommande{id=" + id + ", quantite=" + quantite + ", prixUnitaire=" + prixUnitaire + "}";
    }
}
