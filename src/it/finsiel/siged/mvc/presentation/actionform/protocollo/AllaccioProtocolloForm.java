package it.finsiel.siged.mvc.presentation.actionform.protocollo;

/*
 * import it.finsiel.siged.mvc.vo.lookup.Capitolo; import
 * it.finsiel.siged.mvc.vo.lookup.Soggetto; import
 * it.finsiel.siged.mvc.vo.lookup.TipoDocumento; import
 * it.finsiel.siged.mvc.vo.lookup.Titolario; import
 * it.finsiel.siged.mvc.vo.organizzazione.Ufficio; import
 * it.finsiel.siged.mvc.vo.organizzazione.Utente; import
 * it.finsiel.siged.mvc.vo.registro.Registro;
 * 
 * import java.util.Date; import java.util.HashMap;
 */

import it.finsiel.siged.mvc.presentation.action.protocollo.AllaccioProtocolloAction;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.NumberUtil;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class AllaccioProtocolloForm extends ActionForm {

    static Logger logger = Logger.getLogger(AllaccioProtocolloAction.class
            .getName());

    private Collection allacciabili = new ArrayList();

    private Collection allacciati = new ArrayList();

    private long allaccioProtocolloId;

    private String flagPrincipale;

    private String checkAllaccio;

    private ProtocolloVO fkProtocollo;

    private ProtocolloVO fkProtocolloAllacciato;

    private String allaccioAnnoProtocollo;

    private String allaccioProtocolloDa;

    private String allaccioProtocolloA;

    private String allaccioProtocolloAnno;

    private String btnCercaAllacci;

    private String btnSelezionaAllacci;

    private String btnAnnulla;

    /**
     * @return Returns the allaccioProtocolloA.
     */
    public String getAllaccioProtocolloA() {
        return allaccioProtocolloA;
    }

    /**
     * @param allaccioProtocolloA
     *            The allaccioProtocolloA to set.
     */
    public void setAllaccioProtocolloA(String allaccioProtocolloA) {
        this.allaccioProtocolloA = allaccioProtocolloA;
    }

    /**
     * @return Returns the allaccioProtocolloAnno.
     */
    public String getAllaccioProtocolloAnno() {
        return allaccioProtocolloAnno;
    }

    /**
     * @param allaccioProtocolloAnno
     *            The allaccioProtocolloAnno to set.
     */
    public void setAllaccioProtocolloAnno(String allaccioProtocolloAnno) {
        this.allaccioProtocolloAnno = allaccioProtocolloAnno;
    }

    public long getAllaccioProtocolloId() {
        return allaccioProtocolloId;
    }

    public void setAllaccioProtocolloId(long allaccioProtocolloId) {
        this.allaccioProtocolloId = allaccioProtocolloId;
    }

    public ProtocolloVO getFkProtocollo() {
        return fkProtocollo;
    }

    public void setFkProtocollo(ProtocolloVO fkProtocollo) {
        this.fkProtocollo = fkProtocollo;
    }

    public ProtocolloVO getFkProtocolloAllacciato() {
        return fkProtocolloAllacciato;
    }

    public void setFkProtocolloAllacciato(ProtocolloVO fkProtocolloAllacciato) {
        this.fkProtocolloAllacciato = fkProtocolloAllacciato;
    }

    public String getFlagPrincipale() {
        return flagPrincipale;
    }

    public void setFlagPrincipale(String flagPrincipale) {
        this.flagPrincipale = flagPrincipale;
    }

    /*
     * public AllaccioProtocollo[] getAllacciProtocollo(Protocollo protocollo) {
     * AllaccioProtocollo[] allacci = new AllaccioProtocollo[1]; return allacci; }
     */

    /**
     * @return Returns the allaccioProtocolloDa.
     */
    public String getAllaccioProtocolloDa() {
        return allaccioProtocolloDa;
    }

    /**
     * @param allaccioProtocolloDa
     *            The allaccioProtocolloDa to set.
     */
    public void setAllaccioProtocolloDa(String allaccioProtocolloDa) {
        this.allaccioProtocolloDa = allaccioProtocolloDa;
    }

    /**
     * @return Returns the allaccioAnnoProtocollo.
     */
    public String getAllaccioAnnoProtocollo() {
        return allaccioAnnoProtocollo;
    }

    /**
     * @param allaccioAnnoProtocollo
     *            The allaccioAnnoProtocollo to set.
     */
    public void setAllaccioAnnoProtocollo(String allaccioAnnoProtocollo) {
        this.allaccioAnnoProtocollo = allaccioAnnoProtocollo;
    }

    /**
     * @return Returns the allaccioProtocollo.
     */
    public Collection getAllacciabili() {
        return allacciabili;
    }

    /**
     * @param allaccioProtocollo
     *            The allaccioProtocollo to set.
     */
    public void setAllacciabili(Collection allaccioProtocollo) {
        this.allacciabili = allaccioProtocollo;
    }

    /**
     * @return Returns the btnListaAllacci.
     */
    public String getBtnCercaAllacci() {
        return btnCercaAllacci;
    }

    /**
     * @param btnListaAllacci
     *            The btnListaAllacci to set.
     */
    public void setBtnCercaAllacci(String btnListaAllacci) {
        this.btnCercaAllacci = btnListaAllacci;
    }

    /**
     * @return Returns the btnAnnulla.
     */
    public String getBtnAnnulla() {
        return btnAnnulla;
    }

    /**
     * @param btnAnnulla
     *            The btnAnnulla to set.
     */
    public void setBtnAnnulla(String btnAnnulla) {
        this.btnAnnulla = btnAnnulla;
    }

    /**
     * @return Returns the btnSelezionaAllacci.
     */
    public String getBtnSelezionaAllacci() {
        return btnSelezionaAllacci;
    }

    /**
     * @param btnSelezionaAllacci
     *            The btnSelezionaAllacci to set.
     */
    public void setBtnSelezionaAllacci(String btnSelezionaAllacci) {
        this.btnSelezionaAllacci = btnSelezionaAllacci;
    }

    /**
     * @return Returns the checkAllaccio.
     */
    public String getCheckAllaccio() {
        return checkAllaccio;
    }

    /**
     * @param checkAllaccio
     *            The checkAllaccio to set.
     */
    public void setCheckAllaccio(String checkAllaccio) {
        this.checkAllaccio = checkAllaccio;
    }

    /**
     * @return Returns the allacciati.
     */
    public Collection getAllacciati() {
        return allacciati;
    }

    /**
     * @param allacciati
     *            The allacciati to set.
     */
    public void setAllacciati(Collection allacciati) {
        this.allacciati = allacciati;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (btnCercaAllacci != null) {
            // controllare i campi da, A, Anno
            if (allaccioProtocolloDa != null
                    && !"".equals(allaccioProtocolloDa)
                    && !(NumberUtil.isInteger(allaccioProtocolloDa))) {
                errors.add("allaccioProtocolloDa", new ActionMessage(
                        "formato_numerico"));
            } else if (allaccioProtocolloA != null
                    && !"".equals(allaccioProtocolloA)
                    && !(NumberUtil.isInteger(allaccioProtocolloA))) {
                errors.add("allaccioProtocolloA", new ActionMessage(
                        "formato_numerico"));
            } else if (allaccioProtocolloA != null
                    && !"".equals(allaccioProtocolloA)
                    && (NumberUtil.isInteger(allaccioProtocolloA))
                    && allaccioProtocolloDa != null
                    && !"".equals(allaccioProtocolloDa)
                    && (NumberUtil.isInteger(allaccioProtocolloDa))
                    && NumberUtil.getInt(allaccioProtocolloDa) > NumberUtil
                            .getInt(allaccioProtocolloDa)) {
                errors.add("allaccioIntervalloNumeri", new ActionMessage(
                        "numeri_protocollo_incongruenti"));

            } else if (allaccioProtocolloAnno != null
                    && !"".equals(allaccioProtocolloAnno)
                    && !(NumberUtil.isInteger(allaccioProtocolloAnno))) {
                errors.add("allaccioProtocolloAnno", new ActionMessage(
                        "formato_numerico"));
            }

        }

        return errors;

    }

}