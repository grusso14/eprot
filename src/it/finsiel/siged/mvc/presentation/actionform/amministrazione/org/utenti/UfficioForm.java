package it.finsiel.siged.mvc.presentation.actionform.amministrazione.org.utenti;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiForm;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class UfficioForm extends ActionForm implements AlberoUfficiForm {

    static Logger logger = Logger.getLogger(UfficioForm.class.getName());

    private int id;

    private Boolean attivo;

    // private Boolean ufficioCentrale;

    private String tipo;

    private Boolean accettazioneAutomatica;

    private int aooId;

    private int parentId;

    private String codice;

    private String name;

    private String description;

    private Collection dipendentiUfficio = new ArrayList();

    private Collection uffici = new ArrayList();

    // assegnatari
    private int ufficioCorrenteId;

    private int ufficioSelezionatoId;

    private String ufficioCorrentePath;

    private UfficioVO ufficioCorrente;

    private Ufficio ufficioPadre;

    private Collection pathUffici = new ArrayList(); // di UfficioVO

    private Collection ufficiDipendenti = new ArrayList(); // di UfficioVO

    private String impostaUfficioAction;

    private int ufficiAssegnatiSelezionatiId;

    private Collection ufficiAssegnati;

    private String[] referentiId;

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

    /**
     * @return Returns the impostaUfficioAction.
     */
    public String getImpostaUfficioAction() {
        return impostaUfficioAction;
    }

    /**
     * @return Returns the ufficioCorrentePath.
     */
    public String getUfficioCorrentePath() {
        return ufficioCorrentePath;
    }

    /**
     * @param ufficioCorrentePath
     *            The ufficioCorrentePath to set.
     */
    public void setUfficioCorrentePath(String ufficioCorrentePath) {
        this.ufficioCorrentePath = ufficioCorrentePath;
    }

    /**
     * @param impostaUfficioAction
     *            The impostaUfficioAction to set.
     */
    public void setImpostaUfficioAction(String impostaUfficioAction) {
        this.impostaUfficioAction = impostaUfficioAction;
    }

    /**
     * @return Returns the pathUffici.
     */
    public Collection getPathUffici() {
        return pathUffici;
    }

    /**
     * @param pathUffici
     *            The pathUffici to set.
     */
    public void setPathUffici(Collection pathUffici) {
        this.pathUffici = pathUffici;
    }

    /**
     * @return Returns the ufficiDipendenti.
     */
    public Collection getUfficiDipendenti() {
        return ufficiDipendenti;
    }

    /**
     * @param ufficiDipendenti
     *            The ufficiDipendenti to set.
     */
    public void setUfficiDipendenti(Collection ufficiDipendenti) {
        this.ufficiDipendenti = ufficiDipendenti;
    }

    public Ufficio getUfficioPadre() {
        Organizzazione org = Organizzazione.getInstance();
        if (parentId > 0)
            return org.getUfficio(parentId);
        else
            return null;
    }

    /**
     * @return Returns the ufficioCorrenteId.
     */
    public int getUfficioCorrenteId() {
        return ufficioCorrenteId;
    }

    /**
     * @param ufficioCorrenteId
     *            The ufficioCorrenteId to set.
     */
    public void setUfficioCorrenteId(int ufficioCorrenteId) {
        this.ufficioCorrenteId = ufficioCorrenteId;
    }

    /**
     * @return Returns the ufficioSelezionatoId.
     */
    public int getUfficioSelezionatoId() {
        return ufficioSelezionatoId;
    }

    /**
     * @param ufficioSelezionatoId
     *            The ufficioSelezionatoId to set.
     */
    public void setUfficioSelezionatoId(int ufficioSelezionatoId) {
        this.ufficioSelezionatoId = ufficioSelezionatoId;
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
        UfficioForm.logger = logger;
    }

    /**
     * @return Returns the accettazioneAutomatica.
     */
    public Boolean getAccettazioneAutomatica() {
        return accettazioneAutomatica;
    }

    /**
     * @param accettazioneAutomatica
     *            The accettazioneAutomatica to set.
     */
    public void setAccettazioneAutomatica(Boolean accettazioneAutomatica) {
        this.accettazioneAutomatica = accettazioneAutomatica;
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
     * @return Returns the attivo.
     */
    public Boolean getAttivo() {
        return attivo;
    }

    /**
     * @param attivo
     *            The attivo to set.
     */
    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    /**
     * @return Returns the codice.
     */
    public String getCodice() {
        return codice;
    }

    /**
     * @param codice
     *            The codice to set.
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the parentId.
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     *            The parentId to set.
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * @return Returns the ufficioCorrente.
     */
    public UfficioVO getUfficioCorrente() {
        return ufficioCorrente;
    }

    /**
     * @param ufficioCorrente
     *            The ufficioCorrente to set.
     */
    public void setUfficioCorrente(UfficioVO ufficioCorrente) {
        this.ufficioCorrente = ufficioCorrente;
    }

    /*
     * public Boolean getUfficioCentrale() { return ufficioCentrale; }
     * 
     * public void setUfficioCentrale(Boolean ufficioCentrale) {
     * this.ufficioCentrale = ufficioCentrale; }
     */
    /**
     * @return Returns the dipendentiUfficio.
     */
    public Collection getDipendentiUfficio() {
        return dipendentiUfficio;
    }

    /**
     * @param dipendentiUfficio
     *            The dipendentiUfficio to set.
     */
    public void setDipendentiUfficio(Collection dipendentiUfficio) {
        this.dipendentiUfficio = dipendentiUfficio;
    }

    /**
     * @return Returns the ufficiAssegnati.
     */
    public Collection getUfficiAssegnati() {
        return ufficiAssegnati;
    }

    /**
     * @param ufficiAssegnati
     *            The ufficiAssegnati to set.
     */
    public void setUfficiAssegnati(Collection ufficiAssegnati) {
        this.ufficiAssegnati = ufficiAssegnati;
    }

    /**
     * @return Returns the ufficiAssegnatiSelezionatiId.
     */
    public int getUfficiAssegnatiSelezionatiId() {
        return ufficiAssegnatiSelezionatiId;
    }

    /**
     * @param ufficiAssegnatiSelezionatiId
     *            The ufficiAssegnatiSelezionatiId to set.
     */
    public void setUfficiAssegnatiSelezionatiId(int ufficiAssegnatiSelezionatiId) {
        this.ufficiAssegnatiSelezionatiId = ufficiAssegnatiSelezionatiId;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        setReferentiId(null);
    }

    public void inizializzaForm() {
        setId(0);
        setDescription(null);
        setAttivo(Boolean.FALSE);
        setAccettazioneAutomatica(Boolean.FALSE);
        setDipendentiUfficio(null);
        setReferentiId(null);

    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("btnSalva") != null) {
            if (getDescription() == null || "".equals(getDescription().trim())) {
                errors.add("descrizione", new ActionMessage(
                        "campo.obbligatorio", "Descrizione Ufficio", ""));
            }

        } else if (request.getParameter("impostaUfficioAction") != null
                && ufficioSelezionatoId == 0) {
            errors.add("ufficioSelezionatoId", new ActionMessage(
                    "campo.obbligatorio", "Ufficio", ""));

        }

        return errors;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Collection getUffici() {
        return uffici;
    }

    public void setUffici(Collection uffici) {
        this.uffici = uffici;
    }

    public void setUfficioPadre(Ufficio ufficioPadre) {
        this.ufficioPadre = ufficioPadre;
    }

    public String[] getReferentiId() {
        return referentiId;
    }

    public void setReferentiId(String[] referentiId) {
        this.referentiId = referentiId;
    }

}