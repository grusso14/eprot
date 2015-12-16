package it.finsiel.siged.util;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.servlet.SessionTimeoutNotifier;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author p_finsiel
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class ServletUtil {

    /*
     * Restituisce la path reale su file system del context corrente
     */
    public static String getContextPath(HttpSession session) {
        return session.getServletContext().getRealPath("/");
    }

    /*
     * Restituisce il percorso completo della directory temporanea usata per IO
     * di file , etc il valore dipende dall' id della sessione, dalla cartella
     * "reale" del context e dalla variabile contenuta in web.xml -
     * TEMP_FILE_PATH
     */
    public static String getTempUserPath(HttpSession session) {
        return ((SessionTimeoutNotifier) session
                .getAttribute(Constants.SESSION_NOTIFIER)).getTempPath();
    }

    public static void ForwardTo(HttpServletRequest req,
            HttpServletResponse resp, String forwardUrl) throws IOException,
            ServletException {
        RequestDispatcher dispatcher = req.getSession().getServletContext()
                .getRequestDispatcher(forwardUrl);
        if (dispatcher != null) {
            dispatcher.forward(req, resp);
        } else {
            resp.sendRedirect(forwardUrl);
        }
    }

    public static void include(HttpServletRequest req,
            HttpServletResponse resp, String url) throws IOException,
            ServletException {
        RequestDispatcher dispatcher = req.getSession().getServletContext()
                .getRequestDispatcher(url);
        if (dispatcher != null) {
            dispatcher.include(req, resp);
        } else {
            resp.sendRedirect(url);
        }
    }

}
