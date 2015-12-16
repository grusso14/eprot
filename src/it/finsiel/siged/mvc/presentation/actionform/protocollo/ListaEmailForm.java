package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class ListaEmailForm extends ActionForm {
    static Logger logger = Logger.getLogger(ListaEmailForm.class.getName());

    private Collection allegatiEmail = new ArrayList();

    private int docPrincipaleId;

    private String oggetto;

    private String emailMittente;

    private String nomeMittente;

    private String dataRicezione;

    private String dataSpedizione;

    private String tipoDocumentoPrincipale;

    private int emailId;

    // --------------
    private int emailSelezionataId;

    private Collection listaEmail = new ArrayList();

    public ListaEmailForm() {
    }

    public int getEmailSelezionataId() {
        return emailSelezionataId;
    }

    public void setEmailSelezionataId(int emailSelezionataId) {
        this.emailSelezionataId = emailSelezionataId;
    }

    public Collection getListaEmail() {
        return listaEmail;
    }

    public void setListaEmail(Collection listaEmail) {
        this.listaEmail = listaEmail;
    }

    public String getDataRicezione() {
        return dataRicezione;
    }

    public void setDataRicezione(String dataRicezione) {
        this.dataRicezione = dataRicezione;
    }

    public String getDataSpedizione() {
        return dataSpedizione;
    }

    public void setDataSpedizione(String dataSpedizione) {
        this.dataSpedizione = dataSpedizione;
    }

    public int getDocPrincipaleId() {
        return docPrincipaleId;
    }

    public void setDocPrincipaleId(int documentoPrincipaleId) {
        this.docPrincipaleId = documentoPrincipaleId;
    }

    public String getEmailMittente() {
        return emailMittente;
    }

    public void setEmailMittente(String emailMittente) {
        this.emailMittente = emailMittente;
    }

    public String getNomeMittente() {
        return nomeMittente;
    }

    public void setNomeMittente(String nomeMittente) {
        this.nomeMittente = nomeMittente;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public Collection getAllegatiEmail() {
        return allegatiEmail;
    }

    public void setAllegatiEmail(Collection allegati) {
        this.allegatiEmail = allegati;
    }

    public String getTipoDocumentoPrincipale() {
        return tipoDocumentoPrincipale;
    }

    public void setTipoDocumentoPrincipale(String documentoPrincipale) {
        this.tipoDocumentoPrincipale = documentoPrincipale;
    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (request.getParameter("cancella") != null
                && getEmailSelezionataId() == 0 && getEmailId() == 0) {
            errors.add("nome", new ActionMessage("selezione.obbligatoria",
                    "il messaggio", "da eliminare"));
        } else if (request.getParameter("visualizza") != null
                && getEmailSelezionataId() == 0) {
            errors.add("nome", new ActionMessage("selezione.obbligatoria",
                    "il messaggio", "da visualizzare"));
        } else if (request.getParameter("protocolla") != null) {
            if (!DateUtil.isData(getDataSpedizione()))
                errors.add("dataSpedizione", new ActionMessage(
                        "campo.obbligatorio", "Data di Spedizione", ""));
            if (!DateUtil.isData(getDataRicezione()))
                errors.add("dataRicezione", new ActionMessage(
                        "campo.obbligatorio", "Data di Ricezione", ""));
            // TODO validazione
        }

        return errors;
    }

}