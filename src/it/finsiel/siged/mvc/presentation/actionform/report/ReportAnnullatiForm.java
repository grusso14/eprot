package it.finsiel.siged.mvc.presentation.actionform.report;

import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiUtentiForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public final class ReportAnnullatiForm extends ReportCommonForm implements
        AlberoUfficiUtentiForm {

    private String assegnatario;

    /**
     * @return Returns the assegnatario.
     */
    public String getAssegnatario() {
        return assegnatario;
    }

    /**
     * @param assegnatario
     *            The assegnatario to set.
     */
    public void setAssegnatario(String assegnatario) {
        this.assegnatario = assegnatario;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        return errors;
    }
}