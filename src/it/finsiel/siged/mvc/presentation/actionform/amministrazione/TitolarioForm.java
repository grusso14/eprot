package it.finsiel.siged.mvc.presentation.actionform.amministrazione;

import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.TitolarioDelegate;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.util.NumberUtil;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class TitolarioForm extends ActionForm {

    private int id;

    private String codice;

    private String descrizione;

    private String parentPath;

    private String parentDescrizione;

    private int aooId;

    private int parentId;

    private String massimario;

    private Collection titolariFigli;

    private TitolarioVO titolario;

    private int titolarioPrecedenteId;

    private int titolarioSelezionatoId;

    private Collection ufficiDipendenti;

    private String[] ufficiTitolario;

    private int versione;

    private Collection storiaTitolario;

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
     * @return Returns the parentDescrizione.
     */
    public String getParentDescrizione() {
        return parentDescrizione;
    }

    /**
     * @param parentDescrizione
     *            The parentDescrizione to set.
     */
    public void setParentDescrizione(String parentDescrizione) {
        this.parentDescrizione = parentDescrizione;
    }

    /**
     * @return Returns the parentPath.
     */
    public String getParentPath() {
        return parentPath;
    }

    /**
     * @param parentPath
     *            The parentPath to set.
     */
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
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
     * @return Returns the descrizione.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @param descrizione
     *            The descrizione to set.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
     * @return Returns the titolario.
     */
    public TitolarioVO getTitolario() {
        return titolario;
    }

    /**
     * @param titolario
     *            The titolario to set.
     */
    public void setTitolario(TitolarioVO titolario) {
        this.titolario = titolario;
    }

    public Collection getTitolariFigli() {
        return titolariFigli;
    }

    public void setTitolariFigli(Collection titolariDiscendenti) {
        this.titolariFigli = titolariDiscendenti;
    }

    public int getTitolarioSelezionatoId() {
        return titolarioSelezionatoId;
    }

    public void setTitolarioSelezionatoId(int titolarioSelezionatoId) {
        this.titolarioSelezionatoId = titolarioSelezionatoId;
    }

    public int getTitolarioPrecedenteId() {
        return titolarioPrecedenteId;
    }

    public void setTitolarioPrecedenteId(int titolarioPrecedenteId) {
        this.titolarioPrecedenteId = titolarioPrecedenteId;
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

    /**
     * @return Returns the ufficiTitolario.
     */
    public String[] getUfficiTitolario() {
        return ufficiTitolario;
    }

    /**
     * @param ufficiTitolario
     *            The ufficiTitolario to set.
     */
    public void setUfficiTitolario(String[] ufficiTitolario) {
        this.ufficiTitolario = ufficiTitolario;
    }

    public String getMassimario() {
        return massimario;
    }

    public void setMassimario(String massimario) {
        this.massimario = massimario;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public Collection getStoriaTitolario() {
        return storiaTitolario;
    }

    public void setStoriaTitolario(Collection storiaTitolario) {
        this.storiaTitolario = storiaTitolario;
    }

    public String getPathDescrizioneTitolario() {
        if (getTitolario().getId().intValue() > 0)
            return TitolarioBO.getPathDescrizioneTitolario(getTitolario()
                    .getId().intValue());
        else
            return "";
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        setUfficiTitolario(null);
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("btnConferma") != null) {
            if (codice == null || "".equals(codice.trim())) {
                errors.add("codice", new ActionMessage("campo.obbligatorio",
                        "Codice Titolario", ""));
            } else if (descrizione == null || "".equals(descrizione.trim())) {
                errors.add("descrizione", new ActionMessage(
                        "campo.obbligatorio", "Descrizione Titolario", ""));
            } else if (getMassimario() != null && !"".equals(getMassimario())
                    && !(NumberUtil.isInteger(getMassimario()))) {
                errors.add("massimario", new ActionMessage(
                        "formato.numerico.errato", "Massimario di scarto"));
            }

        } else if (request.getParameter("btnConfermaSposta") != null) {
            if (getTitolario() == null) {
                errors.add("titolario_obbligatorio", new ActionMessage(
                        "campo.obbligatorio", "Titolario",
                        " nella sezione sposta in .."));
            } else if (getId() == getTitolario().getId().intValue()) {
                errors
                        .add(
                                "sposta",
                                new ActionMessage(
                                        "titolario.sposta",
                                        "il livello superiore coincide con la voce selezionata",
                                        ""));
            } else {
                TitolarioDelegate td = TitolarioDelegate.getInstance();
                if (!td.controlloPermessiUffici(getId(), getTitolario().getId()
                        .intValue(), getTitolario().getAooId())) {
                    errors
                            .add(
                                    "sposta",
                                    new ActionMessage(
                                            "titolario.sposta",
                                            "l'argomento selezionato non ha i permessi sugli uffici di quello di partenza",
                                            ""));

                }

            }

        }
        return errors;

    }

}