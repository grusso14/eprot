package it.finsiel.siged.mvc.presentation.actionform.amministrazione;

import it.finsiel.siged.mvc.presentation.action.amministrazione.TipoDocumentoAction;
import it.finsiel.siged.mvc.vo.IdentityVO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class TipoDocumentoForm extends ActionForm {

    static Logger logger = Logger
            .getLogger(TipoDocumentoAction.class.getName());

    // private Collection tipiDocumento;
    private int id;

    private ArrayList tipiDocumento = new ArrayList();

    private int aooId;

    private String descrizione;

    private String flagAttivazione;

    private int protocolli;

    private int numGGScadenza;

    private String flagDefault;

    public Collection getTipiDefault() {
        ArrayList list = new ArrayList(3);
        IdentityVO id;
        id = new IdentityVO();
        id.setCodice("0");
        id.setDescription("No");
        list.add(id);
        id = new IdentityVO();
        id.setCodice("1");
        id.setDescription("Si");
        list.add(id);
        return list;

    }

    /**
     * @return Returns the tipiDocumento.
     */
    public ArrayList getTipiDocumento() {
        return tipiDocumento;
    }

    /**
     * @param tipiDocumento
     *            The tipiDocumento to set.
     */

    public void setTipiDocumento(ArrayList tipiDocumento) {
        this.tipiDocumento = tipiDocumento;
    }

    /**
     * @return Returns the logger.
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * @param logger
     *            The logger to set.
     */
    public static void setLogger(Logger logger) {
        TipoDocumentoForm.logger = logger;
    }

    /**
     * @return Returns the aooId.
     */
    public int getAooId() {
        return aooId;
    }

    /**
     * @param aooId
     *            The aooId to set.
     */
    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    /**
     * @return Returns the descrizioneDoc.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @param descrizioneDoc
     *            The descrizioneDoc to set.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * @return Returns the flagAttivazione.
     */
    public String getFlagAttivazione() {
        return flagAttivazione;
    }

    /**
     * @param flagAttivazione
     *            The flagAttivazione to set.
     */
    public void setFlagAttivazione(String flagAttivazione) {
        this.flagAttivazione = flagAttivazione;
    }

    /**
     * @return Returns the flagDefault.
     */
    public String getFlagDefault() {
        return flagDefault;
    }

    /**
     * @param flagDefault
     *            The flagDefault to set.
     */
    public void setFlagDefault(String flagDefault) {
        this.flagDefault = flagDefault;
    }

    /**
     * @return Returns the numGGScadenza.
     */
    public int getNumGGScadenza() {
        return numGGScadenza;
    }

    /**
     * @param numGGScadenza
     *            The numGGScadenza to set.
     */
    public void setNumGGScadenza(int numGGScadenza) {
        this.numGGScadenza = numGGScadenza;
    }

    /**
     * @return Returns the protocolli.
     */
    public int getProtocolli() {
        return protocolli;
    }

    /**
     * @param protocolli
     *            The protocolli to set.
     */
    public void setProtocolli(int protocolli) {
        this.protocolli = protocolli;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("btnConferma") != null
                && (descrizione == null || "".equals(descrizione.trim()))) {
            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
                    "Descrizione", ""));
        }

        return errors;

    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    public void inizializzaForm() {
        setId(0);
        setDescrizione(null);
        setFlagAttivazione(null);
        setProtocolli(0);
        setNumGGScadenza(0);
        setFlagDefault(null);

    }

}