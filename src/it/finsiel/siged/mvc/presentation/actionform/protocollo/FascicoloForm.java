package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.FascicoloDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.presentation.action.protocollo.FascicoloAction;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.DestinatarioView;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.EmailUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class FascicoloForm extends ActionForm implements AlberoUfficiUtentiForm {
    static Logger logger = Logger.getLogger(FascicoloAction.class.getName());

    public final static String CANCELLAZIONE_FASCICOLO = "Cancellazione";

    public final static String CHIUSURA_FASCICOLO = "agli Atti (Chiuso)";

    public final static String RIAPERTURA_FASCICOLO = "Riapertura";

    public final static String SCARTO_FASCICOLO = "Scarto";

    public final static String INVIO_FASCICOLO = "Invio";

    private int id;

    private int versione;

    private int aooId;

    private int dipendenzaTitolarioUfficio;

    private String codice;

    private String descrizione;

    private String nome;

    private long progressivo;

    private int ufficioResponsabileId;

    private int utenteResponsabileId;

    private String note;

    private int processoId;

    private String oggettoFascicolo;

    private int registroId;

    private String dataApertura;

    private String dataChiusura;

    private int statoFascicolo;

    private TitolarioVO titolario;

    private int titolarioPrecedenteId;

    private int titolarioSelezionatoId;

    private Collection titolariFigli;

    private String capDestinatario;

    // gestione uffici/utenti
    private int ufficioCorrenteId;

    private String ufficioCorrentePath;

    private int ufficioSelezionatoId;

    private int utenteSelezionatoId;

    private UfficioVO ufficioCorrente;

    private Collection ufficiDipendenti;

    private Collection utenti;

    private Collection fascicoli;

    private Collection protocolliFascicolo;

    private Collection fascicoliDocumento;

    private Collection documentiFascicolo;

    private Collection procedimentiFascicolo;

    private Collection faldoniFascicolo;

    private int fascicoloSelezionato;

    private String operazione;

    private int documentoSelezionato;

    private int faldoneSelezionato;

    private int procedimentoSelezionato;

    private String sezioneVisualizzata = "Protocolli";

    private int documentoPrincipaleSelezionato;

    private String[] documentiAllegatiSelezionati;

    // modifiche MinInt 13/02/2006
    private String dataEvidenza;

    private int annoRiferimento;

    private int tipoFascicolo;

    private int tipoDocumento;

    private String tipoProtocollo;

    private Map posizioniFascicolo = new HashMap(3);

    private int posizioneSelezionataId;

    private String posizioneSelezionata;

    private int condizioneFascicolo;

    private String dataUltimoMovimento;

    private String dataCarico;

    private String dataScarto;

    private String dataScarico;

    private String propostaScarto;

    private Collection tipiDocumento;

    private Collection destinazioniScarto;

    private Collection protocolli;

    private Collection procedimenti = new ArrayList();

    private String[] protocolliSelezionati;

    private String[] procedimentiSelezionati;

    private boolean versioneDefault = true;

    private String[] collocazioneLabel = new String[4];

    private String[] collocazioneValore = new String[4];

    private String faldoneCodiceLocale;

    private String faldoneOggetto;

    private boolean isUtenteAbilitatoSuUfficio;

    private boolean indietroVisibile;

    private Map procedimentiFascicoli = new HashMap(2);

    private int mezzoSpedizioneId;

    private int idx;

    private int titoloId;

    private String titoloDestinatario;

    public String getCollocazioneLabel1() {
        return collocazioneLabel[0];
    }

    public void setCollocazioneLabel1(String collocazioneLabel1) {
        this.collocazioneLabel[0] = collocazioneLabel1;
    }

    public String getCollocazioneLabel2() {
        return collocazioneLabel[1];
    }

    public void setCollocazioneLabel2(String collocazioneLabel2) {
        this.collocazioneLabel[1] = collocazioneLabel2;
    }

    public String getCollocazioneLabel3() {
        return collocazioneLabel[2];
    }

    public void setCollocazioneLabel3(String collocazioneLabel3) {
        this.collocazioneLabel[2] = collocazioneLabel3;
    }

    public String getCollocazioneLabel4() {
        return collocazioneLabel[3];
    }

    public void setCollocazioneLabel4(String collocazioneLabel4) {
        this.collocazioneLabel[3] = collocazioneLabel4;
    }

    public String getCollocazioneValore1() {
        return collocazioneValore[0];
    }

    public void setCollocazioneValore1(String collocazioneValore1) {
        this.collocazioneValore[0] = collocazioneValore1;
    }

    public String getCollocazioneValore2() {
        return collocazioneValore[1];
    }

    public void setCollocazioneValore2(String collocazioneValore2) {
        this.collocazioneValore[1] = collocazioneValore2;
    }

    public String getCollocazioneValore3() {
        return collocazioneValore[2];
    }

    public void setCollocazioneValore3(String collocazioneValore3) {
        this.collocazioneValore[2] = collocazioneValore3;
    }

    public String getCollocazioneValore4() {
        return collocazioneValore[3];
    }

    public void setCollocazioneValore4(String collocazioneValore4) {
        this.collocazioneValore[3] = collocazioneValore4;
    }

    public String getDestinatarioMezzoId() {
        return destinatarioMezzoId;
    }

    public void setDestinatarioMezzoId(String destinatarioMezzoId) {
        this.destinatarioMezzoId = destinatarioMezzoId;
    }

    public FascicoloForm() {
    }

    public void setTipiDocumento(Collection tipiDocumento) {
        this.tipiDocumento = tipiDocumento;
    }

    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setPosizioniFascicolo(Map posizioniFascicolo) {
        this.posizioniFascicolo = posizioniFascicolo;
    }

    public int getCondizioneFascicolo() {
        return condizioneFascicolo;
    }

    public void setCondizioneFascicolo(int condizioneFascicolo) {
        this.condizioneFascicolo = condizioneFascicolo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponsabile() {
        Organizzazione org = Organizzazione.getInstance();
        String responsabile = "";
        if (getUfficioResponsabileId() != 0) {
            Ufficio uff = org.getUfficio(getUfficioResponsabileId());
            responsabile = uff.getPath();
            if (getUtenteResponsabileId() != 0) {
                Utente ute = org.getUtente(getUtenteResponsabileId());
                responsabile = uff.getPath()
                        + ute.getValueObject().getFullName();
            }
        }
        return responsabile;
    }

    public String getIntestatario() {
        Organizzazione org = Organizzazione.getInstance();
        String intestatario = "";
        if (getUfficioResponsabileId() != 0) {
            Ufficio uff = org.getUfficio(getUfficioCorrenteId());
            intestatario = uff.getPath();
            if (getUtenteSelezionatoId() != 0) {
                Utente ute = org.getUtente(getUtenteSelezionatoId());
                intestatario = uff.getPath()
                        + ute.getValueObject().getFullName();
            }
        }
        return intestatario;
    }

    public Collection getUfficiDipendenti() {
        return ufficiDipendenti;
    }

    public void setUfficiDipendenti(Collection ufficiDipendenti) {
        this.ufficiDipendenti = ufficiDipendenti;
    }

    public Collection getUtenti() {
        return utenti;
    }

    public Collection getDocumentiFascicolo() {
        return documentiFascicolo;
    }

    public void setDocumentiFascicolo(Collection documentiFascicolo) {
        this.documentiFascicolo = documentiFascicolo;
    }

    public UtenteVO getUtente(int utenteId) {
        for (Iterator i = getUtenti().iterator(); i.hasNext();) {
            UtenteVO ute = (UtenteVO) i.next();
            if (ute.getId().intValue() == utenteId) {
                return ute;
            }
        }
        return null;
    }

    public void setUtenti(Collection utenti) {
        this.utenti = utenti;
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

    public void setUfficioSelezionatoId(int ufficioCorrenteId) {
        this.ufficioSelezionatoId = ufficioCorrenteId;
    }

    public int getUtenteSelezionatoId() {
        return utenteSelezionatoId;
    }

    public void setUtenteSelezionatoId(int utenteCorrenteId) {
        this.utenteSelezionatoId = utenteCorrenteId;
    }

    public UfficioVO getUfficioCorrente() {
        return ufficioCorrente;
    }

    public void setUfficioCorrente(UfficioVO ufficioCorrente) {
        this.ufficioCorrente = ufficioCorrente;
    }

    public String getUfficioCorrentePath() {
        return ufficioCorrentePath;
    }

    public void setUfficioCorrentePath(String ufficioCorrentePath) {
        this.ufficioCorrentePath = ufficioCorrentePath;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDataApertura() {
        return dataApertura;
    }

    public void setDataApertura(String dataApertura) {
        this.dataApertura = dataApertura;
    }

    public String getDataChiusura() {
        return dataChiusura;
    }

    public void setDataChiusura(String dataChiusura) {
        this.dataChiusura = dataChiusura;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOggettoFascicolo() {
        return oggettoFascicolo;
    }

    public void setOggettoFascicolo(String oggettoFascicolo) {
        this.oggettoFascicolo = oggettoFascicolo;
    }

    public int getProcessoId() {
        return processoId;
    }

    public void setProcessoId(int processoId) {
        this.processoId = processoId;
    }

    public long getProgressivo() {
        return progressivo;
    }

    public void setProgressivo(long progressivo) {
        this.progressivo = progressivo;

    }

    public String getProgressivoFormattato() {
        return StringUtil.formattaNumeroProtocollo(String.valueOf(progressivo),
                6);
    }

    public int getRegistroId() {
        return registroId;
    }

    public void setRegistroId(int registroId) {
        this.registroId = registroId;
    }

    public int getStatoFascicolo() {
        return statoFascicolo;
    }

    public void setStatoFascicolo(int stato) {
        this.statoFascicolo = stato;
    }

    public Collection getStatiFascicolo() {
        return FascicoloDelegate.getInstance().getStatiFascicolo(
                getStringaStati());
    }

    private String getStringaStati() {
        String str = null;
        str = Parametri.STATO_FASCICOLO_APERTO
                + ","
                + Parametri.STATO_FASCICOLO_IN_EVIDENZA
                + ","
                + Parametri.STATO_FASCICOLO_CHIUSO
                + (getId() > 0
                        && getStatoFascicolo() == Parametri.STATO_FASCICOLO_CHIUSO ? ","
                        + Parametri.STATO_FASCICOLO_SCARTATO
                        : "");
        return "(" + str + ")";
    }

    public Collection getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Collection fascicoli) {
        this.fascicoli = fascicoli;
    }

    public int getFascicoloSelezionato() {
        return fascicoloSelezionato;
    }

    public void setFascicoloSelezionato(int fascicoloSelezionato) {
        this.fascicoloSelezionato = fascicoloSelezionato;
    }

    public Collection getFascicoliDocumento() {
        return fascicoliDocumento;
    }

    public void setFascicoliDocumento(Collection fascicoliDocumento) {
        this.fascicoliDocumento = fascicoliDocumento;
    }

    public Collection getProtocolliFascicolo() {
        return protocolliFascicolo;
    }

    public void setProtocolliFascicolo(Collection protocolliFascicolo) {
        this.protocolliFascicolo = protocolliFascicolo;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
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

    public int getUfficioResponsabileId() {
        return ufficioResponsabileId;
    }

    public void setUfficioResponsabileId(int ufficioResponsabileId) {
        this.ufficioResponsabileId = ufficioResponsabileId;
    }

    public int getUtenteResponsabileId() {
        return utenteResponsabileId;
    }

    public void setUtenteResponsabileId(int utenteResponsabileId) {
        this.utenteResponsabileId = utenteResponsabileId;
    }

    public String getOperazione() {
        return operazione;
    }

    public void setOperazione(String operazione) {
        this.operazione = operazione;
    }

    public int getFaldoneSelezionato() {
        return faldoneSelezionato;
    }

    public void setFaldoneSelezionato(int faldoneSelezionato) {
        this.faldoneSelezionato = faldoneSelezionato;
    }

    public int getProcedimentoSelezionato() {
        return procedimentoSelezionato;
    }

    public void setProcedimentoSelezionato(int procedimentoSelezionato) {
        this.procedimentoSelezionato = procedimentoSelezionato;
    }

    public void inizializzaForm() {
        setAooId(0);
        setCodice(null);
        setDataApertura(null);
        setDataChiusura(null);
        setDescrizione(null);
        setFascicoli(null);
        setFascicoloSelezionato(0);
        setFaldoneSelezionato(0);
        setProcedimentoSelezionato(0);
        setId(0);
        setNome(null);
        setNote(null);
        setOggettoFascicolo(null);

        setStatoFascicolo(-1);
        setTitolariFigli(null);

        setTitolario(null);
        setTitolarioPrecedenteId(0);
        setTitolarioSelezionatoId(0);
        setDocumentoSelezionato(0);
        destinatari = new HashMap();
        // modifiche MinInt 13/02/2006
        setDataEvidenza(null);
        setDataUltimoMovimento(null);
        setDataCarico(DateUtil.formattaData((new Date(System
                .currentTimeMillis())).getTime()));
        setDataScarto(null);
        setDataScarico(null);
        setPropostaScarto(null);
        setTipoFascicolo(0);
        setCollocazioneValore1(null);
        setCollocazioneValore2(null);
        setCollocazioneValore3(null);
        setCollocazioneValore4(null);

        // setPosizioneSelezionata(null);
        // setPosizioneSelezionataId(0)
        // setCondizioneFascicolo(0);
        // fine modifiche MinInt 13/02/2006

    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        setFlagConoscenza(false);
    }

    public ActionErrors validateInvioProtocollo(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("btnInvioProtocollo") != null) {
            if (destinatari.isEmpty()) {
                errors
                        .add("destinatari", new ActionMessage(
                                "selezione.obbligatoria",
                                "almeno un destinatario", ""));
            }

            if (getDocumentoPrincipaleSelezionato() == 0) {
                errors.add("documento",
                        new ActionMessage("selezione.obbligatoria",
                                "il documento principale", ""));
            }

        } else if (request.getParameter("aggiungiDestinatario") != null) {
            if (EmailConstants.DESTINATARIO_TIPO_EMAIL.equals(mezzoSpedizione)) {
                if (!EmailUtil.isValidEmail(emailDestinatario)) {
                    errors.add("emailDestinatario", new ActionMessage(
                            "destinatario_email"));
                }
            } else if (nominativoDestinatario == null
                    || "".equals(nominativoDestinatario.trim())) {
                errors.add("destinatari", new ActionMessage(
                        "selezione.obbligatoria", "destinatario", ""));
            } else if (!"".equals(dataSpedizione)
                    && !DateUtil.isData(dataSpedizione)) {
                errors.add("dataSpedizione", new ActionMessage(
                        "formato.data.errato", "data spedizione", ""));
                /*
                 * } else if (!"".equals(dataSpedizione) &&
                 * (getMezzoSpedizione() == null || ""
                 * .equals(getMezzoSpedizione().trim()))) {
                 * errors.add("dataSpedizione", new ActionMessage(
                 * "selezione.obbligatoria", "il mezzo spedizione", "in presenza
                 * della data spedizione")); } else if
                 * (!"".equals(getMezzoSpedizione()) && (getDataSpedizione() ==
                 * null || "" .equals(getDataSpedizione().trim()))) {
                 * errors.add("dataSpedizione", new ActionMessage(
                 * "selezione.obbligatoria", "la data spedizione", "in presenza
                 * del mezzo spedizione"));
                 */

            } else if (!"".equals(dataSpedizione)
                    && (getMezzoSpedizioneId() == 0)) {
                errors.add("dataSpedizione", new ActionMessage(
                        "selezione.obbligatoria", "il mezzo spedizione",
                        "in presenza della data spedizione"));
            } else if (getMezzoSpedizioneId() != 0
                    && (getDataSpedizione() == null || ""
                            .equals(getDataSpedizione().trim()))) {
                errors.add("dataSpedizione", new ActionMessage(
                        "selezione.obbligatoria", "la data spedizione",
                        "in presenza del mezzo spedizione"));
            }

        }
        return errors;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (request.getParameter("btnConferma") != null) {
            String dataApertura = getDataApertura();
            String dataChiusura = getDataChiusura();
            Date dataCorrente = new Date(System.currentTimeMillis());
            String currData = DateUtil.formattaData(dataCorrente.getTime());
            // cotrollo che in inserimento venga impostato lo stato a:
            // trattazione, agli atti, in evidenza
            if (getId() == 0
                    && (getStatoFascicolo() == Parametri.STATO_FASCICOLO_INVIATO || getStatoFascicolo() == Parametri.STATO_FASCICOLO_SCARTATO)) {
                errors
                        .add(
                                "stato",
                                new ActionMessage(
                                        "selezione.obbligatoria",
                                        "una posizione tra: in Trattazione, agli Atti, in Evidenza,",
                                        " in fase di inserimento fascicolo"));

            }
            // if (getUtenteSelezionatoId() == 0) {
            // errors.add("referente", new ActionMessage("campo.obbligatorio",
            // "Referente", ""));
            // }
            if ("".equals(getOggettoFascicolo())
                    || getOggettoFascicolo() == null) {
                errors.add("oggettoFoscicolo", new ActionMessage(
                        "campo.obbligatorio", "Oggetto", ""));
            }
            if ("".equals(dataApertura)) {
                errors.add("dataApertura", new ActionMessage(
                        "campo.obbligatorio", "Data apertura", ""));
            } else if (!DateUtil.isData(dataApertura)) {
                // la data di ricezione deve essere nel formato valido:
                // gg/mm/aaaa
                errors.add("dataApertura", new ActionMessage(
                        "formato.data.errato", "Data apertura"));
            }
            if (dataChiusura != null && !"".equals(dataChiusura)) {
                if (!DateUtil.isData(dataChiusura)) {
                    // la data di ricezione deve essere nel formato valido:
                    // gg/mm/aaaa
                    errors.add("dataChiusura", new ActionMessage(
                            "formato.data.errato", "Data chiusura"));
                } else {
                    // la data di ricezione non deve essere successiva a quella
                    // di registrazione
                    if (DateUtil.toDate(dataChiusura).before(
                            DateUtil.toDate(dataApertura))) {
                        errors.add("dataAperturaDa", new ActionMessage(
                                "data_1.non.successiva.data_2", "Apertura",
                                "Chiusura"));
                    }
                }
            }
            if (dataCarico == null || "".equals(dataCarico)) {
                errors.add("dataApertura", new ActionMessage(
                        "campo.obbligatorio", "Data carico", ""));
            } else if (dataCarico != null && !"".equals(dataCarico)) {
                if (!DateUtil.isData(dataCarico)) {
                    errors.add("dataCarico", new ActionMessage(
                            "formato.data.errato", "Data carico"));
                } else if (DateUtil.toDate(dataCarico).after(dataCorrente)) {
                    errors.add("dataCarico", new ActionMessage(
                            "data_1.non.successiva.data_2", "Carico",
                            "Corrente"));
                }
            }
            if (dataScarico != null && !"".equals(dataScarico)) {
                if (!DateUtil.isData(dataScarico)) {
                    errors.add("dataScarico", new ActionMessage(
                            "formato.data.errato", "Data scarico"));
                } else if (dataCarico != null
                        && !"".equals(dataCarico)
                        && DateUtil.toDate(dataCarico).after(
                                DateUtil.toDate(dataScarico))) {
                    errors.add("dataScarico",
                            new ActionMessage("data_1.non.successiva.data_2",
                                    "Carico", "Scarico"));
                }
            }

            // l'anno di riferimento e' obbligatorio
            if (getAnnoRiferimento() == 0) {
                errors.add("nome", new ActionMessage("campo.obbligatorio",
                        "Anno riferimento", ""));
            } else if (NumberUtil.isInteger(String
                    .valueOf(getAnnoRiferimento()))
                    && getAnnoRiferimento() < Parametri.ANNO_INIZIO_CONTROLLO) {
                errors.add("annoRiferimento", new ActionMessage(
                        "formato.anno.errato", "Anno riferimento", " > "
                                + Parametri.ANNO_INIZIO_CONTROLLO));
            }
            if ((dataEvidenza == null || "".equals(dataEvidenza))
                    && getStatoFascicolo() == Parametri.STATO_FASCICOLO_IN_EVIDENZA) {
                errors.add("dataEvidenza", new ActionMessage(
                        "campo.obbligatorio", "Data evidenza", ""));
            } else if (!"".equals(dataEvidenza)
                    && !DateUtil.isData(dataEvidenza)) {
                // la data di Evidenza deve essere nel formato valido:
                // gg/mm/aaaa
                errors.add("dataEvidenza", new ActionMessage(
                        "formato.data.errato", "Data evidenza"));
            } else if (!"".equals(dataEvidenza)
                    && getStatoFascicolo() != Parametri.STATO_FASCICOLO_IN_EVIDENZA) {
                errors.add("dataEvidenza", new ActionMessage(
                        "fascicolo.stato.evidenza", "", ""));
                // setStato(Parametri.STATO_FASCICOLO_IN_EVIDENZA);
            } else if (dataEvidenza != null
                    && !"".equals(dataEvidenza)
                    && DateUtil.toDate(dataEvidenza).before(
                            DateUtil.toDate(currData))) {
                errors.add("dataEvidenza", new ActionMessage(
                        "data_1.non.successiva.data_2", "Odierna", "Evidenza"));
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

            // l'intestatario e' obbligatorio
            if (getMittente() == null) {
                errors.add("titolario", new ActionMessage("campo.obbligatorio",
                        "Intestatario", ""));
            }
        } else if (request.getParameter("btnRimuoviProtocolli") != null
                && getProtocolliSelezionati() == null) {
            // selezionare almeno un protocollo da rimuovere dal Fascicolo
            errors.add("protocollo", new ActionMessage(
                    "selezione.obbligatoria", "almeno un Protocollo",
                    "da rimuovere"));

        } else if (request.getParameter("btnRimuoviProcedimenti") != null
                && getProcedimentiSelezionati() == null) {
            // selezionare almeno un protocollo da rimuovere dal Fascicolo
            errors.add("procedimento", new ActionMessage(
                    "selezione.obbligatoria", "almeno un Procedimento",
                    "da rimuovere"));

        } else if (request.getParameter("btnRimuoviDocumento") != null) {
            // selezionare il documento da rimuovere dal Fascicolo
            if (getDocumentoSelezionato() == 0) {
                errors.add("doumento", new ActionMessage(
                        "selezione.obbligatoria", "il Documento",
                        "da rimuovere"));
            }
        } else if (request.getParameter("btnSelezionaDocumento") != null) {
            // selezionare il documento da inviare al protocollo
            if (getDocumentoSelezionato() == 0) {
                errors.add("doumento", new ActionMessage(
                        "selezione.obbligatoria", "il Documento",
                        "da inviare al protocollo"));
            }
        } else if (request.getParameter("btnSeleziona") != null) {
            // selezionare il fascicolo
            if (getFascicoloSelezionato() == 0) {
                errors.add("nome", new ActionMessage("selezione.obbligatoria",
                        "il Fascicolo", ""));
            }
        } else if (request.getParameter("btnInvio") != null) {
            if (getDocumentiFascicolo() == null
                    || getDocumentiFascicolo().size() == 0) {
                errors.add("fascicolo_no_invio", new ActionMessage(
                        "fascicolo_no_invio", "", ""));
            }
        }

        return errors;
    }

    private boolean modificabile;

    /**
     * @param modificabile
     *            The modificabile to set.
     */
    public void setModificabile(boolean modificabile) {
        this.modificabile = modificabile;
    }

    /**
     * @return Returns the modificabile.
     */
    public boolean isModificabile() {
        return modificabile;
    }

    public String getSezioneVisualizzata() {
        return sezioneVisualizzata;
    }

    public void setSezioneVisualizzata(String sezioneVisualizzata) {
        this.sezioneVisualizzata = sezioneVisualizzata;
    }

    public String getDescrizioneStato() {
        return (String) (LookupDelegate.getInstance().getStatiFascicolo())
                .get(new Integer(getStatoFascicolo()));
    }

    private AssegnatarioView mittente;

    public AssegnatarioView getMittente() {
        return mittente;
    }

    public void setMittente(AssegnatarioView mittente) {
        this.mittente = mittente;
    }

    public int getDocumentoSelezionato() {
        return documentoSelezionato;
    }

    public void setDocumentoSelezionato(int documentoSelezionato) {
        this.documentoSelezionato = documentoSelezionato;
    }

    public String[] getDocumentiAllegatiSelezionati() {
        return documentiAllegatiSelezionati;
    }

    public void setDocumentiAllegatiSelezionati(
            String[] documentiAllegatiSelezionati) {
        this.documentiAllegatiSelezionati = documentiAllegatiSelezionati;
    }

    public int getDocumentoPrincipaleSelezionato() {
        return documentoPrincipaleSelezionato;
    }

    public void setDocumentoPrincipaleSelezionato(
            int documentoPrincipaleSelezionato) {
        this.documentoPrincipaleSelezionato = documentoPrincipaleSelezionato;
    }

    // DESTINATARIO

    private String tipoDestinatario = "F";

    public String getTipoDestinatario() {
        return tipoDestinatario;
    }

    public void setTipoDestinatario(String tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    private String nominativoDestinatario;

    public String getNominativoDestinatario() {
        return nominativoDestinatario;
    }

    public void setNominativoDestinatario(String nominativoDestinatario) {
        this.nominativoDestinatario = nominativoDestinatario;
    }

    private int destinatarioId;

    public int getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(int destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public Collection getDestinatari() {
        return destinatari.values();
    }

    public DestinatarioView getDestinatario(String id) {
        return (DestinatarioView) destinatari.get(id);
    }

    public void aggiungiDestinatario(DestinatarioView destinatario) {
        if (destinatario != null) {
            if (destinatario.getIdx() == 0) {
                int idx = getNextId(destinatariIds);
                destinatario.setIdx(idx);
                destinatari.put(String.valueOf(idx), destinatario);
                destinatariIds.put(String.valueOf(idx), new Integer(idx));
            } else {
                destinatari.put(String.valueOf(destinatario.getIdx()),
                        destinatario);
            }

        }
    }

    /*
     * public void aggiungiDestinatario(DestinatarioView destinatario) { if
     * (destinatario != null) { if (destinatario.getIdx() == 0) { int idx =
     * getNextId(destinatariIds); destinatario.setIdx(idx);
     * destinatari.put(destinatario.getDestinatarioMezzoId(), destinatario);
     * destinatariIds.put(String.valueOf(idx), new Integer(idx)); } else {
     * destinatari.put(destinatario.getDestinatarioMezzoId(), destinatario); } } }
     */

    public void aggiungiProcedimento(ProcedimentoVO p) {
        if (p != null) {
            this.procedimenti.add(p);

        }
    }

    private static int getNextId(Map m) {
        int max = 0;
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            int cur = NumberUtil.getInt(id);
            if (cur > max)
                max = cur;
        }
        return max + 1;
    }

    /*
     * public void rimuoviDestinatario(String destinatario) { //
     * this.destinatari.remove(destinatario); DestinatarioView removed =
     * (DestinatarioView) destinatari .get(destinatario); int idx =
     * removed.getIdx(); destinatari.remove(removed.getDestinatarioMezzoId());
     * destinatariIds.remove(String.valueOf(idx)); }
     */

    public void rimuoviDestinatario(String id) {
        DestinatarioView removed = (DestinatarioView) destinatari.get(id);
        int idx = removed.getIdx();
        destinatari.remove(String.valueOf(removed.getIdx()));
        destinatariIds.remove(String.valueOf(idx));
    }

    public void rimuoviDestinatari() {
        if (destinatari != null) {
            this.destinatari.clear();

            this.destinatariIds.clear();

        }
    }

    private String[] destinatarioSelezionatoId;

    public String[] getDestinatarioSelezionatoId() {
        return destinatarioSelezionatoId;
    }

    public void setDestinatarioSelezionatoId(String[] destinatarioSelezionatoId) {
        this.destinatarioSelezionatoId = destinatarioSelezionatoId;
    }

    // DESTINATARIO

    private String citta;

    private String mezzoSpedizione;

    private String emailDestinatario;

    private String indirizzoDestinatario;

    private String dataSpedizione;

    private boolean flagConoscenza;

    // private Collection mezziDiSpedizione;

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getDataSpedizione() {
        return dataSpedizione;
    }

    public void setDataSpedizione(String dataSpedizione) {
        this.dataSpedizione = dataSpedizione;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }

    public boolean getFlagConoscenza() {
        return flagConoscenza;
    }

    public void setFlagConoscenza(boolean flagConoscenza) {
        this.flagConoscenza = flagConoscenza;
    }

    public String getIndirizzoDestinatario() {
        return indirizzoDestinatario;
    }

    public void setIndirizzoDestinatario(String indirizzoDestinatario) {
        this.indirizzoDestinatario = indirizzoDestinatario;
    }

    public Collection getMezziSpedizione() {
        return getLookupDelegateDelegate().getMezziSpedizione(getAooId());
    }

    private LookupDelegate getLookupDelegateDelegate() {
        return LookupDelegate.getInstance();
    }

    public String getMezzoSpedizione() {
        return mezzoSpedizione;
    }

    public void setMezzoSpedizione(String mezzoSpedizione) {
        this.mezzoSpedizione = mezzoSpedizione;
    }

    public void setDestinatari(Map destinatari) {
        this.destinatari = destinatari;
    }

    public int getAnnoRiferimento() {
        return annoRiferimento;
    }

    public void setAnnoRiferimento(int annoRiferimento) {
        this.annoRiferimento = annoRiferimento;
    }

    public String getDataCarico() {
        return dataCarico;
    }

    public void setDataCarico(String dataCarico) {
        this.dataCarico = dataCarico;
    }

    public String getDataEvidenza() {
        return dataEvidenza;
    }

    public void setDataEvidenza(String dataEvidenza) {
        this.dataEvidenza = dataEvidenza;
    }

    public String getDataScarto() {
        return dataScarto;
    }

    public void setDataScarto(String dataScarto) {
        this.dataScarto = dataScarto;
    }

    public String getDataUltimoMovimento() {
        return dataUltimoMovimento;
    }

    public void setDataUltimoMovimento(String dataUltimoMovimento) {
        this.dataUltimoMovimento = dataUltimoMovimento;
    }

    public String getPropostaScarto() {
        return propostaScarto;
    }

    public void setPropostaScarto(String propostaScarto) {
        this.propostaScarto = propostaScarto;
    }

    public String getDataScarico() {
        return dataScarico;
    }

    public void setDataScarico(String dataScarico) {
        this.dataScarico = dataScarico;
    }

    public int getTipoFascicolo() {
        return tipoFascicolo;
    }

    public Collection getFaldoniFascicolo() {
        return faldoniFascicolo;
    }

    public void setFaldoniFascicolo(Collection faldoniDocumento) {
        this.faldoniFascicolo = faldoniDocumento;
    }

    public Collection getProcedimentiFascicolo() {
        return procedimentiFascicolo;
    }

    public void setProcedimentiFascicolo(Collection procedimentiFascicolo) {
        this.procedimentiFascicolo = procedimentiFascicolo;
    }

    public String getAnnoProgressivo() {
        if (getDataApertura() != null && getProgressivo() > 0) {
            return DateUtil.getYear(DateUtil.toDate(getDataApertura()))
                    + StringUtil.formattaNumeroProtocollo(String
                            .valueOf(progressivo), 6);
        }
        return null;
    }

    public int getPosizioneSelezionataId() {
        return posizioneSelezionataId;
    }

    public void setPosizioneSelezionataId(int posizioneSelezionataId) {
        this.posizioneSelezionataId = posizioneSelezionataId;
    }

    public String getPosizioneSelezionata() {
        return posizioneSelezionata;
    }

    public Collection getTipiFascicolo() {
        return LookupDelegate.getInstance().getTipiFascicolo().values();
    }

    public String getDescrizioneTipoFascicolo() {
        return ((IdentityVO) LookupDelegate.getInstance().getTipiFascicolo()
                .get(new Integer(getTipoFascicolo()))).getDescription();
    }

    public void setTipoFascicolo(int tipoFascicolo) {
        this.tipoFascicolo = tipoFascicolo;
    }

    public void setPosizioneSelezionata(String posizioneSelezionata) {
        this.posizioneSelezionata = posizioneSelezionata;
    }

    public Collection trovaTipiDoc(int aooId) {
        return getLookupDelegateDelegate().getTipiDocumento(getAooId());
    }

    public Collection getTipiDocumento() {
        return getLookupDelegateDelegate().getTipiDocumento(getAooId());
    }

    public Collection getProtocolli() {
        return protocolli;
    }

    public void setProtocolli(Collection protocolli) {
        this.protocolli = protocolli;
    }

    public String[] getProtocolliSelezionati() {
        return protocolliSelezionati;
    }

    public void setProtocolliSelezionati(String[] protocolliSelezionati) {
        this.protocolliSelezionati = protocolliSelezionati;
    }

    public boolean getVersioneDefault() {
        return versioneDefault;
    }

    public void setVersioneDefault(boolean versioneDefault) {
        this.versioneDefault = versioneDefault;
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

    public String getFaldoneCodiceLocale() {
        return faldoneCodiceLocale;
    }

    public void setFaldoneCodiceLocale(String faldoneCodiceLocale) {
        this.faldoneCodiceLocale = faldoneCodiceLocale;
    }

    public String getFaldoneOggetto() {
        return faldoneOggetto;
    }

    public void setFaldoneOggetto(String faldoneOggetto) {
        this.faldoneOggetto = faldoneOggetto;
    }

    public boolean getUtenteAbilitatoSuUfficio() {
        return isUtenteAbilitatoSuUfficio;
    }

    public void setUtenteAbilitatoSuUfficio(boolean isUtenteAbilitatoSuUfficio) {
        this.isUtenteAbilitatoSuUfficio = isUtenteAbilitatoSuUfficio;
    }

    public void inizializzaDestinatarioForm() {
        setDataSpedizione(null);
        setEmailDestinatario(null);
        setCitta(null);
        setIndirizzoDestinatario(null);
        setFlagConoscenza(false);
        setMezzoSpedizione(null);
        setNominativoDestinatario(null);
        setDestinatarioMezzoId(null);
        setTipoDestinatario("F");
        setUtenteSelezionatoId(0);
        setCapDestinatario(null);
        setMezzoSpedizioneId(0);
        setTitoloDestinatario(null);
        setIdx(0);
    }

    // DESTINATARIO
    private String destinatarioMezzoId;

    private Map destinatari = new HashMap(2);

    private Map destinatariIds = new HashMap(2);

    public Collection getDestinazioniScarto() {
        return LookupDelegate.getInstance().getDestinazioniScarto();
    }

    public boolean isIndietroVisibile() {
        return indietroVisibile;
    }

    public void setIndietroVisibile(boolean indietroVisibile) {
        this.indietroVisibile = indietroVisibile;
    }

    public String getCapDestinatario() {
        return capDestinatario;
    }

    public void setCapDestinatario(String capDestinatario) {
        this.capDestinatario = capDestinatario;
    }

    public int getMezzoSpedizioneId() {
        return mezzoSpedizioneId;
    }

    public void setMezzoSpedizioneId(int mezzoId) {
        this.mezzoSpedizioneId = mezzoId;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getTitoloId() {
        return titoloId;
    }

    public void setTitoloId(int titoloId) {
        this.titoloId = titoloId;
    }

    public String getTitoloDestinatario() {
        return titoloDestinatario;
    }

    /**
     * @param titoloDestinatario
     *            The titoloDestinatario to set.
     */
    public void setTitoloDestinatario(String titoloDestinatario) {
        this.titoloDestinatario = titoloDestinatario;
    }

    public Collection getTitoliDestinatario() {
        return getLookupDelegateDelegate().getTitoliDestinatario();
    }

    public int getDipendenzaTitolarioUfficio() {
        return dipendenzaTitolarioUfficio;
    }

    public void setDipendenzaTitolarioUfficio(int dipendenzaTitolarioUfficio) {
        this.dipendenzaTitolarioUfficio = dipendenzaTitolarioUfficio;
    }

}