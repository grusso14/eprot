package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.util.NumberUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
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

public final class ProtocolloIngressoForm extends ProtocolloForm {

    // variabili
    private Integer messaggioEmailId;

    private Map assegnatari; // di AssegnatarioView

    private String[] assegnatariSelezionatiId;
    
    private String[] mittentiSelezionatiId;

	private String assegnatarioCompetente;
    
    private String[] assegnatariCompetenti;

    private String dataProtocolloMittente;

    private String numProtocolloMittente;

    private Collection protocolliMittente;

    private SoggettoVO mittente;
    
    private SoggettoVO multiMittenteCorrente;
    
    private List<SoggettoVO> mittenti;
    
    private String msgAssegnatarioCompetente;

    public ProtocolloIngressoForm() {
        super();
        inizializzaForm();
    }
    
    /**
	 * @return the mittentiSelezionatiId
	 */
	public String[] getMittentiSelezionatiId() {
		return mittentiSelezionatiId;
	}
	
	public boolean fisicaToAdd;
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
	 * @param mittentiSelezionatiId the mittentiSelezionatiId to set
	 */
	public void setMittentiSelezionatiId(String[] mittenti) {
		this.mittentiSelezionatiId = mittenti;
	}

    public String[] getAssegnatariSelezionatiId() {
        return assegnatariSelezionatiId;
    }

    public void setAssegnatariSelezionatiId(String[] assegnatari) {
        this.assegnatariSelezionatiId = assegnatari;
    }

    public String getAssegnatarioCompetente() {
        return assegnatarioCompetente;
    }
    
    public String[] getAssegnatariCompetenti() {
        return assegnatariCompetenti;
    }

    public void setAssegnatarioCompetente(String assegnatarioCompetente) {
        this.assegnatarioCompetente = assegnatarioCompetente;
        for (Iterator i = getAssegnatari().iterator(); i.hasNext();) {
            AssegnatarioView ass = (AssegnatarioView) i.next();
            if (ass.getKey().equals(assegnatarioCompetente)) {
            	ass.setCompetente(ass.getKey().equals(assegnatarioCompetente));
            } 
        }
    }
        
    public void setAssegnatariCompetenti(String[] assegnatariCompetenti) {
            this.assegnatariCompetenti = assegnatariCompetenti;
            	for (Iterator i = getAssegnatari().iterator(); i.hasNext();) {
            		AssegnatarioView ass = (AssegnatarioView) i.next();
	                ass.setCompetente(this.isCompetente(ass));
	            }
    }

    public boolean isCompetente(AssegnatarioView ass) {
		for(String assCompetente : this.assegnatariCompetenti){
			if(assCompetente.equals(ass.getKey())){
				return true;
			}
		}
		return false;
	}

	/**
     * @return Returns the dataProtocolloMittente.
     */
    public String getDataProtocolloMittente() {
        return dataProtocolloMittente;
    }

    /**
     * @param dataProtocolloMittente
     *            The dataProtocolloMittente to set.
     */
    public void setDataProtocolloMittente(String dataProtocolloMittente) {
        this.dataProtocolloMittente = dataProtocolloMittente;
    }

    /**
     * @return Returns the personaFisica mittente.
     */
    public SoggettoVO getMittente() {
        return mittente;
    }

    /**
     * @param mittenteId
     *            The mittenteId to set.
     */
    public void setMittente(SoggettoVO mittente) {
        this.mittente = mittente;
    }

    /**
     * @return Returns the numProtocolloMittente.
     */
    public String getNumProtocolloMittente() {
        return numProtocolloMittente;
    }

    /**
     * @param numProtocolloMittente
     *            The numProtocolloMittente to set.
     */
    public void setNumProtocolloMittente(String numProtocolloMittente) {
        this.numProtocolloMittente = numProtocolloMittente;
    }

    public void removeAssegnatari() {
        if (assegnatari != null)
            assegnatari.clear();
    }

    public Collection getAssegnatari() {
        return assegnatari.values();
    }

    public void aggiungiAssegnatario(AssegnatarioView ass) {
        assegnatari.put(ass.getKey(), ass);
    }

    public void rimuoviAssegnatario(String key) {

        assegnatari.remove(key);
    }

    /**
     * @return Returns the protocolliMittente.
     */
    public Collection getProtocolliMittente() {
        return protocolliMittente;
    }

    public int getNumeroProtocolliMittente() {
        return protocolliMittente.size();
    }

    /**
     * @param protocolliMittente
     *            The protocolliMittente to set.
     */
    public void setProtocolliMittente(Collection protocolliMittente) {
        this.protocolliMittente = protocolliMittente;
    }

    public void inizializzaForm() {
        super.inizializzaForm();
        super.setFlagTipo("I");
        assegnatari = new HashMap();
        mittenti = new ArrayList<SoggettoVO>();
        setAssegnatarioCompetente(null);
        setAssegnatariSelezionatiId(null);
        setDataProtocolloMittente(null);
        setMittente(new SoggettoVO('F'));
        setMultiMittenteCorrente(new SoggettoVO('F'));
        setNumProtocolloMittente(null);
        setProtocolliMittente(null);
        // modifica pino 08/02/2006
        setMsgAssegnatarioCompetente(null);
        getElencoSezioni()
                .add(1, new Sezione("Assegnatari", assegnatari, true));
    }

