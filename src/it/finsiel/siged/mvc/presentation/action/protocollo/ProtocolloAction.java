package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.RegistroBO;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AllaccioView;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.NavBarElement;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.mvc.presentation.action.protocollo.helper.CaricaProtocollo;
import it.flosslab.mvc.presentation.action.protocollo.helper.file.ProtocolloFileUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action </strong> to create a new E-Photo User.
 * 
 * @author Almaviva sud.
 * 
 */

public abstract class ProtocolloAction extends Action {
    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ProtocolloAction.class.getName());
    protected String type;
    /**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public final static String FLAG_PROTOCOLLO_ANNULLATO = "C";

    private void allacciaProtocollo(ProtocolloForm form, HttpSession session,
            ActionMessages errors) {
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        Collection c = delegate.getProtocolliAllacciabili(utente, Integer.parseInt(form.getAllaccioNumProtocollo()), 
        		Integer.parseInt(form.getAllaccioNumProtocollo()), Integer.parseInt(form.getAllaccioAnnoProtocollo()), form.getProtocolloId());
        if (c != null && c.size() > 0) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                AllaccioView allaccio = (AllaccioView) it.next();
                AllaccioVO allaccioVO = new AllaccioVO();
                if (allaccio != null && allaccio.getNumProtAllacciato() > 0) {
                    allaccioVO.setProtocolloAllacciatoId(allaccio.getProtAllacciatoId());
                    allaccioVO.setAllaccioDescrizione(form.getAllaccioNumProtocollo()
                            + "/"
                            + form.getAllaccioAnnoProtocollo()
                            + " ("
                            + allaccio.getTipoProtocollo() + ")");
                    form.allacciaProtocollo(allaccioVO);
                }
            }
        } else {
            errors.add("allacci", new ActionMessage(
                    "protocollo_non_allacciabile"));
        }
    }

    public ActionForward downloadDocumento(ActionMapping mapping,
            DocumentoVO doc, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ActionMessages errors = ProtocolloFileUtility.downloadFile(doc, response);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("erroreDownload");
        }
        ActionForward actionForward = new ActionForward();
        actionForward.setName("none");
        return actionForward;
    }

    
    // --------------------------------------------------------- Public Methods
    // private void impostaTitolario(ProtocolloForm form, int ufficioId,
    // int titolarioId) {
    // TitolarioDelegate td = TitolarioDelegate.getInstance();
    // form.setTitolario(td.getTitolario(ufficioId, titolarioId));
    // form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId));
    // }

    protected void caricaProtocollo(HttpServletRequest request, ProtocolloForm form, String type){
    	if(type == null || type.equals(CaricaProtocollo.PROTOCOLLO_INGRESSO)){
    		CaricaProtocollo.caricaProtocolloIngresso(request, form);
    	}else if(type.equals(CaricaProtocollo.PROTOCOLLO_USCITA)){
    		CaricaProtocollo.caricaProtocolloUscita(request, form);
    	}
    }

    protected abstract void assegnaAdUfficio(ProtocolloForm form, int ufficioId);

    protected abstract void assegnaAdUtente(ProtocolloForm form);

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
        HttpSession session = request.getSession(true);

        /* contenente i nostri dati provenienti dall'html form */
        ProtocolloForm pForm = (ProtocolloForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        // boolean ufficioCompleto =
        // (utente.getUfficioVOInUso().getTipo().equals(
        // UfficioVO.UFFICIO_CENTRALE) || utente.getUfficioVOInUso()
        // .getTipo().equals(UfficioVO.UFFICIO_SEMICENTRALE));
        boolean ufficioCompleto = true;

        caricaProtocollo(request, pForm, type);

        // ricarico i dati dinamici
        if (pForm.getProtocolloId() == 0
                || request.getParameter("annullaAction") != null
                || request.getParameter("btnNuovoProtocollo") != null
                || request.getParameter("btnCopiaProtocollo") != null) {
            RegistroVO registro = (RegistroVO) utente.getRegistroVOInUso();
            pForm.setDataRegistrazione(DateUtil.formattaData(RegistroBO
                    .getDataAperturaRegistro(registro).getTime()));

        }

        if (pForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficioUtentiAbilitati(utente, pForm, ufficioCompleto);
            pForm.setUtenteAbilitatoSuUfficio(utente.isUtenteAbilitatoSuUfficio(pForm.getUfficioCorrenteId()));
        }

        if (request.getParameter("downloadAllegatoId") != null) {
            int docId = NumberUtil.getInt((String) request.getParameter("downloadAllegatoId"));
            DocumentoVO doc = pForm.getDocumentoAllegato(String.valueOf(docId));
            return downloadDocumento(mapping, doc, request, response);

        } else if (request.getParameter("downloadDocumentoPrincipale") != null) {
            DocumentoVO doc = pForm.getDocumentoPrincipale();
            return downloadDocumento(mapping, doc, request, response);

        } else if (request.getParameter("btnNuovoProtocollo") != null) {
            pForm.inizializzaForm();
            session.removeAttribute("tornaDocumento");
            pForm.setAooId(utente.getRegistroVOInUso().getAooId());
            pForm.setTipoDocumentoId(ProtocolloDelegate.getInstance().getDocumentoDefault(utente.getRegistroVOInUso().getAooId()));
            pForm.setVersioneDefault(true);
            saveToken(request);
            return mapping.findForward("nuovoProtocollo");

        } else if (request.getParameter("btnCopiaProtocollo") != null) {

            pForm.inizializzaFormToCopyProtocollo();
            saveToken(request);
            return mapping.findForward("nuovoProtocollo");

        } else if (request.getParameter("annullaAction") != null) {
            // l'utente ha premuto il pulsante "annulla"
            // session.removeAttribute(mapping.getAttribute());
            if ("true".equals(request.getParameter("annullaAction")) || pForm.getProtocolloId() == 0) {
                newProtocollo(session, pForm, utente);
            } else {
                request.setAttribute("protocolloId", new Integer(pForm.getProtocolloId()));
                caricaProtocollo(request, pForm, type);
            }
            saveToken(request);
            return mapping.findForward("input");

        } else if (request.getParameter("btnModificaProtocollo") != null  && request.getAttribute("protocolloId") == null) {
            request.setAttribute("protocolloId", new Integer(pForm.getProtocolloId()));
            saveToken(request);
            // Salva in sessione il form protocollo per confrontarlo con quello nuovo
            ProtocolloForm oldProtocollo = pForm.cloneForm();
            //oldProtocollo.setCognomeMittente(pForm.getCognomeMittente());
            //oldProtocollo.setOggettoGenerico(pForm.getOggettoGenerico());
            request.getSession().setAttribute("oldProtocollo", oldProtocollo);
            return mapping.findForward("modificaProtocollo");

        } else if (request.getParameter("allegaDocumentoAction") != null) {
            // leggo il file e lo salvo nella dir temporanea
        	ProtocolloFileUtility.uploadFile(pForm, request, errors);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            }
            // forward alla pagina di inserimento dei dati
            return mapping.findForward("input");

        } else if (request.getParameter("allegaDocumentoPrincipaleAction") != null) {
        	ProtocolloFileUtility.uploadDocumentoPrincipale(pForm, request, errors);
            if (!errors.isEmpty())
                saveErrors(request, errors);
            return mapping.findForward("input");

        } else if (request.getParameter("allacciaProtocolloAction") != null) {
            allacciaProtocollo(pForm, session, errors);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            }
            return mapping.findForward("input");

        } else if (request.getParameter("btnAllacci") != null) {
            ArrayList navBar = (ArrayList) session.getAttribute(Constants.NAV_BAR);
            NavBarElement elem = new NavBarElement();
            elem.setValue("Allacci");
            elem.setTitle("Cerca protocolli allacciabili");
            navBar.add(elem);
            return mapping.findForward("cercaAllacci");

        } else if (request.getParameter("rimuoviAllegatiAction") != null) {
            String[] allegati = pForm.getAllegatiSelezionatiId();
            if (allegati != null) {
                for (int i = 0; i < allegati.length; i++) {
                    pForm.rimuoviAllegato(allegati[i]);
                    allegati[i] = null;
                }
            }
            return mapping.findForward("input");

        } else if (request.getParameter("rimuoviDocumentoPrincipaleAction") != null) {
            pForm.rimuoviDocumentoPrincipale();
            return mapping.findForward("input");

        } else if (request.getParameter("rimuoviAllacciAction") != null) {
            removeAllacci(pForm);
            return mapping.findForward("input");

        } else if (request.getParameter("impostaUfficioAction") != null) {
            pForm.setUfficioCorrenteId(pForm.getUfficioSelezionatoId());
            pForm.setUtenteAbilitatoSuUfficio(utente.getUfficioVOInUso().getTipo().equals(UfficioVO.UFFICIO_SEMICENTRALE)
            									|| utente.isUtenteAbilitatoSuUfficio(pForm.getUfficioSelezionatoId()));
            AlberoUfficiBO.impostaUfficioUtentiAbilitati(utente, pForm, ufficioCompleto);
            return mapping.findForward("input");

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            pForm.setUfficioCorrenteId(pForm.getUfficioCorrente().getParentId());
            pForm.setUtenteAbilitatoSuUfficio(utente.getUfficioVOInUso().getTipo().equals(UfficioVO.UFFICIO_SEMICENTRALE)
            									|| utente.isUtenteAbilitatoSuUfficio(pForm.getUfficioCorrente().getParentId()));
            
            AlberoUfficiBO.impostaUfficioUtentiAbilitati(utente, pForm, ufficioCompleto);

        } else if (request.getParameter("assegnaUfficioCorrenteAction") != null) {
            assegnaAdUfficio(pForm, pForm.getUfficioCorrenteId());
            return mapping.findForward("input");

        } else if (request.getParameter("assegnaUfficioSelezionatoAction") != null) {
            assegnaAdUfficio(pForm, pForm.getUfficioSelezionatoId());
            return mapping.findForward("input");

        } else if (request.getParameter("assegnaUtenteAction") != null) {
            assegnaAdUtente(pForm);
            return mapping.findForward("input");

        } else if (request.getParameter("dettaglioAction") != null) {
            return mapping.findForward("dettaglio_documento");

        } else if (request.getParameter("btnAnnullaProtocollo") != null) {
            pForm.setNotaAnnullamento(null);
            pForm.setProvvedimentoAnnullamento(null);
            return mapping.findForward("annullaProtocollo");
        } else if (request.getParameter("btnConfermaAnnullamento") != null) {
            errors = pForm.validateAnnullamentoProtocollo(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return (mapping.findForward("annullaProtocollo"));
            }
            ProtocolloVO protocolloVO = ProtocolloDelegate.getInstance().getProtocolloById(pForm.getProtocolloId());
            if (protocolloVO != null) {
                protocolloVO.setStatoProtocollo(FLAG_PROTOCOLLO_ANNULLATO);
                protocolloVO.setNotaAnnullamento(pForm.getNotaAnnullamento());
                protocolloVO.setProvvedimentoAnnullamento(pForm.getProvvedimentoAnnullamento());
                if (ProtocolloDelegate.getInstance().annullaProtocollo(protocolloVO, utente) == ReturnValues.SAVED) {
                    pForm.setModificabile(false);
                    pForm.setDataAnnullamento(DateUtil.formattaData((new Date(System.currentTimeMillis())).getTime()));
                    pForm.setStato(FLAG_PROTOCOLLO_ANNULLATO);
                }
                request.setAttribute("protocolloId", protocolloVO.getId());
                return (mapping.findForward("input"));
            }

        } else if (request.getParameter("ripetiAction") != null) {
            // l'utente ha premuto il pulsante "ripeti dati"
            // session.removeAttribute(mapping.getAttribute());
            if ("true".equals(request.getParameter("ripetiAction")) || pForm.getProtocolloId() == 0) {
                pForm.inizializzaRipetiForm();
                session.removeAttribute("tornaDocumento");
                pForm.setAooId(utente.getRegistroVOInUso().getAooId());
                if (pForm.getTipoDocumentoId() == 0) {
                    pForm.setTipoDocumentoId(ProtocolloDelegate.getInstance().getDocumentoDefault(utente.getRegistroVOInUso().getAooId()));
                }
                pForm.setVersioneDefault(true);
            } else {
                request.setAttribute("protocolloId", new Integer(pForm.getProtocolloId()));
                caricaProtocollo(request, pForm, type);
            }
            saveToken(request);
            return mapping.findForward("input");

        } else if (request.getParameter("btnStoriaProtocollo") != null) {
            request.setAttribute("protocolloId", new Integer(pForm
                    .getProtocolloId()));
            return mapping.findForward("storiaProtocollo");

        } else if (request.getParameter("btnCercaFascicoli") != null) {
            String nomeFascicolo = pForm.getCercaFascicoloNome();
            pForm.setCercaFascicoloNome("");
            request.setAttribute("cercaFascicoliDaProtocollo", nomeFascicolo);
            session.setAttribute("tornaProtocollo", Boolean.TRUE);
            session.removeAttribute("tornaFaldone");
            session.removeAttribute("provenienza");
            session.removeAttribute("cercaFascicoliDaFaldoni");
            session.removeAttribute("tornaFaldone");
            session.removeAttribute("FascicoliDaFaldoni");
            session.removeAttribute("btnCercaFascicoli");
            session.removeAttribute("tornaProcedimento");
            return mapping.findForward("cercaFascicolo");

        } else if (request.getParameter("btnCercaProcedimenti") != null) {
            String oggettoProcedimento = pForm.getOggettoProcedimento();
            pForm.setOggettoProcedimento("");
            request.setAttribute("cercaProcedimentiDaProtocollo", oggettoProcedimento);
            session.setAttribute("procedimentiDaProtocollo", Boolean.TRUE);
            session.setAttribute("indietroProcedimentiDaProtocollo",Boolean.TRUE);
            session.setAttribute("risultatiProcedimentiDaProtocollo",Boolean.TRUE);
            session.removeAttribute("tornaProtocollo");
            session.removeAttribute("ricercaSemplice");
            session.removeAttribute("tornaFaldone");
            session.removeAttribute("provenienza");
            session.removeAttribute("btnCercaProcedimentiDaFaldoni");
            session.removeAttribute("tornaFascicolo");
            return mapping.findForward("cercaProcedimento");

        } else if (request.getParameter("rimuoviFascicoli") != null) {
            if (pForm.getFascicoloSelezionatoId() != null) {
                String[] fascicoli = pForm.getFascicoloSelezionatoId();
                for (int i = 0; i < fascicoli.length; i++) {
                    if (fascicoli[i] != null) {
                        pForm.rimuoviFascicolo(Integer.parseInt(fascicoli[i]));
                        fascicoli[i] = null;
                    }
                }
            }
            return mapping.findForward("input");

        } else if (request.getParameter("rimuoviProcedimenti") != null) {
            String[] procedimenti = pForm.getProcedimentoSelezionatoId();
            if (procedimenti != null) {
                for (int i = 0; i < procedimenti.length; i++) {
                    if (procedimenti[i] != null) {
                        pForm.rimuoviProcedimento(Integer.parseInt(procedimenti[i]));
                        procedimenti[i] = null;
                    }
                }
            }
            return mapping.findForward("input");

        } else if (request.getParameter("btnNuovoFascicolo") != null) {
            session.setAttribute("tornaProtocollo", Boolean.TRUE);
            return mapping.findForward("nuovoFascicolo");

        } else if (request.getParameter("btnRimuoviFascicolo") != null) {
            return mapping.findForward("rimuoviFascicolo");

        } else if (request.getParameter("btnNuovoProcedimento") != null) {
            creaNuovoProcedimento(request, session, pForm);
            return mapping.findForward("nuovoProcedimento");
        } else if (request.getParameter("fascicoloId") != null) {
            request.setAttribute("fascicoloId", new Integer(request.getParameter("fascicoloId")));
            return (mapping.findForward("fascicolo"));
        } else if (request.getParameter("btnStampaEtichettaProtocollo") != null) {
            request.setAttribute("protocolloId", new Integer(pForm.getProtocolloId()));
            return (mapping.findForward("stampaEtichetta"));
        }
        
        // demanda alle sottoclassi
        return null;
    }

	/**
	 * @param session
	 * @param pForm
	 * @param utente
	 */
	private void newProtocollo(HttpSession session, ProtocolloForm pForm,Utente utente) {
		pForm.inizializzaForm();
		session.removeAttribute("tornaDocumento");
		pForm.setAooId(utente.getRegistroVOInUso().getAooId());
		pForm.setTipoDocumentoId(ProtocolloDelegate.getInstance().getDocumentoDefault(utente.getRegistroVOInUso().getAooId()));
		pForm.setVersioneDefault(true);
		if (session.getAttribute("PROTOCOLLI_EMERGENZA") != null) {
		    pForm.setNumeroProtocolliRegistroEmergenza(((Integer) session.getAttribute("PROTOCOLLI_EMERGENZA")).intValue());

		} else {
		    pForm.setNumeroProtocolliRegistroEmergenza(0);
		}
	}

	/**
	 * @param request
	 * @param session
	 * @param pForm
	 */
	private void creaNuovoProcedimento(HttpServletRequest request,
			HttpSession session, ProtocolloForm pForm) {
		ProcedimentoVO newP = new ProcedimentoVO();
		if (ProtocolloVO.FLAG_TIPO_PROTOCOLLO_USCITA.equals(pForm
		        .getFlagTipo())) {
		    newP.setUfficioId(pForm.getUfficioProtocollatoreId());
		} else {
		    Collection asse = ((ProtocolloIngressoForm) pForm)
		            .getAssegnatari();
		    Iterator it = asse.iterator();
		    while (it.hasNext()) {
		        AssegnatarioView av = (AssegnatarioView) it.next();
		        if (av.isCompetente()) {
		            newP.setUfficioId(av.getUfficioId());
		            break;
		        }
		    }
		}

		if (pForm.getTitolario() != null && pForm.getTitolario().getId() != null){
			newP.setTitolarioId(pForm.getTitolario().getId().intValue());
		}
		newP.setOggetto(pForm.getOggetto());
		newP.setNumeroProtovollo(DateUtil.getYear(DateUtil.toDate(pForm.getDataRegistrazione())) + StringUtil.formattaNumeroProtocollo(pForm.getNumeroProtocollo(), 7));
		newP.setProtocolloId(pForm.getProtocolloId());
		request.setAttribute("procedimentoPrecaricato", newP);
		session.setAttribute("tornaProtocollo", Boolean.TRUE);
	}

	/**
	 * @param pForm
	 */
	private void removeAllacci(ProtocolloForm pForm) {
		String[] allacci = pForm.getAllaccioSelezionatoId();
		if (allacci != null) {
		    for (int i = 0; i < allacci.length; i++) {
		        if (allacci[i] != null) {
		            pForm.rimuoviAllaccio(Integer.parseInt(allacci[i]));
		            allacci[i] = null;
		        }
		    }
		}
	}
}