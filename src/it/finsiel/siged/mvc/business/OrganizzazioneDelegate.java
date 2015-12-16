package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.AreaOrganizzativa;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.OrganizzazioneDAO;
import it.finsiel.siged.mvc.plugin.PersistencePlugIn;
import it.finsiel.siged.mvc.vo.organizzazione.AmministrazioneVO;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.task.jobs.CodaInvioEmailJob;
import it.finsiel.siged.task.jobs.FetchEmailJob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class OrganizzazioneDelegate {

    private static Logger logger = Logger
            .getLogger(OrganizzazioneDelegate.class.getName());

    private OrganizzazioneDAO organizzazioneDAO = null;

    private ServletConfig config = null;

    private static OrganizzazioneDelegate delegate = null;

    private OrganizzazioneDelegate() {
        // Connect to DAO
        try {
            if (organizzazioneDAO == null) {
                config = PersistencePlugIn.servletConfig;
                organizzazioneDAO = (OrganizzazioneDAO) DAOFactory
                        .getDAO(Constants.ORGANIZZAZIONE_DAO_CLASS);

                logger.debug("OrganizzazioneDAO instantiated:"
                        + Constants.ORGANIZZAZIONE_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error(
                    "Exception while connecting to OrganizzazioneDAOjdbc!!", e);
        }

    }

    public static OrganizzazioneDelegate getInstance() {
        if (delegate == null)
            delegate = new OrganizzazioneDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.ORGANIZZAZIONE_DELEGATE;
    }

    public void loadOrganizzazione() {
        Organizzazione org = null;
        AreaOrganizzativa aoo = null;
        RegistroDelegate registroDelegate = null;

        try {
            org = Organizzazione.getInstance();
            org.resetOrganizzazione();
            org.setValueObject(organizzazioneDAO.getAmministrazione());
            Collection areeOrganizzative = AreaOrganizzativaDelegate
                    .getInstance().getAreeOrganizzative();
            for (Iterator i = areeOrganizzative.iterator(); i.hasNext();) {
                aoo = new AreaOrganizzativa((AreaOrganizzativaVO) i.next());
                org.addAreaOrganizzativa(aoo);
            }
            for (Iterator i = organizzazioneDAO.getUffici().iterator(); i
                    .hasNext();) {
                UfficioVO uff = (UfficioVO) i.next();
                aoo = org.getAreaOrganizzativa(uff.getAooId());
                org.addUfficio(new Ufficio(uff));
            }

            Collection uffici = org.getUffici();
            for (Iterator i = uffici.iterator(); i.hasNext();) {
                Ufficio ui = (Ufficio) i.next();
                int parentId = ui.getValueObject().getParentId();
                if (parentId == 0) {
                    aoo = org.getAreaOrganizzativa(ui.getValueObject()
                            .getAooId());
                    aoo.setUfficioCentrale(ui);
                } else {
                    for (Iterator j = uffici.iterator(); j.hasNext();) {
                        Ufficio uj = (Ufficio) j.next();
                        int id = uj.getValueObject().getId().intValue();
                        if (id == parentId) {
                            ui.setUfficioDiAppartenenza(uj);
                            break;
                        }
                    }
                }
            }
            Collection utenti = UtenteDelegate.getInstance().getUtenti();
            for (Iterator u = utenti.iterator(); u.hasNext();) {
                UtenteVO uteVO = (UtenteVO) u.next();

                Utente ute = new Utente(uteVO);
                org.addUtente(ute);
                Collection ids = getIdentificativiUffici(uteVO.getId()
                        .intValue());
                for (Iterator i = ids.iterator(); i.hasNext();) {
                    int uffId = ((Integer) i.next()).intValue();
                    int uteId = ute.getValueObject().getId().intValue();
                    // logger.info(org.getUfficio(uffId));
                    if (org.getUfficio(uffId).getValueObject().getParentId() > 0) {
                        org.getUfficio(uffId).addUtente(ute);
                        if (isUtenteReferenteUfficio(uffId, uteId))
                            org.getUfficio(uffId).addUtenteReferente(ute);
                    }
                }
            }
            registroDelegate = RegistroDelegate.getInstance();
            for (Iterator i = registroDelegate.getRegistri().iterator(); i
                    .hasNext();) {
                org.addRegistro((RegistroVO) i.next());
            }
        } catch (DataException de) {
            logger.error("OrganizzazioneDelegate failed.", de);
        } catch (Exception e) {
            logger.error("OrganizzazioneDelegate failed.", e);
        }
    }

    public ArrayList getIdentificativiUffici(int utenteId) {
        ArrayList ufficiIds = new ArrayList();
        try {
            ufficiIds = organizzazioneDAO.getIdentificativiUffici(utenteId);

        } catch (DataException de) {
            logger.error("getUfficiUtenteIds failed.", de);
        }
        return ufficiIds;
    }

    private boolean isUtenteReferenteUfficio(int ufficioId, int utenteId)
            throws DataException {
        boolean isReferente = false;
        try {
            isReferente = UfficioDelegate.getInstance()
                    .isUtenteReferenteUfficio(ufficioId, utenteId);
        } catch (DataException de) {
            logger.error("", de);
        }
        return isReferente;

    }

    public AmministrazioneVO getAmministrazione() {
        // utilizzato
        AmministrazioneVO amministrazioneVO = null;

        try {
            amministrazioneVO = organizzazioneDAO.getAmministrazione();
        } catch (DataException de) {
            logger.error("", de);
        }
        return amministrazioneVO;
    }

    public Collection getAreeOrganizzative() throws DataException {
        Collection aoo = new ArrayList();
        try {
            aoo = AreaOrganizzativaDelegate.getInstance()
                    .getAreeOrganizzative();
            logger.info("aoo trovate:" + aoo.size());
        } catch (DataException de) {
            logger.error("getAreeOrganizzative failed.", de);
            throw new DataException("cannot.load.aoolist");
        }
        return aoo;
    }

    public AmministrazioneVO updateAmministrazione(AmministrazioneVO ammVO) {
        try {

            return organizzazioneDAO.updateAmministrazione(ammVO);
        } catch (DataException de) {
            logger.error("updateAmministrazione failed.", de);
        }
        return null;

    }

    public void caricaServiziEmail() {

        String SCHEDULER_NAME = "EPROT_SCHEDULER";
        try {

            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            try {
                logger.debug("shutting down EmailDaemon...");
                scheduler.shutdown(true);
                logger.debug("stopped.");
            } catch (SchedulerException e) {
                logger.error("", e);
            }

            scheduler = factory.getScheduler();

            Iterator aree = Organizzazione.getInstance().getAreeOrganizzative()
                    .iterator();
            boolean startScheduler = false;
            while (aree.hasNext()) {
                AreaOrganizzativa aoo = (AreaOrganizzativa) aree.next();
                AreaOrganizzativaVO aooVO = AreaOrganizzativaDelegate
                        .getInstance().getAreaOrganizzativa(
                                aoo.getValueObject().getId().intValue());
                if (aoo == null) {
                    logger
                            .warn("Organizzazione contiene un AOO (model) ma la base dati non ha corrispondeza (vo)"
                                    + aoo.getUfficioCentrale().getValueObject());
                    continue;
                } else if (aooVO.getPecAbilitata()) {
                    startScheduler = true;
                    String tmpPath = config.getServletContext()
                            .getRealPath("/")
                            + config.getServletContext().getInitParameter(
                                    Constants.TEMP_PEC_PATH)
                            + aooVO.getId().intValue();
                    // in minuti
                    int intervallo = aooVO.getPecTimer();
                    //intervallo = 5;
                    // pop3
                    JobDetail protocolloIngresso = new JobDetail(
                            "ProtocolliIngressoEmailAoo_"
                                    + aoo.getValueObject().getId().intValue(),
                            SCHEDULER_NAME, FetchEmailJob.class);

                    JobDetail protocolloUscita = new JobDetail(
                            "ProtocolliUscitaEmailAoo_"
                                    + aoo.getValueObject().getId().intValue(),
                            SCHEDULER_NAME, CodaInvioEmailJob.class);

                    // POP3S
                    protocolloIngresso.getJobDataMap().put("aoo_id",
                            aooVO.getId());
                    // SMTP(SSL)

                    protocolloUscita.getJobDataMap().put("aoo_id",
                            aooVO.getId());
                    // temp Folders setup
                    protocolloIngresso.getJobDataMap().put("mail.tempfolder",
                            tmpPath);
                    protocolloUscita.getJobDataMap().put("mail.tempfolder",
                            tmpPath);

                    SimpleTrigger triggerIngresso = new SimpleTrigger(
                            "ProtocolliIngressoTrigger_"
                                    + aooVO.getId().intValue(), SCHEDULER_NAME,
                            new Date(), null,
                            SimpleTrigger.REPEAT_INDEFINITELY,
                            intervallo * 60L * 1000L);

                    SimpleTrigger triggerUscita = new SimpleTrigger(
                            "ProtocolliUscitaTrigger_"
                                    + aooVO.getId().intValue(), SCHEDULER_NAME,
                            new Date(), null,
                            SimpleTrigger.REPEAT_INDEFINITELY,
                            intervallo * 60L * 1000L);

                    scheduler.scheduleJob(protocolloIngresso, triggerIngresso);
                    scheduler.scheduleJob(protocolloUscita, triggerUscita);
                }
            }
            if (startScheduler)
                scheduler.start();
        } catch (SchedulerException e) {
            logger.error("", e);
        }

    }

}