    public void inizializzaRipetiForm() {
        super.inizializzaRipetiForm();
        super.setFlagTipo("I");
        getElencoSezioni()
                .add(1, new Sezione("Assegnatari", assegnatari, true));

        if ((getAssegnatarioCompetente() == null)
                && (getAssegnatariSelezionatiId() == null)) {
            assegnatari = new HashMap();
        }
        // setAssegnatarioCompetente(null);
        // setAssegnatariSelezionatiId(null);
        setDataProtocolloMittente(null);
        if (mittente == null)
            setMittente(new SoggettoVO('F'));
        setNumProtocolloMittente(null);
        setProtocolliMittente(null);
        // modifica pino 08/02/2006
        setMsgAssegnatarioCompetente(null);
    }

    // ================================================================ //
    // =================== VALIDAZIONE ============================== //
    // ================================================================ //

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        if (isDipTitolarioUfficio()
                && "Titolario".equals(getSezioneVisualizzata())) {
            if (getAssegnatari().size() == 0) {
                errors.add("mittenteDenominazione", new ActionMessage(
                        "selezione.obbligatoria",
                        "un assegnatario di competenza",
                        "per la gestione del titolario"));
                setSezioneVisualizzata("Assegnatari");
            }

        } else if (request.getParameter("btnCercaProtMitt") != null) {
            if (getNumProtocolloMittente() == null
                    || "".equals(getNumProtocolloMittente())) {
                errors.add("numProtocolloMittente", new ActionMessage(
                        "cerca_protocollo_mittente"));
                /*
                 * } else if (getProtocolliMittente() == null ||
                 * !getProtocolliMittente().isEmpty()) {
                 * errors.add("numProtocolloMittente", new ActionMessage(
                 * "cerca_protocollo_mittente_empty"));
                 */
            }

        } else if (request.getParameter("salvaAction") != null) {
            
        	//Modifica Daniele Sanna 03/09/2008
        	//Validazione Multimittente
        	if (LookupDelegate.tipiPersona[0].getTipo().equals(getMittente().getTipo())) {
                // Persona Fisica - il cognome e' obbligatorio
                if (getMittente().getCognome() == null  || "".equals(getMittente().getCognome().trim())){
                	 errors.add("mittenteCognome", new ActionMessage("campo.obbligatorio", "Cognome", "del mittente"));
                }
                   
            }else if (LookupDelegate.tipiPersona[2].getTipo().equals(getMittente().getTipo())) {
                // Multimittente - il cognome e' obbligatorio per ciascun mittente
                if (invalidMultiMittenti()){
                	 errors.add("mittenteCognome", new ActionMessage("campo.obbligatorio", "Cognome", "del mittente"));
                }
                   
            } else { // Persona Giuridica - la denominazione ? obbligatoria
                if (getMittente().getDescrizioneDitta() == null  || "".equals(getMittente().getDescrizioneDitta().trim())){
                	errors.add("mittenteDenominazione", new ActionMessage("campo.obbligatorio", "Denominazione","del mittente"));
                }
                    
            }
            String cap = getMittente().getIndirizzo().getCap();
            if (!"".equals(cap) && NumberUtil.isInteger(cap)
                    && cap.length() != 5) {
                errors.add("cap", new ActionMessage("cap_numerico_5"));
            }
            if (getAssegnatari().size() == 0) {
                // ci deve essere almeno un assegnatario
                errors.add("assegnatari", new ActionMessage(
                        "assegnatari_obbligatorio"));
            } else if (isRiservato()) {
                // se il flag riservato e' selezionato ci deve essere solo un
                // assegnatario per competenza e deve essere un utente
                if (getAssegnatari().size() == 1) {
                    Iterator i = getAssegnatari().iterator();
                    AssegnatarioView assegnatario = (AssegnatarioView) i.next();
                    if (assegnatario.getUtenteId() == 0) {
                        errors.add("assegnatari", new ActionMessage(
                                "assegnatario_non_utente"));
                    }
                } else {
                    errors.add("assegnatari", new ActionMessage(
                            "assegnatari_da_rimuovere"));
                }
            }
            if (getAssegnatarioCompetente() == null
                    || "".equals(getAssegnatarioCompetente())) {
                // ci deve essere almeno un assegnatario di competenza
                errors.add("assegnatarioCompetente", new ActionMessage(
                        "assegnatario_competente_obbligatorio"));
            }
        }
        return errors;
    }

    private boolean invalidMultiMittenti() {
		List<SoggettoVO> mittenti = getMittenti();
		for(SoggettoVO mittente : mittenti){
			if("".equals(mittente.getCognome()) || mittente.getCognome() == null){
				return true;
			}
		}
    	return false;
	}

	public Integer getMessaggioEmailId() {
        return messaggioEmailId;
    }

    public void setMessaggioEmailId(Integer messaggioEmailId) {
        this.messaggioEmailId = messaggioEmailId;
    }

    public String getMsgAssegnatarioCompetente() {
        return msgAssegnatarioCompetente;
    }

    public void setMsgAssegnatarioCompetente(String messaggio) {
        this.msgAssegnatarioCompetente = messaggio;
    }

	public List<SoggettoVO> getMittenti() {
		return mittenti;
	}

	public SoggettoVO getMultiMittenteCorrente() {
		return multiMittenteCorrente;
	}

	public void setMultiMittenteCorrente(SoggettoVO multiMittenteCorrente) {
		this.multiMittenteCorrente = multiMittenteCorrente;
	}
}
