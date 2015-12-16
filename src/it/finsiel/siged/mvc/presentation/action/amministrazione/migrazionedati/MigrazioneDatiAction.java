package it.finsiel.siged.mvc.presentation.action.amministrazione.migrazionedati;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.MenuDelegate;
import it.finsiel.siged.mvc.business.MigrazioneDatiDelegate;
import it.finsiel.siged.mvc.business.OrganizzazioneDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.migrazionedati.MigrazioneDatiForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action </strong> to create a new E-Photo Utente.
 * 
 * @author Spadafora.
 * 
 */

public class MigrazioneDatiAction extends Action {

    static Logger logger = Logger.getLogger(MigrazioneDatiAction.class
            .getName());

    private void ricaricaOrganizzazione() {
        OrganizzazioneDelegate.getInstance().loadOrganizzazione();
        servlet.getServletContext().setAttribute(Constants.ORGANIZZAZIONE_ROOT,
                Organizzazione.getInstance());
        Menu rm = MenuDelegate.getInstance().getRootMenu();
        servlet.getServletContext().setAttribute(Constants.MENU_ROOT, rm);
        OrganizzazioneDelegate.getInstance().caricaServiziEmail();

        LookupDelegate ld = LookupDelegate.getInstance();
        servlet.getServletContext().setAttribute(
                LookupDelegate.getIdentifier(), ld);
        ld.caricaTabelle(servlet.getServletConfig().getServletContext());
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        MigrazioneDatiForm upForm = (MigrazioneDatiForm) form;
        ActionMessages errors = new ActionMessages();
        MigrazioneDatiDelegate delegate = MigrazioneDatiDelegate.getInstance();
        try {
            if (delegate.isCaricamentoEffettuato()) {
                return mapping.findForward("noneffettuabile");
            }
            if (request.getParameter("SalvaAction") != null) {
                logger.info("Inizio migrazione dati");

                delegate
                        .caricaDati(
                                upForm.getFileUffici().getInputStream(),
                                upForm.getFileUtenti().getInputStream(),
                                upForm.getFilePermessi().getInputStream(),
                                upForm.getFileUteRegistri().getInputStream(),
                                upForm.getFileTitolario().getInputStream(),
                                upForm.getFileProtocolli().getInputStream(),
                                upForm.getFileProtocolliAss().getInputStream(),
                                upForm.getFileProtocolliDest().getInputStream(),
                                upForm.getFileStoriaProtocolli()
                                        .getInputStream(), upForm
                                        .getFileFaldoni().getInputStream(),
                                upForm.getFileReferentiUfficio()
                                        .getInputStream(),
                                upForm.getFileProcedimenti().getInputStream(),
                                upForm.getFileTipiProcedimenti()
                                        .getInputStream(), upForm
                                        .getFileTitolariUffici()
                                        .getInputStream(), upForm
                                        .getFileFascicoli().getInputStream(),
                                upForm.getFileStoriaFascicoli()
                                        .getInputStream(), upForm
                                        .getFileFaldoniFascicoli()
                                        .getInputStream(), upForm
                                        .getFileFascicoliProtocolli()
                                        .getInputStream(), upForm
                                        .getFileProcedimentiFaldone()
                                        .getInputStream(), upForm
                                        .getFileProcedimentiFascicoli()
                                        .getInputStream(), upForm
                                        .getFileProtocolliProcedimenti()
                                        .getInputStream(), upForm
                                        .getFileRubrica().getInputStream(),
                                upForm.getFileListaDistribuzione()
                                        .getInputStream(), upForm
                                        .getFileRubricaListaDistribuzione()
                                        .getInputStream(), upForm
                                        .getFileStoriaProcedimenti()
                                        .getInputStream());
                ricaricaOrganizzazione();
                logger.info("migrazione effettuata correttamente");
                return (mapping.findForward("ok"));
            } else if (request.getParameter("CaricaDaCartellaAction") != null) {
                
                logger.info("Inizio migrazione dati");
                delegate.caricaDatiDaCartella(upForm.getDirFilesMigrazione());
                logger.info("migrazione effettuata correttamente");
                return (mapping.findForward("ok"));

            }

        } catch (Exception e) {
            logger.error("Exception errore: ", e);
            // return (mapping.findForward("errore"));
            errors.add("err.formato.files", new ActionMessage(
                    "err.formato.files"));

        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return (mapping.getInputForward());
    }
}
