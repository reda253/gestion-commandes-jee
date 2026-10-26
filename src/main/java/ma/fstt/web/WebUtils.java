package ma.fstt.web;

import jakarta.servlet.http.HttpServletRequest;

/** Petites méthodes utilitaires partagées par les servlets. */
final class WebUtils {

    private WebUtils() {
    }

    static String action(HttpServletRequest req) {
        String a = req.getParameter("action");
        return a == null ? "list" : a;
    }

    static Long id(HttpServletRequest req) {
        return Long.valueOf(req.getParameter("id"));
    }

    static Long optionalId(HttpServletRequest req) {
        String v = req.getParameter("id");
        return (v == null || v.isBlank()) ? null : Long.valueOf(v);
    }

    /** Message affiché une seule fois après une redirection (pattern Post/Redirect/Get). */
    static void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flashType", type);
        req.getSession().setAttribute("flashMessage", message);
    }

    static String rootMessage(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null) root = root.getCause();
        return root.getMessage();
    }
}
