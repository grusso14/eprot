package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.presentation.helper.FascicoloView;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.FaldoneVO;
import it.finsiel.siged.util.DateUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class ProcedimentoForm extends ActionForm implements
        AlberoUfficiUtentiForm {

    static Logger logger = Logger.getLogger(ProcedimentoForm.class.getName());

    private String dataAvvio;//

    private int procedimentoId;

    private int ufficioId;

    private int titolarioId;

    private int statoId;//

    private Map statiProcedimento = new HashMap(2);//

    private int tipoFinalitaId;//

    private Map tipiFinalita = new HashMap(2);//

    private String oggettoProcedimento;//

    private int tipoProcedimentoId;//

    private String tipoProcedimento;

    private ArrayList tipiProcedimento;//

    private ArrayList tipiProcedimentoByUfficio;//

    private int referenteId;//

    private Map referenti = new HashMap(2);

    private String nomeReferente;//

    private String responsabile;//

    private String posizione;//

    private Map posizioniProcedimento = new HashMap(2);

    private String dataEvidenza;//

    private String note;//

    private int protocolloId;//

    private String numeroProtocollo;//

    private String numeroProcedimento;//

    private int anno;

    private int numero;

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

    private boolean dipTitolarioUfficio;

    private boolean versioneDefault;

    private int versione;

    // ------- allacci ----------- //
    private Map protocolli = new HashMap(2);

    private Map fascicoli = new HashMap(2);

    private Map faldoni = new HashMap(2);

    private String[] fascicoliSelezionati = null;

    private String[] faldoniSelezionati = null;

    private String[] protocolliSelezionati = null;

    private int progressivo;

    // -------- campi di ricerca --------- //

    private String cercaFascicoloNome = null;

    private String cercaFaldoneOggetto = null;

    private String cercaProtocolloOggetto = null;

    private boolean indietroVisibile;

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

    public int getReferenteId() {
        return referenteId;
    }

    public void setReferenteId(int responsabileId) {
        this.referenteId = responsabileId;
    }

    public int getTitolarioId() {
        return titolarioId;
    }

    public void setTitolarioId(int titolarioId) {
        this.titolarioId = titolarioId;
    }

    public int getUfficioId() {
        return ufficioId;
    }

    public void setUfficioId(int ufficioId) {
        this.ufficioId = ufficioId;
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

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNumeroProcedimento() {
        return numeroProcedimento;
    }

    public void setNumeroProcedimento(String numeroProcedimento) {
        this.numeroProcedimento = numeroProcedimento;
    }

    public int getProcedimentoId() {
        return procedimentoId;
    }

    public void setProcedimentoId(int procedimentoId) {
        this.procedimentoId = procedimentoId;
    }

    public int getProtocolloId() {
        return protocolloId;
    }

    public void setProtocolloId(int protocolloId) {
        this.protocolloId = protocolloId;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String referente) {
        this.responsabile = referente;
    }

    public int getStatoId() {
        return statoId;
    }

    public void setStatoId(int statoId) {
        this.statoId = statoId;
    }

    public int getTipoFinalitaId() {
        return tipoFinalitaId;
    }

    public void setTipoFinalitaId(int tipoFinalitaId) {
        this.tipoFinalitaId = tipoFinalitaId;
    }

    public int getTipoProcedimentoId() {
        return tipoProcedimentoId;
    }

    public void setTipoProcedimentoId(int tipoProcedimentoId) {
        this.tipoProcedimentoId = tipoProcedimentoId;
    }

    public void setDataAvvio(String dataAvvio) {
        this.dataAvvio = dataAvvio;
    }

    public void setDataEvidenza(String dataEvidenza) {
        this.dataEvidenza = dataEvidenza;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public String getDataAvvio() {
        return dataAvvio;
    }

    public String getDataEvidenza() {
        return dataEvidenza;
    }

    public String getPosizione() {
        return posizione;
    }

    public String getNomeReferente() {
        return nomeReferente;
    }

    public void setNomeReferente(String nomeResponsabile) {
        this.nomeReferente = nomeResponsabile;
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

    public Map getTipiFinalita() {
        return tipiFinalita;
    }

    public Collection getTipiFinalitaCollection() {
        return tipiFinalita.values();
    }

    public void setTipiFinalita(Map tipiFinalita) {
        this.tipiFinalita = tipiFinalita;
    }

    public void setTipiProcedimento(ArrayList tipiProcedimento) {
        this.tipiProcedimento = tipiProcedimento;
    }

    public ArrayList getTipiProcedimento() {
        return tipiProcedimento;
    }

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
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

    /**
     * 
     * @return map of FaldoneVO
     */
    public Map getFaldoni() {
        return faldoni;
    }

    /**
     * 
     * @return collection of FaldoneVO
     */
    public Collection getFaldoniCollection() {
        return faldoni.values();
    }

    public void setFaldoni(Map faldoni) {
        this.faldoni = faldoni;
    }

    /**
     * 
     * @return map of FasvicoloView
     */
    public Map getFascicoli() {
        return fascicoli;
    }

    /**
     * 
     * @return collection of FasvicoloView
     */
    public Collection getFascicoliCollection() {
        return fascicoli.values();
    }

    public void setFascicoli(Map fascicoli) {
        this.fascicoli = fascicoli;
    }

    /**
     * 
     * @return map of ProtocolloView
     */
    public Map getProtocolli() {
        return protocolli;
    }

    /**
     * 
     * @return collection of ProtocolloView
     */
    public Collection getProtocolliCollection() {
        return protocolli.values();
    }

    public void setProtocolli(Map protocolli) {
        this.protocolli = protocolli;
    }

    public String[] getFaldoniSelezionati() {
        return faldoniSelezionati;
    }

    public void setFaldoniSelezionati(String[] faldoniSelezionati) {
        this.faldoniSelezionati = faldoniSelezionati;
    }

    public String[] getFascicoliSelezionati() {
        return fascicoliSelezionati;
    }

    public void setFascicoliSelezionati(String[] fascicoliSelezionati) {
        this.fascicoliSelezionati = fascicoliSelezionati;
    }

    public String[] getProtocolliSelezionati() {
        return protocolliSelezionati;
    }

    public void setProtocolliSelezionati(String[] protocolliSelezionati) {
        this.protocolliSelezionati = protocolliSelezionati;
    }

    public void rimuoviFascicolo(String id) {
        this.fascicoli.remove(id);
    }

    public void aggiungiFascicolo(FascicoloView f) {
        this.fascicoli.put(String.valueOf(f.getId()), f);
    }

    public void rimuoviFaldone(String id) {
        this.faldoni.remove(id);
    }

    public void aggiungiFaldone(FaldoneVO f) {
        this.faldoni.put(String.valueOf(f.getId()), f);
    }

    public void rimuoviProtocollo(String id) {
        this.protocolli.remove(id);
    }

    public void aggiungiProtocollo(ReportProtocolloView p) {
        this.protocolli.put(String.valueOf(p.getProtocolloId()), p);
    }

    public void aggiungiReferente(UtenteVO u) {
        this.referenti.put(String.valueOf(u.getId().intValue()), u);

    }

    public String getCercaFascicoloNome() {
        return cercaFascicoloNome;
    }

    public void setCercaFascicoloNome(String carcaFascicoloNome) {
        this.cercaFascicoloNome = carcaFascicoloNome;
    }

    public String getCercaFaldoneOggetto() {
        return cercaFaldoneOggetto;
    }

    public void setCercaFaldoneOggetto(String cercaFaldoneOggetto) {
        this.cercaFaldoneOggetto = cercaFaldoneOggetto;
    }

    public String getCercaProtocolloOggetto() {
        return cercaProtocolloOggetto;
    }

    public void setCercaProtocolloOggetto(String cercaProtocolloOggetto) {
        this.cercaProtocolloOggetto = cercaProtocolloOggetto;
    }

    public Map getReferenti() {
        return referenti;
    }

    public Collection getReferentiCollection() {
        return referenti.values();
    }

    public void setReferenti(Map referenti) {
        this.referenti = referenti;
    }

    public void inizializzaForm() {
        setAnno(0);
        setDataAvvio(null);
        setDataEvidenza(null);
        setNote(null);
        setNumero(0);
        setNumeroProcedimento(null);
        setOggettoProcedimento(null);
        setPosizione("T");
        setProcedimentoId(0);
        setProtocolloId(0);
        setResponsabile(null);
        setReferenteId(0);
        setStatoId(0);
        setTipoFinalitaId(0);
        setTipoProcedimentoId(0);
        setTitolariFigli(null);
        setTitolario(null);
        setTitolarioId(0);
        setTitolarioPrecedenteId(0);
        setTitolarioSelezionatoId(0);
        setUfficiDipendenti(null);
        setUfficioCorrente(null);
        setUfficioCorrenteId(0);
        setUfficioCorrentePath(null);
        setUfficioId(0);
        setUfficioSelezionatoId(0);
        setUtenteSelezionatoId(0);
        setUtenti(null);
        setFaldoni(new HashMap(2));
        setFaldoniSelezionati(null);
        setFascicoli(new HashMap(2));
        setFaldoniSelezionati(null);
        setProtocolli(new HashMap(2));
        setProtocolliSelezionati(null);
        setVersione(0);
        // setPosizione(null);
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        Utente utente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
        dipTitolarioUfficio = utente.getAreaOrganizzativa()
                .getDipendenzaTitolarioUfficio() == 1;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("salvaAction") != null) {
            if (getTipoProcedimentoId() == 0) {
                errors.add("tipo Procedimento", new ActionMessage(
                        "campo.obbligatorio", "Tipo finalità del procedimento",
                        ""));
            }
            if (getDataAvvio() == null || "".equals(getDataAvvio())) {
                errors.add("dataAvvio", new ActionMessage("campo.obbligatorio",
                        "Data Avvio", ""));
            } else if (!DateUtil.isData(getDataAvvio())) {
                errors.add("dataAvvio", new ActionMessage(
                        "formato.data.errato", "Data Avvio"));
            }
            Utente utente = (Utente) request.getSession().getAttribute(
                    Constants.UTENTE_KEY);
            if (getTitolario() == null) {
                if (utente.getAreaOrganizzativa()
                        .getDipendenzaTitolarioUfficio() == 1) {
                    errors.add("titolario", new ActionMessage(
                            "campo.obbligatorio", "Titolario", ""));
                }
            } else {
                if (getTitolario().getParentId() == 0
                        && utente.getAreaOrganizzativa()
                                .getTitolarioLivelloMinimo() > 1) {
                    errors.add("titolario", new ActionMessage(
                            "fascicolo.titolario.livello", "", ""));

                }
            }
            if (!(getUfficioCorrenteId() > 0)) {
                errors.add("ufficio", new ActionMessage("campo.obbligatorio",
                        "Uffcio", ""));
            }
            if (getPosizione().equals("E")) {
                if (getDataEvidenza() == null || "".equals(getDataEvidenza())) {
                    errors.add("dataEvidenza", new ActionMessage(
                            "campo.obbligatorio", "Data Evidenza", ""));
                } else if (!DateUtil.isData(getDataEvidenza())) {
                    errors.add("dataEvidenza", new ActionMessage(
                            "formato.data.errato", "Data Evidenza"));
                }
            }
        }
        return errors;
    }

    public ArrayList getTipiProcedimentoByUfficio() {
        return tipiProcedimentoByUfficio;
    }

    public void setTipiProcedimentoByUfficio(ArrayList tipiProcedimentoByUfficio) {
        this.tipiProcedimentoByUfficio = tipiProcedimentoByUfficio;
    }

    public boolean isDipTitolarioUfficio() {
        return dipTitolarioUfficio;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public int getProgressivo() {
        return progressivo;
    }

    public void setProgressivo(int progressivo) {
        this.progressivo = progressivo;
    }

    public boolean isVersioneDefault() {
        return versioneDefault;
    }

    public void setVersioneDefault(boolean versioneDefault) {
        this.versioneDefault = versioneDefault;
    }

    public String getTipoProcedimento() {
        return tipoProcedimento;
    }

    public void setTipoProcedimento(String tipoProcedimento) {
        this.tipoProcedimento = tipoProcedimento;
    }

    public boolean isIndietroVisibile() {
        return indietroVisibile;
    }

    public void setIndietroVisibile(boolean indietroVisibile) {
        this.indietroVisibile = indietroVisibile;
    }

    public void setDipTitolarioUfficio(boolean dipTitolarioUfficio) {
        this.dipTitolarioUfficio = dipTitolarioUfficio;
    }

}