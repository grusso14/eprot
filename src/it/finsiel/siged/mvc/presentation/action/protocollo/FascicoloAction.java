package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.Fascicolo;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.FascicoloDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.documentale.DocumentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.FaldoneForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.FascicoloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.documentale.FileVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloFascicoloVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

public class FascicoloAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(FascicoloAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        FascicoloForm fascicoloForm = (FascicoloForm) form;
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        int aooId = utente.getAreaOrganizzativa().getId().intValue();
        fascicoloForm.setAooId(aooId);
        boolean indietroVisibile = false;

        fascicoloForm.setIndietroVisibile(indietroVisibile);
        // boolean ufficioCompleto =
        // (utente.getUfficioVOInUso().getTipo().equals(
        // UfficioVO.UFFICIO_CENTRALE) || utente.getUfficioVOInUso()
        // .getTipo().equals(UfficioVO.UFFICIO_SEMICENTRALE));
        boolean ufficioCompleto = true;

        if (fascicoloForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficioUtentiReferenti(utente, fascicoloForm,
                    ufficioCompleto);
            fascicoloForm.setUtenteAbilitatoSuUfficio(utente
                    .isUtenteAbilitatoSuUfficio(fascicoloForm
                            .getUfficioCorrenteId()));

        }
        fascicoloForm.setDipendenzaTitolarioUfficio(utente
                .getAreaOrganizzativa().getDipendenzaTitolarioUfficio());
        if (form == null) {
            logger.info(" Creating new FascicoloAction");
            form = new FascicoloForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        if (request.getParameter("btnNuovo") != null) {
            fascicoloForm.inizializzaForm();

            fascicoloForm.setFaldoneOggetto(null);
            fascicoloForm.setFaldoneCodiceLocale(null);

            int titolarioId = 0;
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            fascicoloForm.setCollocazioneLabel1(bundle
                    .getMessage("fascicolo.collocazione.label1"));
            fascicoloForm.setCollocazioneLabel2(bundle
                    .getMessage("fascicolo.collocazione.label2"));
            fascicoloForm.setCollocazioneLabel3(bundle
                    .getMessage("fascicolo.collocazione.label3"));
            fascicoloForm.setCollocazioneLabel4(bundle
                    .getMessage("fascicolo.collocazione.label4"));

            if (session.getAttribute("protocolloForm") != null) {
                ProtocolloForm pForm = (ProtocolloForm) session
                        .getAttribute("protocolloForm");
                if (pForm != null && pForm.getTitolario() != null) {
                    titolarioId = pForm.getTitolario().getId().intValue();
                }
            }
            impostaNuovoFascicoloForm(titolarioId, fascicoloForm, utente);
            AlberoUfficiBO.impostaUfficio(utente, fascicoloForm,
                    ufficioCompleto);
            assegnaAdUfficio(fascicoloForm, fascicoloForm
                    .getUfficioCorrenteId());
            AlberoUfficiBO.impostaUfficioUtentiReferenti(utente, fascicoloForm,
                    ufficioCompleto);
            if (Boolean.TRUE.equals(session.getAttribute("tornaFaldone"))) {
                fascicoloForm.setFaldoneCodiceLocale((String) request
                        .getAttribute("codiceLocaleFaldone"));
                fascicoloForm.setFaldoneOggetto((String) request
                        .getAttribute("oggettoFaldone"));
                indietroVisibile = true;
                fascicoloForm.setIndietroVisibile(indietroVisibile);
            }
            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                indietroVisibile = true;
                fascicoloForm.setIndietroVisibile(indietroVisibile);
            }
            return (mapping.findForward("fascicolo"));
        } else if (fascicoloForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, fascicoloForm,
                    ufficioCompleto);
            return (mapping.findForward("fascicolo"));
        } else if (request.getParameter("impostaUfficioAction") != null) {
            if (utente.getUfficioVOInUso().getTipo().equals(
                    UfficioVO.UFFICIO_SEMICENTRALE)
                    || utente.isUtenteAbilitatoSuUfficio(fascicoloForm
                            .getUfficioSelezionatoId())) {
                fascicoloForm.setUfficioCorrenteId(fascicoloForm
                        .getUfficioSelezionatoId());
                AlberoUfficiBO.impostaUfficioUtentiReferenti(utente,
                        fascicoloForm, ufficioCompleto);
                assegnaAdUfficio(fascicoloForm, fascicoloForm
                        .getUfficioSelezionatoId());
                impostaTitolario(fascicoloForm, utente, 0);
            } else {
                errors.add("utente_non_abilitato_ufficio", new ActionMessage(
                        "utente_non_abilitato_ufficio", utente.getValueObject()
                                .getUsername(), ""));
                saveErrors(request, errors);
            }
            return (mapping.findForward("fascicolo"));
        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            if (fascicoloForm.getUfficioCorrente().getParentId() > 0
                    && (utente.getUfficioVOInUso().getTipo().equals(
                            UfficioVO.UFFICIO_SEMICENTRALE) || utente
                            .isUtenteAbilitatoSuUfficio(fascicoloForm
                                    .getUfficioCorrente().getParentId()))) {
                fascicoloForm.setUfficioCorrenteId(fascicoloForm
                        .getUfficioCorrente().getParentId());
                AlberoUfficiBO.impostaUfficioUtentiReferenti(utente,
                        fascicoloForm, ufficioCompleto);
                assegnaAdUfficio(fascicoloForm, fascicoloForm
                        .getUfficioCorrenteId());

                impostaTitolario(fascicoloForm, utente, 0);
            }
            return (mapping.findForward("fascicolo"));

        } else if (request.getParameter("assegnaUtenteAction") != null) {
            assegnaAdUtente(fascicoloForm);
            return mapping.findForward("fascicolo");

        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (fascicoloForm.getTitolario() != null) {
                fascicoloForm.setTitolarioPrecedenteId(fascicoloForm
                        .getTitolario().getId().intValue());
            }
            impostaTitolario(fascicoloForm, utente, fascicoloForm
                    .getTitolarioSelezionatoId());
            return (mapping.findForward("fascicolo"));
        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            impostaTitolario(fascicoloForm, utente, fascicoloForm
                    .getTitolarioPrecedenteId());
            if (fascicoloForm.getTitolario() != null) {
                fascicoloForm.setTitolarioPrecedenteId(fascicoloForm
                        .getTitolario().getParentId());
            }
            return (mapping.findForward("fascicolo"));
        } else if (request.getParameter("btnModifica") != null) {
            caricaFascicolo(request, fascicoloForm);
            FascicoloVO fVO = FascicoloDelegate.getInstance()
                    .getFascicoloVOById(
                            Integer.parseInt(request.getParameter("id")));
            // modifiche 29/03/2006
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            if (fVO.getCollocazioneLabel1() == null) {
                fVO.setCollocazioneLabel1(bundle
                        .getMessage("fascicolo.collocazione.label1"));
            }
            if (fVO.getCollocazioneLabel2() == null) {
                fVO.setCollocazioneLabel2(bundle
                        .getMessage("fascicolo.collocazione.label2"));
            }
            if (fVO.getCollocazioneLabel3() == null) {
                fVO.setCollocazioneLabel3(bundle
                        .getMessage("fascicolo.collocazione.label3"));
            }
            if (fVO.getCollocazioneLabel4() == null) {
                fVO.setCollocazioneLabel4(bundle
                        .getMessage("fascicolo.collocazione.label4"));
            }
            // fine modifiche 29/03/2006
            impostaFascicoloForm(fVO, fascicoloForm, utente);
            // fascicoloForm.setUfficioCorrenteId(fVO.getUfficioIntestatarioId());
            AlberoUfficiBO.impostaUfficioUtentiReferenti(utente, fascicoloForm,
                    ufficioCompleto);
            return (mapping.findForward("fascicolo"));

        } else if (request.getParameter("btnConferma") != null) {
            errors = fascicoloForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return (mapping.findForward("fascicolo"));
            }
            FascicoloVO fVO = new FascicoloVO();
            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                session.removeAttribute("tornaProtocollo");
                ProtocolloForm pForm = (ProtocolloForm) session
                        .getAttribute("protocolloForm");
                fVO = impostaFascicoloVO(fascicoloForm, utente);
                pForm.aggiungiFascicolo(fVO);
                impostaTitolario(pForm, utente, fVO.getTitolarioId());

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
                fVO = impostaFascicoloVO(fascicoloForm, utente);
                pForm.aggiungiFascicolo(fVO);
                impostaTitolario(pForm, utente, fVO.getTitolarioId());
                return (mapping.findForward("tornaDocumento"));

            } else if (Boolean.TRUE
                    .equals(session.getAttribute("tornaFaldone"))) {
                FaldoneForm fForm = (FaldoneForm) session
                        .getAttribute("faldoneForm");
                fVO = impostaFascicoloVO(fascicoloForm, utente);
                FascicoloVO nuovo = FascicoloDelegate.getInstance()
                        .nuovoFascicolo(fVO);
                if (ReturnValues.SAVED == nuovo.getReturnValue()) {
                    Fascicolo f = new Fascicolo();
                    f.setFascicoloVO(nuovo);
                    fForm.aggiungiFascicolo(f);
                    session.removeAttribute("tornaFaldone");
                    return (mapping.findForward("tornaFaldone"));
                } else {
                    // errore nel salvataggio
                    errors.add("fascicolo", new ActionMessage(
                            "errore_nel_salvataggio"));
                }
            } else {
                FascicoloDelegate fd = FascicoloDelegate.getInstance();
                if (fascicoloForm.getId() > 0) {
                    fVO = impostaFascicoloVO(fascicoloForm, utente);
                    boolean isModificato = fascicoloModificato(fVO);
                    if (isModificato) {
                        if (fascicoloForm.getDataScarico() == null
                                || "".equals(fascicoloForm.getDataScarico())) {

                            errors
                                    .add(
                                            "dataScarico",
                                            new ActionMessage(
                                                    "campo.obbligatorio",
                                                    "Data scarico",
                                                    "(se si modifica almeno uno tra  Ufficio, Titolario, Referente, Stato, data evidenza, oggetto)"));
                            saveErrors(request, errors);
                            return (mapping.findForward("fascicolo"));
                        } else {
                            fVO.setDataCarico((new Date(System
                                    .currentTimeMillis())));
                        }

                    }
                    fVO = fd.salvaFascicolo(fVO);
                    if (fVO == null) {
                        errors.add("errore_nel_salvataggio", new ActionMessage(
                                "errore_nel_salvataggio", "", ""));
                        saveErrors(request, errors);
                    } else if (fVO.getReturnValue() == ReturnValues.SAVED) {
                        errors.add("fascicolo", new ActionMessage(
                                "fascicolo_registrato", "Modificato", ""));
                        saveErrors(request, errors);
                    }

                } else {

                    fVO = impostaFascicoloVO(fascicoloForm, utente);
                    fVO = fd.nuovoFascicolo(fVO);
                    if (fVO.getReturnValue() == ReturnValues.SAVED) {
                        errors.add("fascicolo", new ActionMessage(
                                "fascicolo_registrato", "Registrato", ""));
                        saveErrors(request, errors);
                    }

                }
            }
            request.setAttribute("fascicoloId", fVO.getId());
            //caricaFascicolo(request, fascicoloForm);

        } else if (request.getParameter("btnRimuovi") != null) {
            ProtocolloForm pForm = (ProtocolloForm) session
                    .getAttribute("protocolloForm");
            if (pForm instanceof ProtocolloIngressoForm) {
                return (mapping.findForward("tornaProtocolloIngresso"));
            } else {
                return (mapping.findForward("tornaProtocolloUscita"));
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
            } else {
                caricaFascicolo(request, fascicoloForm);
                return (mapping.findForward("input"));
            }
        } else if (request.getParameter("btnAnnullaSelezione") != null) {
            fascicoloForm.inizializzaForm();
            return (mapping.findForward("lista"));

        } else if (request.getParameter("btnCancella") != null) {
            caricaFascicolo(request, fascicoloForm);
            fascicoloForm.setOperazione(FascicoloForm.CANCELLAZIONE_FASCICOLO);
            return (mapping.findForward("confermaOperazione"));
        } else if (request.getParameter("btnChiudi") != null) {
            caricaFascicolo(request, fascicoloForm);
            if (isFascicoloClosable(fascicoloForm)) {
                fascicoloForm.setOperazione(FascicoloForm.CHIUSURA_FASCICOLO);
                return (mapping.findForward("confermaOperazione"));
            } else {
                errors.add("fascicolo_no_chiusura", new ActionMessage(
                        "fascicolo_no_chiusura", "", ""));
            }

        } else if (request.getParameter("btnRiapri") != null) {
            caricaFascicolo(request, fascicoloForm);
            fascicoloForm.setOperazione(FascicoloForm.RIAPERTURA_FASCICOLO);
            return (mapping.findForward("confermaOperazione"));
        } else if (request.getParameter("btnScarta") != null) {
            caricaFascicolo(request, fascicoloForm);
            fascicoloForm.setOperazione(FascicoloForm.SCARTO_FASCICOLO);
            return (mapping.findForward("confermaOperazione"));
        } else if (request.getParameter("btnInvio") != null) {
            caricaFascicolo(request, fascicoloForm);
            errors = fascicoloForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                return (mapping.findForward("invioProtocollo"));
            }
        } else if (request.getParameter("btnAnnullaInvio") != null) {
            caricaFascicolo(request, fascicoloForm);
            if (!FascicoloDelegate.getInstance().esisteFascicoloInCodaInvio(
                    fascicoloForm.getId())) {
                errors.add("fascicolo_no_annulla_invio", new ActionMessage(
                        "fascicolo_no_annulla_invio", "", ""));
            } else {
                FascicoloDelegate fd = FascicoloDelegate.getInstance();
                if (fd.annullaInvioFascicolo(fascicoloForm.getId(), utente
                        .getValueObject().getUsername(), fascicoloForm
                        .getVersione()) == 1) {
                    errors.add("invio_fascicolo", new ActionMessage(
                            "operazione_ok", "", ""));
                    request.setAttribute("fascicoloId", new Integer(
                            fascicoloForm.getId()));
                } else {
                    errors.add("invio_fascicolo", new ActionMessage(
                            "errore_nel_salvataggio"));
                }
                saveErrors(request, errors);
            }
        } else if (request.getParameter("btnOperazioni") != null) {
            if (fascicoloForm.getDataEvidenza() != null) {
                fascicoloForm.setDataEvidenza(null);
            }
            return setOperazione(fascicoloForm, mapping, request, response);

        } else if (request.getParameter("btnSelezionaDocumento") != null) {
            caricaFascicolo(request, fascicoloForm);
            errors = fascicoloForm.validate(mapping, request);
            if (errors.isEmpty()) {
                request.setAttribute("documentoId", new Integer(request
                        .getParameter("documentoSelezionato")));
                return (mapping.findForward("visualizzaDocumento"));
            }

        } else if (request.getParameter("btnAggiungiDocumenti") != null) {
            caricaFascicolo(request, fascicoloForm);
            session.setAttribute("tornaFascicolo", Boolean.TRUE);
            session.setAttribute("provenienza", "fascicoloDocumento");
            return (mapping.findForward("documenti"));

        } else if (request.getParameter("btnRimuoviDocumento") != null) {
            // caricaFascicolo(request, fascicoloForm);
            errors = fascicoloForm.validate(mapping, request);
            if (errors.isEmpty()) {
                int documentoId = Integer.parseInt(request
                        .getParameter("documentoSelezionato"));
                int fascicoloId = fascicoloForm.getId();
                FascicoloDelegate.getInstance().rimuoviDocumentoDaFascicolo(
                        fascicoloId, documentoId, fascicoloForm.getVersione());
                request.setAttribute("fascicoloId", new Integer(fascicoloId));
            }

        } else if (request.getParameter("btnStoria") != null) {
            request.setAttribute("fascicoloId", new Integer(fascicoloForm
                    .getId()));
            caricaFascicolo(request, fascicoloForm);
            return (mapping.findForward("storiaFascicolo"));

        } else if (request.getAttribute("fascicoloId") != null) {
            caricaFascicolo(request, fascicoloForm);
            return (mapping.findForward("input"));
        } else if (request.getParameter("downloadDocprotocolloSelezionato") != null) {
            Integer protId = new Integer(request
                    .getParameter("downloadDocprotocolloSelezionato"));
            request.setAttribute("downloadDocprotocolloSelezionato", protId);
            ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
            Ufficio uff = Organizzazione.getInstance().getUfficio(
                    utente.getUfficioVOInUso().getId().intValue());
            if (pd.isUtenteAbilitatoView(utente, uff, protId.intValue())) {
                return (mapping.findForward("downloadDocumentoProtocollo"));

            } else {
                errors.add("permessi.utenti.lettura", new ActionMessage(
                        "permessi.utenti.lettura", "", ""));
            }

        } else if (request.getParameter("btnAggiungiProtocolli") != null) {
            // fascicoloForm.setProtocolli((ProtocolloDelegate.getInstance()
            // .cercaProtocolli(utente, null)).values());
            session.setAttribute("provenienza", "fascicoloProtocollo");
            session.setAttribute("tornaFascicolo", Boolean.TRUE);
            return (mapping.findForward("associaProtocolli"));
        } else if (request.getParameter("btnRimuoviProtocolli") != null) {
            String[] idf = fascicoloForm.getProtocolliSelezionati();
            FascicoloVO fVO = impostaFascicoloVO(fascicoloForm, utente);

            if (FascicoloDelegate.getInstance().eliminaProtocolliFascicolo(fVO,
                    idf, utente.getValueObject().getUsername()) != ReturnValues.SAVED) {
                errors.add("errore_nel_salvataggio", new ActionMessage(
                        "errore_nel_salvataggio", "", ""));
                saveErrors(request, errors);

            }
            request.setAttribute("fascicoloId", new Integer(fascicoloForm
                    .getId()));

        } else if (request.getParameter("btnSelezionaProtocolli") != null) {
            String[] idf = fascicoloForm.getProtocolliSelezionati();
            FascicoloVO fVO = impostaFascicoloVO(fascicoloForm, utente);

            if (FascicoloDelegate.getInstance().salvaProtocolliFascicolo(fVO,
                    idf, utente.getValueObject().getUsername()) != ReturnValues.SAVED) {
                errors.add("errore_nel_salvataggio", new ActionMessage(
                        "errore_nel_salvataggio", "", ""));
                saveErrors(request, errors);

            }
            request.setAttribute("fascicoloId", new Integer(fascicoloForm
                    .getId()));

        } else if (request.getParameter("btnAggiungiProcedimenti") != null) {

            session.setAttribute("provenienza", "fascicoloProcedimenti");
            session.setAttribute("tornaFascicolo", Boolean.TRUE);
            session.removeAttribute("risultatiProcedimentiDaProtocollo");

            return (mapping.findForward("associaProcedimenti"));

        } else if (request.getParameter("btnSelezionaProcedimenti") != null) {
            String[] idp = fascicoloForm.getProcedimentiSelezionati();
            FascicoloVO fVO = impostaFascicoloVO(fascicoloForm, utente);

            if (FascicoloDelegate.getInstance().salvaProcedimentiFascicolo(fVO,
                    idp, utente.getValueObject().getUsername()) != ReturnValues.SAVED) {
                errors.add("errore_nel_salvataggio", new ActionMessage(
                        "errore_nel_salvataggio", "", ""));
                saveErrors(request, errors);

            }
            request.setAttribute("fascicoloId", new Integer(fascicoloForm
                    .getId()));
        } else if (request.getParameter("btnRimuoviProcedimenti") != null) {
            String[] idp = fascicoloForm.getProcedimentiSelezionati();
            FascicoloVO fVO = impostaFascicoloVO(fascicoloForm, utente);

            if (FascicoloDelegate.getInstance().eliminaProcedimentiFascicolo(
                    fVO, idp, utente.getValueObject().getUsername()) != ReturnValues.SAVED) {
                errors.add("errore_nel_salvataggio", new ActionMessage(
                        "errore_nel_salvataggio", "", ""));
                saveErrors(request, errors);

            }
            request.setAttribute("fascicoloId", new Integer(fascicoloForm
                    .getId()));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        caricaFascicolo(request, fascicoloForm);
        Collection tipiDocumento = LookupDelegate.getInstance()
                .getTipiDocumento(aooId);
        fascicoloForm.setTipiDocumento(tipiDocumento);
        return (mapping.findForward("input"));
    }

    private void impostaFascicoloForm(FascicoloVO fascicoloVO,
            FascicoloForm fascicoloForm, Utente utente) {
        fascicoloForm.setId(fascicoloVO.getId().intValue());
        fascicoloForm.setAooId(fascicoloVO.getAooId());
        fascicoloForm.setCodice(fascicoloVO.getCodice());

        // argomento titolario
        impostaTitolario(fascicoloForm, utente, fascicoloVO.getTitolarioId());
        fascicoloForm.setDataApertura(DateUtil.formattaData(fascicoloVO
                .getDataApertura().getTime()));
        if (fascicoloVO.getDataChiusura() != null) {
            fascicoloForm.setDataChiusura(DateUtil.formattaData(fascicoloVO
                    .getDataChiusura().getTime()));
        }
        fascicoloForm.setDescrizione(fascicoloVO.getDescrizione());
        fascicoloForm.setNome(fascicoloVO.getNome());
        fascicoloForm.setNote(fascicoloVO.getNote());
        fascicoloForm.setOggettoFascicolo(fascicoloVO.getOggetto());
        fascicoloForm.setProcessoId(fascicoloVO.getProcessoId());
        fascicoloForm.setProgressivo(fascicoloVO.getProgressivo());
        fascicoloForm.setRegistroId(fascicoloVO.getRegistroId());
        fascicoloForm.setUfficioResponsabileId(fascicoloVO
                .getUfficioResponsabileId());
        fascicoloForm.setUtenteResponsabileId(fascicoloVO
                .getUtenteResponsabileId());
        fascicoloForm.setStatoFascicolo(fascicoloVO.getStato());
        fascicoloForm.setUfficioCorrenteId(fascicoloVO
                .getUfficioIntestatarioId());
        fascicoloForm.setUtenteSelezionatoId(fascicoloVO
                .getUtenteIntestatarioId());
        fascicoloForm.setVersione(fascicoloVO.getVersione());
        if (fascicoloVO.getUfficioIntestatarioId() > 0) {
            Organizzazione org = Organizzazione.getInstance();
            Ufficio uff = org
                    .getUfficio(fascicoloVO.getUfficioIntestatarioId());
            Utente ute = org.getUtente(fascicoloVO.getUtenteIntestatarioId());
            fascicoloForm.setMittente(newMittente(uff, ute));
        }
        fascicoloForm.setAnnoRiferimento(fascicoloVO.getAnnoRiferimento());
        fascicoloForm.setTipoFascicolo(fascicoloVO.getTipoFascicolo());
        if (fascicoloVO.getDataEvidenza() != null) {
            fascicoloForm.setDataEvidenza(DateUtil.formattaData(fascicoloVO
                    .getDataEvidenza().getTime()));
        } else {
            fascicoloForm.setDataEvidenza(null);
        }
        if (fascicoloVO.getDataUltimoMovimento() != null) {
            fascicoloForm.setDataUltimoMovimento(DateUtil
                    .formattaData(fascicoloVO.getDataUltimoMovimento()
                            .getTime()));
        } else {
            fascicoloForm.setDataUltimoMovimento(null);
        }
        if (fascicoloVO.getDataCarico() != null) {
            fascicoloForm.setDataCarico(DateUtil.formattaData(fascicoloVO
                    .getDataCarico().getTime()));
        } else {
            fascicoloForm.setDataCarico(null);
        }
        if (fascicoloVO.getDataScarto() != null) {
            fascicoloForm.setDataScarto(DateUtil.formattaData(fascicoloVO
                    .getDataScarto().getTime()));
        } else {
            fascicoloForm.setDataScarto(null);
        }

        if (fascicoloVO.getDataScarico() != null) {
            fascicoloForm.setDataScarico(DateUtil.formattaData(fascicoloVO
                    .getDataScarico().getTime()));
        } else {
            fascicoloForm.setDataScarico(DateUtil.formattaData(System
                    .currentTimeMillis()));
        }

        fascicoloForm.setPosizioneSelezionataId(fascicoloVO
                .getPosizioneFascicolo());
        fascicoloForm.setPosizioneSelezionata(""
                + fascicoloVO.getPosizioneFascicolo());

        fascicoloForm.setVersione(fascicoloVO.getVersione());
        fascicoloForm
                .setCollocazioneLabel1(fascicoloVO.getCollocazioneLabel1());
        fascicoloForm
                .setCollocazioneLabel2(fascicoloVO.getCollocazioneLabel2());
        fascicoloForm
                .setCollocazioneLabel3(fascicoloVO.getCollocazioneLabel3());
        fascicoloForm
                .setCollocazioneLabel4(fascicoloVO.getCollocazioneLabel4());
        fascicoloForm.setCollocazioneValore1(fascicoloVO
                .getCollocazioneValore1());
        fascicoloForm.setCollocazioneValore2(fascicoloVO
                .getCollocazioneValore2());
        fascicoloForm.setCollocazioneValore3(fascicoloVO
                .getCollocazioneValore3());
        fascicoloForm.setCollocazioneValore4(fascicoloVO
                .getCollocazioneValore4());

    }

    private void impostaNuovoFascicoloForm(int titolarioId,
            FascicoloForm fascicolo, Utente utente) {
        fascicolo.inizializzaForm();
        // argomento titolario
        impostaTitolario(fascicolo, utente, titolarioId);
        fascicolo.setId(0);
        fascicolo.setUfficioResponsabileId(utente.getUfficioInUso());
        fascicolo.setUtenteResponsabileId(utente.getValueObject().getId()
                .intValue());
        fascicolo.setDataApertura(DateUtil.formattaData((new Date(System
                .currentTimeMillis())).getTime()));
        fascicolo.setAnnoRiferimento(DateUtil.getYear(new Date(System
                .currentTimeMillis())));
        fascicolo.setUfficioCorrenteId(utente.getUfficioInUso());
        fascicolo.setAooId(utente.getAreaOrganizzativa().getId().intValue());
        Collection tipiDoc = LookupDelegate.getInstance().getTipiDocumento(
                fascicolo.getAooId());
        fascicolo.setTipiDocumento(tipiDoc);
        fascicolo.setVersione(0);

    }

    private boolean isFascicoloClosable(FascicoloForm fascicolo) {

        boolean closable = true;
        Iterator it = fascicolo.getDocumentiFascicolo().iterator();
        while (it.hasNext()) {
            FileVO d = (FileVO) it.next();
            if (Parametri.STATO_LAVORAZIONE.equals(d.getStatoArchivio())) {
                closable = false;
                break;
            }
        }
        return closable;
    }

    private void aggiornaFascicoloForm(Fascicolo fascicolo,
            FascicoloForm fForm, Utente utente) {
        impostaFascicoloForm(fascicolo.getFascicoloVO(), fForm, utente);
        aggiornaStatoFascicoloForm(fascicolo.getFascicoloVO(), fForm, utente);
        fForm.setDocumentiFascicolo(fascicolo.getDocumenti());
        Iterator it = fascicolo.getProtocolli().iterator();
        Collection protocolliView = new ArrayList();
        while (it.hasNext()) {
            ProtocolloFascicoloVO pf = (ProtocolloFascicoloVO) it.next();
            protocolliView.add(ProtocolloDelegate.getInstance()
                    .getProtocolloView(pf.getProtocolloId()));
        }

        fForm.setProtocolliFascicolo(protocolliView);
        fForm.setFaldoniFascicolo(fascicolo.getFaldoni());
        fForm.setProcedimentiFascicolo(fascicolo.getProcedimenti());
        fForm.setProcedimenti(fascicolo.getProcedimenti());
        FascicoloVO fVO = fascicolo.getFascicoloVO();
        fForm.setCodice(fVO.getAnnoRiferimento()
                + StringUtil.formattaNumeroProtocollo(String.valueOf(fVO
                        .getProgressivo()), 6));
    }

    private FascicoloVO impostaFascicoloVO(FascicoloForm fForm, Utente utente) {
        FascicoloVO fVO = new FascicoloVO();
        fVO.setAooId(utente.getUfficioVOInUso().getAooId());
        fVO.setRegistroId(utente.getRegistroInUso());
        fVO.setCodice(fForm.getCodice());
        Date dataApertura = DateUtil.toDate(fForm.getDataApertura());
        if (dataApertura != null) {
            fVO.setDataApertura(new java.sql.Date(dataApertura.getTime()));
        } else {
            fVO.setDataApertura(null);
        }
        Date dataChiusura = DateUtil.toDate(fForm.getDataChiusura());
        if (dataChiusura != null) {
            fVO.setDataChiusura(new java.sql.Date(dataChiusura.getTime()));
        } else {
            fVO.setDataChiusura(null);
        }

        fVO.setDescrizione(fForm.getDescrizione());
        fVO.setId(fForm.getId());
        fVO.setNome(fForm.getNome());
        fVO.setNote(fForm.getNote());
        fVO.setOggetto(fForm.getOggettoFascicolo());
        fVO.setStato(fForm.getStatoFascicolo());
        // titolario
        if (fForm.getTitolario() != null) {
            fVO.setTitolarioId(fForm.getTitolario().getId().intValue());
        }

        // trattato da
        fVO.setUfficioResponsabileId(utente.getUfficioInUso());
        fVO.setUtenteResponsabileId(utente.getValueObject().getId().intValue());
        // ufficio
        fVO.setUfficioIntestatarioId(fForm.getUfficioCorrenteId());
        fVO.setUtenteIntestatarioId(fForm.getUtenteSelezionatoId());

        fVO.setVersione(fForm.getVersione());

        if (fVO.getId().intValue() == 0) {
            fVO.setRowCreatedUser(utente.getValueObject().getUsername());
            fVO.setRowCreatedTime(new Date(System.currentTimeMillis()));
            fVO.setDataCarico(new Date(System.currentTimeMillis()));
        } else {
            fVO.setRowUpdatedUser(utente.getValueObject().getUsername());
            Date dataCarico = DateUtil.toDate(fForm.getDataCarico());
            if (dataCarico != null) {
                fVO.setDataCarico(new java.sql.Date(dataCarico.getTime()));
            } else {
                fVO.setDataCarico(null);
            }
        }
        fVO.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        fVO.setAnnoRiferimento(fForm.getAnnoRiferimento());
        fVO.setTipoFascicolo(fForm.getTipoFascicolo());

        Date dataEvidenza = DateUtil.toDate(fForm.getDataEvidenza());

        if (dataEvidenza != null
                && fVO.getStato() == Parametri.STATO_FASCICOLO_IN_EVIDENZA) {
            fVO.setDataEvidenza(new java.sql.Date(dataEvidenza.getTime()));
        } else {
            fVO.setDataEvidenza(null);
        }

        Date dataUltimoMovimento = DateUtil.toDate(fForm
                .getDataUltimoMovimento());
        if (dataUltimoMovimento != null) {
            fVO.setDataUltimoMovimento(new java.sql.Date(dataUltimoMovimento
                    .getTime()));
        } else {
            fVO.setDataUltimoMovimento(null);
        }

        Date dataScarto = DateUtil.toDate(fForm.getDataScarto());
        if (dataScarto != null) {
            fVO.setDataScarto(new java.sql.Date(dataScarto.getTime()));
        } else {
            fVO.setDataScarto(null);
        }

        Date dataScarico = DateUtil.toDate(fForm.getDataScarico());
        if (dataScarico != null) {
            fVO.setDataScarico(new java.sql.Date(dataScarico.getTime()));
        } else {
            fVO.setDataScarico(null);
        }

        fVO.setCollocazioneLabel1(fForm.getCollocazioneLabel1());
        fVO.setCollocazioneLabel2(fForm.getCollocazioneLabel2());
        fVO.setCollocazioneLabel3(fForm.getCollocazioneLabel3());
        fVO.setCollocazioneLabel4(fForm.getCollocazioneLabel4());
        fVO.setCollocazioneValore1(fForm.getCollocazioneValore1());
        fVO.setCollocazioneValore2(fForm.getCollocazioneValore2());
        fVO.setCollocazioneValore3(fForm.getCollocazioneValore3());
        fVO.setCollocazioneValore4(fForm.getCollocazioneValore4());
        return fVO;
    }

    private void impostaTitolario(FascicoloForm form, Utente utente,
            int titolarioId) {
        int ufficioId = utente.getUfficioInUso();
        if (utente.getAreaOrganizzativa().getDipendenzaTitolarioUfficio() == 1) {
            ufficioId = form.getUfficioCorrenteId();
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);
    }

    private void impostaTitolario(ProtocolloForm form, Utente utente,
            int titolarioId) {
        int ufficioId = utente.getUfficioInUso();
        if (utente.getAreaOrganizzativa().getDipendenzaTitolarioUfficio() == 1) {
            ufficioId = form.getUfficioCorrenteId();
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);

    }

    private void impostaTitolario(DocumentoForm form, Utente utente,
            int titolarioId) {
        int ufficioId = utente.getUfficioInUso();
        if (utente.getAreaOrganizzativa().getDipendenzaTitolarioUfficio() == 1) {
            ufficioId = form.getUfficioCorrenteId();
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);

    }

    protected void caricaFascicolo(HttpServletRequest request,
            FascicoloForm form) {
        Integer fascicoloId = (Integer) request.getAttribute("fascicoloId");
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        Fascicolo fascicolo;

        Integer versioneId = (Integer) request.getAttribute("versioneId");

        if (fascicoloId != null) {
            FascicoloDelegate fd = FascicoloDelegate.getInstance();
            int id = fascicoloId.intValue();
            if (versioneId == null) {
                fascicolo = fd.getFascicoloById(id);
                form.setVersioneDefault(true);
            } else {
                int versione = versioneId.intValue();
                fascicolo = fd.getFascicoloByIdVersione(id, versione);
                form.setVersioneDefault(false);
            }
            session.setAttribute(Constants.FASCICOLO, fascicolo);
        } else {
            fascicolo = (Fascicolo) session.getAttribute(Constants.FASCICOLO);
        }
        aggiornaFascicoloForm(fascicolo, form, utente);
    }

    private void aggiornaStatoFascicoloForm(FascicoloVO fascicolo,
            FascicoloForm form, Utente utente) {
        boolean modificabile = false;
        int statoFascicolo = fascicolo.getStato();
        if (statoFascicolo != Parametri.STATO_FASCICOLO_SCARTATO) {
            if (utente.getUfficioVOInUso().getParentId() == 0
                    || utente.isUtenteAbilitatoSuUfficio(fascicolo
                            .getUfficioIntestatarioId())
                    || utente.isUtenteAbilitatoSuUfficio(fascicolo
                            .getUfficioResponsabileId())) {
                modificabile = true;
            }

        }
        form.setModificabile(modificabile);
    }

    private boolean fascicoloModificato(FascicoloVO fVO) {
        FascicoloDelegate fd = FascicoloDelegate.getInstance();
        FascicoloVO f = fd.getFascicoloVOById(fVO.getId().intValue());
        if (f.getUfficioIntestatarioId() != fVO.getUfficioIntestatarioId()
                || f.getTitolarioId() != fVO.getTitolarioId()
                || f.getUtenteIntestatarioId() != fVO.getUtenteIntestatarioId()
                || f.getStato() != fVO.getStato()
                || !StringUtil.getStringa(f.getOggetto()).trim()
                        .equalsIgnoreCase(
                                StringUtil.getStringa(fVO.getOggetto()).trim())
                || f.getDataEvidenza() != fVO.getDataEvidenza()) {
            return true;
        }
        return false;
    }

    private AssegnatarioView newMittente(Ufficio ufficio, Utente utente) {
        UfficioVO uffVO = ufficio.getValueObject();
        AssegnatarioView mittente = new AssegnatarioView();
        mittente.setUfficioId(uffVO.getId().intValue());
        mittente.setDescrizioneUfficio(ufficio.getPath());
        mittente.setNomeUfficio(uffVO.getDescription());
        if (utente != null) {
            UtenteVO uteVO = utente.getValueObject();
            mittente.setUtenteId(uteVO.getId().intValue());
            mittente.setNomeUtente(uteVO.getFullName());
        }
        return mittente;
    }

    protected void assegnaAdUfficio(FascicoloForm form, int ufficioId) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(ufficioId);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(ufficioId);
        ass.setNomeUfficio(uff.getValueObject().getDescription());
        ass.setDescrizioneUfficio(uff.getPath());
        form.setMittente(ass);
    }

    protected void assegnaAdUtente(FascicoloForm form) {
        AssegnatarioView ass = new AssegnatarioView();
        ass.setUfficioId(form.getUfficioCorrenteId());
        ass.setNomeUfficio(form.getUfficioCorrente().getDescription());
        ass.setDescrizioneUfficio(form.getUfficioCorrentePath());
        ass.setUtenteId(form.getUtenteSelezionatoId());
        UtenteVO ute = form.getUtente(form.getUtenteSelezionatoId());
        ass.setNomeUtente(ute.getFullName());
        form.setMittente(ass);
    }

    private ActionForward setOperazione(FascicoloForm fascicoloForm,
            ActionMapping mapping, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        Fascicolo fascicolo = (Fascicolo) session
                .getAttribute(Constants.FASCICOLO);
        // aggiornaFascicoloForm(fascicolo, fascicoloForm, utente);
        fascicolo.getFascicoloVO().setUfficioResponsabileId(
                utente.getUfficioInUso());
        fascicolo.getFascicoloVO().setUtenteResponsabileId(
                utente.getValueObject().getId().intValue());

        FascicoloDelegate fd = FascicoloDelegate.getInstance();
        int fascicoloId = fascicoloForm.getId();
        int returnValue = ReturnValues.UNKNOWN;
        fascicolo.getFascicoloVO().setDataEvidenza(null);
        if (fascicoloForm.getOperazione().equals(
                FascicoloForm.CHIUSURA_FASCICOLO)) {
            // chiusura fascicolo
            fascicolo.getFascicoloVO().setDataChiusura(
                    new Date(System.currentTimeMillis()));
            returnValue = fd.chiudiFascicolo(fascicolo, utente);
        } else if (fascicoloForm.getOperazione().equals(
                FascicoloForm.CANCELLAZIONE_FASCICOLO)) {
            returnValue = fd.cancellaFascicolo(fascicoloId);
            if (returnValue == ReturnValues.UNKNOWN) {
                errors
                        .add(
                                "fascicolo",
                                new ActionMessage(
                                        "record_non_cancellabile",
                                        "si è verificato un errore in fase di salvataggio dei dati",
                                        ""));
                saveErrors(request, errors);
            } else if (returnValue == ReturnValues.NOT_SAVED) {
                errors
                        .add(
                                "fascicolo",
                                new ActionMessage(
                                        "record_non_cancellabile",
                                        "il fascicolo",
                                        "Verificare se esistono collegamenti con protocolli, documenti, faldoni, procedimenti"));
                saveErrors(request, errors);
            } else if (returnValue == ReturnValues.SAVED) {
                errors.add("fascicolo", new ActionMessage("cancellazione_ok",
                        "", ""));
                saveErrors(request, errors);
                return (mapping.findForward("lista"));

            }
        } else if (fascicoloForm.getOperazione().equals(
                FascicoloForm.RIAPERTURA_FASCICOLO)) {
            fascicolo.getFascicoloVO().setDataChiusura(null);
            fascicolo.getFascicoloVO().setDataCarico(
                    new Date(System.currentTimeMillis()));

            returnValue = fd.riapriFascicolo(fascicolo, utente);
        } else if (fascicoloForm.getOperazione().equals(
                FascicoloForm.SCARTO_FASCICOLO)) {
            int massimario = (fascicoloForm.getTitolario() != null ? fascicoloForm
                    .getTitolario().getMassimario()
                    : 0);
            if (massimario > 0) {
                // controllo il massimario di scarto
                Date dataCreazione = DateUtil.toDate(fascicoloForm
                        .getDataApertura());

                Date dataCorrente = new Date(System.currentTimeMillis());
                if (!dataCorrente.after(DateUtil.getDataFutura(dataCreazione
                        .getTime(), massimario))) {
                    errors.add("fascicolo_no_chiusura", new ActionMessage(
                            "fascicollo.scarto_non_eseguibile",
                            "" + massimario, ""));
                    saveErrors(request, errors);
                    return (mapping.findForward("confermaOperazione"));
                }

            }
            // scarto fascicolo
            returnValue = fd.scartaFascicolo(fascicoloForm.getId(),
                    fascicoloForm.getPropostaScarto(), utente.getValueObject()
                            .getUsername(), fascicoloForm.getVersione());
        }

        request.setAttribute("fascicoloId", new Integer(fascicoloId));
        caricaFascicolo(request, fascicoloForm);
        if (returnValue == ReturnValues.INVALID) {
            errors.add("errore_nel_salvataggio", new ActionMessage(
                    "errore_nel_salvataggio", "", ""));
            saveErrors(request, errors);
            return (mapping.findForward("confermaOperazione"));
        }

        return (mapping.findForward("input"));

    }
}