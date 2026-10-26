package ma.fstt.web;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.dao.DaoException;
import ma.fstt.entities.Client;
import ma.fstt.service.IClientDao;
import ma.fstt.service.ICommandeDao;

/**
 * Contrôleur de l'Espace client.
 * GET  /clients                   -> liste (+ recherche ?q=)
 * GET  /clients?action=new        -> formulaire d'ajout
 * GET  /clients?action=edit&id=   -> formulaire de modification
 * GET  /clients?action=details&id=-> fiche client + ses commandes
 * POST /clients?action=save       -> ajout ou modification
 * POST /clients?action=delete&id= -> suppression
 */
@WebServlet("/clients")
public class ClientServlet extends HttpServlet {

    private static final String VIEWS = "/WEB-INF/views/client/";

    @Inject
    private IClientDao clientDao;

    @Inject
    private ICommandeDao commandeDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = WebUtils.action(req);
        switch (action) {
            case "new" -> {
                req.setAttribute("client", new Client());
                req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Client c = clientDao.findById(WebUtils.id(req));
                if (c == null) { notFound(req, resp); return; }
                req.setAttribute("client", c);
                req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
            }
            case "details" -> {
                Client c = clientDao.findById(WebUtils.id(req));
                if (c == null) { notFound(req, resp); return; }
                req.setAttribute("client", c);
                req.setAttribute("commandes", commandeDao.findByClient(c.getId()));
                req.getRequestDispatcher(VIEWS + "details.jsp").forward(req, resp);
            }
            default -> {
                String q = req.getParameter("q");
                req.setAttribute("clients", (q == null || q.isBlank()) ? clientDao.findAll() : clientDao.search(q.trim()));
                req.getRequestDispatcher(VIEWS + "list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = WebUtils.action(req);
        try {
            if ("delete".equals(action)) {
                clientDao.delete(WebUtils.id(req));
                WebUtils.flash(req, "success", "Client supprimé avec succès.");
            } else if ("save".equals(action)) {
                Client c = new Client();
                c.setId(WebUtils.optionalId(req));
                c.setNom(req.getParameter("nom").trim());
                c.setPrenom(req.getParameter("prenom").trim());
                c.setEmail(req.getParameter("email").trim());
                c.setTelephone(req.getParameter("telephone"));
                c.setAdresse(req.getParameter("adresse"));
                if (c.getId() == null) {
                    clientDao.save(c);
                    WebUtils.flash(req, "success", "Client ajouté avec succès.");
                } else {
                    clientDao.update(c);
                    WebUtils.flash(req, "success", "Client modifié avec succès.");
                }
            }
        } catch (DaoException e) {
            String cause = WebUtils.rootMessage(e);
            WebUtils.flash(req, "error", cause != null && cause.contains("Duplicate")
                    ? "Cet email est déjà utilisé par un autre client."
                    : e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/clients");
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        WebUtils.flash(req, "error", "Client introuvable.");
        resp.sendRedirect(req.getContextPath() + "/clients");
    }
}
