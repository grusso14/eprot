package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Protocollo;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.ConfigurazioneUtenteDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.SoggettoDelegate;
import it.finsiel.siged.mvc.business.StoriaProtocolloDelegate;
import it.finsiel.siged.mvc.business.TitoliDestinatarioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloUscitaForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.DestinatarioView;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ConfigurazioneUtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.mvc.presentation.action.protocollo.helper.CaricaProtocollo;
import it.flosslab.mvc.presentation.action.protocollo.helper.form.AggiornaProtocolloUscitaForm;
import it.flosslab.mvc.presentation.action.protocollo.helper.model.AggiornaProtocolloUscitaModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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

public class ProtocolloUscitaAction extends ProtocolloAction {
    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ProtocolloUscitaAction.class
            .getName());

    protected void assegnaAdUfficio(ProtocolloForm form, int ufficioId) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(ufficioId);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(ufficioId);
        ass.setNomeUfficio(uff.getValueObject().getDescription());
        ass.setDescrizioneUfficio(uff.getPath());
        ((ProtocolloUscitaForm) form).setMittente(ass);
        form.setTitolario(null);
    }

    protected void assegnaAdUtente(ProtocolloForm form) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(form.getUfficioCorrenteId());
        ass.setNomeUfficio(form.getUfficioCorrente().getDescription());
        ass.setDescrizioneUfficio(form.getUfficioCorrentePath());
        ass.setUtenteId(form.getUtenteSelezionatoId());
        UtenteVO ute = form.getUtente(form.getUtenteSelezionatoId());
        ass.setNomeUtente(ute.getFullName());
        ((ProtocolloUscitaForm) form).setMittente(ass);
        form.setTitolario(null);
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
        super.setType(CaricaProtocollo.PROTOCOLLO_USCITA);
        HttpSession session = request.getSession(true);

        /* contenente i nostri dati provenienti dall'html form */

        ProtocolloUscitaForm pForm = (ProtocolloUscitaForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        RegistroVO registro = (RegistroVO) utente.getRegistroVOInUso();

        if (!registro.getApertoUscita()) {
            errors.add("apertoUscita", new ActionMessage("registro_chiuso"));
            saveErrors(request, errors);
        }

        if (request.getParameter("annullaAction") != null
                && request.getParameter("btnCopiaProtocollo") == null) {
            Organizzazione org = Organizzazione.getInstance();
            Ufficio uff = org.getUfficio(utente.getUfficioInUso());
            pForm.setMittente(AggiornaProtocolloUscitaForm.newMittente(uff, null));
        }

        session.setAttribute("protocolloForm", pForm);

        if (pForm.getTitolario() == null) {
        	AggiornaProtocolloUscitaForm.impostaTitolario(pForm, utente, 0);
        }

        ActionForward actionForward = super.execute(mapping, form, request,
                response);
        if (actionForward != null && "none".equals(actionForward.getName())) {
            return null;
        } else if (actionForward != null) {
            return actionForward;
        }

        if (request.getParameter("aggiungiDestinatario") != null) {

            errors = pForm.validateDestinatario(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                aggiungiDestinatario(pForm, session);
            }

        } else if (request.getParameter("rimuoviDestinatari") != null) {
            String[] destinatari = pForm.getDestinatariSelezionatiId();
            if (destinatari != null) {
                for (int i = 0; i < destinatari.length; i++) {
                    pForm.rimuoviDestinatario(destinatari[i]);
                    destinatari[i] = null;
                }
            } else {
                errors.add("rimuoviDestinatari", new ActionMessage(
                        "destinatario_rimuovi"));
                saveErrors(request, errors);
            }
            pForm.inizializzaDestinatarioForm();
        } else if (request.getParameter("listaDistribuzione") != null) {
            request
                    .setAttribute("nomeLista", pForm
                            .getNominativoDestinatario());
            session.setAttribute("tornaProtocollo", Boolean.TRUE);
            session.setAttribute("provenienza", "listaDistribuzioneProtocollo");
            return mapping.findForward("cercaListaDistribuzione");
        } else if (request.getParameter("cercaDestinatari") != null) {
            session.setAttribute("tornaProtocollo", Boolean.TRUE);
            if ("F".equals(pForm.getTipoDestinatario())) {
                request.setAttribute("cognome", pForm
                        .getNominativoDestinatario());
                request.setAttribute("nome", "");
                session.setAttribute("provenienza", "personaFisicaProtocollo");
                pForm.setCognomeMittente("");
                return mapping.findForward("cercaPersonaFisica");
            } else if ("G".equals(pForm.getTipoDestinatario())) {
                request.setAttribute("descrizioneDitta", pForm
                        .getNominativoDestinatario());
                session.setAttribute("provenienza",
                        "personaGiuridicaProtocollo");
                pForm.setNominativoDestinatario("");
                return mapping.findForward("cercaPersonaGiuridica");
                // } else if ("L".equals(pForm.getTipoDestinatario())) {
                // request.setAttribute("nomeLista", pForm
                // .getNominativoDestinatario());
                // session.setAttribute("tornaProtocollo", Boolean.TRUE);
                // return mapping.findForward("cercaListaDistribuzione");
            } else {
                // AOO
                request.setAttribute("codice", pForm
                        .getNominativoDestinatario());
                return mapping.findForward("cercaAoo");
            }
        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario().getId()
                        .intValue());
            }
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            AggiornaProtocolloUscitaForm.impostaTitolario(pForm, ute, pForm.getTitolarioSelezionatoId());
            return mapping.findForward("input");

        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            AggiornaProtocolloUscitaForm.impostaTitolario(pForm, ute, pForm.getTitolarioPrecedenteId());
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario()
                        .getParentId());
            }
            return mapping.findForward("input");

        } else if (request.getParameter("btnStampaEtichette") != null) {
            Collection destinatari = pForm.getDestinatari();
            int protocolloId = pForm.getProtocolloId();
            logger.info("Ci sono " + destinatari.size() + " destinatari");
            session.setAttribute("destinatari", destinatari);
            session.setAttribute("protocolloId", "" + protocolloId);
            return mapping.findForward("stampaEtichetteDestinatari");

        } else if (request.getParameter("parCaricaListaId") != null) {
            int listaId = NumberUtil.getInt(request
                    .getParameter("parCaricaListaId"));
            ArrayList lista = SoggettoDelegate.getInstance()
                    .getDestinatariListaDistribuzione(listaId);
            AggiornaProtocolloUscitaForm.aggiungiDestinatariListaDistribuzioneForm(lista, pForm);
            session.removeAttribute("tornaProtocollo");
        } else if (request.getParameter("salvaAction") != null
                && errors.isEmpty()) {
            // aggiorno il modello
            if (isTokenValid(request, true)) {
                Utente ute = (Utente) session
                        .getAttribute(Constants.UTENTE_KEY);
                ProtocolloUscita protocolloUscita = null;

                ProtocolloVO protocollo = null;
                ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
                if (pForm.getProtocolloId() == 0) {
                    protocolloUscita = ProtocolloBO
                            .getDefaultProtocolloUscita(ute);
                    AggiornaProtocolloUscitaModel.aggiorna(pForm, protocolloUscita, ute);
                    protocollo = delegate.registraProtocollo(protocolloUscita,
                            ute, true);
                } else {
                    protocolloUscita = (ProtocolloUscita) session
                            .getAttribute("protocolloUscita");
                    AggiornaProtocolloUscitaModel.aggiorna(pForm, protocolloUscita, ute);
                    protocollo = delegate.aggiornaProtocolloUscita(
                            protocolloUscita, ute);
                }
                if (protocollo == null) {
                    errors.add("errore_nel_salvataggio", new ActionMessage(
                            "errore_nel_salvataggio", "", ""));
                    saveErrors(request, errors);
                } else if (protocollo.getReturnValue() == ReturnValues.SAVED) {
                    request.setAttribute("protocolloId", protocollo.getId());
                    caricaProtocollo(request, pForm, CaricaProtocollo.PROTOCOLLO_USCITA);
                    if (session.getAttribute("PROTOCOLLI_EMERGENZA") != null) {
                        pForm
                                .setNumeroProtocolliRegistroEmergenza(((Integer) session
                                        .getAttribute("PROTOCOLLI_EMERGENZA"))
                                        .intValue());
                    }

                    return mapping.findForward("visualizzaProtocollo");
                } else if (protocollo.getReturnValue() == ReturnValues.OLD_VERSION) {
                    errors.add("generale",
                            new ActionMessage("versione_vecchia"));
                    saveErrors(request, errors);
                    request.setAttribute("protocolloId", protocollo.getId());
                } else {
                    errors.add("generale", new ActionMessage(
                            "errore_nel_salvataggio"));
                    saveErrors(request, errors);
                }
                resetToken(request);
            } else {
                System.out.println("Token in not valid");
                return mapping.findForward("saveFailure");
            }

        } else if (request.getParameter("btnRipetiDatiU") != null) {
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

            ProtocolloBO.getProtocolloUscitaConfigurazioneUtente(pForm,
                    configurazioneVO, utenteId);
            pForm.inizializzaFormToCopyProtocollo();
            saveToken(request);
            return mapping.findForward("nuovoProtocolloRipeti");

        } else if (request.getParameter("destinatarioId") != null
                && !request.getParameter("destinatarioId").equals("")) {
            String destinatarioId = (String) request
                    .getParameter("destinatarioId");
            AggiornaProtocolloUscitaForm.aggiornaDestinatarioForm(destinatarioId, pForm);

        } else if (request.getParameter("cercaProcedimenti") != null) {
            session.setAttribute("tornaProtocolloUscita", Boolean.TRUE);
            return mapping.findForward("cercaProcedimenti");
        }
        pForm.setOggettoProcedimento("");
        return (mapping.findForward("input"));
    }

    private void aggiungiDestinatario(ProtocolloUscitaForm form,
            HttpSession session) {
        DestinatarioView destinatario = new DestinatarioView();
        destinatario.setFlagTipoDestinatario(form.getTipoDestinatario());
        if("F".equals(form.getTipoDestinatario())){
        	destinatario.setNome(form.getNomeDestinatario());
        	destinatario.setCognome(form.getCognomeDestinatario());
        }else{
        	destinatario.setDestinatario(form.getNominativoDestinatario().trim());
        }
        destinatario.setEmail(form.getEmailDestinatario());
        destinatario.setCapDestinatario(form.getCapDestinatario());
        destinatario.setCitta(form.getCitta());
        destinatario.setIndirizzo(form.getIndirizzoDestinatario());
        destinatario.setDataSpedizione(form.getDataSpedizione());
        destinatario.setMezzoSpedizioneId(form.getMezzoSpedizioneId());

        destinatario.setIdx(form.getIdx());
        String desc = LookupDelegate.getInstance().getDescMezzoSpedizione(
                form.getMezzoSpedizioneId()).getDescription();
        destinatario.setMezzoDesc(desc);

        destinatario.setTitoloDestinatario("--");
        destinatario.setTitoloId(form.getTitoloId());
        if (form.getTitoloId() > 0) {
            destinatario.setTitoloDestinatario(TitoliDestinatarioDelegate
                    .getInstance().getTitoloDestinatario(form.getTitoloId())
                    .getDescription());
        }
        destinatario.setNote(form.getNoteDestinatario());
        destinatario.setFlagConoscenza(form.getFlagConoscenza());
        form.aggiungiDestinatario(destinatario);
        form.inizializzaDestinatarioForm();
    }

   


}