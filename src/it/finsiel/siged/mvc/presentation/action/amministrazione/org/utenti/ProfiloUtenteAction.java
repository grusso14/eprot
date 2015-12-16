package it.finsiel.siged.mvc.presentation.action.amministrazione.org.utenti;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.MenuBO;
import it.finsiel.siged.mvc.bo.OrganizzazioneBO;
import it.finsiel.siged.mvc.bo.RegistroBO;
import it.finsiel.siged.mvc.business.AmministrazioneDelegate;
import it.finsiel.siged.mvc.business.OrganizzazioneDelegate;
import it.finsiel.siged.mvc.business.RegistroDelegate;
import it.finsiel.siged.mvc.business.UfficioDelegate;
import it.finsiel.siged.mvc.business.UtenteDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.org.utenti.ProfiloUtenteForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.lookup.ProfiloVO;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
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
 * Implementation of <strong>Action </strong> to create a new E-Photo Utente.
 * 
 * @author Almaviva sud.
 * 
 */

public class ProfiloUtenteAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger
            .getLogger(ProfiloUtenteAction.class.getName());

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

        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession();
        ProfiloUtenteForm profiloForm = (ProfiloUtenteForm) form;
        UtenteVO utenteVO = new UtenteVO();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

        if (form == null) {
            form = new ProfiloUtenteForm();
            request.setAttribute(mapping.getAttribute(), form);
        }
        if (profiloForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, profiloForm, true);
        }

        String parId = request.getParameter("parId");
        if (parId != null) {
            if (NumberUtil.isInteger(parId)) {
                int id = NumberUtil.getInt(parId);
                utenteVO = UtenteDelegate.getInstance().getUtente(id);
                if (utenteVO != null
                        && utenteVO.getReturnValue() == ReturnValues.FOUND) {
                    caricaDatiNelForm(profiloForm, utenteVO, utente);
                } else {
                    logger.warn("Utente non trovato. id=" + parId);
                }
                request.setAttribute(mapping.getAttribute(), profiloForm);
                return mapping.findForward("input");
            } else {
                logger.warn("Utente non trovato. id=" + parId);
            }
        } else if (request.getParameter("btnSalva") != null) {
            // validazione dei dati
            errors = profiloForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("input");
            }

            try {
                if (UtenteDelegate.getInstance().isUsernameUsed(
                        profiloForm.getUserName(), profiloForm.getId())) {
                    errors.add("userName", new ActionMessage(
                            "username_gia_utilizzato"));
                    saveErrors(request, errors);
                    return mapping.findForward("input");
                }
            } catch (Exception e) {
                logger.error("", e);
                errors.add("generale", new ActionMessage(
                        "errore_nel_salvataggio"));
                saveErrors(request, errors);
                return mapping.findForward("input");
            }
            // fine validazioni

            caricaDatiNelVO(utenteVO, profiloForm, utente);
            String[] aRegistri = profiloForm.getRegistriSelezionatiId();
            if (profiloForm.getId() == 0) {
                // nuovo
                utenteVO.setRowCreatedUser(utente.getValueObject()
                        .getUsername());

                Collection cMenu = new ArrayList();
                cMenu = AmministrazioneDelegate.getInstance().getMenuByProfilo(
                        profiloForm.getProfiloId());
                String[] aMenu = new String[cMenu.size()];
                cMenu.toArray(aMenu);

                Collection cUffici = new ArrayList(profiloForm.getUffici());
                String[] aUffici = new String[cUffici.size()];
                int i = 0;
                for (Iterator iter = cUffici.iterator(); iter.hasNext();) {
                    AssegnatarioView element = (AssegnatarioView) iter.next();
                    aUffici[i++] = element.getKey();
                }

                if (aUffici != null && aUffici.length > 0) {
                    utenteVO = UtenteDelegate.getInstance().newUtenteVO(
                            utenteVO, aUffici, impostaFunzioniMenu(aMenu),
                            aRegistri, utente);
                    if (utenteVO.getReturnValue() != ReturnValues.SAVED) {
                        errors.add("generale", new ActionMessage(
                                "errore_nel_salvataggio"));
                    }
                } else {
                    errors.add("generale", new ActionMessage(
                            "selezione.obbligatoria",
                            "almeno un Ufficio di competenza", ""));
                }
            } else {
                // update
                utenteVO.setRowUpdatedUser(utente.getValueObject()
                        .getUsername());
                utenteVO = UtenteDelegate.getInstance().updateUtenteVO(
                        utenteVO, aRegistri, utente);
                if (utenteVO.getReturnValue() != ReturnValues.SAVED) {
                    errors.add("generale", new ActionMessage(
                            "errore_nel_salvataggio"));
                }
            }

            if (utenteVO.getReturnValue() == ReturnValues.SAVED) {
                aggiornaUtenteOrganizzazione(utenteVO);
                return mapping.findForward("success");
            }

            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("input");
            }
        } else if (request.getParameter("impostaUfficioAction") != null) {
            profiloForm.setUfficioCorrenteId(profiloForm
                    .getUfficioSelezionatoId());
            AlberoUfficiBO.impostaUfficio(utente, profiloForm, true);

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            profiloForm.setUfficioCorrenteId(profiloForm.getUfficioCorrente()
                    .getParentId());
            AlberoUfficiBO.impostaUfficio(utente, profiloForm, true);

        } else if (request.getParameter("assegnaUfficioCorrenteAction") != null) {
            assegnaAdUfficio(profiloForm, profiloForm.getUfficioCorrenteId());
            return mapping.findForward("input");
        } else if (request.getParameter("rimuoviAssegnatariAction") != null) {
            rimuoviUffici(profiloForm);

        } else if (request.getParameter("assegnaUfficioSelezionatoAction") != null) {
            assegnaAdUfficio(profiloForm, profiloForm.getUfficioSelezionatoId());
            return mapping.findForward("input");
        } else if (request.getParameter("btnIndietro") != null) {
            profiloForm.inizializzaForm();
            return (mapping.findForward("indietro"));

        } else if (request.getParameter("btnAnnulla") != null) {
            profiloForm.inizializzaForm();
            profiloForm.setProfiliMenu(AmministrazioneDelegate.getInstance()
                    .getProfili(utente.getRegistroVOInUso().getAooId()));
            profiloForm
                    .setRegistri(RegistroDelegate.getInstance()
                            .getRegistriByAooId(
                                    utente.getRegistroVOInUso().getAooId()));

            AlberoUfficiBO.impostaUfficio(utente, profiloForm, true);

        } else if (request.getParameter("btnPermessiUffici") != null) {
            Organizzazione org = Organizzazione.getInstance();
            int utenteId = profiloForm.getId();
            int ufficioCorrenteId;
            if (request.getParameter("ufficioId") != null) {
                ufficioCorrenteId = new Integer(request
                        .getParameter("ufficioId")).intValue();
            } else {
                ufficioCorrenteId = profiloForm.getUfficioCorrenteId();
            }
            profiloForm.setUfficioCorrenteId(ufficioCorrenteId);
            profiloForm.setUfficioCorrentePath(org
                    .getUfficio(ufficioCorrenteId).getPath());
            profiloForm.setFunzioniMenuSelezionate(UtenteDelegate.getInstance()
                    .getFunzioniByUfficioUtente(utenteId, ufficioCorrenteId));
            Collection menuLista = new ArrayList();
            Menu rootMenu = (Menu) getServlet().getServletContext()
                    .getAttribute(Constants.MENU_ROOT);
            MenuBO.aggiungiMenu("", rootMenu, menuLista);

            profiloForm.setFunzioniMenu(menuLista);
            return (mapping.findForward("permessi"));

        } else if (request.getParameter("btnConfermaPermessi") != null) {
            int utenteId = profiloForm.getId();
            int ufficioCorrenteId = profiloForm.getUfficioCorrenteId();
            String[] aFunzioni = profiloForm.getFunzioniMenuSelezionate();
            UtenteVO vo = UtenteDelegate.getInstance().getUtente(utenteId);

            int retVal = UtenteDelegate.getInstance().permessiUtente(utenteId,
                    ufficioCorrenteId, impostaFunzioniMenu(aFunzioni), utente,
                    vo, true);
            if (retVal == ReturnValues.SAVED) {
                profiloForm.setUfficiAssegnati(UtenteDelegate.getInstance()
                        .getUfficiByUtente(utenteId));
                ActionMessages msg = new ActionMessages();
                msg.add("salvato", new ActionMessage("permessi.salvati"));
                saveMessages(request, msg);
            } else {
                errors.add("generale", new ActionMessage(
                        "errore_nel_salvataggio"));
                saveErrors(request, errors);
                return mapping.findForward("input");
            }
        } else if (request.getParameter("btnAbilitaTutto") != null ||
        		request.getParameter("assegnaAmministrazione") != null ||
        		request.getParameter("assegnaDocumentale") != null ||
        		request.getParameter("assegnaReport") != null ||
        		request.getParameter("assegnaProtocollazione") != null) {
            int utenteId = profiloForm.getId();
            UtenteVO vo = UtenteDelegate.getInstance().getUtente(utenteId);
            int ufficioCorrenteId = profiloForm.getUfficioCorrenteId();
            Collection funzioni = new ArrayList();
            if(request.getParameter("assegnaAmministrazione") != null){
            	funzioni = AmministrazioneDelegate.getInstance().getFunzioniAmministrazioneMenu();
                
            }else if(request.getParameter("assegnaDocumentale") != null){
            	funzioni = AmministrazioneDelegate.getInstance().getFunzioniDocumentaleMenu();
                
            }else if(request.getParameter("assegnaReport") != null){
            	funzioni = AmministrazioneDelegate.getInstance().getFunzioniReportMenu();
                
            }else if(request.getParameter("assegnaProtocollazione") != null){
            	funzioni = AmministrazioneDelegate.getInstance().getFunzioniProtocollazioneMenu();
                
            }else{
            	funzioni = AmministrazioneDelegate.getInstance().getFunzioniMenu();
            }
            
            int i = 0;
            String[] aFunzioni = new String[funzioni.size()];
            for (Iterator iter = funzioni.iterator(); iter.hasNext();) {
                MenuVO element = (MenuVO) iter.next();
                aFunzioni[i++] = (element.getId()).toString();
            }
            Organizzazione org = Organizzazione.getInstance();
            if (request.getParameter("ufficioId") != null) {
                ufficioCorrenteId = new Integer(request
                        .getParameter("ufficioId")).intValue();
            } else {
                ufficioCorrenteId = profiloForm.getUfficioCorrenteId();
            }
            profiloForm.setUfficioCorrenteId(ufficioCorrenteId);
            profiloForm.setUfficioCorrentePath(org
                    .getUfficio(ufficioCorrenteId).getPath());
            profiloForm.setFunzioniMenuSelezionate(UtenteDelegate.getInstance()
                    .getFunzioniByUfficioUtente(utenteId, ufficioCorrenteId));
            Collection menuLista = new ArrayList();
            addPermessiToForm(profiloForm, funzioni);
            Menu rootMenu = (Menu) getServlet().getServletContext()
                    .getAttribute(Constants.MENU_ROOT);
            MenuBO.aggiungiMenu("", rootMenu, menuLista);
            aggiornaForm(profiloForm, funzioni);
            
            profiloForm.setUfficiAssegnati(UtenteDelegate.getInstance().getUfficiByUtente(utenteId));
            ActionMessages msg = new ActionMessages();
            
            return mapping.findForward("permessi");
            
        } else if (request.getParameter("btnCancellaTutto") != null) {
            int utenteId = profiloForm.getId();
            int ufficioCorrenteId = profiloForm.getUfficioCorrenteId();
            int retVal = UtenteDelegate.getInstance().cancellaPermessiUtente(
                    utenteId, ufficioCorrenteId, utente);
            if (retVal != ReturnValues.SAVED) {
                errors.add("generale", new ActionMessage(
                        "errore_nel_salvataggio"));
                saveErrors(request, errors);
                return mapping.findForward("input");
            } else {
                profiloForm.setUfficiAssegnati(UtenteDelegate.getInstance()
                        .getUfficiByUtente(utenteId));
            }
        }

        return (mapping.findForward("input"));

    }

    private void addPermessiToForm(ProfiloUtenteForm profiloForm,
			Collection funzioni) {
		Set<String> permessiSet = new HashSet<String>();
    	ArrayList<String> toAdd = new ArrayList<String>();
    	for (Iterator iter = funzioni.iterator(); iter.hasNext();) {
            MenuVO element = (MenuVO) iter.next();
            toAdd.add((element.getId()).toString());
        }
    	for(String elemento : profiloForm.getFunzioniMenuSelezionate()){
    		permessiSet.add(elemento);
    	}
    	permessiSet.addAll(toAdd);
    	String[] permessiNew = new String[permessiSet.size()];
		
    	permessiSet.toArray(permessiNew);
    	profiloForm.setFunzioniMenuSelezionate(permessiNew);
	
		
	}

	private void aggiornaForm(ProfiloUtenteForm profiloForm, Collection funzioni) {
    	Set<String> permessiSet = new HashSet<String>();
    	ArrayList<String> toAdd = new ArrayList<String>();
    	for (Iterator iter = funzioni.iterator(); iter.hasNext();) {
            MenuVO element = (MenuVO) iter.next();
            toAdd.add((element.getId()).toString());
        }
    	for(Object elemento : profiloForm.getFunzioniMenuSelezionate()){
    		permessiSet.add((String)elemento);
    	}
    	permessiSet.addAll(toAdd);
    	profiloForm.setFunzioniMenu(permessiSet);
		
	}

	public void caricaDatiNelVO(UtenteVO vo, ProfiloUtenteForm form,
            Utente utente) {
        vo.setId(form.getId());
        vo.setUsername(form.getUserName());
        vo.setPassword(form.getPassWord());
        vo.setCognome(form.getCognome());
        vo.setNome(form.getNome());
        vo.setCodiceFiscale(form.getCodiceFiscale());
        vo.setEmailAddress(form.getEmailAddress());
        vo.setMatricola(form.getMatricola());
        vo.setAbilitato(form.getAbilitato());
        vo.setDataFineAttivita(DateUtil.toDate(form.getDataFineAttivita()));
        vo.setAooId(utente.getValueObject().getAooId());
    }

    public void caricaDatiNelForm(ProfiloUtenteForm form, UtenteVO vo,
            Utente utente) {
        form.setUserName(vo.getUsername());
        form.setId(vo.getId().intValue());
        form.setPassWord(vo.getPassword());
        form.setConfermaPassword(vo.getPassword());
        form.setCognome(vo.getCognome());
        form.setNome(vo.getNome());
        form.setCodiceFiscale(vo.getCodiceFiscale());
        form.setEmailAddress(vo.getEmailAddress());
        form.setMatricola(vo.getMatricola());
        form.setAbilitato(vo.isAbilitato());
        if (vo.getDataFineAttivita() != null)
            form.setDataFineAttivita(DateUtil.formattaData(vo
                    .getDataFineAttivita().getTime()));
        String[] permessiRegistri = UtenteDelegate.getInstance()
                .getPermessiRegistri(vo.getId().intValue());
        if (permessiRegistri != null && permessiRegistri.length > 0) {
            form.setRegistriSelezionatiId(UtenteDelegate.getInstance()
                    .getPermessiRegistri(vo.getId().intValue()));
        }
        form.setProfiliMenu(AmministrazioneDelegate.getInstance().getProfili(
                utente.getRegistroVOInUso().getAooId()));
        form.setUfficiAssegnati(UtenteDelegate.getInstance().getUfficiByUtente(
                form.getId()));

        form.setRegistri(RegistroDelegate.getInstance().getRegistriByAooId(
                utente.getRegistroVOInUso().getAooId()));

    }

    private String[] impostaFunzioniMenu(String[] funzioniSelezionate) {
        Organizzazione org = Organizzazione.getInstance();
        HashMap mappaFunzioni = new HashMap();
        if (funzioniSelezionate != null) {
            for (int i = 0; i < funzioniSelezionate.length; i++) {
                Integer funzione = new Integer(funzioniSelezionate[i]);
                if (funzione != null) {
                    Menu menu = org.getMenu(funzione.intValue());
                    while (menu.getValueObject().getId().intValue() > 0) {
                        mappaFunzioni.put(menu.getValueObject().getId(), menu
                                .getValueObject().getId().toString());
                        menu = menu.getParent();
                    }
                }
            }
        }
        Collection cMenu = new ArrayList();
        cMenu = mappaFunzioni.values();
        String[] aMenu = new String[cMenu.size()];
        cMenu.toArray(aMenu);
        return aMenu;
    }

    private void rimuoviUffici(ProfiloUtenteForm form) {
        String[] uffici = form.getUfficiSelezionatiId();
        if (uffici != null) {
            for (int i = 0; i < uffici.length; i++) {
                String ufficio = uffici[i];
                if (ufficio != null) {
                    form.rimuoviUfficio(ufficio);
                }
            }
        }
    }

    protected void assegnaAdUfficio(ProfiloUtenteForm form, int ufficioId) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(ufficioId);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(ufficioId);
        ass.setNomeUfficio(uff.getValueObject().getDescription());
        ass.setDescrizioneUfficio(uff.getPath());
        ProfiloUtenteForm pForm = (ProfiloUtenteForm) form;
        pForm.aggiungiUfficio(ass);
        form.setUfficioSelezionatoId(0);
    }

    private void aggiornaUtenteOrganizzazione(UtenteVO utenteVO)
            throws DataException {
        Organizzazione org = Organizzazione.getInstance();
        OrganizzazioneDelegate organizzazioneDelegate = OrganizzazioneDelegate
                .getInstance();
        int utenteId = utenteVO.getId().intValue();
        Utente utente = org.getUtente(utenteId);
        if (utente == null) {
            utente = new Utente(utenteVO);
        } else {
            utente.setValueObject(utenteVO);
        }
        RegistroDelegate registroDelegate = RegistroDelegate.getInstance();

        Map registri = registroDelegate.getRegistriUtente(utenteId);
        utente.setRegistri(registri);
        utente.setRegistroUfficialeId(RegistroBO
                .getRegistroUfficialeId(registri.values()));

        Collection ids = organizzazioneDelegate
                .getIdentificativiUffici(utenteId);
        for (Iterator i = ids.iterator(); i.hasNext();) {
            int uffId = ((Integer) i.next()).intValue();
            // logger.info(org.getUfficio(uffId));
            if (org.getUfficio(uffId).getValueObject().getParentId() > 0) {
                org.getUfficio(uffId).addUtente(utente);
                if (UfficioDelegate.getInstance().isUtenteReferenteUfficio(
                        uffId, utenteId))
                    org.getUfficio(uffId).addUtenteReferente(utente);
            }
        }

        org.addUtente(utente);
    }

}