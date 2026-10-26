package ma.fstt.entities;

import java.io.Serializable;

/** Java Bean représentant un produit. */
public class Produit implements Serializable {

    private Long id;
    private String nom;
    private String description;
    private double prix;
    private int quantiteStock;

    public Produit() {
    }

    public Produit(Long id, String nom, String description, double prix, int quantiteStock) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }

    @Override
    public String toString() {
        return "Produit{id=" + id + ", nom=" + nom + ", prix=" + prix + "}";
    }
}
