package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.business.ConfigurazioneUtenteDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.RegistroDelegate;
import it.finsiel.siged.mvc.business.StoriaProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.NavBarElement;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.ConfigurazioneUtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.flosslab.mvc.presentation.action.protocollo.helper.CaricaProtocollo;
import it.flosslab.mvc.presentation.action.protocollo.helper.form.AggiornaProtocolloIngressoForm;
import it.flosslab.mvc.presentation.action.protocollo.helper.model.AggiornaProtocolloIngressoModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * @author Almaviva sud.
 * 
 */

public class ProtocolloIngressoAction extends ProtocolloAction {
    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ProtocolloIngressoAction.class
            .getName());

    


    protected void assegnaAdUfficio(ProtocolloForm form, int ufficioId) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(ufficioId);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(ufficioId);
        ass.setNomeUfficio(uff.getValueObject().getDescription());
        ass.setDescrizioneUfficio(uff.getPath());
        ProtocolloIngressoForm pForm = (ProtocolloIngressoForm) form;
        pForm.aggiungiAssegnatario(ass);
        if (pForm.getAssegnatarioCompetente() == null) {
        	pForm.setAssegnatarioCompetente(ass.getKey());
        }
        form.setUfficioSelezionatoId(0);
        if (form.isDipTitolarioUfficio()) {
            form.setTitolario(null);
        }

    }


    protected void assegnaAdUtente(ProtocolloForm form) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(form.getUfficioCorrenteId());
        ass.setNomeUfficio(form.getUfficioCorrente().getDescription());
        ass.setDescrizioneUfficio(form.getUfficioCorrentePath());
        ass.setUtenteId(form.getUtenteSelezionatoId());
        UtenteVO ute = form.getUtente(form.getUtenteSelezionatoId());
        ass.setNomeUtente(ute.getFullName());
        ProtocolloIngressoForm pForm = (ProtocolloIngressoForm) form;
        pForm.aggiungiAssegnatario(ass);
        if (pForm.getAssegnatarioCompetente() == null) {
            pForm.setAssegnatarioCompetente(ass.getKey());
        }
        if (form.isDipTitolarioUfficio()) {
            form.setTitolario(null);
        }

    }

    private void rimuoviAssegnatari(ProtocolloIngressoForm form) {
        String[] assegnatari = form.getAssegnatariSelezionatiId();
        String assegnatarioCompetente = form.getAssegnatarioCompetente();
        if (assegnatari != null) {
            for (int i = 0; i < assegnatari.length; i++) {
                String assegnatario = assegnatari[i];
                if (assegnatario != null) {
                    form.rimuoviAssegnatario(assegnatario);
                    if (assegnatario.equals(assegnatarioCompetente)) {
                        form.setAssegnatarioCompetente(null);
                    }
                }
            }
            if (form.getAssegnatarioCompetente() == null) {
                Iterator i = form.getAssegnatari().iterator();
                if (i.hasNext()) {
                    AssegnatarioView ass = (AssegnatarioView) i.next();
                    form.setAssegnatarioCompetente(ass.getKey());
                }
            }
        }
    }
    
    /**
     * @param form
     */
    private void rimuoviMultiMittenti(ProtocolloIngressoForm form) {
        String[] mittentiSelezionati = form.getMittentiSelezionatiId();
        List<SoggettoVO> mittenti = form.getMittenti();
        List<SoggettoVO> mittentiToRemove = new ArrayList<SoggettoVO>();
        if (mittentiSelezionati != null) {
            for (int i = 0; i < mittentiSelezionati.length; i++) {
            	mittentiToRemove.add(mittenti.get(new Integer(mittentiSelezionati[i])));
            }
        }
        mittenti.removeAll(mittentiToRemove);
        form.setMittentiSelezionatiId(new String[]{});
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * 
     * @exception Exception
     *                if business logic throws an exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        /* qui vanno eventuali errori */
        ActionMessages errors = new ActionMessages();
        super.setType(CaricaProtocollo.PROTOCOLLO_INGRESSO);
        HttpSession session = request.getSession();

        /* contenente i nostri dati provenienti dall'html form */
        ProtocolloIngressoForm pForm = (ProtocolloIngressoForm) form;

        updateAssegnatariCompetenti(pForm);
        session.setAttribute("protocolloForm", pForm);
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        RegistroVO registro = (RegistroVO) utente.getRegistroVOInUso();

        if (!registro.getApertoIngresso()) {
            errors.add("apertoIngresso", new ActionMessage("registro_chiuso"));
            saveErrors(request, errors);
        }

        if (pForm.getTitolario() == null) {
        	AggiornaProtocolloIngressoForm.impostaTitolario(pForm, utente, 0);
        }

        ActionForward actionForward = super.execute(mapping, form, request,
                response);
        if (actionForward != null && "none".equals(actionForward.getName())) {
            return null;
        } else if (actionForward != null) {
            return actionForward;
        }
        // Selezione tipologia di mittente
        if (request.getParameter("cercaMittenteAction") != null) {
            session.setAttribute("tornaProtocollo", Boolean.TRUE);
            ArrayList navBar = (ArrayList) session
                    .getAttribute(Constants.NAV_BAR);
            NavBarElement elem = new NavBarElement();
            elem.setValue("Mittente");
            navBar.add(elem);
            if ("F".equals(pForm.getMittente().getTipo())) {
                elem.setTitle("Seleziona persona fisica");
                request.setAttribute("cognome", pForm.getMittente()
                        .getCognome());
                request.setAttribute("nome", pForm.getMittente().getNome());
                session.setAttribute("provenienza", "personaFisicaProtocolloI");
                return mapping.findForward("cercaPersonaFisica");
            } else {
                elem.setTitle("Seleziona persona giuridica");
                request.setAttribute("descrizioneDitta", pForm.getMittente()
                        .getDescrizioneDitta());
                session.setAttribute("provenienza",
                        "personaGiuridicaProtocolloI");
                return mapping.findForward("cercaPersonaGiuridica");
            }
            
        } else if (request.getParameter("rimuoviAssegnatariAction") != null) {
            rimuoviAssegnatari(pForm);
        } else if (request.getParameter("rimuoviMultiMittentiAction") != null) {
        	rimuoviMultiMittenti(pForm);
        }if (request.getParameter("assegnaMittenteAction") != null) {
            aggiungiMittente(pForm);
        } if (request.getParameter("impostaTitolarioAction") != null) {
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario().getId()
                        .intValue());
            }
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            AggiornaProtocolloIngressoForm.impostaTitolario(pForm, ute, pForm.getTitolarioSelezionatoId());
            return mapping.findForward("input");
        } else if (request.getParameter("btnRipetiDatiI") != null) {
            int utenteId = utente.getValueObject().getId().intValue();

            ConfigurazioneUtenteVO configurazioneVO = null;
            if (session.getAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO") != null) {
                configurazioneVO = (ConfigurazioneUtenteVO) session
                        .getAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO");
            } else {

                configurazioneVO = ConfigurazioneUtenteDelegate.getInstance()
                        .getConfigurazione(utenteId);
                session.setAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO",
                        configurazioneVO);
            }

            ProtocolloBO.getProtocolloIngressoConfigurazioneUtente(pForm,
                    configurazioneVO, utenteId);
            saveToken(request);
            return mapping.findForward("nuovoProtocolloRipeti");
        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            AggiornaProtocolloIngressoForm.impostaTitolario(pForm, ute, pForm.getTitolarioPrecedenteId());
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario()
                        .getParentId());
            }
            return mapping.findForward("input");

        } else if (request.getParameter("btnCercaProtMitt") != null) {
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
            pForm.setProtocolliMittente(delegate.getProtocolliByProtMittente(
                    ute, pForm.getNumProtocolloMittente()));
            if (pForm.getProtocolliMittente().isEmpty()) {
                errors.add("numProtocolloMittente", new ActionMessage(
                        "cerca_protocollo_mittente_empty"));
                saveErrors(request, errors);
            } else {
                return mapping.findForward("cercaProtocolloMittente");
            }

        } else if (request.getParameter("salvaAction") != null && errors.isEmpty()) {
        	//logger.log(Priority.DEBUG, "context path per salva"+request.getContextPath());
            if (isTokenValid(request, true)) {
                return saveProtocollo(mapping, request, errors, session, pForm);
            } else {
                return mapping.findForward("saveFailure");
            }
        } else if (request.getParameter("cercaProcedimenti") != null) {
            session.setAttribute("tornaProtocolloIngresso", Boolean.TRUE);
            return mapping.findForward("cercaProcedimenti");
        }
        pForm.setOggettoProcedimento("");
        return mapping.findForward("input");
    }

	/**
	 * @param mapping
	 * @param request
	 * @param errors
	 * @param session
	 * @param pForm
	 * @return
	 */
	private ActionForward saveProtocollo(ActionMapping mapping,
			HttpServletRequest request, ActionMessages errors,
			HttpSession session, ProtocolloIngressoForm pForm) {
		// aggiorno il modello
		Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
		// controllo che gli uffici e/o utenti assegnatari abbiano i
		// permessi sul registro in uso
		if (pForm.getAssegnatari().size() > 0) {
		    int ufficioId = 0;
		    int utenteId = 0;
		    for (Iterator i = pForm.getAssegnatari().iterator(); i.hasNext();) {
		    	AssegnatarioView assegnatario = (AssegnatarioView) i.next();
		    	ufficioId = assegnatario.getUfficioId();
		    	utenteId = assegnatario.getUtenteId();
		    	if (!RegistroDelegate.getInstance().isAbilitatoRegistro(ute.getRegistroInUso(),ufficioId, utenteId)) {
		    		if (utenteId > 0) {
		    			errors.add("utente_non_abilitato",new ActionMessage("utente_non_abilitato",assegnatario.getNomeUtente()," (rimuoverlo dagli assegnatari)"));
		    		} else {
		    			errors.add("ufficio_non_abilitato",new ActionMessage("ufficio_non_abilitato",assegnatario.getNomeUfficio()," (rimuoverlo dagli assegnatari)"));
		    		}
		    	}
		    }
		    if (!errors.isEmpty()) {
		        saveErrors(request, errors);
		        saveToken(request);
		        return mapping.findForward("input");
		    }
		}
		ProtocolloIngresso protocolloIngresso = null;

		ProtocolloVO protocollo = null;
		ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
		if (pForm.getProtocolloId() == 0) {
		    protocolloIngresso = ProtocolloBO.getDefaultProtocolloIngresso(ute);
		    AggiornaProtocolloIngressoModel.aggiorna(pForm, protocolloIngresso, ute);
		    protocollo = delegate.registraProtocollo(protocolloIngresso, ute, false);
		} else {
			pForm.setMotivazione(aggiornaProtocolloFormMotivazione(pForm, session));
		    protocolloIngresso = (ProtocolloIngresso) session.getAttribute("protocolloIngresso");
		    AggiornaProtocolloIngressoModel.aggiorna(pForm, protocolloIngresso, ute);
		    protocollo = delegate.aggiornaProtocolloIngresso(protocolloIngresso, ute);
		}
		if (protocollo == null) {
		    errors.add("errore_nel_salvataggio", new ActionMessage("errore_nel_salvataggio", "", ""));
		    saveErrors(request, errors);
		} else if (protocollo.getReturnValue() == ReturnValues.SAVED) {
		    request.setAttribute("protocolloId", protocollo.getId());
		    caricaProtocollo(request, pForm, CaricaProtocollo.PROTOCOLLO_INGRESSO);
		    if (session.getAttribute("PROTOCOLLI_EMERGENZA") != null) {
		        pForm.setNumeroProtocolliRegistroEmergenza(((Integer) session.getAttribute("PROTOCOLLI_EMERGENZA")).intValue());
		    }
		    return mapping.findForward("visualizzaProtocollo");

		} else if (protocollo.getReturnValue() == ReturnValues.OLD_VERSION) {
		    errors.add("generale",new ActionMessage("versione_vecchia"));
		    saveErrors(request, errors);
		    request.setAttribute("protocolloId", protocollo.getId());
		} else {
		    errors.add("generale", new ActionMessage("errore_nel_salvataggio"));
		    saveErrors(request, errors);
		}
		resetToken(request);
		pForm.setOggettoProcedimento("");
		return mapping.findForward("input");
	}
	
	

    private String aggiornaProtocolloFormMotivazione(
			ProtocolloIngressoForm form, HttpSession session) {
		ProtocolloForm pif =  (ProtocolloForm)session.getAttribute("oldProtocollo");
		String motivazione = "";
/*		if(pif.getCognomeMittente() != form.getCognomeMittente()) {
			motivazione = "Campi modificati: cognome mittente, ";
		}
		if(pif.getOggettoGenerico() != form.getOggettoGenerico()) {
			if("".equals(motivazione)) {
				motivazione = "Campi modificati: ";
			}
			motivazione += "oggetto generico, ";
		}*/
		motivazione = form.getDifferences(pif, form.getMotivazione());
		return motivazione;
	}


	private void updateAssegnatariCompetenti(ProtocolloIngressoForm form) {
		List<String> assCompetenti = new ArrayList<String>();
		for (Iterator i = form.getAssegnatari().iterator(); i.hasNext();) {
    		AssegnatarioView ass = (AssegnatarioView) i.next();
            if(ass.isCompetente()){
            	assCompetenti.add(ass.getKey());
            }
          
        }
		String[] assCompArray = new String[assCompetenti.size()];
		int index = 0;
		for(String assString : assCompetenti){
			assCompArray[index] = assString;
			index++;
		}
		form.setAssegnatariCompetenti(assCompArray);
		
	}

	private void aggiungiMittente(ProtocolloIngressoForm form) {
		List<SoggettoVO> mittenti = form.getMittenti();
		if(mittenti.size() == 0){
			form.setMittente(form.getMultiMittenteCorrente());
		}
		
		form.getMittenti().add(form.getMultiMittenteCorrente());
		form.setMultiMittenteCorrente(new SoggettoVO("M"));
		form.setMittente(new SoggettoVO("M"));
	}

}