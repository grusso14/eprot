package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.EmailDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ListaEmailLogForm;

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

public class ListaEmailLogAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger
            .getLogger(ListaEmailLogAction.class.getName());

    // --------------------------------------------------------- Public Methods

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        ListaEmailLogForm listaForm = (ListaEmailLogForm) form;

        if (listaForm == null) {
            listaForm = new ListaEmailLogForm();
            request.setAttribute(mapping.getAttribute(), listaForm);
        }

        if (request.getParameter("elimina") != null) {
            errors = listaForm.validate(mapping, request);
            String[] idSelezionati = listaForm.getEmailSelezionateId();
            if (!EmailDelegate.getInstance().eliminaEmailLog(idSelezionati)) {
                errors.add("general", new ActionMessage("database.load"));
            }
        }
        errors = listaForm.validate(mapping, request);
        if (errors.isEmpty()) {
            int tipoLog = listaForm.getTipoEvento();
            try {
                listaForm.setListaEmail(EmailDelegate.getInstance()
                        .getListaLog(utente.getValueObject().getAooId(),
                                tipoLog));
            } catch (Exception e) {
                errors.add("general", new ActionMessage("database.load"));
            }
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return (mapping.findForward("input"));
    }

}
