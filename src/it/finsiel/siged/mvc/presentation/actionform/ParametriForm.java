package it.finsiel.siged.mvc.presentation.actionform;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.mvc.presentation.helper.ReportType;

import java.util.Collection;
import java.util.HashMap;

import org.apache.struts.action.ActionForm;

/**
 * @author Calli
 * 
 */
public abstract class ParametriForm extends ActionForm {

    private HashMap reportFormats = new HashMap();

    public String getMozioneUscita() {
        return Parametri.LABEL_MOZIONE_USCITA;
    }

    public String getTutti() {
        return Parametri.TUTTI;
    }

    public String getFlagSospeso() {
        return Parametri.FLAG_STATO_SOSPESO;
    }

    public String getLabelSospeso() {
        return Parametri.LABEL_STATO_SOSPESO;
    }

    public String getAnnullato() {
        return Parametri.ANNULLATO;
    }

    public String getLabelAnnullato() {
        return Parametri.LABEL_ANNULLATO;
    }

    public void addReportType(ReportType type) {
        reportFormats.put(type.getTipoReport(), type);
    }

    public boolean removeReportType(String type) {
        return reportFormats.remove(type) != null;
    }

    public ReportType getReportType(String key) {
        return (ReportType) reportFormats.get(key);
    }

    public Collection getReportFormatsCollection() {
        return reportFormats.values();
    }

    /**
     * @return Returns the reportFormats.
     */
    public HashMap getReportFormats() {
        return reportFormats;
    }

    /**
     * @param reportFormats
     *            The reportFormats to set.
     */
    public void setReportFormats(HashMap reportFormats) {
        this.reportFormats = reportFormats;
    }
}