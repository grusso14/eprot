package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class RicercaProcedimentoForm extends ActionForm implements
        AlberoUfficiUtentiForm {

    static Logger logger = Logger.getLogger(RicercaProcedimentoForm.class
            .getName());

    private String anno;

    private String numero;

    private String dataAvvioInizio;

    private String dataAvvioFine;

    private int statoId;

    private Map statiProcedimento = new HashMap(2);

    private String oggettoProcedimento;

    private String posizione;

    private Map posizioniProcedimento = new HashMap(2);

    private String dataEvidenzaInizio;

    private String dataEvidenzaFine;

    private String note;

    private int aooId;

    // uffici - titolario

    private int ufficioCorrenteId;

    private String ufficioCorrentePath;

    private int ufficioSelezionatoId;

    private int utenteSelezionatoId;

    private UfficioVO ufficioCorrente;

    private Collection ufficiDipendenti;

    private Collection utenti;

    private TitolarioVO titolario;

    private int titolarioPrecedenteId;

    private int titolarioSelezionatoId;

    private Collection titolariFigli;

    private Collection procedimenti;

    private String[] procedimentiSelezionati;
    
    private boolean indietroVisibile;
    
    /* Modifiche Greco del 28 marzo */
    private int ufficioRicercaId;
    private Utente utenteCorrente;

    
    public int getUfficioRicercaId() {
        return ufficioRicercaId;
    }

    public void setUfficioRicercaId(int ufficioRicercaId) {
        this.ufficioRicercaId = ufficioRicercaId;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.utenteCorrente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
    }
    
    public boolean isTuttiUffici() {
        if (getUfficioCorrente() == null)
            return false;
        boolean tutti = getUfficioCorrente().getParentId() == 0;
        if (!tutti) {
            tutti = getUfficioCorrente().getId().equals(
                    utenteCorrente.getUfficioVOInUso().getId());
        }
        return tutti;
    }
    
    /* ************************* */
    
    

    public String getNote() {
        return note;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOggettoProcedimento() {
        return oggettoProcedimento;
    }

    public void setOggettoProcedimento(String oggetto) {
        this.oggettoProcedimento = oggetto;
    }

    public int getUtenteSelezionatoId() {
        return utenteSelezionatoId;
    }

    public void setUtenteSelezionatoId(int utenteSelezionatoId) {
        this.utenteSelezionatoId = utenteSelezionatoId;
    }

    public Collection getUtenti() {
        return utenti;
    }

    public void setUtenti(Collection utenti) {
        this.utenti = utenti;
    }

    public Collection getUfficiDipendenti() {
        return ufficiDipendenti;
    }

    public void setUfficiDipendenti(Collection ufficiDipendenti) {
        this.ufficiDipendenti = ufficiDipendenti;
    }

    public UfficioVO getUfficioCorrente() {
        return ufficioCorrente;
    }

    public void setUfficioCorrente(UfficioVO ufficioCorrente) {
        this.ufficioCorrente = ufficioCorrente;
    }

    public int getUfficioCorrenteId() {
        return ufficioCorrenteId;
    }

    public void setUfficioCorrenteId(int ufficioCorrenteId) {
        this.ufficioCorrenteId = ufficioCorrenteId;
    }

    public String getUfficioCorrentePath() {
        return ufficioCorrentePath;
    }

    public void setUfficioCorrentePath(String ufficioCorrentePath) {
        this.ufficioCorrentePath = ufficioCorrentePath;
    }

    public int getUfficioSelezionatoId() {
        return ufficioSelezionatoId;
    }

    public void setUfficioSelezionatoId(int ufficioSelezionatoId) {
        this.ufficioSelezionatoId = ufficioSelezionatoId;
    }

    public Collection getTitolariFigli() {
        return titolariFigli;
    }

    public void setTitolariFigli(Collection titolariFigli) {
        this.titolariFigli = titolariFigli;
    }

    public TitolarioVO getTitolario() {
        return titolario;
    }

    public void setTitolario(TitolarioVO titolario) {
        this.titolario = titolario;
    }

    public int getTitolarioPrecedenteId() {
        return titolarioPrecedenteId;
    }

    public void setTitolarioPrecedenteId(int titolarioPrecedenteId) {
        this.titolarioPrecedenteId = titolarioPrecedenteId;
    }

    public int getTitolarioSelezionatoId() {
        return titolarioSelezionatoId;
    }

    public void setTitolarioSelezionatoId(int titolarioSelezionatoId) {
        this.titolarioSelezionatoId = titolarioSelezionatoId;
    }

    public int getStatoId() {
        return statoId;
    }

    public void setStatoId(int statoId) {
        this.statoId = statoId;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public String getPosizione() {
        return posizione;
    }

    public Map getStatiProcedimento() {
        return statiProcedimento;
    }

    public Collection getStatiProcedimentoCollection() {
        return statiProcedimento.values();
    }

    public void setStatiProcedimento(Map statiProcedimento) {
        this.statiProcedimento = statiProcedimento;
    }

    public Map getPosizioniProcedimento() {
        return posizioniProcedimento;
    }

    public void setPosizioniProcedimento(Map posizioniProcedimento) {
        this.posizioniProcedimento = posizioniProcedimento;
    }

    public Collection getPosizioniProcedimentoCollection() {
        return posizioniProcedimento.values();
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getDataAvvioFine() {
        return dataAvvioFine;
    }

    public void setDataAvvioFine(String dataAvvioFine) {
        this.dataAvvioFine = dataAvvioFine;
    }

    public String getDataAvvioInizio() {
        return dataAvvioInizio;
    }

    public void setDataAvvioInizio(String dataAvvioInizio) {
        this.dataAvvioInizio = dataAvvioInizio;
    }

    public String getDataEvidenzaFine() {
        return dataEvidenzaFine;
    }

    public void setDataEvidenzaFine(String dataEvidenzaFine) {
        this.dataEvidenzaFine = dataEvidenzaFine;
    }

    public String getDataEvidenzaInizio() {
        return dataEvidenzaInizio;
    }

    public void setDataEvidenzaInizio(String dataEvidenzaInizio) {
        this.dataEvidenzaInizio = dataEvidenzaInizio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Collection getProcedimenti() {
        return procedimenti;
    }

    public void setProcedimenti(Collection procedimenti) {
        this.procedimenti = procedimenti;
    }

    public String[] getProcedimentiSelezionati() {
        return procedimentiSelezionati;
    }

    public void setProcedimentiSelezionati(String[] procedimentiSelezionati) {
        this.procedimentiSelezionati = procedimentiSelezionati;
    }

    public void inizializzaForm() {
        setAnno(null);
        setDataAvvioInizio(null);
        setDataAvvioFine(null);
        setDataEvidenzaInizio(null);
        setDataEvidenzaFine(null);
        setNote(null);
        setNumero(null);
        setOggettoProcedimento(null);
        setPosizione("T");
        setStatoId(0);
        setTitolariFigli(null);
        setTitolario(null);
        setTitolarioPrecedenteId(0);
        setTitolarioSelezionatoId(0);
        setUfficiDipendenti(null);
        setUfficioCorrente(null);
        setUfficioCorrenteId(0);
        setUfficioCorrentePath(null);
        setUfficioSelezionatoId(0);
        setUtenteSelezionatoId(0);
        setUtenti(null);
        setProcedimenti(null);
        setProcedimentiSelezionati(null);
        setPosizione(null);
    }
    
    public void resetForm() {
        setAnno(null);
        setDataAvvioInizio(null);
        setDataAvvioFine(null);
        setDataEvidenzaInizio(null);
        setDataEvidenzaFine(null);
        setNote(null);
        setNumero(null);
        setOggettoProcedimento(null);
        setPosizione("T");
        setStatoId(0);
       // setTitolariFigli(null);
       // setTitolario(null);
      //  setTitolarioPrecedenteId(0);
      //  setTitolarioSelezionatoId(0);
        setUfficiDipendenti(null);
        //setUfficioCorrente(null);
        setUfficioRicercaId(0);
        //setUfficioCorrenteId(0);
        setUfficioCorrentePath(null);
        setUfficioSelezionatoId(0);
        setUtenteSelezionatoId(0);
        setUtenti(null);
        setProcedimenti(null);
        setProcedimentiSelezionati(null);
        setPosizione(null);
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        return errors;
    }

    public boolean isIndietroVisibile() {
        return indietroVisibile;
    }

    public void setIndietroVisibile(boolean indietroVisibile) {
        this.indietroVisibile = indietroVisibile;
    }

}