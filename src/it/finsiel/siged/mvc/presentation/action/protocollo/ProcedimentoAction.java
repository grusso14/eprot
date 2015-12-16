package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Procedimento;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.AmministrazioneDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.ProcedimentoDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.UfficioDelegate;
import it.finsiel.siged.mvc.business.UtenteDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProcedimentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;

import java.util.SortedMap;
import java.util.TreeMap;

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

public class ProcedimentoAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ProcedimentoAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        ProcedimentoForm pForm = (ProcedimentoForm) form;
        Organizzazione org = Organizzazione.getInstance();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        int aooId = utente.getRegistroVOInUso().getAooId();
        boolean ufficioCompleto = (utente.getUfficioVOInUso().getTipo().equals(
                UfficioVO.UFFICIO_CENTRALE) || utente.getUfficioVOInUso()
                .getTipo().equals(UfficioVO.UFFICIO_SEMICENTRALE));
        boolean indietroVisibile = false;
        pForm.setIndietroVisibile(indietroVisibile);
        if (request.getParameter("annullaAction") != null
                || request.getAttribute("procedimentoPrecaricato") != null
                || request.getParameter("visualizzaProcedimentoId") != null) {
            Ufficio uff = org.getUfficio(utente.getUfficioInUso());
            pForm.inizializzaForm();
            pForm.setAooId(utente.getValueObject().getAooId());
            AlberoUfficiBO.impostaUfficio(utente, pForm, ufficioCompleto);
            pForm.setStatiProcedimento(LookupDelegate.getStatiProcedimento());
            pForm.setTipiFinalita(LookupDelegate.getTipiFinalitaProcedimento());

            // pForm.setTipiProcedimento(AmministrazioneDelegate.getInstance()
            // .getTipiProcedimento(aooId));
            int ufficioCorrente = utente.getUfficioVOInUso().getId().intValue();
            pForm.setTipiProcedimento(AmministrazioneDelegate.getInstance()
                    .getTipiProcedimento(aooId, ufficioCorrente));
            pForm.setPosizioniProcedimento(LookupDelegate
                    .getPosizioniProcedimento());
            pForm.setUfficioCorrente(uff.getValueObject());
            caricaReferenti(uff.getValueObject().getId().intValue(), pForm, org);
            session.setAttribute(mapping.getAttribute(), pForm);
        }
        if (request.getAttribute("procedimentoPrecaricato") != null) {
            ProcedimentoVO pre = (ProcedimentoVO) request
                    .getAttribute("procedimentoPrecaricato");
            request.removeAttribute("procedimentoPrecaricato");
            aggiornaProcedimentoForm(pre, pForm, utente);
            AlberoUfficiBO.impostaUfficio(utente, pForm, ufficioCompleto);
            impostaTitolario(pForm, utente, pForm.getTitolarioSelezionatoId());
            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                indietroVisibile = true;
                pForm.setIndietroVisibile(indietroVisibile);
            }
            return mapping.findForward("input");
        }
        if (request.getParameter("visualizzaProcedimentoId") != null) {
            if (NumberUtil.isInteger(request
                    .getParameter("visualizzaProcedimentoId"))) {
                Procedimento pro = ProcedimentoDelegate
                        .getInstance()
                        .getProcedimentoById(
                                NumberUtil
                                        .getInt(request
                                                .getParameter("visualizzaProcedimentoId")));
                if (pro != null) {
                    caricaProcedimentoForm(pForm, pro.getProcedimentoVO());
                    pForm.setTipiProcedimento(AmministrazioneDelegate
                            .getInstance().getTipiProcedimento(aooId,
                                    pForm.getUfficioCorrenteId()));
                    pForm.setProtocolli(pro.getProtocolli());
                    pForm.setFaldoni(pro.getFaldoni());
                    pForm.setFascicoli(pro.getFascicoli());
                    AlberoUfficiBO.impostaUfficio(utente, pForm,
                            ufficioCompleto);
                    TitolarioBO.impostaTitolario(pForm, pro.getProcedimentoVO()
                            .getUfficioId(), pro.getProcedimentoVO()
                            .getTitolarioId());
                    caricaReferenti(pForm.getUfficioCorrenteId(), pForm, org);
                } else {
                    errors.add("general", new ActionMessage(
                            "procedimento.non.torvato"));
                    saveErrors(request, errors);
                    return (mapping.findForward("input"));
                }
            }

        }
        if (pForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, pForm, ufficioCompleto);
        }

        if (pForm.getTitolario() == null) {
            impostaTitolario(pForm, utente, 0);
        }
        // -------------- GESTIONE UFFICI e TITOLARIO ------------------ //
        if (request.getParameter("impostaUfficioAction") != null) {
            pForm.setUfficioCorrenteId(pForm.getUfficioSelezionatoId());
            AlberoUfficiBO.impostaUfficio(utente, pForm, ufficioCompleto);
            impostaTitolario(pForm, utente, 0);
            caricaReferenti(pForm.getUfficioSelezionatoId(), pForm, org);
            return mapping.findForward("input");
        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            pForm
                    .setUfficioCorrenteId(pForm.getUfficioCorrente()
                            .getParentId());
            AlberoUfficiBO.impostaUfficio(utente, pForm, ufficioCompleto);
            impostaTitolario(pForm, utente, 0);
            caricaReferenti(pForm.getUfficioCorrenteId(), pForm, org);

        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario().getId()
                        .intValue());
            }
            TitolarioBO.impostaTitolario(pForm, pForm.getUfficioCorrenteId(),
                    pForm.getTitolarioSelezionatoId());
            return mapping.findForward("input");
        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            TitolarioBO.impostaTitolario(pForm, pForm.getUfficioCorrenteId(),
                    pForm.getTitolarioPrecedenteId());
            if (pForm.getTitolario() != null) {
                pForm.setTitolarioPrecedenteId(pForm.getTitolario()
                        .getParentId());
            }
            return mapping.findForward("input");
        }// ---------------------------------------------------------------
        // //
        else if (request.getParameter("visualizzaFascicoloId") != null) {
            // visualizzaFascicoloId
            if (NumberUtil.isInteger(request
                    .getParameter("visualizzaFascicoloId"))) {
                Integer fId = Integer.valueOf(request
                        .getParameter("visualizzaFascicoloId"));
                request.setAttribute("fascicoloId", fId);
                return mapping.findForward("visualizzaFascicolo");
            } else {
                logger.warn("Id Fascicolo non di formato numerico:"
                        + request.getParameter("visualizzaFascicoloId"));
            }
            return mapping.findForward("input");
        } else if (request.getParameter("btnCercaFascicoliDaProcedimento") != null) {
            session.setAttribute("tornaProcedimento", Boolean.TRUE);
            String nomeFascicolo = pForm.getCercaFascicoloNome();
            pForm.setCercaFascicoloNome("");
            request.setAttribute("cercaFascicoliDaProcedimento", nomeFascicolo);
            request.setAttribute("provenienza", "FascicoliDaProcedimento");
            return mapping.findForward("cercaFascicoliDaProcedimento");
        } else if (request.getParameter("rimuoviFascicoli") != null) {
            // fascicoliSelezionati
            String[] ids = pForm.getFascicoliSelezionati();
            rimuoviFascicoli(ids, pForm);
        } else if (request.getParameter("btnCercaProtocolliDaProcedimento") != null) {
            request.setAttribute("cercaProtocolliDaProcedimento", pForm
                    .getCercaProtocolloOggetto());
            pForm.setCercaProtocolloOggetto("");
            session.setAttribute("provenienza", "protocolliDaProcedimento");
            session.setAttribute("indietroProtocolliDaProcedimento",
                    Boolean.TRUE);
            session.setAttribute("tornaProcedimento", Boolean.TRUE);
            return mapping.findForward("cercaProtocolliDaProcedimento");
        } else if (request.getParameter("rimuoviProtocolli") != null) {
            // protocolliSelezionati
            String[] ids = pForm.getProtocolliSelezionati();
            rimuoviProtocolli(ids, pForm);
        } else if (request.getParameter("visualizzaProtocolloId") != null) {
            // visualizzaProtocolloId
        } else if (request.getParameter("btnCercaFaldoniDaProcedimento") != null) {
            request.setAttribute("cercaFaldoneDaProcedimento", pForm
                    .getCercaFaldoneOggetto());
            pForm.setCercaFaldoneOggetto("");
            // request.removeAttribute("oggetto");

            session.setAttribute("provenienza", "faldoniDaProcedimento");
            session.setAttribute("tornaProcedimento", Boolean.TRUE);
            return mapping.findForward("cercaFaldoniDaProcedimento");
        } else if (request.getParameter("rimuoviFaldoni") != null) {
            // faldoniSelezionati
            String[] ids = pForm.getFaldoniSelezionati();
            rimuoviFaldoni(ids, pForm);
        } else if (request.getParameter("visualizzaFaldoneId") != null) {
            // visualizzaFaldoneId
            request.setAttribute("caricaFaldoneId", request
                    .getParameter("visualizzaFaldoneId"));
            return mapping.findForward("visualizzaFaldone");
        } else if (request.getParameter("impostaPosizioneAction") != null) {
            return mapping.findForward("input");
        } else if (request.getParameter("btnAnnulla") != null) {

            if (Boolean.TRUE.equals(session.getAttribute("tornaProtocollo"))) {
                session.removeAttribute("tornaProtocollo");
                pForm.setOggettoProcedimento("");
                ProtocolloForm protForm = (ProtocolloForm) session
                        .getAttribute("protocolloForm");
                if (protForm instanceof ProtocolloIngressoForm) {
                    return (mapping.findForward("tornaProtocolloIngresso"));
                } else {
                    return (mapping.findForward("tornaProtocolloUscita"));
                }
            }
        } else if (request.getParameter("salvaAction") != null) {
            if (!pForm.getPosizione().equals("E")) {
                pForm.setDataEvidenza(null);
            }
            errors = pForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                ProcedimentoVO vo = new ProcedimentoVO();
                Procedimento pro = new Procedimento();
                caricaProcedimentoVO(pForm, vo);
                pro.setProcedimentoVO(vo);
                pro.setFaldoni(pForm.getFaldoni());
                pro.setFascicoli(pForm.getFascicoli());
                pro.setProtocolli(pForm.getProtocolli());
                ProcedimentoVO newVo = new ProcedimentoVO();
                boolean isModificato = true;
                if (pForm.getProcedimentoId() > 0) {
                    isModificato = procedimentoModificato(vo);
                }
                if (isModificato) {
                    newVo = ProcedimentoDelegate.getInstance()
                            .salvaProcedimento(pro, utente);
                } else {
                    newVo = ProcedimentoDelegate.getInstance()
                            .aggiornaProcedimento(pro, utente);
                }

                if (newVo.getReturnValue() != ReturnValues.SAVED) {
                    errors.add("general", new ActionMessage(
                            "errore_nel_salvataggio"));
                    saveErrors(request, errors);
                } else {
                    // salvato!
                    if (pForm.getProcedimentoId() == 0) {
                        ProtocolloProcedimentoVO proc = new ProtocolloProcedimentoVO();
                        proc.setProcedimentoId(newVo.getId().intValue());
                        proc.setProtocolloId(pForm.getProtocolloId());

                        /*
                         * Modifica del 03032006 Greco Bosco
                         */
                        proc.setNumeroProcedimento(newVo
                                .getNumeroProcedimento());
                        // proc.setNumeroProcedimento(newVo.getAnno() + "/"
                        // + newVo.getNumero());
                        proc.setOggetto(newVo.getOggetto());
                        ProtocolloForm protocolloForm = (ProtocolloForm) session
                                .getAttribute("protocolloForm");
                        protocolloForm.aggiungiProcedimento(proc);
                        protocolloForm.setStato("P");
                        session.setAttribute("protocolloForm", protocolloForm);
                        if ("I".equals(protocolloForm.getFlagTipo())) {
                            return mapping
                                    .findForward("tornaProtocolloIngresso");
                        } else {
                            return mapping.findForward("tornaProtocolloUscita");
                        }
                    } else {
                        caricaProcedimentoForm(pForm, newVo);
                        errors.add("general", new ActionMessage("msg.salvato"));
                        saveErrors(request, errors);
                    }
                }
            }

        } else if (request.getParameter("btnStoria") != null) {
            request.setAttribute("procedimentoId", new Integer(pForm
                    .getProcedimentoId()));
            caricaProcedimento(request, pForm);
            return (mapping.findForward("storiaProcedimento"));
        } else if (request.getAttribute("procedimentoId") != null) {
            caricaProcedimento(request, pForm);
            if (request.getAttribute("btnRitornoStoria") != null) {
                request.removeAttribute("btnRitornoStoria");
                return (mapping.findForward("input"));

            } else {
                return (mapping.findForward("visualizzaProcedimento"));
            }

        }
        return (mapping.findForward("input"));
    }

    private void rimuoviFascicoli(String[] ids, ProcedimentoForm form) {
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                form.rimuoviFascicolo(ids[i]);
            }
        }
    }

    private void rimuoviFaldoni(String[] ids, ProcedimentoForm form) {
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                form.rimuoviFaldone(ids[i]);
            }
        }
    }

    private void rimuoviProtocolli(String[] ids, ProcedimentoForm form) {
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                form.rimuoviProtocollo(ids[i]);
            }
        }
    }

    private void aggiornaProcedimentoForm(ProcedimentoVO pVO,
            ProcedimentoForm pForm, Utente utente) {
        if (pVO.getDataAvvio() != null)
            pForm.setDataAvvio(DateUtil.formattaData(pVO.getDataAvvio()
                    .getTime()));
        if (pVO.getDataEvidenza() != null)
            pForm.setDataEvidenza(DateUtil.formattaData(pVO.getDataEvidenza()
                    .getTime()));
        pForm.setNote(pVO.getNote());
        pForm.setProgressivo(pVO.getNumero());

        pForm.setNumeroProcedimento(pVO.getNumeroProcedimento());
        pForm.setNumeroProtocollo(pVO.getNumeroProtovollo());
        pForm.setOggettoProcedimento(pVO.getOggetto());
        pForm.setPosizione(pVO.getPosizione());
        pForm.setProtocolloId(pVO.getProtocolloId());
        pForm.setReferenteId(pVO.getReferenteId());
        pForm.setResponsabile(pVO.getResponsabile());
        pForm.setStatoId(pVO.getStatoId());
        pForm.setTipoFinalitaId(pVO.getTipoFinalitaId());
        pForm.setTipoProcedimentoId(pVO.getTipoProcedimentoId());
        pForm.setTipoProcedimento(pVO.getTipoProcedimentoDesc());
        pForm.setTitolarioSelezionatoId(pVO.getTitolarioId());
        pForm.setUfficioCorrenteId(pVO.getUfficioId());
        pForm.setVersione(pVO.getVersione());
        Organizzazione org = Organizzazione.getInstance();
        Ufficio ufficioCorrente = org.getUfficio(pVO.getUfficioId());
        Ufficio uff = ufficioCorrente;
        pForm.setUfficioCorrentePath(uff.getValueObject().getDescription());
        impostaTitolario(pForm, utente, pVO.getUfficioId());
        UtenteDelegate referente = UtenteDelegate.getInstance();
        pForm.setNomeReferente(referente.getUtente(pVO.getReferenteId())
                .getUsername());
        ProtocolloDelegate protocollo = ProtocolloDelegate.getInstance();
        pForm.setNumeroProtocollo(pVO.getAnno()
                + StringUtil.formattaNumeroProtocollo(protocollo
                        .getProtocolloById(pVO.getProtocolloId())
                        .getNumProtocollo()
                        + "", 7));

        TitolarioBO.impostaTitolario(pForm, pVO.getUfficioId(), pVO
                .getTitolarioId());

    }

    private void caricaProcedimentoVO(ProcedimentoForm form, ProcedimentoVO vo) {
        vo.setId(form.getProcedimentoId());
        vo.setAooId(form.getAooId());
        vo.setAnno(form.getAnno());
        if (DateUtil.isData(form.getDataAvvio()))
            vo.setDataAvvio(DateUtil.toDate(form.getDataAvvio()));
        if (DateUtil.isData(form.getDataEvidenza()))
            vo.setDataEvidenza(DateUtil.toDate(form.getDataEvidenza()));
        vo.setNote(form.getNote());
        vo.setNumero(form.getNumero());
        vo.setNumeroProcedimento(form.getNumeroProcedimento());
        vo.setNumeroProtovollo(form.getNumeroProtocollo());
        vo.setOggetto(form.getOggettoProcedimento());
        vo.setPosizione(form.getPosizione());
        vo.setProtocolloId(form.getProtocolloId());
        vo.setReferenteId(form.getReferenteId());
        vo.setResponsabile(form.getResponsabile());
        vo.setStatoId(form.getStatoId());
        vo.setTipoFinalitaId(form.getTipoFinalitaId());
        vo.setTipoProcedimentoId(form.getTipoProcedimentoId());
        vo.setVersione(form.getVersione());
        if (form.getTitolario() != null) {
            vo.setTitolarioId(form.getTitolario().getId().intValue());
        } else {
            vo.setTitolarioId(0);
        }
        vo.setUfficioId(form.getUfficioCorrenteId());
    }

    protected void caricaProcedimento(HttpServletRequest request,
            ProcedimentoForm form) {
        Integer procedimentoId = (Integer) request
                .getAttribute("procedimentoId");
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        ProcedimentoVO procedimento = new ProcedimentoVO();
        Integer versioneId = (Integer) request.getAttribute("versioneId");
        if (procedimentoId != null) {
            ProcedimentoDelegate fd = ProcedimentoDelegate.getInstance();
            if (versioneId == null) {
                procedimento = fd.getProcedimentoVO(procedimentoId.intValue());
                form.setVersioneDefault(true);
            } else {
                int versione = versioneId.intValue();
                int id = procedimentoId.intValue();

                int versioneCorrente = fd.getProcedimentoVO(
                        procedimentoId.intValue()).getVersione();
                if (versioneCorrente > versione) {
                    form.setVersioneDefault(false);
                    procedimento = fd.getProcedimentoByIdVersione(id, versione);
                } else {
                    form.setVersioneDefault(true);
                    procedimento = fd.getProcedimentoVO(id);
                }

            }
            session.setAttribute(Constants.PROCEDIMENTO, procedimento.getId());

        } else {
            procedimento = (ProcedimentoVO) session
                    .getAttribute(Constants.PROCEDIMENTO);
        }
        aggiornaProcedimentoForm(procedimento, form, utente);
    }

    private void caricaProcedimentoForm(ProcedimentoForm form, ProcedimentoVO vo) {
        form.setProcedimentoId(vo.getId().intValue());
        form.setAnno(vo.getAnno());
        if (vo.getDataAvvio() != null)
            form.setDataAvvio(DateUtil
                    .formattaData(vo.getDataAvvio().getTime()));
        if (vo.getDataEvidenza() != null)
            form.setDataEvidenza(DateUtil.formattaData(vo.getDataEvidenza()
                    .getTime()));
        form.setNote(vo.getNote());
        form.setNumero(vo.getNumero());
        form.setNumeroProcedimento(vo.getNumeroProcedimento());
        form.setNumeroProtocollo(vo.getNumeroProtovollo());
        form.setOggettoProcedimento(vo.getOggetto());
        form.setPosizione(vo.getPosizione());
        form.setProtocolloId(vo.getProtocolloId());
        form.setReferenteId(vo.getReferenteId());
        form.setResponsabile(vo.getResponsabile());
        form.setStatoId(vo.getStatoId());
        form.setTipoFinalitaId(vo.getTipoFinalitaId());
        form.setTipoProcedimentoId(vo.getTipoProcedimentoId());
        form.setTitolarioSelezionatoId(vo.getTitolarioId());
        form.setUfficioCorrenteId(vo.getUfficioId());
        form.setVersione(vo.getVersione());
    }

    private void caricaReferenti(int ufficioId, ProcedimentoForm form,
            Organizzazione org) {
        String[] ids = UfficioDelegate.getInstance().getReferentiByUfficio(
                ufficioId);
        SortedMap referenti = new TreeMap();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                Utente u = org.getUtente(NumberUtil.getInt(ids[i]));
                referenti.put(u.getValueObject().getCognomeNome(), u
                        .getValueObject());

                // form.aggiungiReferente(u.getValueObject());
            }

            form.setReferenti(referenti);
        }
    }

    private void impostaTitolario(ProcedimentoForm form, Utente utente,
            int titolarioId) {
        int ufficioId;
        if (utente.getAreaOrganizzativa().getDipendenzaTitolarioUfficio() == 1) {
            ufficioId = form.getUfficioCorrenteId();
        } else {
            ufficioId = utente.getUfficioInUso();
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);
    }

    private boolean procedimentoModificato(ProcedimentoVO pVO) {
        ProcedimentoDelegate pd = ProcedimentoDelegate.getInstance();
        ProcedimentoVO f = pd.getProcedimentoVO(pVO.getId().intValue());

        if (f.getPosizione().equals(pVO.getPosizione())) {
            return false;
        } else {

            return true;
        }
    }

}