package it.finsiel.siged.mvc.presentation.action;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.RegistroBO;
import it.finsiel.siged.mvc.bo.UfficioBO;
import it.finsiel.siged.mvc.business.RegistroEmergenzeDelegate;
import it.finsiel.siged.mvc.presentation.actionform.SelezionaRegistroUfficioForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action </strong> to create a new E-Photo Utente.
 * 
 * @author Almaviva sud.
 * 
 */

public final class SelezionaRegistroUfficioAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger
            .getLogger(SelezionaRegistroUfficioAction.class.getName());

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * 
     * @exception Exception
     *                if business logic throws an exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Utente utente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
        ActionMessages errors = new ActionMessages();
        if (form == null)
            form = new SelezionaRegistroUfficioForm();
        SelezionaRegistroUfficioForm formSel = (SelezionaRegistroUfficioForm) form;
        // l'utente ha clickato sul pulsante submit
        if (formSel.getButtonSubmit() != null) {
            errors = formSel.validate(mapping, request);
            // ci sono errori? controllo sulla validit? degli id e se l'utente
            // ha accesso a tali id
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                // tutto ok, selezioniamo l'ufficio in uso e il registro in uso
                // utente.setRegistroInUso( (RegistroBO.getUnicoRegistro(
                // utente.getRegistri())).getId().intValue() );
                // utente.setUfficioInUso( (UfficioBO.getUnicoUfficio(
                // utente.getUffici())).getId().intValue() );
                utente.setRegistroInUso(formSel.getRegistroId());
                utente.setUfficioInUso(formSel.getUfficioId());
                // forward alla pagina di default dell'utente
                return (mapping.findForward("pagina_iniziale"));
            }
        }
        formSel.setRegistri(RegistroBO
                .getRegistriOrdinatiByFlagUfficiale(utente
                        .getRegistriCollection()));
        formSel.setUffici(UfficioBO.getUfficiOrdinati(utente
                .getUfficiCollection()));
        request.setAttribute(mapping.getAttribute(), formSel);
        // modifica Registro Emergenza
        int numProtocolliRegistroEmergenza = RegistroEmergenzeDelegate
                .getInstance().getNumeroProtocolliPrenotati(
                        utente.getRegistroInUso());
        request.getSession().setAttribute(
                "PROTOCOLLI_EMERGENZA",
                (numProtocolliRegistroEmergenza > 0 ? new Integer(
                        numProtocolliRegistroEmergenza) : null));
        // fine modifica Registro Emergenza
        return mapping.findForward("input");
    }
    // ------------------------------------------------------ Protected Methods

}