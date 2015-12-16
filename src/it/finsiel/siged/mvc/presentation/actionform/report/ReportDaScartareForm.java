package it.finsiel.siged.mvc.presentation.actionform.report;

import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiUtentiForm;
import it.finsiel.siged.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class ReportDaScartareForm extends ReportCommonForm implements
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

        if (getDataInizio() != null && !"".equals(getDataInizio().trim())
                && !DateUtil.isData(getDataInizio())) {
            errors.add("dataInizio", new ActionMessage("formato.data.errato",
                    "Data inizio", ""));
        } else if (getDataFine() != null && !"".equals(getDataFine().trim())
                && !DateUtil.isData(getDataFine())) {
            errors.add("dataFine", new ActionMessage("formato.data.errato",
                    "Data fine", ""));
        } else if (getDataInizio() != null
                && !"".equals(getDataInizio().trim())
                && (getDataFine() == null || "".equals(getDataFine().trim()))) {
            errors.add("dataInizio", new ActionMessage("campo.obbligatorio",
                    "Data fine", "(se impostata Data inizio)"));
        } else if (getDataFine() != null
                && !"".equals(getDataFine().trim())
                && (getDataInizio() == null || ""
                        .equals(getDataInizio().trim()))) {
            errors.add("dataInizio", new ActionMessage("campo.obbligatorio",
                    "Data fine", "(se impostata Data inizio)"));
        } else if (getDataFine() != null
                && !"".equals(getDataFine().trim())
                && getDataInizio() != null
                && !"".equals(getDataInizio().trim())
                && DateUtil.toDate(getDataFine()).before(
                        DateUtil.toDate(getDataInizio()))) {
            errors.add("dataInizio", new ActionMessage("date_incongruenti"));
        }
        return errors;
    }
}