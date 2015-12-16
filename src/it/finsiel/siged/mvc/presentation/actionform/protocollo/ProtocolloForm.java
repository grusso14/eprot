package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.presentation.actionform.UploaderForm;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.mvc.business.OggettarioDelegate;
import it.flosslab.mvc.vo.OggettoVO;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class ProtocolloForm extends UploaderForm implements
        AlberoUfficiUtentiForm, Cloneable {
    public class Sezione implements Serializable{
        private String nome;

        private boolean obbligatorio;

        private Map mappa;

        protected Sezione(String nome) {
            this.nome = nome;
        }

        protected Sezione(String nome, boolean obbligatorio) {
            this.nome = nome;
            this.obbligatorio = obbligatorio;
        }

        protected Sezione(String nome, Map mappa) {
            this(nome);
            this.mappa = mappa;
        }

        protected Sezione(String nome, Map mappa, boolean obbligatorio) {
            this(nome, obbligatorio);
            this.mappa = mappa;
        }

        public String getNome() {
            String nome = this.nome;
            if (mappa != null && mappa.size() > 0) {
                nome += " (" + mappa.size() + ")";
            }
            return nome;
        }

        public boolean isCorrente() {
            String nome = sezioneVisualizzata;
            int i = nome.indexOf(" (");
            if (i > 0) {
                nome = nome.substring(0, i);
            }
            return this.nome.equals(nome);
        }

        public boolean isObbligatorio() {
            return obbligatorio;
        }

    }

    private List elencoSezioni;

    private String sezioneVisualizzata;

    private int aooId;

    private int protocolloId;

    private int numero;

    private int versione;

    private String flagTipo;

    private String stato;

    private String numeroProtocollo;

    private int ufficioCorrenteId;

    private String ufficioCorrentePath;

    private int ufficioSelezionatoId;

    private int utenteSelezionatoId;

    private UfficioVO ufficioCorrente;

    private Collection ufficiDipendenti;

    private Collection utenti;

    private boolean riservato;
    
    /* FLOSSLAB 27-11-2008 Daniele Sanna */
    private boolean oggettoToAdd;
    private boolean fisicaToAdd;
    private boolean giuridicaToAdd;
    /**
	 * @return the giuridicaToAdd
	 */
	public boolean isGiuridicaToAdd() {
		return giuridicaToAdd;
	}

	/**
	 * @param giuridicaToAdd the giuridicaToAdd to set
	 */
	public void setGiuridicaToAdd(boolean giuridicaToAdd) {
		this.giuridicaToAdd = giuridicaToAdd;
	}

	/**
	 * @return the fisicaToAdd
	 */
	public boolean isFisicaToAdd() {
		return fisicaToAdd;
	}

	/**
	 * @param fisicaToAdd the fisicaToAdd to set
	 */
	public void setFisicaToAdd(boolean fisicaToAdd) {
		this.fisicaToAdd = fisicaToAdd;
	}

	/**
	 * @return the oggettoToAdd
	 */
	public boolean isOggettoToAdd() {
		return oggettoToAdd;
	}

	/**
	 * @param oggettoToAdd the oggettoToAdd to set
	 */
	public void setOggettoToAdd(boolean oggettoToAdd) {
		this.oggettoToAdd = oggettoToAdd;
	}
	/* FINE FLOSSLAB 27-11-2008 Daniele Sanna */
    
	private String[] allegatiSelezionatiId;

    private String descrizioneAnnotazione;

    private String posizioneAnnotazione;

    private String chiaveAnnotazione;

    private String[] allaccioSelezionatoId;

    private String allaccioProtocolloId;

    private String allaccioAnnoProtocollo;

    private String allaccioNumProtocollo;

    private Map protocolliAllacciati;

    private int tipoDocumentoId;

    private String dataDocumento;

    private String dataRicezione;

    private String dataRegistrazione;

    private String oggetto;
    
    private String oggettoGenerico;

    private String cognomeMittente;

    private Integer documentoId;

    private DocumentoVO documentoPrincipale;

    private Map documentiAllegati;

    private TitolarioVO titolario;

    private int titolarioPrecedenteId;

    private int titolarioSelezionatoId;

    private Collection titolariFigli;

    private String[] fascicoloSelezionatoId;

    private String[] procedimentoSelezionatoId;

    private boolean modificabile;

    // dati protocollo annullato
    private String dataAnnullamento;

    private String notaAnnullamento;

    private String provvedimentoAnnullamento;

    // dati fascicolo
    private FascicoloVO fascicolo;

    private Collection fascicoli;

    private Map fascicoliProtocollo;

    // dati procedimento
    private String oggettoProcedimento;

    private Map procedimentiProtocollo;

    private boolean versioneDefault = true;

    private int numProtocolloEmergenza;

    private int utenteProtocollatoreId;

    private int ufficioProtocollatoreId;

    private int numeroProtocolliRegistroEmergenza;

    private boolean dipTitolarioUfficio;

    private boolean isUtenteAbilitatoSuUfficio;

    public String cercaFascicoloNome;

    public String cercaOggettoProcedimento;

    private boolean documentoVisibile;
    
    /** Campo motivazione modifica protocollo; roberto onnis 26/02/09 **/
    private String motivazione;

    public String getCercaFascicoloNome() {
        return cercaFascicoloNome;
    }

    public void setCercaFascicoloNome(String cercaFascicoloNome) {
        this.cercaFascicoloNome = cercaFascicoloNome;
    }

    public boolean isDipTitolarioUfficio() {
        return dipTitolarioUfficio;
    }

    public ProtocolloForm() {
        inizializzaForm();
    }

    public List getElencoSezioni() {
        return elencoSezioni;
    }

    public Collection getUfficiDipendenti() {
        return ufficiDipendenti;
    }

    public void setUfficiDipendenti(Collection ufficiDipendenti) {
        this.ufficiDipendenti = ufficiDipendenti;
    }

    public String getFlagTipo() {
        return flagTipo;
    }

    public void setFlagTipo(String flagTipo) {
        this.flagTipo = flagTipo;
    }

    public Collection getUtenti() {
        return utenti;
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

    public String getSezioneVisualizzata() {
        return sezioneVisualizzata;
    }

    public void setSezioneVisualizzata(String displaySection) {
        this.sezioneVisualizzata = displaySection;
    }

    public int getUfficioCorrenteId() {
        return ufficioCorrenteId;
    }

    public void setUfficioCorrenteId(int ufficioCorrenteId) {
        this.ufficioCorrenteId = ufficioCorrenteId;
    }

    /**
     * @return Returns the modificabile.
     */
    public boolean isModificabile() {
        return modificabile;
    }

    /**
     * @param modificabile
     *            The modificabile to set.
     */
    public void setModificabile(boolean modificabile) {
        this.modificabile = modificabile;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public int getUfficioSelezionatoId() {
        return ufficioSelezionatoId;
    }

    public void setUfficioSelezionatoId(int ufficioCorrenteId) {
        this.ufficioSelezionatoId = ufficioCorrenteId;
    }

    /**
     * @return Returns the allaccioAnnoProtocollo.
     */
    public String getAllaccioAnnoProtocollo() {
        return allaccioAnnoProtocollo;
    }

    /**
     * @param allaccioAnnoProtocollo
     *            The allaccioAnnoProtocollo to set.
     */
    public void setAllaccioAnnoProtocollo(String allaccioAnnoProtocollo) {
        this.allaccioAnnoProtocollo = allaccioAnnoProtocollo;
    }

    /**
     * @return Returns the allaccioProtocolloId.
     */
    public String getAllaccioProtocolloId() {
        return allaccioProtocolloId;
    }

    /**
     * @param allaccioProtocolloId
     *            The allaccioProtocolloId to set.
     */
    public void setAllaccioProtocolloId(String allaccioProtocolloId) {
        this.allaccioProtocolloId = allaccioProtocolloId;
    }

    /**
     * @return Returns the tipoDocumentoId.
     */

    public int getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    /**
     * @param tipoDocumentoId
     *            The tipoDocumentoId to set.
     */
    public void setTipoDocumentoId(int tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    /**
     * @return Returns the dataDocumento.
     */
    public String getDataDocumento() {
        return dataDocumento;
    }

    /**
     * @param dataDocumento
     *            The dataDocumento to set.
     */
    public void setDataDocumento(String dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    /**
     * @return Returns the dataRicezione.
     */
    public String getDataRicezione() {
        return dataRicezione;
    }

    /**
     * @param dataRicezione
     *            The dataRicezione to set.
     */
    public void setDataRicezione(String dataRicezione) {
        this.dataRicezione = dataRicezione;
    }

    /**
     * @return Returns the oggetto.
     */
    public boolean isDocumentoRiservato() {
        if (!getRiservato() || isModificabile())
            return false;
        else
            return true;
    }

    public String getOggetto() {
        if (!getRiservato() || isModificabile() || isDocumentoVisibile()) {
           if(null == this.oggettoGenerico || "".equals(this.oggettoGenerico)){   //TODO riverificare il funzionamento ed ottimizzarne la gestione
            	return this.oggetto;
           }else{
           		return this.oggettoGenerico;
           }
        } else {
            return Parametri.PROTOCOLLO_RISERVATO;
        }
    }
    
    public String getOggettoGenerico() {
        return this.oggettoGenerico;
    }

    /**
     * @param oggetto
     *            The oggetto to set.
     */
    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }
    
	/**
	 * @param oggettoGenerico the oggettoGenerico to set
	 */
	public void setOggettoGenerico(String oggettoGenerico) {
		this.oggettoGenerico = oggettoGenerico;
	}

    /**
     * @return Returns the protocolliAllacciati.
     */
    public Collection getProtocolliAllacciati() {
        return protocolliAllacciati.values();
    }

    public void allacciaProtocollo(AllaccioVO allaccio) {
        if (allaccio != null) {
            this.protocolliAllacciati.put(new Integer(allaccio
                    .getProtocolloAllacciatoId()), allaccio);
        }
    }

    public void rimuoviAllaccio(int allaccioId) {
        this.protocolliAllacciati.remove(new Integer(allaccioId));
    }

    /**
     * @return Returns the documentiAllegati.
     */
    public Collection getDocumentiAllegatiCollection() {
        return documentiAllegati.values();
    }

    public void setDocumentiAllegati(Map documenti) {
        this.documentiAllegati = documenti;
    }

    public Map getDocumentiAllegati() {
        return documentiAllegati;
    }

    public void allegaDocumento(DocumentoVO doc) {
        ProtocolloBO.putAllegato(doc, this.documentiAllegati);
    }

    public void rimuoviAllegato(String allegatoId) {
        DocumentoVO d = (DocumentoVO) documentiAllegati.remove(allegatoId);
    }

    public void rimuoviDocumentoPrincipale() {
        setDocumentoPrincipale(new DocumentoVO());
    }

    public DocumentoVO getDocumentoAllegato(Object key) {
        return (DocumentoVO) this.documentiAllegati.get(key);
    }

    public DocumentoVO getDocumentoAllegato(int idx) {
        return (DocumentoVO) this.documentiAllegati.get(String.valueOf(idx));
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

    /**
     * @return Returns the fascicolazione.
     */
    public String[] getFascicoloSelezionatoId() {
        return fascicoloSelezionatoId;
    }

    /**
     * @param fascicolazione
     *            The fascicolazione to set.
     */
    public void setFascicoloSelezionatoId(String[] fascicoloSelezionatoId) {
        this.fascicoloSelezionatoId = fascicoloSelezionatoId;
    }

    public Collection getProvince() {
        return getLookupDelegateDelegate().getProvince();
    }

    public Collection getTipiDocumento() {
        return getLookupDelegateDelegate().getTipiDocumento(getAooId());
    }
    
    public Collection getOggettario(){
    	return getOggettarioDelegate().getOggetti();
    }

    public Collection getTitoliDestinatario() {
        return getLookupDelegateDelegate().getTitoliDestinatario();
    }

    private LookupDelegate getLookupDelegateDelegate() {
        return LookupDelegate.getInstance();
    }
    
    private OggettarioDelegate getOggettarioDelegate() {
        return OggettarioDelegate.getInstance();
    }

    /**
     * @return Returns the id.
     */
    public int getProtocolloId() {
        return protocolloId;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setProtocolloId(int id) {
        this.protocolloId = id;
    }

    /**
     * @return Returns the allaccioNumProtocollo.
     */
    public String getAllaccioNumProtocollo() {
        return allaccioNumProtocollo;
    }

    /**
     * @param allaccioNumProtocollo
     *            The allaccioNumProtocollo to set.
     */
    public void setAllaccioNumProtocollo(String allaccioNumProtocollo) {
        this.allaccioNumProtocollo = allaccioNumProtocollo;
    }

    /**
     * @return Returns the allaccioSelezionatoId.
     */
    public String[] getAllaccioSelezionatoId() {
        return allaccioSelezionatoId;
    }

    /**
     * @param allaccioSelezionatoId
     *            The allaccioSelezionatoId to set.
     */
    public void setAllaccioSelezionatoId(String[] allaccioSelezionatoId) {
        this.allaccioSelezionatoId = allaccioSelezionatoId;
    }

    /**
     * @return Returns the descrizioneAnnotazione.
     */
    public String getDescrizioneAnnotazione() {
        return descrizioneAnnotazione;
    }

    /**
     * @param descrizioneAnnotazione
     *            The descrizioneAnnotazione to set.
     */
    public void setDescrizioneAnnotazione(String descrizioneAnnotazione) {
        this.descrizioneAnnotazione = descrizioneAnnotazione;
    }

    /**
     * @return Returns the chiaveAnnotazione.
     */
    public String getChiaveAnnotazione() {
        return chiaveAnnotazione;
    }

    /**
     * @param chiaveAnnotazione
     *            The chiaveAnnotazione to set.
     */
    public void setChiaveAnnotazione(String chiaveAnnotazione) {
        this.chiaveAnnotazione = chiaveAnnotazione;
    }

    /**
     * @return Returns the posizioneAnnotazione.
     */
    public String getPosizioneAnnotazione() {
        return posizioneAnnotazione;
    }

    /**
     * @param posizioneAnnotazione
     *            The posizioneAnnotazione to set.
     */
    public void setPosizioneAnnotazione(String posizioneAnnotazione) {
        this.posizioneAnnotazione = posizioneAnnotazione;
    }

    /**
     * @return Returns the riservato.
     */
    public boolean getRiservato() {
        return riservato;
    }

    /**
     * @param riservato
     *            The riservato to set.
     */
    public void setRiservato(boolean riservato) {
        this.riservato = riservato;
    }

    public String[] getAllegatiSelezionatiId() {
        return allegatiSelezionatiId;
    }

    public void setAllegatiSelezionatiId(String[] allegatiSelezionatoId) {
        this.allegatiSelezionatiId = allegatiSelezionatoId;
    }

    public DocumentoVO getDocumentoPrincipale() {
        return documentoPrincipale;
    }

    public void setDocumentoPrincipale(DocumentoVO documentoPrincipale) {
        this.documentoPrincipale = documentoPrincipale;
    }

    public int getUtenteSelezionatoId() {
        return utenteSelezionatoId;
    }

    public void setUtenteSelezionatoId(int utenteCorrenteId) {
        this.utenteSelezionatoId = utenteCorrenteId;
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

    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    public void setNumeroProtocollo(String numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    public String getDataRegistrazione() {
        return dataRegistrazione;
    }

    public String getDataRegistrazioneToEtichetta() {
        if (dataRegistrazione != null)
            return dataRegistrazione.substring(0, 10);
        else
            return dataRegistrazione;
    }

    public void setDataRegistrazione(String dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public String getProtocolloRiservato() {
        return Parametri.PROTOCOLLO_RISERVATO;
    }

    public int getNumero() {
        return numero;
    }

    public String getNumeroEtichetta() {
        return StringUtil.formattaNumeroProtocollo(numero + "", 7);
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    /**
     * @return Returns the dataAnnullamento.
     */
    public String getDataAnnullamento() {
        return dataAnnullamento;
    }

    /**
     * @param dataAnnullamento
     *            The dataAnnullamento to set.
     */
    public void setDataAnnullamento(String dataAnnullamento) {
        this.dataAnnullamento = dataAnnullamento;
    }

    /**
     * @return Returns the notaAnnullamento.
     */
    public String getNotaAnnullamento() {
        return notaAnnullamento;
    }

    /**
     * @param notaAnnullamento
     *            The notaAnnullamento to set.
     */
    public void setNotaAnnullamento(String notaAnnullamento) {
        this.notaAnnullamento = notaAnnullamento;
    }

    /**
     * @return Returns the provvedimentoAnnullamento.
     */
    public String getProvvedimentoAnnullamento() {
        return provvedimentoAnnullamento;
    }

    /**
     * @param provvedimentoAnnullamento
     *            The provvedimentoAnnullamento to set.
     */
    public void setProvvedimentoAnnullamento(String provvedimentoAnnullamento) {
        this.provvedimentoAnnullamento = provvedimentoAnnullamento;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public FascicoloVO getFascicolo() {
        return fascicolo;
    }

    public void setFascicolo(FascicoloVO fascicolo) {
        this.fascicolo = fascicolo;
    }

    public Collection getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Collection fascicoli) {
        this.fascicoli = fascicoli;
    }

    /**
     * @return Returns the protocolliAllacciati.
     */
    public Collection getFascicoliProtocollo() {
        return fascicoliProtocollo.values();
    }

    public void aggiungiFascicolo(FascicoloVO fascicolo) {
        if (fascicolo != null) {
            this.fascicoliProtocollo.put(fascicolo.getId(), fascicolo);
        }
    }

    public void rimuoviFascicolo(int fascicoloId) {
        this.fascicoliProtocollo.remove(new Integer(fascicoloId));
    }

    public String getDescrizioneStatoProtocollo() {
        return ProtocolloBO.getStatoProtocollo(getFlagTipo(), getStato());
    }

    public boolean getVersioneDefault() {
        return versioneDefault;
    }

    public void setVersioneDefault(boolean versioneDefault) {
        this.versioneDefault = versioneDefault;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public int getNumProtocolloEmergenza() {
        return numProtocolloEmergenza;
    }

    public void setNumProtocolloEmergenza(int numProtocolloEmergenza) {
        this.numProtocolloEmergenza = numProtocolloEmergenza;
    }

    public int getUfficioProtocollatoreId() {
        return ufficioProtocollatoreId;
    }

    public void setUfficioProtocollatoreId(int ufficioProtocollatoreId) {
        this.ufficioProtocollatoreId = ufficioProtocollatoreId;
    }

    public int getUtenteProtocollatoreId() {
        return utenteProtocollatoreId;
    }

    public void setUtenteProtocollatoreId(int utenteProtocollatoreId) {
        this.utenteProtocollatoreId = utenteProtocollatoreId;
    }

    public String getProtocollatore() {
        Organizzazione org = Organizzazione.getInstance();
        return org.getUfficio(getUfficioProtocollatoreId()).getValueObject()
                .getDescription()
                + "/"
                + org.getUtente(getUtenteProtocollatoreId()).getValueObject()
                        .getFullName();
    }

    public int getNumeroProtocolliRegistroEmergenza() {
        return numeroProtocolliRegistroEmergenza;
    }

    public void setNumeroProtocolliRegistroEmergenza(
            int numeroProtocolliRegistroEmergenza) {
        this.numeroProtocolliRegistroEmergenza = numeroProtocolliRegistroEmergenza;
    }

    public void inizializzaForm() {
        setAllaccioAnnoProtocollo(null);
        setAllaccioNumProtocollo(null);
        setAllaccioProtocolloId(null);
        setAllaccioSelezionatoId(null);
        setAllegatiSelezionatiId(null);
        setChiaveAnnotazione(null);
        setDataDocumento(null);
        setDataRicezione(null);
        setDescrizioneAnnotazione(null);
        documentiAllegati = new HashMap(2);
        setDocumentoPrincipale(new DocumentoVO());
        setFascicoloSelezionatoId(null);
        setRiservato(false);
        setProtocolloId(0);
        setOggetto(null);
        setPosizioneAnnotazione(null);
        protocolliAllacciati = new HashMap();
        setSezioneVisualizzata("Mittente");
        fascicolo = new FascicoloVO();
        setTitolariFigli(null);
        setTitolario(null);
        setTitolarioPrecedenteId(0);
        setTitolarioSelezionatoId(0);
        setUfficioSelezionatoId(0);
        setUtenteSelezionatoId(0);
        setModificabile(true);
        setFascicolo(new FascicoloVO());
        setFascicoli(null);
        setOggettoProcedimento(null);
        fascicoliProtocollo = new HashMap();
        procedimentiProtocollo = new HashMap();
        setProvvedimentoAnnullamento(null);
        setNotaAnnullamento(null);
        setNumProtocolloEmergenza(0);
        setVersione(0);
        aggiornaSezioni();
    }

    public void aggiornaSezioni() {
        elencoSezioni = new ArrayList();
        elencoSezioni.add(new Sezione("Mittente", true));
        elencoSezioni.add(new Sezione("Allegati", documentiAllegati));
        elencoSezioni.add(new Sezione("Allacci", protocolliAllacciati));
        elencoSezioni.add(new Sezione("Annotazioni"));
        elencoSezioni.add(new Sezione("Titolario", isDipTitolarioUfficio()));
        elencoSezioni.add(new Sezione("Fascicoli", fascicoliProtocollo));
        elencoSezioni.add(new Sezione("Procedimenti", procedimentiProtocollo));
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        Utente utente = (Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY);
        if (utente != null) {
            dipTitolarioUfficio = utente.getAreaOrganizzativa()
                    .getDipendenzaTitolarioUfficio() == 1;
            // TODO: verificare setRiservato(false);
        }
    }

    public void inizializzaRipetiForm() {
        setAllaccioAnnoProtocollo(null);
        setAllaccioNumProtocollo(null);
        setAllaccioProtocolloId(null);
        setAllaccioSelezionatoId(null);
        setAllegatiSelezionatiId(null);
        setChiaveAnnotazione(null);
        if (dataDocumento == null) {
            setDataDocumento(null);
        }
        if (dataRicezione == null) {
            setDataRicezione(null);
        }
        setDescrizioneAnnotazione(null);
        documentiAllegati = new HashMap();
        setDocumentoPrincipale(new DocumentoVO());
        setFascicoloSelezionatoId(null);
        setRiservato(false);
        setProtocolloId(0);

        setPosizioneAnnotazione(null);
        protocolliAllacciati = new HashMap();
        setSezioneVisualizzata("Mittente");
        fascicolo = new FascicoloVO();
        setTitolariFigli(null);
        if (titolario == null) {
            setTitolario(null);
            setTitolarioPrecedenteId(0);
            setTitolarioSelezionatoId(0);
        }
        setUfficioSelezionatoId(0);
        setUtenteSelezionatoId(0);
        setModificabile(true);
        setFascicolo(new FascicoloVO());
        setFascicoli(null);
        setOggettoProcedimento(null);
        fascicoliProtocollo = new HashMap();
        procedimentiProtocollo = new HashMap();
        setProvvedimentoAnnullamento(null);
        setNotaAnnullamento(null);
        setNumProtocolloEmergenza(0);
        setVersione(0);
        aggiornaSezioni();
    }

    public void inizializzaFormToCopyProtocollo() {
        documentiAllegati = new HashMap(2);
        setDocumentoPrincipale(new DocumentoVO());
        setProtocolloId(0);
        setVersione(0);
    }

    public ActionErrors validateAnnullamentoProtocollo(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("btnConfermaAnnullamento") != null
                && (getProvvedimentoAnnullamento() == null || ""
                        .equals(getProvvedimentoAnnullamento()))) {
            errors.add("annullaProtocollo", new ActionMessage(
                    "campo.obbligatorio", "Provvedimento annullamento", ""));
        }
        return errors;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (request.getParameter("allacciaProtocolloAction") != null) {
            if ("".equals(getAllaccioAnnoProtocollo())) {
                errors.add("allaccioAnnoProtocollo", new ActionMessage(
                        "campo.obbligatorio", "Anno allaccio", ""));
            } else if (!NumberUtil.isInteger(getAllaccioAnnoProtocollo())) {
                errors.add("allaccioAnnoProtocollo", new ActionMessage(
                        "formato.numerico.errato", "Anno allaccio"));
            }
            if ("".equals(getAllaccioNumProtocollo())) {
                errors.add("allaccioNumProtocollo", new ActionMessage(
                        "campo.obbligatorio", "Numero allaccio", ""));
            } else if (!NumberUtil.isInteger(getAllaccioNumProtocollo())) {
                errors.add("allaccioNumProtocollo", new ActionMessage(
                        "formato.numerico.errato", "Numero allaccio"));
            }

        } else if (request.getParameter("downloadAllegatoId") != null) {
            if (!NumberUtil.isInteger(request
                    .getParameter("downloadAllegatoId"))) {
                errors.add("downloadAllegatoId", new ActionMessage(
                        "formato.numerico.errato", "Identificativo Documento"));
            }

        } else if (request.getParameter("salvaAction") != null) {
            String dataDoc = getDataDocumento();
            String dataRic = getDataRicezione();
            String dataReg = getDataRegistrazione();

            if (dataDoc != null && !"".equals(dataDoc)) {
                if (!DateUtil.isData(dataDoc)) {
                    // la data del documento deve essere nel formato valido:
                    // gg/mm/aaaa
                    errors.add("dataDocumento", new ActionMessage(
                            "formato.data.errato", "Data"));
                } else {
                    // la data del documento non deve essere successiva a quella
                    // di registrazione
                    if (DateUtil.toDate(dataReg).before(
                            DateUtil.toDate(dataDoc))) {
                        errors.add("dataRegistrazione", new ActionMessage(
                                "data_registrazione.minore.data_documento"));
                    }
                }
            }
            if (dataRic != null && !"".equals(dataRic)) {
                if (!DateUtil.isDataOra(dataRic)) {
                    // la data di ricezione deve essere nel formato valido:
                    // gg/mm/aaaa
                    errors.add("dataRicezione", new ActionMessage(
                            "formato.data.errato", "Ricevuto il"));
                } else {
                    // la data di ricezione non deve essere successiva a quella
                    // di registrazione
                    if (DateUtil.toDate(dataReg).before(
                            DateUtil.toDate(dataRic))) {
                        errors.add("dataRegistrazione", new ActionMessage(
                                "data_registrazione.minore.data_ricezione"));
                    }
                }
            }
            
            // la data del documento non deve essere successiva a quella di
            // ricezione
            if (dataDoc != null
                    && !"".equals(dataDoc)
                    && DateUtil.isData(dataDoc)
                    && dataRic != null
                    && !"".equals(dataRic)
                    && DateUtil.isData(dataRic)
                    && DateUtil.toDate(dataRic)
                            .before(DateUtil.toDate(dataDoc))) {
                errors.add("dataRicezione", new ActionMessage(
                        "data_ricezione.minore.data_documento"));
            }
            // tipoDocumento e' obbligatorio
            if (getTipoDocumentoId() == 0) {
                errors.add("tipoDocumento", new ActionMessage(
                        "campo.obbligatorio", "Tipo documento", ""));
            }
            // l'oggetto e' obbligatorio
            if (getOggetto() == null || "".equals(getOggetto().trim())) {
            	errors.add("oggetto", new ActionMessage("campo.obbligatorio",
                        "Oggetto", ""));
            }
            else if("Altro".equals(getOggetto()) && (getOggettoGenerico() == null || "".equals(getOggettoGenerico()))) {
                errors.add("oggetto", new ActionMessage("campo.obbligatorio",
                        "Oggetto", ""));
            	}

            if (getFascicoliProtocollo().size() > 0 && getTitolario() == null) {
                errors.add("fascicolo",
                        new ActionMessage("campo.obbligatorio", "Titolario",
                                ", se � stato impostato almeno fascicolo,"));
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
            if(getProtocolloId() != 0 && (null == getMotivazione() || "".equals(getMotivazione()))) {
            	errors.add("motivazione", new ActionMessage(
                            "campo.obbligatorio", "Motivazione", ""));
            }
        }
        return errors;
    }

    private void fillMotivazione() {
		
		
	}

	public String getCognomeMittente() {
        return cognomeMittente;
    }

    public void setCognomeMittente(String cognomeMittente) {
        this.cognomeMittente = cognomeMittente;
    }

    public Integer getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(Integer documentoId) {
        this.documentoId = documentoId;
    }

    public String getOggettoProcedimento() {
        return oggettoProcedimento;
    }

    public void setOggettoProcedimento(String oggettoProcedimento) {
        this.oggettoProcedimento = oggettoProcedimento;
    }

    public String[] getProcedimentoSelezionatoId() {
        return procedimentoSelezionatoId;
    }

    public void setProcedimentoSelezionatoId(String[] procedimentoSelezionatoId) {
        this.procedimentoSelezionatoId = procedimentoSelezionatoId;
    }

    public Collection getProcedimentiProtocollo() {
        return procedimentiProtocollo.values();
    }

    public void aggiungiProcedimento(ProtocolloProcedimentoVO procedimento) {
        this.procedimentiProtocollo.put(new Integer(procedimento
                .getProcedimentoId()), procedimento);
    }

    public void rimuoviProcedimento(int procedimentoId) {
        this.procedimentiProtocollo.remove(new Integer(procedimentoId));
    }

    public boolean getUtenteAbilitatoSuUfficio() {
        return isUtenteAbilitatoSuUfficio;
    }

    public void setUtenteAbilitatoSuUfficio(boolean isUtenteAbilitatoSuUfficio) {
        this.isUtenteAbilitatoSuUfficio = isUtenteAbilitatoSuUfficio;
    }

    public String getCercaOggettoProcedimento() {
        return cercaOggettoProcedimento;
    }

    public void setCercaOggettoProcedimento(String cercaOggettoProcedimento) {
        this.cercaOggettoProcedimento = cercaOggettoProcedimento;
    }

    public boolean isDocumentoVisibile() {
        return documentoVisibile;
    }

    public void setDocumentoVisibile(boolean documentoVisibile) {
        this.documentoVisibile = documentoVisibile;
    }
    
    /**
     * modifica roberto onnis 26/02/2009
     * @return String
     */
	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}
	
	/**
	 * fine modifica
	 */
	

	/**
	 * modifica roberto onnis 6/3/09
	 */ 
	
    private String[] metodi = {"getOggettario", "getProvince", "getTitoliDestinatario", "getMotivazione"};
	
	public String getDifferences(ProtocolloForm old, String motivazioneUtente) {
		String motivazione = "";
		try {
			Class<?> c = Class.forName("it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm");
		    Method[] allMethods = c.getDeclaredMethods();
			//info = Introspector.getBeanInfo( ProtocolloIngressoForm.class );

        //for ( Method pd : info.get() ) {
        for ( Method pd :  allMethods) {
	        	if(Modifier.isPublic(pd.getModifiers()) && pd.getName().startsWith("get") && pd.getParameterTypes().length == 0) {
	        		//System.out.println("parameters: " + pd.getParameterTypes().length);
	        		//System.out.println("Public: " + pd.getModifiers());
	        		//System.out.println(MethodUtils.invokeMethod((ProtocolloForm)this, pd.getName(), null));
	        		Object newObject = MethodUtils.invokeMethod((ProtocolloForm)this, pd.getName(), null);
	        		
	        		Object oldObject = MethodUtils.invokeMethod((ProtocolloForm)old, pd.getName(), null);
	        		//System.out.println(newObject == null ? true : newObject.equals(oldObject));
	        		if(notInMethods(pd.getName()) && compareObjects(oldObject, newObject)) {
	        			if("".equals(motivazione)) {
	        				motivazione = "Campi modificati: ";
	        			}
	        			motivazione += pd.getName().substring(3) + ", ";
	        		} 
	        	//System.out.println( pd.getName() );
	        	}
        	}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		motivazione = motivazione.substring(0, motivazione.length() - 2) + "; ";
        motivazione += "Motivazione fornita dall'utente: " + motivazioneUtente;
		return motivazione;
	}
	
	private boolean notInMethods(String nome) {
		for(String metodo : this.metodi) {
			if(metodo.equals(nome)) {
				return false;
			}
		}
		return true;
	}
	
	public ProtocolloForm cloneForm() {
		try {
			return (ProtocolloForm) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return this;
		}
	}


	/**
	 * Restituisce true se i due oggetti sono diversi
	 * @param o1
	 * @param o2
	 * @return boolean
	 */
	private boolean compareObjects(Object o1, Object o2) {
		if(o1 == null && o2 == null) {
			return false;
		} else if(("".equals(o1) && o2 == null) || ("".equals(o2) && o1 == null)) {
			return false;
		}else if((o1 == null) || (o2 == null) || !o1.equals(o2)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * fine modifica
	 */

}