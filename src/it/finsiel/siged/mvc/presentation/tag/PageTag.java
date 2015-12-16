package it.finsiel.siged.mvc.presentation.tag;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.MenuDelegate;
import it.finsiel.siged.mvc.presentation.helper.NavBarElement;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

;

/**
 * @author Marcello Spadafora
 * 
 */

public class PageTag extends TagSupport {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void tabMenu() throws IOException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = req.getSession();
        Menu rootMenu = (Menu) pageContext.getServletContext().getAttribute(
                Constants.MENU_ROOT);

        if (rootMenu != null) {
            Menu currentMenu = (Menu) session
                    .getAttribute(Constants.CURRENT_MENU);
            if (currentMenu != null) {
                Menu parent;
                while ((parent = currentMenu.getParent()) != null
                        && parent != rootMenu) {
                    currentMenu = parent;
                }
            }
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            MenuDelegate delegate = MenuDelegate.getInstance();
            for (Iterator i = rootMenu.getChildren().iterator(); i.hasNext();) {
                Menu menu = (Menu) i.next();
                MenuVO vo = menu.getValueObject();
                if (delegate.isUserEnabled(utente, menu)) {
                    String url = req.getContextPath() + "/menu.do?id="
                            + vo.getId();
                    out.print("<a");
                    if (menu == currentMenu) {
                        out.print(" class='selected'");
                    }
                    out.print(" href='" + url + "'");
                    out.print(" title =\"" + vo.getDescription() + "\">");
                    out.println(vo.getTitle() + "</a>");
                }
            }
        }
    }

    public void sideMenu() throws IOException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest request = (HttpServletRequest) pageContext
                .getRequest();
        HttpSession session = request.getSession();
        Menu currentMenu = (Menu) session.getAttribute(Constants.CURRENT_MENU);
        if (currentMenu != null) {
            if (currentMenu.getChildren().size() == 0) {
                currentMenu = currentMenu.getParent();
            }
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            MenuDelegate delegate = MenuDelegate.getInstance();
            for (Iterator i = currentMenu.getChildren().iterator(); i.hasNext();) {
                Menu child = (Menu) i.next();
                MenuVO vo = child.getValueObject();
                if (delegate.isUserEnabled(utente, child)) {
                    String url = request.getContextPath() + "/menu.do?id="
                            + vo.getId();
                    out.print("<a href='" + url + "'");
                    out.print(" title=\"" + vo.getDescription() + "\">");
                    if (child.getChildren().size() > 0) {
                        out.print("<strong>" + vo.getTitle() + "</strong>");
                    } else {
                        out.print(vo.getTitle());
                    }
                    out.println("</a>");
                }
            }
        }
    }

    public void navBar() throws IOException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = req.getSession();
        ArrayList navBar = (ArrayList) session.getAttribute(Constants.NAV_BAR);

        if (navBar != null) {
            int n = 0;
            for (Iterator i = navBar.iterator(); i.hasNext();) {
                NavBarElement elem = (NavBarElement) i.next();
                if (++n == navBar.size()) {
                    out.print("<span title=\"");
                    out.print(elem.getTitle());
                    out.print("\">");
                    out.print(elem.getValue());
                    out.println("</span>");
                } else {
                    out.print("<a href=\"" + req.getContextPath());
                    out.print("/navbar.do?position=" + n);
                    out.print("\" title=\"");
                    out.print(elem.getTitle());
                    out.print("\">");
                    out.print(elem.getValue());
                    out.print("</a>");
                    out.println("<span> &gt; </span>");
                }
            }
        }
    }

    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = req.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        try {
            String scriptPrefix = "<script type='text/javascript' src='"
                    + req.getContextPath() + "/script/";
            out.println("<!DOCTYPE html");
            out.println("     PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
            out.println("     \"DTD/xhtml1-strict.dtd\">");
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            out.println("<head>");
            out.print("<title>e-Prot - ");
            out.print(getTitle() + " (" + utente.getValueObject().getUsername()
                    + ")");
            out.println("</title>");
            out.print("<link rel='stylesheet' type='text/css' href='");
            out.print(req.getContextPath());
            out.println("/style/style.css' />");
/*            out.print("<style type='text/css'>@import url('");
            out.print(req.getContextPath());
            out.println("/script/calendar/calendar-blue.css');</style>");*/
            out.print("<style type='text/css'>@import url('");
            out.print(req.getContextPath());
            out.println("/script/jQuery/lib/thickbox.css');</style>"); 
            out.print("<style type='text/css'>@import url('");
            out.print(req.getContextPath());
            out.println("/script/jQuery/jquery.autocomplete.css');</style>");  
            out.print("<style type='text/css'>@import url('");
            out.print(req.getContextPath());
            out.println("/script/jQuery/jquery.dynDateTime-0.2/css/calendar-win2k-cold-2.css');</style>");            
/*            out.println(scriptPrefix + "calendar/calendar.js'></script>");
            out.println(scriptPrefix + "calendar/lang/calendar-it.js'></script>");
            out.println(scriptPrefix + "calendar/calendar-setup.js'></script>");*/
            out.println(scriptPrefix + "doc/albero.js'></script>");
            
            out.println(scriptPrefix + "jQuery/lib/jquery.js'></script>");
            out.println(scriptPrefix + "jQuery/lib/jquery.bgiframe.min.js'></script>");
            out.println(scriptPrefix + "jQuery/lib/jquery.ajaxQueue.js'></script>");
            out.println(scriptPrefix + "jQuery/lib/thickbox-compressed.js'></script>");
            out.println(scriptPrefix + "jQuery/lib/jquery.js'></script>");           
            out.println(scriptPrefix + "jQuery/jquery.autocomplete.js'></script>");
            out.println(scriptPrefix + "jQuery/jquery.dynDateTime-0.2/jquery.dynDateTime.min.js'></script>");
            out.println(scriptPrefix + "jQuery/jquery.dynDateTime-0.2/lang/calendar-it.min.js'></script>");
            
            out.println("</head>");
            out.println("<body>");
            out.println("<table id='layout' summary=''>");
            out.println("<tr>");
            out.println("<td id='header' colspan='2'>");

            out.print("<img title=\"eProt\" border=\"0\" src=\""
                    + req.getContextPath() + '/' + Constants.LOGO_EPROT
                    + "\" />");
            out.print("<img title=\""
                    + utente.getAreaOrganizzativa().getDescription()
                    + "\"  border=\"0\" src=\"" + req.getContextPath() + '/'
                    + Constants.LOGO_AMMINISTRAZIONE + "\" />");
            out.println("<span class='account'>");
            out.print("<strong>");
            out.println(utente.getAreaOrganizzativa().getCodi_aoo());
            out.println(" - ");
            out.println(utente.getAreaOrganizzativa().getDescription());
            out.println("</strong>&nbsp;&nbsp;");
            out.print("Ufficio: <strong>");
            out.print(utente.getUfficioVOInUso().getDescription());
            out.println("</strong>&nbsp;&nbsp;");
            out.print("Utente: <strong>");
            out.print(utente.getValueObject().getFullName());
            out.println("</strong>");
            out.print("<br />Registro: <strong>");
            out.print(utente.getRegistroVOInUso().getDescrizioneRegistro());
            out.println("</strong>&nbsp;&nbsp;");
            out.print("Ultimo protocollo: <strong>");
            out.print(utente.getUltimoProtocollo());
            out.println("</strong>");
            out.println("</span>");
            out.println("<br class='hidden' />");
            out.println("<a class='hidden' href='#body'>Salta il menu</a>");
            out.println("<br class='hidden' />");
            out.println("<div id='tabmenu'>");
            this.tabMenu();
            out.print("<a class='logout' href='");
            out.print(req.getContextPath());
            out.print("/help/sommario.html' target='_blank'>Help</a>");
            out.print("<a class='logout' href='");
            out.print(req.getContextPath());
            out.println("/logoff.do'>Logout</a>");
            out.print("<a class='logout' href='");
            out.print(req.getContextPath());
            out.print("/cambioPwd.do'>Cambio password</a>");
            out.println("</div>");
            out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td id='sidebar'>");
            out.println("<div id='sidemenu'>");
            out.println("<div id='sidemenu_aux'>");
            this.sideMenu();
            out.println("<br class='hidden' />");
            out.println("</div>");
            out.println("</div>");
            out.println("</td>");
            out.println("<td id='body'>");
            out.println("<a name='body' />");
            out.println("<div id='navbar'>");
            this.navBar();
            out.println("</div>");
        } catch (IOException e) {
        }
        return Tag.EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            out.println("</td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        } catch (IOException e) {
        }
        return 0;
    }
}