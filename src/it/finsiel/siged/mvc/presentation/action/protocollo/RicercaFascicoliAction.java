package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.Fascicolo;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.FascicoloDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.documentale.DocumentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.FaldoneForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProcedimentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaFascicoliForm;
import it.finsiel.siged.mvc.presentation.helper.FascicoloView;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

public class RicercaFascicoliAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(RicercaFascicoliAction.class
            .getName());

    // --------------------------------------------------------- Public Methods

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        RicercaFascicoliForm ricercaFascicoliForm = (RicercaFascicoliForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        Organizzazione org = Organizzazione.getInstance();
        boolean preQuery = false;

        boolean ufficioCompleto = (utente.getUfficioVOInUso().getTipo()
                .equals(UfficioVO.UFFICIO_CENTRALE));

        session.setAttribute("fascicoloForm", ricercaFascicoliForm);

        if (form == null) {
            logger.info(" Creating new FascicoloAction");
            form = new RicercaFascicoliForm();
            ricercaFascicoliForm.inizializzaForm();
            request.setAttribute(mapping.getAttribute(), form);
        }

        if (ricercaFascicoliForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, ricercaFascicoliForm,
                    ufficioCompleto);
        }

        if (request.getParameter("impostaUfficioAction") != null) {
            ricercaFascicoliForm.setUfficioCorrenteId(ricercaFascicoliForm
                    .getUfficioSelezionatoId());
            AlberoUfficiBO.impostaUfficio(utente, ricercaFascicoliForm,
                    ufficioCompleto);
            impostaTitolario(ricercaFascicoliForm, utente, 0);
            return mapping.findForward("input");
        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            ricercaFascicoliForm.setUfficioCorrenteId(ricercaFascicoliForm
                    .getUfficioCorrente().getParentId());
            AlberoUfficiBO.impostaUfficio(utente, ricercaFascicoliForm,
                    ufficioCompleto);
            impostaTitolario(ricercaFascicoliForm, utente, 0);
        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (ricercaFascicoliForm.getTitolario() != null) {
                ricercaFascicoliForm
                        .setTitolarioPrecedenteId(ricercaFascicoliForm
                                .getTitolario().getId().intValue());
            }
            TitolarioBO.impostaTitolario(ricercaFascicoliForm,
                    ricercaFascicoliForm.getUfficioCorrenteId(),
                    ricercaFascicoliForm.getTitolarioSelezionatoId());
            return mapping.findForward("input");
        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            TitolarioBO.impostaTitolario(ricercaFascicoliForm,
                    ricercaFascicoliForm.getUfficioCorrenteId(),
                    ricercaFascicoliForm.getTitolarioPrecedenteId());
            if (ricercaFascicoliForm.getTitolario() != null) {
                ricercaFascicoliForm
                        .setTitolarioPrecedenteId(ricercaFascicoliForm
                                .getTitolario().getParentId());
            }
            return mapping.findForward("input");
        } else if (request.getParameter("cercaFascicoliDaProtocollo") != null) {
            String oggetto = (String) request
                    .getParameter("cercaFascicoliDaProtocollo");
            ricercaFascicoliForm.inizializzaForm();
            preQuery = true;
        } else if (request.getParameter("annullaAction") != null || preQuery) {
            Ufficio uff = org.getUfficio(utente.getUfficioInUso());
            ricercaFascicoliForm.setAooId(utente.getValueObject().getAooId());
            ricercaFascicoliForm.setUfficioCorrenteId(utente.getUfficioInUso());
            AlberoUfficiBO.impostaUfficio(utente, ricercaFascicoliForm,
                    ufficioCompleto);
            impostaTitolario(ricercaFascicoliForm, utente, 0);
            ricercaFascicoliForm.setUfficioCorrente(uff.getValueObject());
            session.setAttribute(mapping.getAttribute(), ricercaFascicoliForm);
            if (!(preQuery)) {
                return (mapping.findForward("input"));
            }
        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (ricercaFascicoliForm.getTitolario() != null) {
                ricercaFascicoliForm
                        .setTitolarioPrecedenteId(ricercaFascicoliForm
                                .getTitolario().getId().intValue());
            }
            TitolarioBO.impostaTitolario(ricercaFascicoliForm, utente
                    .getUfficioInUso(), ricercaFascicoliForm
                    .getTitolarioSelezionatoId());

        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            TitolarioBO.impostaTitolario(ricercaFascicoliForm, utente
                    .getRegistroInUso(), ricercaFascicoliForm
                    .getTitolarioPrecedenteId());
            if (ricercaFascicoliForm.getTitolario() != null) {
                ricercaFascicoliForm
                        .setTitolarioPrecedenteId(ricercaFascicoliForm
                                .getTitolario().getParentId());
            }
        } else if (request.getParameter("btnCercaFascicoli") != null
                || preQuery == true
                || request.getParameter("cercaFascicoliDaFaldoni") != null
                || request.getParameter("cercaFascicoliDaProcedimento") != null) {
            // parametri di ricerca
            Date dataAperturaDa = null;
            Date dataAperturaA = null;
            Date dataEvidenzaDa = null;
            Date dataEvidenzaA = null;
            int progressivo = 0;
            int anno = 0;
            AlberoUfficiBO.impostaUfficio(utente, ricercaFascicoliForm,
                    ufficioCompleto);
            if (request.getParameter("progressivo") != null
                    && !"".equals(request.getParameter("progressivo")))
                progressivo = new Integer(request.getParameter("progressivo"))
                        .intValue();
            if (request.getParameter("anno") != null
                    && !"".equals(request.getParameter("anno")))
                anno = new Integer(request.getParameter("anno")).intValue();
            String oggetto = getOggettoSearch(request, ricercaFascicoliForm);
            String note = request.getParameter("noteFascicolo");
            String stato = "-1";
            if (request.getParameter("stato") != null) {
                stato = request.getParameter("stato");
            }

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            if (request.getParameter("dataAperturaDa") != null
                    && !"".equals(request.getParameter("dataAperturaDa"))) {
                dataAperturaDa = df.parse(request
                        .getParameter("dataAperturaDa"));
            }
            if (request.getParameter("dataAperturaA") != null
                    && !"".equals(request.getParameter("dataAperturaA"))) {
                dataAperturaA = new Date(df.parse(
                        request.getParameter("dataAperturaA")).getTime()
                        + Constants.GIORNO_MILLISECONDS - 1);
            }
            if (request.getParameter("dataEvidenzaDa") != null
                    && !"".equals(request.getParameter("dataEvidenzaDa"))) {
                dataEvidenzaDa = df.parse(request
                        .getParameter("dataEvidenzaDa"));
            }
            if (request.getParameter("dataEvidenzaA") != null
                    && !"".equals(request.getParameter("dataEvidenzaA"))) {
                dataEvidenzaA = new Date(df.parse(
                        request.getParameter("dataEvidenzaA")).getTime()
                        + Constants.GIORNO_MILLISECONDS - 1);
            }

            int titolarioId = 0;
            if (ricercaFascicoliForm.getTitolario() != null) {
                titolarioId = ricercaFascicoliForm.getTitolario().getId()
                        .intValue();
            }
            int ufficioId = 0;
            if (ricercaFascicoliForm.getUfficioRicercaId() > 0) {
                ufficioId = ricercaFascicoliForm.getUfficioRicercaId();

            }
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            int maxRighe = Integer.parseInt(bundle
                    .getMessage("protocollo.max.righe.lista"));

            FascicoloDelegate fascicoloDelegate = FascicoloDelegate
                    .getInstance();
            int conta = fascicoloDelegate.contaFascicoli(utente, progressivo,
                    anno, oggetto, note, stato, titolarioId, dataAperturaDa,
                    dataAperturaA, dataEvidenzaDa, dataEvidenzaA, ufficioId);
            ricercaFascicoliForm.setFascicoli(null);
            if (conta == 0) {
                errors.add("nessun_dato", new ActionMessage("nessun_dato", "",
                        ""));
            } else if (conta <= maxRighe) {
                ricercaFascicoliForm.setFascicoli(FascicoloDelegate
                        .getInstance().getFascicoli(utente, progressivo, anno,
                                oggetto, note, stato, titolarioId,
                                dataAperturaDa, dataAperturaA, dataEvidenzaDa,
                                dataEvidenzaA, ufficioId));
                return (mapping.findForward("input"));
            } else {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + conta, "fascicoli", ""
                                + maxRighe));
            }

        } else if (request.getParameter("btnAnnulla") != null) {
            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                session.removeAttribute("tornaProtocollo");
                ProtocolloForm pForm = (ProtocolloForm) session
                        .getAttribute("protocolloForm");
                if (pForm instanceof ProtocolloIngressoForm) {
                    return (mapping.findForward("tornaProtocolloIngresso"));
                } else {
                    return (mapping.findForward("tornaProtocolloUscita"));
                }
            } else if (Boolean.TRUE.equals(session
                    .getAttribute("tornaDocumento"))) {
                session.removeAttribute("tornaDocumento");
                return (mapping.findForward("tornaDocumento"));
            } else if (Boolean.TRUE
                    .equals(session.getAttribute("tornaFaldone"))) {
                session.removeAttribute("tornaFaldone");
                return (mapping.findForward("tornaFaldone"));
            } else if (Boolean.TRUE.equals(session
                    .getAttribute("tornaProcedimento"))) {
                session.removeAttribute("tornaProcedimento");
                return (mapping.findForward("tornaProcedimento"));
            }

        } else if (request.getParameter("btnReset") != null) {
            ricercaFascicoliForm.inizializzaForm();
            TitolarioBO.impostaTitolario(ricercaFascicoliForm, utente
                    .getUfficioInUso(), ricercaFascicoliForm
                    .getTitolarioSelezionatoId());

        } else if (request.getParameter("btnAnnullaRicerca") != null) {
            session.removeAttribute("tornaProtocollo");
            session.removeAttribute("tornaDocumento");
            session.removeAttribute("tornaFaldone");
            session.removeAttribute("cercaFascicoliDaFaldoni");
            session.removeAttribute("tornaProcedimento");
            session.removeAttribute("elencoProtocolliDaProcedimento");
            ricercaFascicoliForm.inizializzaForm();
            if (ricercaFascicoliForm.getTitolario() == null) {
                TitolarioBO.impostaTitolario(ricercaFascicoliForm, utente
                        .getUfficioInUso(), 0);
            }
            return (mapping.findForward("input"));

        } else if (request.getParameter("protocolloSelezionato") != null) {
            Integer protId = new Integer(request
                    .getParameter("protocolloSelezionato"));
            request.setAttribute("protocolloId", protId);
            ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
            ProtocolloVO v = pd.getProtocolloById(protId.intValue());
            String tipoProt = v.getFlagTipo();

            Ufficio uff = org.getUfficio(utente.getUfficioVOInUso().getId()
                    .intValue());
            if (pd.isUtenteAbilitatoView(utente, uff, protId.intValue())) {
                if ("I".equals(tipoProt)) {
                    return (mapping.findForward("visualizzaProtocolloIngresso"));
                } else {
                    return (mapping.findForward("visualizzaProtocolloUscita"));
                }
            } else {
                errors.add("permessi.utenti.lettura", new ActionMessage(
                        "permessi.utenti.lettura", "", ""));
                saveErrors(request, errors);
                return (mapping.findForward("fascicolo"));
            }

        } else if (request.getParameter("downloadDocumentoId") != null) {
            int idDoc = new Integer(request.getParameter("downloadDocumentoId"))
                    .intValue();
            DocumentoVO d = DocumentoDelegate.getInstance().getDocumento(idDoc);
            response.setContentType(d.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename="
                    + d.getFileName());
            response.setHeader("Cache-control", "");
            DocumentoDelegate.getInstance().writeDocumentToStream(idDoc,
                    response.getOutputStream());
            return null;
        } else if (request.getParameter("btnSeleziona") != null) {
            String[] fascicoliSelezionati = ricercaFascicoliForm
                    .getFascicoliSelezionati();
            int fascicoloId = NumberUtil.getInt(request
                    .getParameter("fascicoloSelezionato"));
            FascicoloDelegate fd = FascicoloDelegate.getInstance();

            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                session.removeAttribute("tornaProtocollo");
                ProtocolloForm pForm = (ProtocolloForm) session
                        .getAttribute("protocolloForm");
                Fascicolo fascicolo = fd.getFascicoloById(fascicoloId);
                pForm.aggiungiFascicolo(fascicolo.getFascicoloVO());
                ricercaFascicoliForm.inizializzaForm();
                if (pForm instanceof ProtocolloIngressoForm) {
                    return (mapping.findForward("tornaProtocolloIngresso"));
                } else {
                    return (mapping.findForward("tornaProtocolloUscita"));
                }

            } else if (Boolean.TRUE.equals(session
                    .getAttribute("tornaDocumento"))) {
                session.removeAttribute("tornaDocumento");
                DocumentoForm pForm = (DocumentoForm) session
                        .getAttribute("documentoForm");
                Fascicolo fascicolo = fd.getFascicoloById(fascicoloId);
                pForm.aggiungiFascicolo(fascicolo.getFascicoloVO());
                TitolarioBO.impostaTitolario(pForm, utente.getUfficioInUso(),
                        fascicolo.getFascicoloVO().getTitolarioId());
                ricercaFascicoliForm.inizializzaForm();
                return (mapping.findForward("tornaDocumento"));

            } else if (Boolean.TRUE
                    .equals(session.getAttribute("tornaFaldone"))) {
                FaldoneForm fForm = (FaldoneForm) session
                        .getAttribute("faldoneForm");
                for (int i = 0; fascicoliSelezionati != null
                        && i < fascicoliSelezionati.length; i++) {
                    Fascicolo fa = fd.getFascicoloById(NumberUtil
                            .getInt(fascicoliSelezionati[i]));
                    fForm.aggiungiFascicolo(fa);
                }
                ricercaFascicoliForm.inizializzaForm();
                session.removeAttribute("tornaFaldone");
                return (mapping.findForward("tornaFaldone"));
            } else if (Boolean.TRUE.equals(session
                    .getAttribute("tornaProcedimento"))) {
                ProcedimentoForm fForm = (ProcedimentoForm) session
                        .getAttribute("procedimentoForm");
                for (int i = 0; fascicoliSelezionati != null
                        && i < fascicoliSelezionati.length; i++) {
                    FascicoloView fa = fd.getFascicoloViewById(NumberUtil
                            .getInt(fascicoliSelezionati[i]));
                    fForm.aggiungiFascicolo(fa);
                }
                ricercaFascicoliForm.inizializzaForm();
                session.removeAttribute("tornaProcedimento");
                return (mapping.findForward("tornaProcedimento"));
            } else {
                request.setAttribute("fascicoloId", new Integer(fascicoloId));
                return (mapping.findForward("fascicolo"));
            }
        } else {
            ricercaFascicoliForm.inizializzaForm();
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return (mapping.findForward("input"));
    }

    private void impostaTitolario(RicercaFascicoliForm form, Utente utente,
            int titolarioId) {
        int ufficioId;
        if (utente.getAreaOrganizzativa().getDipendenzaTitolarioUfficio() == 1) {
            ufficioId = form.getUfficioCorrenteId();
        } else {
            ufficioId = utente.getUfficioInUso();
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);
    }

    private String getOggettoSearch(HttpServletRequest request,
            RicercaFascicoliForm ricercaFascicoliForm) {
        String oggetto = null;
        HttpSession session = request.getSession();
        if (Boolean.TRUE.equals(session.getAttribute("tornaFaldone"))) {

            oggetto = (String) request.getAttribute("cercaFascicoliDaFaldoni");

            if (request.getAttribute("cercaFascicoliDaFaldoni") == null) {
                ricercaFascicoliForm.setOggettoFascicolo(ricercaFascicoliForm
                        .getOggettoFascicolo());
                oggetto = ricercaFascicoliForm.getOggettoFascicolo();
                ricercaFascicoliForm.setOggettoFascicolo("");
            } else {
                ricercaFascicoliForm.setOggettoFascicolo(oggetto);
                ricercaFascicoliForm.setOggettoFascicolo("");
            }
        } else if ("FascicoliDaProcedimento".equals(request
                .getAttribute("provenienza"))) {
            oggetto = (String) request
                    .getAttribute("cercaFascicoliDaProcedimento");
            ricercaFascicoliForm.setOggettoFascicolo("");
            // request.getAttribute("cercaFascicoliDaProcedimento");
        } else if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
            oggetto = (String) request
                    .getAttribute("cercaFascicoliDaProtocollo");
            if (request.getAttribute("cercaFascicoliDaProtocollo") == null) {
                ricercaFascicoliForm.setOggettoFascicolo(ricercaFascicoliForm
                        .getOggettoFascicolo());
                oggetto = ricercaFascicoliForm.getOggettoFascicolo();
                ricercaFascicoliForm.setOggettoFascicolo("");
            } else {
                ricercaFascicoliForm.setOggettoFascicolo(oggetto);
                ricercaFascicoliForm.setOggettoFascicolo("");
            }
        } else {
            oggetto = request.getParameter("oggettoFascicolo");
            ricercaFascicoliForm.setOggettoFascicolo("");
        }
        return oggetto;

    }

}
