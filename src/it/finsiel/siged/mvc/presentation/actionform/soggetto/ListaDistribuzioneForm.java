package it.finsiel.siged.mvc.presentation.actionform.soggetto;

import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class ListaDistribuzioneForm extends ActionForm {

    private ArrayList elencoListaDistribuzione = new ArrayList();

    private String descrizione;

    private String partitaIva;

    private String cognome;

    private String nome;

    private int codice;

    private Map elencoSoggettiListaDistribuzione = new HashMap();

    private String cerca;

    private String salvaAction;

    private String deleteAction;

    private String tipo;

    // private ListaVO soggetti;

    private String descrizioneDitta;

    private Collection listaPersoneLD;

    private String tornaListaPG;

    private Map soggetti;

    private String[] soggettiSelezionatiId;

    public Map getSoggetti() {
        return soggetti;
    }

    public void setSoggetti(Map soggetti) {
        this.soggetti = soggetti;
    }

    public String getTornaListaPG() {
        return tornaListaPG;
    }

    public void setTornaListaPG(String tornaListaPG) {
        this.tornaListaPG = tornaListaPG;
    }

    /**
     * @return Returns the logger.
     */

    public String getListaSize() {
        if (elencoListaDistribuzione == null)
            return "0";
        else
            return "" + elencoListaDistribuzione.size();
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
    }

    public ActionErrors validateDatiInserimento(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (request.getParameter("btnConferma") != null
                && (descrizione == null || "".equals(descrizione.trim()))) {
            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
                    "Descrizione", ""));
        } else if (getDescrizione() == null
                || "".equals(getDescrizione().trim())) {
            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
                    "descrizione", ""));
        }

        return errors;
    }

    /**
     * @return Returns the btnCercaDescrizione.
     */
    public String getCerca() {
        return cerca;
    }

    // /**
    // * @param btnCercaDescrizione
    // * The btnCercaDescrizione to set.
    // */
    public void setCerca(String cerca) {
        this.cerca = cerca;
    }

    /**
     * @return Returns the confermaLDAction.
     */
    public String getSalvaAction() {
        return salvaAction;
    }

    /**
     * @param confermaPFAction
     *            The confermaLDAction to set.
     */
    public void setSalvaAction(String confermaPFAction) {
        this.salvaAction = confermaPFAction;
    }

    /**
     * @return Returns the deleteLDAction.
     */
    public String getDeleteAction() {
        return deleteAction;
    }

    /**
     * @param deletePFAction
     *            The deleteLDAction to set.
     */
    public void setDeleteAction(String deletePFAction) {
        this.deleteAction = deletePFAction;
    }

    /**
     * @param Descrizione
     *            The Descrizione to get.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @param Descrizione
     *            The Descrizione to set.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    /**
     * @param PartitaIva
     *            The PartitaIva to set.
     */
    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getCognome() {
        return cognome;
    }

    /**
     * @param Cognome
     *            The Cognome to set.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    /**
     * @param Nome
     *            The Nome to set.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param codice
     *            The id to get.
     */
    public int getCodice() {
        return codice;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setCodice(int id) {
        this.codice = id;
    }

    public Collection getElencoSoggettiListaDistribuzioneCollection() {
        return elencoSoggettiListaDistribuzione.values();
    }

    public Map getElencoSoggettiListaDistribuzione() {
        return elencoSoggettiListaDistribuzione;
    }

    public void setElencoSoggettiListaDistribuzione(
            Map elencoSoggettiListaDistribuzione) {
        this.elencoSoggettiListaDistribuzione = elencoSoggettiListaDistribuzione;
    }

    public void aggiungiSoggetto(SoggettoVO vo) {
        if (vo != null) {
            this.elencoSoggettiListaDistribuzione.put(String.valueOf(vo.getId()
                    .intValue()), vo);
        }
    }

    public void rimuoviSoggetto(String key) {
        this.elencoSoggettiListaDistribuzione.remove(key);
    }

    public void reset(ActionMapping arg0, ServletRequest arg1) {
        // TODO Auto-generated method stub
        setCodice(0);
        setDescrizione(null);

    }

    public ArrayList getElencoListaDistribuzione() {
        return elencoListaDistribuzione;
    }

    public void setElencoListaDistribuzione(ArrayList elencoListaDistribuzione) {
        this.elencoListaDistribuzione = elencoListaDistribuzione;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescrizioneDitta() {
        return descrizioneDitta;
    }

    public void setDescrizioneDitta(String descrizioneDitta) {
        this.descrizioneDitta = descrizioneDitta;
    }

    public Collection getListaPersoneLD() {
        return listaPersoneLD;
    }

    public void setListaPersoneLD(Collection listaPersoneLD) {
        this.listaPersoneLD = listaPersoneLD;
    }

    public ActionErrors validateLDPG(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (cerca != null
                && (descrizioneDitta == null || "".equals(descrizioneDitta))
                && (partitaIva == null || "".equals(partitaIva))) {

            errors.add("descrizioneDitta", new ActionMessage(
                    "parametri_ricerca_persona_giuridica"));
        }
        return errors;

    }

    public void inizializzaForm() {
        setElencoListaDistribuzione(null);
        setDescrizione(null);
        setPartitaIva(null);
        setCognome(null);
        setNome(null);
        setCodice(0);
        setElencoSoggettiListaDistribuzione(new HashMap());
        setCerca(null);
        setSalvaAction(null);
        setDeleteAction(null);
        setTipo("F");
        setDescrizioneDitta(null);
        setListaPersoneLD(null);
        setTornaListaPG(null);

    }

    public void rimuoviSoggetti(String soggetti) {
        this.soggetti.remove(soggetti);
    }

    public void rimuoviSoggetti() {
        if (soggetti != null) {
            this.soggetti.clear();
        }
    }

    public String[] getSoggettiSelezionatiId() {
        return soggettiSelezionatiId;
    }

    public void setSoggettiSelezionatiId(String[] soggettiSelezionatiId) {
        this.soggettiSelezionatiId = soggettiSelezionatiId;
    }

}
