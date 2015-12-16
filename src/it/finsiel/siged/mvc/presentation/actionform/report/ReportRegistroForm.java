package it.finsiel.siged.mvc.presentation.actionform.report;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiUtentiForm;
import it.finsiel.siged.util.DateUtil;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class ReportRegistroForm extends ReportCommonForm implements
        AlberoUfficiUtentiForm {

    private Utente utenteCorrente;

    private String tipoProtocollo;

    private String ufficioSelezionato;

    public String getUfficioSelezionato() {
        return ufficioSelezionato;
    }

    public void setUfficioSelezionato(String ufficioSelezionato) {
        this.ufficioSelezionato = ufficioSelezionato;
    }

    /**
     * @return Returns the tipoProtocollo.
     */
    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    /**
     * @param tipoProtocollo
     *            The tipoProtocollo to set.
     */
    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }

    public boolean getTuttiUffici() {
        if (getUfficioCorrente() == null)
            return false;
        boolean tutti = getUfficioCorrente().getParentId() == 0;
        if (!tutti) {
            tutti = getUfficioCorrente().getId().equals(
                    utenteCorrente.getUfficioVOInUso().getId());
        }
        return tutti;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.utenteCorrente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (getDataInizio() == null || "".equals(getDataInizio())) {
            errors.add("dataInizio", new ActionMessage("campo.obbligatorio",
                    "data inizio", ""));
        } else if (!DateUtil.isData(getDataInizio())) {
            errors.add("dataInizio", new ActionMessage("formato.data.errato",
                    "data inizio", ""));
        }
        if (getDataFine() == null || "".equals(getDataFine())) {
            errors.add("dataFine", new ActionMessage("campo.obbligatorio",
                    "data fine", ""));
        } else if (!DateUtil.isData(getDataFine())) {
            errors.add("dataFine", new ActionMessage("formato.data.errato",
                    "data fine", ""));
        }
        if (getDataFine() != null
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