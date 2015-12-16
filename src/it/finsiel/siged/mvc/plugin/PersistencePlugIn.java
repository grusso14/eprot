package it.finsiel.siged.mvc.plugin;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.MenuDelegate;
import it.finsiel.siged.mvc.business.OrganizzazioneDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

/*
 * @author Almaviva sud.
 */

public final class PersistencePlugIn implements PlugIn {

    // ----------------------------------------------------- Instance Variables

    /**
     * The application configuration for our owning module.
     */
    private ModuleConfig config = null;

    /**
     * this is necessary to de-couple the persistence by individual Delegate,
     * which could have different persistence mechanism.
     */
    private HashMap dataProviders = new HashMap();

    private ArrayList errors = new ArrayList();

    /**
     * Logging output for this plug in instance.
     */
    static Logger logger = Logger.getLogger(PersistencePlugIn.class.getName());

    /**
     * The {@link ActionServlet}owning this application.
     */
    private ActionServlet servlet = null;

    public static ServletConfig servletConfig = null;

    // ------------------------------------------------------------- Properties

    /**
     * The web application resource path of our database settings.
     */
    private String configFile = "persistence.config";

    public String getConfigFile() {
        return (this.configFile);
    }

    public void setConfigFile(String configResourceFile) {
        this.configFile = configResourceFile;
    }

    // --------------------------------------------------------- PlugIn Methods

    /**
     * Gracefully shut down this database, releasing any resources that were
     * allocated at initialization.
     */
    public void destroy() {
        logger.debug("Finalizing plug in PersistencePlugin...");
        Iterator i = dataProviders.keySet().iterator();
        while (i.hasNext()) {
            try {
                String provider = (String) i.next();
                logger.debug("Finalizing Provider:" + provider);
                DataProvider dp = (DataProvider) dataProviders.get(provider);
                dp.finalize();
                servlet.getServletContext().removeAttribute(dp.getIdentifier());
                dp = null;
            } catch (Exception e) {
                logger.error("Error Finalizing Provider", e);
            }
        }
        dataProviders.clear();
        config = null;
    }

    /**
     * Initialize and load our initial database from persistent storage.
     * 
     * @param servlet
     *            The ActionServlet for this web application
     * @param config
     *            The ApplicationConfig for our owning module
     * 
     * @exception ServletException
     *                if we cannot configure ourselves correctly
     */
    public void init(ActionServlet servlet, ModuleConfig config)
            throws ServletException {
        // Remember our associated configuration and servlet
        this.config = config;
        this.servlet = servlet;
        servletConfig = servlet.getServletConfig();

        // Initialize all Providers to be used by the application to communicate
        // with exernal
        // Systems
        // initializeProviders(servlet);
        // initialize all of our BusinessDelegate and make them
        // available in the servletcontext
        initializeBusinessDelegate(servlet);
        // Setup and cache other required data
        // setupCache(servlet, config);
    }

    // private void displayConfigNotFoundMessage() {
    // LogLog
    // .error("No Database configuration file found at given path. Falling
    // back.");
    // }

    // --------------------------------------------------------- Public Methods

    public void initializeProviders(ActionServlet servlet) {
        // now instantiate all different providers
        // a jdbc manager that point to our main db
        // try {
        //
        // DataProvider dpDef = JDBCManager.getInstance(Constants.JDBCMAN_1);
        // dataProviders.put(dpDef.getIdentifier(), dpDef);
        // servlet.getServletContext().setAttribute(dpDef.getIdentifier(),
        // dpDef);
        // } catch (DataException de) {
        // errors.add("JDBCManager " + Constants.JDBCMAN_1 + " non caricato.");
        // LogLog.error("", de);
        // }
        // try {
        // DataProvider dpDef = JDBCManager.getInstance(Constants.JDBCMAN_2);
        // dataProviders.put(dpDef.getIdentifier(), dpDef);
        // servlet.getServletContext().setAttribute(dpDef.getIdentifier(),
        // dpDef);
        //
        // } catch (DataException de) {
        // errors.add("JDBCManager " + Constants.JDBCMAN_2 + " non caricato.");
        // LogLog.error("", de);
        // }

    }

    public void initializeBusinessDelegate(ActionServlet servlet) {

        OrganizzazioneDelegate delegate = OrganizzazioneDelegate.getInstance();
        delegate.loadOrganizzazione();
        servlet.getServletContext().setAttribute(Constants.ORGANIZZAZIONE_ROOT,
                Organizzazione.getInstance());
        OrganizzazioneDelegate.getInstance().caricaServiziEmail();

        Menu rm = MenuDelegate.getInstance().getRootMenu();
        servlet.getServletContext().setAttribute(Constants.MENU_ROOT, rm);

        LookupDelegate ld = LookupDelegate.getInstance();
        servlet.getServletContext().setAttribute(
                LookupDelegate.getIdentifier(), ld);

        ld.caricaTabelle(servlet.getServletConfig().getServletContext());

        if (!errors.isEmpty()) {
            Iterator i = errors.iterator();
            while (i.hasNext()) {
                logger.warn((String) i.next());
            }
        }
    }
}