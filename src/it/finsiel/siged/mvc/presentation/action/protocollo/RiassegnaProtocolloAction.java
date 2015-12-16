package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class RiassegnaProtocolloAction extends ProtocolloAction {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(RiassegnaProtocolloAction.class
            .getName());

    // --------------------------------------------------------- Public Methods
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward actionForward = super.execute(mapping, form, request,
                response);
        if (actionForward != null) {
            return actionForward;
        }

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession(true); // we create one if does
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();

        ProtocolloIngressoForm pForm = (ProtocolloIngressoForm) form;
        /* contenente i nostri dati provenienti dall'html form */
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

        session.setAttribute("protocolloForm", pForm);
        if (form == null) {
            logger.info(" Creating new riassegnazioneAction");
            form = new ProtocolloIngressoForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        Integer protocolloId = (Integer) request.getAttribute("protocolloId");
        if (pForm.getUfficioCorrenteId() == 0) {
            boolean ufficioCompleto = (utente.getUfficioVOInUso().getTipo()
                    .equals(UfficioVO.UFFICIO_CENTRALE) || utente
                    .getUfficioVOInUso().getTipo().equals(
                            UfficioVO.UFFICIO_SEMICENTRALE));

            AlberoUfficiBO.impostaUfficioUtentiAbilitati(utente, pForm, ufficioCompleto);
        }

        if (protocolloId != null) {
            int id = protocolloId.intValue();
            ProtocolloIngresso protocollo = ProtocolloDelegate.getInstance()
                    .getProtocolloIngressoById(id);

            aggiornaForm(protocollo, pForm, session);
            session.setAttribute("protocolloIngresso", protocollo);
            return mapping.findForward("input");
        }

        if (request.getParameter("salvaAction") != null) {
            if (pForm.getAssegnatari().size() == 0) {
                // ci deve essere almeno un assegnatario
                errors.add("assegnatari", new ActionMessage(
                        "assegnatari_obbligatorio"));
            } else if (pForm.getAssegnatarioCompetente() == null
                    || "".equals(pForm.getAssegnatarioCompetente())) {
                // ci deve essere almeno un assegnatario di competenza
                errors.add("assegnatarioCompetente", new ActionMessage(
                        "assegnatario_competente_obbligatorio"));
            }

            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return (mapping.findForward("input"));
            } else {
                ProtocolloIngresso protocolloIngresso = (ProtocolloIngresso) session
                        .getAttribute("protocolloIngresso");
                aggiornaAssegnatariModel(pForm, protocolloIngresso, utente);
                delegate.riassegnaProtocollo(protocolloIngresso, utente);
                if (session.getAttribute("TORNA_SCARICO") != null) {
                    session.removeAttribute("TORNA_SCARICO");
                    return (mapping.findForward("presaInCarico"));
                } else {
                    return (mapping.findForward("listaRespinti"));
                }
            }

        } else if (request.getParameter("rimuoviAssegnatariAction") != null) {
            rimuovoAssegnatari(pForm);
            return mapping.findForward("edit");

        } else if (request.getParameter("annullaAction") != null) {
            return mapping.findForward("cercaProtocolliScarico");
        } else {
            if (request.getParameter("SCARICO") != null) {
                session.setAttribute("TORNA_SCARICO", "TRUE");
            } else {
                session.setAttribute("TORNA_SCARICO", null);
            }
            return mapping.getInputForward();
        }

    }

    public void aggiornaForm(ProtocolloIngresso protocollo,
            ProtocolloIngressoForm form, HttpSession session) {
        form.inizializzaForm();

        // dati generali
        aggiornaDatiGeneraliForm(protocollo.getProtocollo(), form);

        // assegnatari
        aggiornaAssegnatariForm(protocollo, form);
    }

    private void aggiornaDatiGeneraliForm(ProtocolloVO protocollo,
            ProtocolloForm form) {
        Integer id = protocollo.getId();
        if (id != null) {
            form.setProtocolloId(id.intValue());
            form.setDataRegistrazione(DateUtil.formattaData(protocollo
                    .getDataRegistrazione().getTime()));
        } else {
            form.setProtocolloId(0);
        }
        form.setAooId(protocollo.getAooId());
        form.setNumeroProtocollo(protocollo.getNumProtocollo() + "/"
                + protocollo.getAnnoRegistrazione());
        form.setTipoDocumentoId(protocollo.getTipoDocumentoId());
        Date dataDoc = protocollo.getDataDocumento();
        form.setDataDocumento(dataDoc == null ? null : DateUtil
                .formattaData(dataDoc.getTime()));
        Date dataRic = protocollo.getDataRicezione();
        form.setDataRicezione(dataRic == null ? null : DateUtil
                .formattaData(dataRic.getTime()));
        form.setRiservato(protocollo.isRiservato());
        form.setStato(protocollo.getStatoProtocollo());
        form.setOggetto(protocollo.getOggetto());
        form
                .setUfficioProtocollatoreId(protocollo
                        .getUfficioProtocollatoreId());
        form.setUtenteProtocollatoreId(protocollo.getUtenteProtocollatoreId());
        form.setDocumentoVisibile(true);
    }

    private void aggiornaAssegnatariForm(ProtocolloIngresso protocollo,
            ProtocolloIngressoForm form) {
        Organizzazione org = Organizzazione.getInstance();
        form.setAssegnatarioCompetente(null);
        for (Iterator i = protocollo.getAssegnatari().iterator(); i.hasNext();) {
            AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
            AssegnatarioView ass = new AssegnatarioView();
            int uffId = assegnatario.getUfficioAssegnatarioId();
            ass.setUfficioId(uffId);
            Ufficio uff = org.getUfficio(uffId);
            if (uff != null) {
                ass.setNomeUfficio(uff.getValueObject().getDescription());
            }
            int uteId = assegnatario.getUtenteAssegnatarioId();
            ass.setUtenteId(uteId);
            Utente ute = org.getUtente(uteId);
            if (ute != null) {
                ass.setNomeUtente(ute.getValueObject().getFullName());
            }
            form.aggiungiAssegnatario(ass);
            if (assegnatario.isCompetente()) {
                form.setAssegnatarioCompetente(ass.getKey());
            }
        }
    }

    protected void caricaProtocollo(HttpServletRequest request,
            ProtocolloForm form) {
    }

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
    }

    private void rimuovoAssegnatari(ProtocolloIngressoForm form) {
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
        }
    }

    private void aggiornaAssegnatariModel(ProtocolloIngressoForm form,
            ProtocolloIngresso protocollo, Utente utente) {
        protocollo.removeAssegnatari();
        // modifica pino 09/02/2006
        protocollo.setMsgAssegnatarioCompetente(form
                .getMsgAssegnatarioCompetente());
        String assComp = form.getAssegnatarioCompetente();
        UtenteVO uteVO = utente.getValueObject();
        Collection assegnatari = form.getAssegnatari();
        if (assegnatari != null) {
            for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                AssegnatarioView ass = (AssegnatarioView) i.next();
                AssegnatarioVO assegnatario = new AssegnatarioVO();
                Date now = new Date();
                assegnatario.setDataAssegnazione(now);
                assegnatario.setDataOperazione(now);
                assegnatario.setRowCreatedUser(uteVO.getUsername());
                assegnatario.setRowUpdatedUser(uteVO.getUsername());
                assegnatario.setUfficioAssegnanteId(utente.getUfficioInUso());
                assegnatario.setUtenteAssegnanteId(uteVO.getId().intValue());
                assegnatario.setUfficioAssegnatarioId(ass.getUfficioId());
                assegnatario.setUtenteAssegnatarioId(ass.getUtenteId());
                // assegnatario.setStatoAssegnazione(ass.getStato());
                if (ass.getKey().equals(assComp)) {
                    assegnatario.setCompetente(true);
                    // modifica pino 08/02/2006
                    assegnatario.setMsgAssegnatarioCompetente(form
                            .getMsgAssegnatarioCompetente());
                    Organizzazione org = Organizzazione.getInstance();
                    UfficioVO uff = org.getUfficio(ass.getUfficioId())
                            .getValueObject();
                    if (ass.getStato() != 'S') {
                        assegnatario.setStatoAssegnazione(ass.getStato());
                    } else if (form.getRiservato()
                            || (uff.isAccettazioneAutomatica() && ass
                                    .getUtenteId() > 0)) {
                        assegnatario.setStatoAssegnazione('A');
                        if (!protocollo.getProtocollo().getStatoProtocollo()
                                .equals("P")) {
                            protocollo.getProtocollo().setStatoProtocollo("N");
                        }
                    } else {
                        if (!protocollo.getProtocollo().getStatoProtocollo()
                                .equals("P")) {
                            protocollo.getProtocollo().setStatoProtocollo("S");
                        }
                    }
                }
                protocollo.aggiungiAssegnatario(assegnatario);
            }
        }
    }

}
