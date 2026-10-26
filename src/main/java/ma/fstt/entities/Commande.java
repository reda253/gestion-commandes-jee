package ma.fstt.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Java Bean représentant une commande passée par un client. */
public class Commande implements Serializable {

    private Long id;
    private LocalDate dateCommande;
    private String statut;
    private Client client;
    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande() {
    }

    public Commande(Long id, LocalDate dateCommande, String statut, Client client) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.client = client;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateCommande() { return dateCommande; }
    public void setDateCommande(LocalDate dateCommande) { this.dateCommande = dateCommande; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public List<LigneCommande> getLignes() { return lignes; }
    public void setLignes(List<LigneCommande> lignes) { this.lignes = lignes; }

    /** Montant total de la commande (somme des sous-totaux des lignes). */
    public double getTotal() {
        return lignes.stream().mapToDouble(LigneCommande::getSousTotal).sum();
    }

    @Override
    public String toString() {
        return "Commande{id=" + id + ", date=" + dateCommande + ", statut=" + statut + "}";
    }
}
