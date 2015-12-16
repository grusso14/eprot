package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.FascicoloDelegate;
import it.finsiel.siged.mvc.presentation.action.protocollo.FascicoloAction;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class RicercaFascicoliForm extends ActionForm implements
        AlberoUfficiUtentiForm {
    static Logger logger = Logger.getLogger(FascicoloAction.class.getName());

    private String anno;

    private String progressivo;

    private String oggettoFascicolo;

    private String noteFascicolo;

    private String dataAperturaDa;

    private String dataAperturaA;

    private String dataEvidenzaDa;

    private String dataEvidenzaA;

    private int stato;

    private TitolarioVO titolario;

    private int titolarioPrecedenteId;

    private int titolarioSelezionatoId;

    private Collection titolariFigli;

    private Collection statiFascicolo;

    private int ufficioCorrenteId;

    private int ufficioSelezionatoId;

    private UfficioVO ufficioCorrente;

    private Collection ufficiDipendenti;

    private Collection fascicoli;

    private int fascicoloSelezionato;

    private String[] fascicoliSelezionati;

    private int ufficioRicercaId;

    private Utente utenteCorrente;

    private int utenteSelezionatoId;

    private Collection utenti;

    private String ufficioCorrentePath;

    private int aooId;

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public String getUfficioCorrentePath() {
        return ufficioCorrentePath;
    }

    public void setUfficioCorrentePath(String ufficioCorrentePath) {
        this.ufficioCorrentePath = ufficioCorrentePath;
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public void setUtenteCorrente(Utente utenteCorrente) {
        this.utenteCorrente = utenteCorrente;
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

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.utenteCorrente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
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

    public int getUfficioRicercaId() {
        return ufficioRicercaId;
    }

    public void setUfficioRicercaId(int ufficioRicercaId) {
        this.ufficioRicercaId = ufficioRicercaId;
    }

    public RicercaFascicoliForm() {
    }

    public String[] getFascicoliSelezionati() {
        return fascicoliSelezionati;
    }

    public void setFascicoliSelezionati(String[] fascicoliSelezionati) {
        this.fascicoliSelezionati = fascicoliSelezionati;
    }

    public String getProgressivo() {
        return progressivo;
    }

    public void setProgressivo(String progressivo) {
        this.progressivo = progressivo;
    }

    public String getOggettoFascicolo() {
        return oggettoFascicolo;
    }

    public void setOggettoFascicolo(String oggettoFascicolo) {
        this.oggettoFascicolo = oggettoFascicolo;
    }

    public int getStato() {
        return stato;
    }

    public void setStato(int stato) {
        this.stato = stato;
    }

    public Collection getStatiFascicolo() {
        return FascicoloDelegate.getInstance().getStatiFascicolo(null);
    }

    public Collection getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Collection fascicoli) {
        this.fascicoli = fascicoli;
    }

    public String getDataAperturaA() {
        return dataAperturaA;
    }

    public void setDataAperturaA(String dataAperturaA) {
        this.dataAperturaA = dataAperturaA;
    }

    public String getDataAperturaDa() {
        return dataAperturaDa;
    }

    public void setDataAperturaDa(String dataAperturaDa) {
        this.dataAperturaDa = dataAperturaDa;
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

    public int getFascicoloSelezionato() {
        return fascicoloSelezionato;
    }

    public void setFascicoloSelezionato(int fascicoloSelezionato) {
        this.fascicoloSelezionato = fascicoloSelezionato;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public void inizializzaForm() {
        setDataAperturaA(null);
        setDataAperturaDa(null);
        setDataEvidenzaDa(null);
        setDataEvidenzaA(null);
        setFascicoli(null);
        setProgressivo(null);
        setOggettoFascicolo(null);
        setNoteFascicolo(null);
        setStato(-1);
        setAnno(null);
        setTitolariFigli(null);
        setTitolario(null);
        setTitolarioPrecedenteId(0);
        setTitolarioSelezionatoId(0);
        setFascicoloSelezionato(0);
        setUfficioCorrenteId(0);

    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (request.getParameter("btnCercaFascicoli") != null) {
            if (getProgressivo() != null && !"".equals(getProgressivo())) {
                if (!NumberUtil.isInteger(getProgressivo())) {
                    errors
                            .add("progressivo", new ActionMessage(
                                    "formato.numerico.errato",
                                    "progressivo Fascicolo"));
                }
            }

            String dataAperturaDa = getDataAperturaDa();
            String dataAperturaA = getDataAperturaA();

            if (dataAperturaDa != null && !"".equals(dataAperturaDa)) {
                if (!DateUtil.isData(dataAperturaDa)) {
                    errors.add("dataAperturaDa", new ActionMessage(
                            "formato.data.errato", "Data apertura Da"));
                }
            }
            if (dataAperturaA != null && !"".equals(dataAperturaA)) {
                if (!DateUtil.isData(dataAperturaA)) {
                    errors.add("dataAperturaA", new ActionMessage(
                            "formato.data.errato", "Data apertura A"));
                }
            }
            if (dataAperturaDa != null && !"".equals(dataAperturaDa)
                    && dataAperturaA != null && !"".equals(dataAperturaA)) {
                if (DateUtil.toDate(dataAperturaDa).before(
                        DateUtil.toDate(dataAperturaDa))) {
                    errors.add("dataAperturaDa", new ActionMessage(
                            "data_1.non.successiva.data_2", "Data apertura A",
                            "Data apertura Da"));
                }
            }

            if (dataEvidenzaDa != null && !"".equals(dataEvidenzaDa)) {
                if (!DateUtil.isData(dataEvidenzaDa)) {
                    errors.add("dataEvidenzaDa", new ActionMessage(
                            "formato.data.errato", "Data Evidenza Da"));
                }
            }
            if (dataEvidenzaA != null && !"".equals(dataEvidenzaA)) {
                if (!DateUtil.isData(dataEvidenzaA)) {
                    errors.add("dataEvidenzaA", new ActionMessage(
                            "formato.data.errato", "Data Evidenza A"));
                }
            }
            if (dataEvidenzaDa != null && !"".equals(dataEvidenzaDa)
                    && dataEvidenzaA != null && !"".equals(dataEvidenzaA)) {
                if (DateUtil.toDate(dataEvidenzaDa).before(
                        DateUtil.toDate(dataEvidenzaDa))) {
                    errors.add("dataEvidenzaDa", new ActionMessage(
                            "data_1.non.successiva.data_2", "Data Evidenza A",
                            "Data Evidenza Da"));
                }
            }

        } else if (request.getParameter("btnSeleziona") != null) {
            // selezionare il fascicolo
            if (getFascicoloSelezionato() == 0
                    && (getFascicoliSelezionati() == null || getFascicoliSelezionati().length == 0)) {
                errors.add("fascicolo", new ActionMessage(
                        "selezione.obbligatoria", "un Fascicolo", ""));
            }
        }
        return errors;
    }

    public String getDescrizioneStato() {
        if (stato == 0)
            return "Aperto";
        else if (stato == 1)
            return "Chiuso";
        if (stato == 2)
            return "Scartato";
        return null;
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

    public int getUfficioSelezionatoId() {
        return ufficioSelezionatoId;
    }

    public void setUfficioSelezionatoId(int ufficioSelezionatoId) {
        this.ufficioSelezionatoId = ufficioSelezionatoId;
    }

    public String getNoteFascicolo() {
        return noteFascicolo;
    }

    public void setNoteFascicolo(String noteFascicolo) {
        this.noteFascicolo = noteFascicolo;
    }

    public String getDataEvidenzaA() {
        return dataEvidenzaA;
    }

    public void setDataEvidenzaA(String dataEvidenzaA) {
        this.dataEvidenzaA = dataEvidenzaA;
    }

    public String getDataEvidenzaDa() {
        return dataEvidenzaDa;
    }

    public void setDataEvidenzaDa(String dataEvidenzaDa) {
        this.dataEvidenzaDa = dataEvidenzaDa;
    }

}