package it.finsiel.siged.mvc.presentation.actionform;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.RegistroBO;
import it.finsiel.siged.mvc.bo.UfficioBO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/*
 * @author Almaviva sud.
 */

public final class SelezionaRegistroUfficioForm extends ActionForm {

    private Collection uffici = new ArrayList();

    private Collection registri = new ArrayList();

    private int ufficioId;

    private int registroId;

    private String buttonSubmit;

    /**
     * @return Returns the buttonSubmit.
     */
    public String getButtonSubmit() {
        return buttonSubmit;
    }

    /**
     * @param buttonSubmit
     *            The buttonSubmit to set.
     */
    public void setButtonSubmit(String buttonSubmit) {
        this.buttonSubmit = buttonSubmit;
    }

    /**
     * @return Returns the registri.
     */
    public Collection getRegistri() {
        return registri;
    }

    /**
     * @param registri
     *            The registri to set.
     */
    public void setRegistri(Collection registri) {
        this.registri = registri;
    }

    /**
     * @return Returns the registroId.
     */
    public int getRegistroId() {
        return registroId;
    }

    /**
     * @param registroId
     *            The registroId to set.
     */
    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    /**
     * @return Returns the uffici.
     */
    public Collection getUffici() {
        return uffici;
    }

    /**
     * @param uffici
     *            The uffici to set.
     */
    public void setUffici(Collection uffici) {
        this.uffici = uffici;
    }

    /**
     * @return Returns the ufficioId.
     */
    public int getUfficioId() {
        return ufficioId;
    }

    /**
     * @param ufficioId
     *            The ufficioId to set.
     */
    public void setUfficioId(int ufficioId) {
        this.ufficioId = ufficioId;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        ufficioId = 0;
        registroId = 0;
        uffici = new ArrayList();
        registri = new ArrayList();
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (registroId == 0)
            errors.add("registroId", new ActionMessage(
                    "error.registroId.required"));
        if (ufficioId == 0)
            errors.add("ufficioId", new ActionMessage(
                    "error.ufficioId.required"));

        Utente utente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
        if (!RegistroBO.controllaPermessoRegistro(registroId, utente))
            errors.add("registroId", new ActionMessage(
                    "error.registroId.non_autorizzato"));
        if (!UfficioBO.controllaPermessoUfficio(ufficioId, utente))
            errors.add("ufficioId", new ActionMessage(
                    "error.ufficioId.non_autorizzato"));

        return errors;
    }

}