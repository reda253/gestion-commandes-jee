package ma.fstt.web;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.dao.DaoException;
import ma.fstt.entities.Produit;
import ma.fstt.service.IProduitDao;

/**
 * Contrôleur de l'Espace produit (CRUD + recherche).
 */
@WebServlet("/produits")
public class ProduitServlet extends HttpServlet {

    private static final String VIEWS = "/WEB-INF/views/produit/";

    @Inject
    private IProduitDao produitDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (WebUtils.action(req)) {
            case "new" -> {
                req.setAttribute("produit", new Produit());
                req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Produit p = produitDao.findById(WebUtils.id(req));
                if (p == null) {
                    WebUtils.flash(req, "error", "Produit introuvable.");
                    resp.sendRedirect(req.getContextPath() + "/produits");
                    return;
                }
                req.setAttribute("produit", p);
                req.getRequestDispatcher(VIEWS + "form.jsp").forward(req, resp);
            }
            default -> {
                String q = req.getParameter("q");
                req.setAttribute("produits", (q == null || q.isBlank()) ? produitDao.findAll() : produitDao.search(q.trim()));
                req.getRequestDispatcher(VIEWS + "list.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = WebUtils.action(req);
        try {
            if ("delete".equals(action)) {
                produitDao.delete(WebUtils.id(req));
                WebUtils.flash(req, "success", "Produit supprimé avec succès.");
            } else if ("save".equals(action)) {
                Produit p = new Produit();
                p.setId(WebUtils.optionalId(req));
                p.setNom(req.getParameter("nom").trim());
                p.setDescription(req.getParameter("description"));
                p.setPrix(Double.parseDouble(req.getParameter("prix")));
                p.setQuantiteStock(Integer.parseInt(req.getParameter("quantiteStock")));
                if (p.getId() == null) {
                    produitDao.save(p);
                    WebUtils.flash(req, "success", "Produit ajouté avec succès.");
                } else {
                    produitDao.update(p);
                    WebUtils.flash(req, "success", "Produit modifié avec succès.");
                }
            }
        } catch (DaoException e) {
            WebUtils.flash(req, "error", e.getMessage());
        } catch (NumberFormatException e) {
            WebUtils.flash(req, "error", "Prix ou quantité invalide.");
        }
        resp.sendRedirect(req.getContextPath() + "/produits");
    }
}
