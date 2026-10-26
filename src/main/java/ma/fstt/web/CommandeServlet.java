package ma.fstt.web;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.dao.DaoException;
import ma.fstt.entities.Client;
import ma.fstt.entities.Commande;
import ma.fstt.entities.LigneCommande;
import ma.fstt.entities.Produit;
import ma.fstt.service.IClientDao;
import ma.fstt.service.ICommandeDao;
import ma.fstt.service.ILigneCommandeDao;
import ma.fstt.service.IProduitDao;

/**
 * Contrôleur de l'Espace commande : CRUD des commandes et de leurs lignes.
 * GET  /commandes                         -> liste
 * GET  /commandes?action=new|edit&id=     -> formulaire commande
 * GET  /commandes?action=details&id=      -> détail + lignes + ajout de ligne
 * POST /commandes?action=save             -> ajout / modification commande
 * POST /commandes?action=delete&id=       -> suppression commande
 * POST /commandes?action=addLigne         -> ajout d'une ligne
 * POST /commandes?action=updateLigne      -> modification quantité d'une ligne
 * POST /commandes?action=deleteLigne      -> suppression d'une ligne
 */
@WebServlet("/commandes")
public class CommandeServlet extends HttpServlet {

    private static final String VIEWS = "/WEB-INF/views/commande/";
    static final List<String> STATUTS = List.of("EN_COURS", "VALIDEE", "LIVREE", "ANNULEE");

    @Inject private ICommandeDao commandeDao;
    @Inject private ILigneCommandeDao ligneDao;
    @Inject private IClientDao clientDao;
    @Inject private IProduitDao produitDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (WebUtils.action(req)) {
            case "new" -> {
                Commande co = new Commande();
                co.setDateCommande(LocalDate.now());
                co.setStatut("EN_COURS");
                String clientId = req.getParameter("clientId");
                if (clientId != null) co.setClient(clientDao.findById(Long.valueOf(clientId)));
                showForm(req, resp, co);
            }
            case "edit" -> {
                Commande co = commandeDao.findById(WebUtils.id(req));
                if (co == null) { notFound(req, resp); return; }
                showForm(req, resp, co);
            }
            case "details" -> {
                Commande co = commandeDao.findById(WebUtils.id(req));
                if (co == null) { notFound(req, resp); return; }
                req.setAttribute("commande", co);
                req.setAttribute("produits", produitDao.findAll());
                req.getRequestDispatcher(VIEWS + "details.jsp").forward(req, resp);
            }
            default -> {
                req.setAttribute("commandes", commandeDao.findAll());
                req.getRequestDispatcher(VIEWS + "list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = WebUtils.action(req);
        String redirect = "/commandes";
        try {
            switch (action) {
                case "save" -> {
                    Commande co = new Commande();
                    co.setId(WebUtils.optionalId(req));
                    co.setDateCommande(LocalDate.parse(req.getParameter("dateCommande")));
                    co.setStatut(req.getParameter("statut"));
                    Client c = new Client();
                    c.setId(Long.valueOf(req.getParameter("clientId")));
                    co.setClient(c);
                    if (co.getId() == null) {
                        commandeDao.save(co);
                        WebUtils.flash(req, "success", "Commande créée. Ajoutez maintenant des produits.");
                    } else {
                        commandeDao.update(co);
                        WebUtils.flash(req, "success", "Commande modifiée avec succès.");
                    }
                    redirect = "/commandes?action=details&id=" + co.getId();
                }
                case "delete" -> {
                    commandeDao.delete(WebUtils.id(req));
                    WebUtils.flash(req, "success", "Commande supprimée (stock restauré).");
                }
                case "addLigne" -> {
                    Long commandeId = Long.valueOf(req.getParameter("commandeId"));
                    redirect = "/commandes?action=details&id=" + commandeId;
                    Produit p = produitDao.findById(Long.valueOf(req.getParameter("produitId")));
                    int quantite = Integer.parseInt(req.getParameter("quantite"));
                    if (p == null || quantite <= 0) {
                        WebUtils.flash(req, "error", "Produit ou quantité invalide.");
                    } else {
                        Commande co = new Commande();
                        co.setId(commandeId);
                        // Le prix unitaire est figé au moment de la commande
                        ligneDao.save(new LigneCommande(null, quantite, p.getPrix(), co, p));
                        WebUtils.flash(req, "success", "Produit « " + p.getNom() + " » ajouté à la commande.");
                    }
                }
                case "updateLigne" -> {
                    redirect = "/commandes?action=details&id=" + req.getParameter("commandeId");
                    int quantite = Integer.parseInt(req.getParameter("quantite"));
                    if (quantite <= 0) {
                        WebUtils.flash(req, "error", "La quantité doit être positive.");
                    } else {
                        LigneCommande l = new LigneCommande();
                        l.setId(WebUtils.id(req));
                        l.setQuantite(quantite);
                        ligneDao.update(l);
                        WebUtils.flash(req, "success", "Quantité mise à jour.");
                    }
                }
                case "deleteLigne" -> {
                    redirect = "/commandes?action=details&id=" + req.getParameter("commandeId");
                    ligneDao.delete(WebUtils.id(req));
                    WebUtils.flash(req, "success", "Ligne supprimée (stock restauré).");
                }
                default -> { }
            }
        } catch (DaoException e) {
            WebUtils.flash(req, "error", e.getMessage());
        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            WebUtils.flash(req, "error", "Valeur saisie invalide.");
        }
        resp.sendRedirect(req.getContextPath() + redirect);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Commande co)
            throws ServletException, IOException {
        req.setAttribute("commande", co);
        req.setAttribute("clients", clientDao.findAll());
        req.setAttribute("statuts", STATUTS);
        req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebUtils.flash(req, "error", "Commande introuvable.");
        resp.sendRedirect(req.getContextPath() + "/commandes");
    }
}
