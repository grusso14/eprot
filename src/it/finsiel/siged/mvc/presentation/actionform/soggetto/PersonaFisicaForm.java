package it.finsiel.siged.mvc.presentation.actionform.soggetto;

import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.SoggettoDelegate;
import it.finsiel.siged.mvc.presentation.action.protocollo.ProtocolloIngressoAction;
import it.finsiel.siged.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class PersonaFisicaForm extends SoggettoForm {

    static Logger logger = Logger.getLogger(ProtocolloIngressoAction.class
            .getName());

    private ArrayList listaPersone = new ArrayList();

    private String cognome;

    private String nome;

    private String codiceFiscale;

    private String codMatricola;

    private String qualifica;

    private String dataNascita;

    private String sesso;

    private String comuneNascita;

    private String statoCivile;

    private String provinciaNascitaId;

    private String cerca;

    private String salvaAction;

    private String deleteAction;
    
    private Collection statiCivili;
    
    private boolean indietroVisibile;

    public boolean isIndietroVisibile() {
        return indietroVisibile;
    }

    public void setIndietroVisibile(boolean indietroVisibile) {
        this.indietroVisibile = indietroVisibile;
    }

    /**
     * @return Returns the codiceFiscale.
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * @param codiceFiscale
     *            The codiceFiscale to set.
     */
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    /**
     * @return Returns the cognome.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome
     *            The cognome to set.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return Returns the nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome
     *            The nome to set.
     */
    public void setNome(String nome) {
        this.nome = nome;
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
        PersonaFisicaForm.logger = logger;
    }

    /**
     * @return Returns the codMatricola.
     */
    public String getCodMatricola() {
        return codMatricola;
    }

    /**
     * @param codMatricola
     *            The codMatricola to set.
     */
    public void setCodMatricola(String codMatricola) {
        this.codMatricola = codMatricola;
    }

    /**
     * @return Returns the comuneNascita.
     */
    public String getComuneNascita() {
        return comuneNascita;
    }

    /**
     * @param comuneNascita
     *            The comuneNascita to set.
     */
    public void setComuneNascita(String comuneNascita) {
        this.comuneNascita = comuneNascita;
    }

    /**
     * @return Returns the dataNascita.
     */
    public String getDataNascita() {
        return dataNascita;
    }

    /**
     * @param dataNascita
     *            The dataNascita to set.
     */
    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @return Returns the provinciaNascitaId.
     */
    public String getProvinciaNascitaId() {
        return provinciaNascitaId;
    }

    /**
     * @param provinciaNascitaId
     *            The provinciaNascitaId to set.
     */
    public void setProvinciaNascitaId(String provinciaNascitaId) {
        this.provinciaNascitaId = provinciaNascitaId;
    }

    /**
     * @return Returns the qualifica.
     */
    public String getQualifica() {
        return qualifica;
    }

    /**
     * @param qualifica
     *            The qualifica to set.
     */
    public void setQualifica(String qualifica) {
        this.qualifica = qualifica;
    }

    /**
     * @return Returns the sesso.
     */
    public String getSesso() {
        return sesso;
    }

    /**
     * @param sesso
     *            The sesso to set.
     */
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    /**
     * @return Returns the statoCivile.
     */
    public String getStatoCivile() {
        return statoCivile;
    }

    /**
     * @param statoCivile
     *            The statoCivile to set.
     */
    public void setStatoCivile(String statoCivile) {
        this.statoCivile = statoCivile;
    }

    public Collection getProvince() {
        return LookupDelegate.getInstance().getProvince();
    }

    public Collection getListaPersone() {
        return listaPersone;
    }

    public Collection getStatiCivili() {
        return SoggettoDelegate.getInstance().getLstStatoCivile();
    }

    /**
     * @param listaPersone
     *            The listaPersone to set.
     */
    public void setListaPersone(ArrayList listaPersone) {
        this.listaPersone = listaPersone;
    }

    public String getListaSize() {
        if (listaPersone == null)
            return "0";
        else
            return "" + listaPersone.size();
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (cerca != null && (cognome == null || "".equals(cognome.trim()))
                && (nome == null || "".equals(nome.trim()))
                && (codiceFiscale == null || "".equals(codiceFiscale.trim()))) {
            errors.add("cognome", new ActionMessage(
                    "parametri_ricerca_persona_fisica"));
        }
        return errors;

    }

    public ActionErrors validateDatiInserimento(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (cognome == null || "".equals(cognome)) {
            errors.add("cognome", new ActionMessage("cognome_obbligatorio"));
        } else if (dataNascita != null && !"".equals(dataNascita)
                && !DateUtil.isData(dataNascita)) {
            errors.add("dataNascita", new ActionMessage("formato_data"));
        }

        return errors;
    }

    /**
     * @return Returns the btnCercaCognome.
     */
    public String getCerca() {
        return cerca;
    }

    /**
     * @param btnCercaCognome
     *            The btnCercaCognome to set.
     */
    public void setCerca(String cerca) {
        this.cerca = cerca;
    }

    /**
     * @return Returns the confermaPFAction.
     */
    public String getSalvaAction() {
        return salvaAction;
    }

    /**
     * @param confermaPFAction
     *            The confermaPFAction to set.
     */
    public void setSalvaAction(String confermaPFAction) {
        this.salvaAction = confermaPFAction;
    }

    /**
     * @param statiCivili
     *            The statiCivili to set.
     */
    public void setStatiCivili(Collection statiCivili) {
        this.statiCivili = statiCivili;
    }

    /**
     * @return Returns the deletePFAction.
     */
    public String getDeleteAction() {
        return deleteAction;
    }

    /**
     * @param deletePFAction
     *            The deletePFAction to set.
     */
    public void setDeleteAction(String deletePFAction) {
        this.deleteAction = deletePFAction;
    }

    
}
