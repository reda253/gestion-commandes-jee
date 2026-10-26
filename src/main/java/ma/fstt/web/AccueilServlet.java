package ma.fstt.web;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.service.IClientDao;
import ma.fstt.service.ICommandeDao;
import ma.fstt.service.IProduitDao;

/** Page d'accueil : tableau de bord avec le nombre d'éléments de chaque espace. */
@WebServlet("/accueil")
public class AccueilServlet extends HttpServlet {

    @Inject private IClientDao clientDao;
    @Inject private IProduitDao produitDao;
    @Inject private ICommandeDao commandeDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("nbClients", clientDao.findAll().size());
        req.setAttribute("nbProduits", produitDao.findAll().size());
        req.setAttribute("nbCommandes", commandeDao.findAll().size());
        req.getRequestDispatcher("/WEB-INF/views/accueil.jsp").forward(req, resp);
    }
}
