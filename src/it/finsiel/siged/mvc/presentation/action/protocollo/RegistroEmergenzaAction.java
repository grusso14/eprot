package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.RegistroEmergenzeDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.ScartoProtocolliForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RegistroEmergenzaForm;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class RegistroEmergenzaAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(RegistroEmergenzaAction.class
            .getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession();

        RegistroEmergenzaForm emergenzaForm = (RegistroEmergenzaForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

        if (form == null) {
            logger.info(" Creating new RegistroEmergenzaAction");
            form = new ScartoProtocolliForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        if (request.getParameter("salvaEmergenzaAction") != null) {
            int returnValues;
            errors = emergenzaForm.validateDatiInserimento(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return (mapping.findForward("input"));

            } else {
                int protIng = emergenzaForm.getNumeroProtocolliIngresso();
                int protUscita = emergenzaForm.getNumeroProtocolliUscita();
                ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();

                returnValues = delegate.registraProtocolliEmergenza(protIng,
                        protUscita, utente);
                // inserireil metodo delegat
            }
            // operazione è andata a buon fine
            ActionMessages msg = new ActionMessages();
            if (returnValues == ReturnValues.SAVED) {
                msg.add("operazioneEffettuata", new ActionMessage(
                        "msg.operazione.effettuata"));
                saveMessages(request, msg);
                // Collection protocolli = new ArrayList();
                RegistroEmergenzeDelegate protocolliDelegate = RegistroEmergenzeDelegate
                        .getInstance();
                Collection p = protocolliDelegate.getProtocolliPrenotati(utente
                        .getRegistroVOInUso().getId().intValue());
                emergenzaForm.setProtocolliPrenotati(p);
                request.getSession().setAttribute("PROTOCOLLI_EMERGENZA",
                        new Integer(p.size()));
            } else {
                errors.add("errore_nel_salvataggio", new ActionMessage(
                        "errore_nel_salvataggio"));
                saveErrors(request, errors);
            }

            return (mapping.findForward("lista"));
        } else if (request.getParameter("protocolloSelezionato") != null) {
            String tipoProtocollo = new String(request
                    .getParameter("tipoProtocollo"));
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloSelezionato")));
            if ("I".equals(tipoProtocollo)) {
                return (mapping.findForward("visualizzaProtocolloIngresso"));
            } else {
                return (mapping.findForward("visualizzaProtocolloUscita"));
            }

        } else if (request.getParameter("listaProtocolliPrenotati") != null) {
            RegistroEmergenzeDelegate protocolliDelegate = RegistroEmergenzeDelegate
                    .getInstance();
            Collection p = new ArrayList();
            p = protocolliDelegate.getProtocolliPrenotati(utente
                    .getRegistroVOInUso().getId().intValue());
            if (p != null && p.size() > 0) {
                emergenzaForm.setProtocolliPrenotati(p);
            } else {
                request.getSession().setAttribute("PROTOCOLLI_EMERGENZA", null);
                errors.add("nessun_dato", new ActionMessage("nessun_dato"));
                saveErrors(request, errors);
            }
            return (mapping.findForward("lista"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        logger.info("Execute RegistroEmergenzaAction");

        return (mapping.findForward("input"));
    }
}