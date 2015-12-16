package it.finsiel.siged.mvc.presentation.action.amministrazione.org.utenti;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.business.UfficioDelegate;
import it.finsiel.siged.mvc.business.UtenteDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.org.utenti.UfficioForm;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;

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

public class UfficioAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(UfficioAction.class.getName());

    // --------------------------------------------------------- Public Methods

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        UfficioForm ufficioForm = (UfficioForm) form;
        UfficioVO ufficioVO = new UfficioVO();

        if (form == null) {
            form = new UfficioForm();
            request.setAttribute(mapping.getAttribute(), form);
        }
        if (ufficioForm.getId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
        }

        if (request.getParameter("impostaUfficioAction") != null) {
            ufficioForm.setUfficioCorrenteId(ufficioForm
                    .getUfficioSelezionatoId());
            AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
            ufficioForm.setParentId(ufficioForm.getUfficioCorrente()
                    .getParentId());

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            ufficioForm.setUfficioSelezionatoId(0);
            ufficioForm.setUfficioCorrenteId(ufficioForm.getUfficioCorrente()
                    .getParentId());
            AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
            ufficioForm.setParentId(ufficioForm.getUfficioCorrente()
                    .getParentId());
        } else if (request.getParameter("btnNuovo") != null) {
            ufficioForm.setAooId(utente.getRegistroVOInUso().getAooId());
            ufficioForm.inizializzaForm();
            ufficioForm.setParentId(ufficioForm.getUfficioCorrenteId());
            request.setAttribute(mapping.getAttribute(), ufficioForm);
            return mapping.findForward("aggiorna");

        } else if (request.getParameter("btnModifica") != null) {
            caricaDatiNelForm(ufficioForm, ufficioForm.getUfficioCorrenteId());
            ufficioForm.setAooId(utente.getRegistroVOInUso().getAooId());

            ufficioForm.setReferentiId(UfficioDelegate.getInstance()
                    .getReferentiByUfficio(ufficioForm.getUfficioCorrenteId()));
            request.setAttribute(mapping.getAttribute(), ufficioForm);
            return mapping.findForward("aggiorna");

        } else if (request.getParameter("btnCancella") != null) {
            UfficioDelegate td = UfficioDelegate.getInstance();
            if (!td.cancellaUfficio(ufficioForm.getUfficioCorrenteId())) {
                errors.add("ufficioNonCancellabile", new ActionMessage(
                        "record_non_cancellabile", "l'Ufficio", ""));
            } else {
                removeUfficio(ufficioForm.getUfficioCorrenteId());
                ufficioForm.setUfficioCorrenteId(ufficioForm.getParentId());
                AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
                errors.add("ufficioCancellato", new ActionMessage(
                        "operazione_ok"));
            }
            saveErrors(request, errors);
            return (mapping.findForward("input"));

        } else if (request.getParameter("btnAnnulla") != null) {
            AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
        } else if (request.getParameter("btnSalva") != null) {

//            if (ufficioForm.getId() > 0
//                    && (ufficioForm.getDipendentiUfficio() != null && ufficioForm
//                            .getDipendentiUfficio().size() > 0)
//                    && ufficioForm.getReferentiId() == null) {
//                errors.add("referenti", new ActionMessage(
//                        "selezione.obbligatoria",
//                        "almeno un referente per ufficio", ""));
//            } else {
                caricaDatiNelVO(ufficioVO, ufficioForm, utente);
                int returnValue = aggiornaUfficio(ufficioVO, ufficioForm
                        .getReferentiId());
                if (returnValue != ReturnValues.SAVED) {
                    if (returnValue == ReturnValues.FOUND) {
                        errors.add("record_non_inseribile", new ActionMessage(
                                "record_non_modificabile", "l'Ufficio",
                                " - descrizione già utilizzata "));

                    } else {
                        errors.add("generale", new ActionMessage(
                                "errore_nel_salvataggio"));

                    }
                } else {
                    ufficioForm.setUfficioCorrenteId(ufficioForm.getParentId());
                    AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
                }
            //            }
        } else {
            ufficioForm.inizializzaForm();
            AlberoUfficiBO.impostaUfficio(utente, ufficioForm, true);
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("aggiorna");
        }

        return (mapping.findForward("input"));
    }

    public void caricaDatiNelVO(UfficioVO vo, UfficioForm form, Utente utente) {
        vo.setId(form.getId());
        vo.setDescription(form.getDescription());
        vo.setAttivo(form.getAttivo() != null ? form.getAttivo().booleanValue()
                : false);
        // vo.setUfficioCentrale(form.getUfficioCentrale()!=null?form.getUfficioCentrale().booleanValue():false);
        vo.setTipo(form.getTipo());
        vo
                .setAccettazioneAutomatica(form.getAccettazioneAutomatica() != null ? form
                        .getAccettazioneAutomatica().booleanValue()
                        : false);
        vo.setAooId(utente.getUfficioVOInUso().getAooId());
        vo.setParentId(form.getParentId());
        if (form.getId() > 0) {
            vo.setRowCreatedUser(utente.getValueObject().getUsername());
        }
        vo.setRowUpdatedUser(utente.getValueObject().getUsername());
    }

    public void caricaDatiNelForm(UfficioForm form, int ufficioId) {
        Organizzazione org = Organizzazione.getInstance();
        UfficioVO ufficioVO = org.getUfficio(ufficioId).getValueObject();
        form.setId(ufficioId);
        form.setParentId(ufficioVO.getParentId());
        form.setDescription(ufficioVO.getDescription());
        form.setAttivo(Boolean.valueOf(ufficioVO.isAttivo()));
        form.setAccettazioneAutomatica(Boolean.valueOf(ufficioVO
                .isAccettazioneAutomatica()));
        // form.setUfficioCentrale(Boolean.valueOf(ufficioVO.isUfficioCentrale()));
        form.setTipo(ufficioVO.getTipo());
        UfficioDelegate td = UfficioDelegate.getInstance();
        form.setDipendentiUfficio(td.getUtentiByUfficio(ufficioId));
    }

    public int aggiornaUfficio(UfficioVO ufficioVO, String[] referenti) {
        int ufficioId = ufficioVO.getId().intValue();
        ufficioVO = UfficioDelegate.getInstance().salvaUfficio(ufficioVO,
                referenti);
        if (ufficioVO.getReturnValue() == ReturnValues.SAVED) {
            Organizzazione org = Organizzazione.getInstance();
            Ufficio uff;

            if (ufficioId == 0) {
                uff = new Ufficio(ufficioVO);
                Ufficio uffPadre = org.getUfficio(ufficioVO.getParentId());
                uff.setUfficioDiAppartenenza(uffPadre);
            } else {
                uff = org.getUfficio(ufficioId);
                uff.setValueObject(ufficioVO);
            }
            uff.removeReferenti();
            int i = 0;
            UtenteDelegate delegate = UtenteDelegate.getInstance();

            if (referenti != null) {
                while (i < referenti.length) {
                    int utenteId = Integer.parseInt(referenti[i]);
                    UtenteVO uteVO = delegate.getUtente(utenteId);
                    Utente ute = new Utente(uteVO);
                    uff.addUtenteReferente(ute);
                    i = i + 1;
                }
            }

            org.addUfficio(uff);
        }
        return ufficioVO.getReturnValue();
    }

    private void removeUfficio(int ufficioId) {
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(ufficioId);
        org.removeUfficio(new Integer(ufficioId));
        Ufficio uffPadre = org.getUfficio(uff.getValueObject().getParentId());
        uffPadre.removeUfficioDipendente(new Integer(ufficioId));
    }
}
