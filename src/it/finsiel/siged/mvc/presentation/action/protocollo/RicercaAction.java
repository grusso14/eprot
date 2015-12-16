package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.Fascicolo;
import it.finsiel.siged.model.organizzazione.AreaOrganizzativa;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.TitolarioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProcedimentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaForm;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.FileUtil;
import it.finsiel.siged.util.NumberUtil;
import it.flosslab.dao.search.SearcherProtocolloDao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

public class RicercaAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(RicercaAction.class.getName());

    public final static String FLAG_PROTOCOLLO_ANNULLATO = "C";

    public final static Integer FLAG_PROTOCOLLO_RISERVATO = new Integer(1);

    // --------------------------------------------------------- Public Methods

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Extract attributes we will need

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession();
        ProtocolloDelegate protocolloDelegate = ProtocolloDelegate
                .getInstance();
        TitolarioDelegate titolarioDelegate = TitolarioDelegate.getInstance();

        RicercaForm ricercaForm = (RicercaForm) form;
        ricercaForm.setDestinatari(null);
        boolean indietroVisibile = false;
        ricercaForm.setIndietroVisibile(indietroVisibile);

        LookupDelegate lookupDelegate = LookupDelegate.getInstance();
        ricercaForm.setStatiProtocollo(lookupDelegate
                .getStatiProtocollo(ricercaForm.getTipoProtocollo()));

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        // Ufficio uff;
        // uff.getUfficiDipendenti()
        boolean ufficioCompleto = (utente.getUfficioVOInUso().getTipo()
                .equals(UfficioVO.UFFICIO_CENTRALE));
        boolean preQuery = false;
        ricercaForm.setAooId(utente.getRegistroVOInUso().getAooId());
        if (form == null) {
            logger.info(" Creating new RicercaAction");
            form = new RicercaForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        if (request.getParameter("btnAnnulla") != null) {
            // session.removeAttribute("provenienza");
            if ("fascicoloProtocollo".equals(session
                    .getAttribute("provenienza"))) {
                indietroVisibile = true;
                ricercaForm.setIndietroVisibile(indietroVisibile);
            }
            if ("protocolliDaProcedimento".equals(session
                    .getAttribute("provenienza"))) {
                indietroVisibile = true;
                ricercaForm.setIndietroVisibile(indietroVisibile);
            }

            if (session.getAttribute("indietroProtocolliDaProcedimento") == Boolean.TRUE) {
                indietroVisibile = true;
                ricercaForm.setIndietroVisibile(indietroVisibile);
            }
            ricercaForm.inizializzaForm();
            if (request.getAttribute("cercaProtocolliDaProcedimento") != null) {
                preQuery = true;
                ricercaForm.setOggetto((String) request
                        .getAttribute("cercaProtocolliDaProcedimento"));
            }
            AlberoUfficiBO.impostaUfficio(utente, ricercaForm, ufficioCompleto);
            impostaUfficio(utente, ricercaForm, ufficioCompleto);
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    0);
        }

        if (ricercaForm.getTitolario() == null) {
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    0);
        }

        if (ricercaForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, ricercaForm, ufficioCompleto);
            impostaUfficio(utente, ricercaForm, ufficioCompleto);
        }
        if (request.getParameter("impostaUfficioAction") != null) {
            logger.info("impostaUfficioAction: "
                    + request.getParameter("impostaUfficioAction"));
            ricercaForm.setUfficioCorrenteId(ricercaForm
                    .getUfficioSelezionatoId());
            AlberoUfficiBO.impostaUfficioUtenti(utente, ricercaForm,
                    ufficioCompleto);
            return mapping.findForward("input");

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            ricercaForm.setUfficioCorrenteId(ricercaForm.getUfficioCorrente()
                    .getParentId());
            AlberoUfficiBO.impostaUfficioUtenti(utente, ricercaForm,
                    ufficioCompleto);

        } else if (ricercaForm.getBtnCerca() != null || preQuery) {
            session.removeAttribute("procedimentiDaFaldoni");
            session.removeAttribute("indietroProcedimentiDaFaldoni");
            if ("fascicoloProtocollo".equals(session
                    .getAttribute("provenienza"))) {
                indietroVisibile = true;
                ricercaForm.setIndietroVisibile(indietroVisibile);
            }
            ricercaForm.setBtnCerca(null);
            ricercaForm.setProtocolloSelezionato(null);
            ricercaForm.setDocumentoSelezionato(null);
            // controllo numero righe di ritorno lista protocolli
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            int maxRighe = Integer.parseInt(bundle
                    .getMessage("protocollo.max.righe.lista"));
            HashMap hashMap = getParametriRicerca(ricercaForm, utente);
            Organizzazione org = Organizzazione.getInstance();
            Ufficio uff = org.getUfficio(utente.getUfficioVOInUso().getId()
                    .intValue());

            int contaRighe = protocolloDelegate.contaProtocolli(utente, uff,
                    hashMap);
            if (contaRighe <= maxRighe) {
                if (contaRighe > 0) {
                    ricercaForm.setProtocolli(protocolloDelegate
                            .cercaProtocolli(utente, uff, hashMap));
                    return (mapping.findForward("lista"));
                } else {
                    errors.add("nessun_dato", new ActionMessage("nessun_dato"));
                }

            } else {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + contaRighe, "protocolli", ""
                                + maxRighe));
            }

        }

        if (request.getParameter("btnRicerca") != null) {
            session.removeAttribute("protocolliDaProcedimento");
            ricercaForm.inizializzaForm();
            AlberoUfficiBO.impostaUfficio(utente, ricercaForm, ufficioCompleto);
            impostaUfficio(utente, ricercaForm, ufficioCompleto);
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    0);
        } else if (ricercaForm.getBtnCercaArgomento() != null) {
            ricercaForm.setBtnCercaArgomento(null);
            String codiceArgomento = (String) request
                    .getParameter("codiceArgomento");
            String descrizioneArgomento = (String) request
                    .getParameter("descrizioneArgomento");
            ricercaForm.setArgomenti(titolarioDelegate.getListaTitolario(utente
                    .getUfficioVOInUso().getAooId(), codiceArgomento,
                    descrizioneArgomento));
            return (mapping.findForward("listaArgomenti"));

        } else if (ricercaForm.getBtnCercaDestinatario() != null) {
            ricercaForm.setBtnCercaDestinatario(null);
            String destinatario = (String) request.getParameter("destinatario");
            ricercaForm.setDestinatari(protocolloDelegate
                    .getDestinatari(destinatario));
            return (mapping.findForward("listaDestinatari"));

        } else if (ricercaForm.getBtnCercaMittente() != null) {
            ricercaForm.setBtnCercaMittente(null);
            String mittente = (String) request.getParameter("mittente");
            ricercaForm.setMittenti(protocolloDelegate.getMittenti(mittente));
            return (mapping.findForward("listaMittenti"));

        } else if (request.getParameter("protocolloSelezionato") != null) {
            // TODO: ottimizzare ripresa tipo.
            ProtocolloVO v = ProtocolloDelegate.getInstance()
                    .getProtocolloById(
                            NumberUtil.getInt(request.getParameter("protocolloSelezionato")));
            String tipoProt = v.getFlagTipo();
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloSelezionato")));
            if ("I".equals(tipoProt)) {
                return (mapping.findForward("visualizzaProtocolloIngresso"));
            } else {
                return (mapping.findForward("visualizzaProtocolloUscita"));
            }

        } else if (request.getParameter("downloadDocprotocolloSelezionato") != null) {
            Integer id = new Integer(Integer.parseInt(request
                    .getParameter("downloadDocprotocolloSelezionato")));
            ProtocolloVO p = ProtocolloDelegate.getInstance()
                    .getProtocolloById(id.intValue());

            InputStream is = null;
            OutputStream os = null;

            try {
                DocumentoVO doc = DocumentoDelegate.getInstance().getDocumento(
                        p.getDocumentoPrincipaleId().intValue());

                if (doc != null) {
                    os = response.getOutputStream();
                    response.setContentType(doc.getContentType());
                    response.setHeader("Content-Disposition",
                            "attachment;filename=" + doc.getFileName());
                    response.setHeader("Cache-control", "");
                    if ((doc.getId() != null && !doc.isMustCreateNew())) {
                        DocumentoDelegate.getInstance().writeDocumentToStream(
                                doc.getId().intValue(), os);
                    } else {
                        is = new FileInputStream(doc.getPath());
                        FileUtil.writeFile(is, os);
                    }

                }
            } catch (FileNotFoundException e) {
                logger.error("", e);
                errors.add("download", new ActionMessage("error.notfound"));
            } catch (IOException e) {
                logger.error("", e);
                errors.add("download", new ActionMessage("error.cannot.read"));
            } catch (DataException e) {
                logger.error("", e);
                errors.add("download", new ActionMessage("error.cannot.read"));
            } finally {
                FileUtil.closeIS(is);
                FileUtil.closeOS(os);
            }

            // ActionForward actionForward = new ActionForward();
            // actionForward.setName("none");
            return null;
        } else if ((request.getParameter("documentoId") != null)
                && (request.getParameter("tipoProt") != null)) {
            int documentoId = Integer.parseInt(request.getAttribute(
                    "documentoId").toString());
            ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
            request.setAttribute("oggetto", (delegate.getDocId(Integer
                    .parseInt(request.getParameter("documentoId")))));
            String tipo = request.getAttribute("tipoProt").toString();

        } else if (request.getParameter("btnSelezionaProtocolli") != null) {
            String[] ids = ricercaForm.getProtocolliSelezionati();
            if (session.getAttribute("elencoProtocolliDaProcedimento") == Boolean.TRUE) {
                ProcedimentoForm pf = (ProcedimentoForm) session
                        .getAttribute("procedimentoForm");
                if (ids != null) {
                    for (int i = 0; i < ids.length; i++) {
                        ReportProtocolloView p = ricercaForm
                                .getProtocolloView(new Integer(ids[i]));
                        if (p.getProtocolloId() == pf.getProtocolloId()) {
                            p.setModificabile(false);
                        } else {
                            p.setModificabile(true);
                        }

                        pf.aggiungiProtocollo(p);

                    }
                }
                session.removeAttribute("tornaProcedimento");
                return mapping.findForward("tornaProcedimento");
            } else if (session.getAttribute("tornaFascicolo") == Boolean.TRUE) {
                Fascicolo f = (Fascicolo) session
                        .getAttribute(Constants.FASCICOLO);
                if (ids != null) {
                    Collection protocolliFascicolo = new ArrayList();
                    for (int i = 0; i < ids.length; i++) {
                        ReportProtocolloView p = ricercaForm
                                .getProtocolloView(new Integer(ids[i]));
                        protocolliFascicolo.add(p);
                    }
                    f.setProtocolli(protocolliFascicolo);
                }
                session.removeAttribute("tornaFascicolo");
                return mapping.findForward("tornaFascicolo");

            }

        } else if (request.getParameter("impostaTitolarioAction") != null) {
            if (ricercaForm.getTitolario() != null) {
                ricercaForm.setTitolarioPrecedenteId(ricercaForm.getTitolario()
                        .getId().intValue());
            }
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    ricercaForm.getTitolarioSelezionatoId());
            return mapping.findForward("input");

        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    ricercaForm.getTitolarioPrecedenteId());
            if (ricercaForm.getTitolario() != null) {
                ricercaForm.setTitolarioPrecedenteId(ricercaForm.getTitolario()
                        .getParentId());
            }
            return mapping.findForward("input");

        } else if (ricercaForm.getBtnSeleziona() != null) {
            ricercaForm.setBtnSeleziona(null);

        } else if (request.getParameter("parMittente") != null) {
            ricercaForm.setMittente(request.getParameter("parMittente"));

        } else if (request.getParameter("parDestinatario") != null) {
            ricercaForm
                    .setDestinatario(request.getParameter("parDestinatario"));

        } else if (request.getParameter("parArgomento") != null) {
            ricercaForm.setIdArgomento(request.getParameter("parArgomento"));
            TitolarioVO titolarioVO = titolarioDelegate.getTitolario(utente
                    .getUfficioInUso(), (new Integer(request
                    .getParameter("parArgomento"))).intValue(), utente
                    .getUfficioVOInUso().getAooId());
            ricercaForm.setDescrizioneArgomento(titolarioVO.getDescrizione());
            ricercaForm.setPathArgomento(titolarioVO.getCodice());

        } else if (request.getParameter("impostaUfficioProtAction") != null) {
            logger.info("impostaUfficioProtAction: "
                    + request.getParameter("impostaUfficioProtAction"));
            ricercaForm.setUfficioProtCorrenteId(ricercaForm
                    .getUfficioProtSelezionatoId());
            impostaUfficioUtenti(utente, ricercaForm, ufficioCompleto);

        } else if (request.getParameter("ufficioProtPrecedenteAction") != null) {
            ricercaForm.setUfficioProtCorrenteId(ricercaForm
                    .getUfficioProtCorrente().getParentId());
            impostaUfficioUtenti(utente, ricercaForm, ufficioCompleto);
        }

        else if (request.getParameter("indietro") != null) {
            if ("fascicoloProtocollo".equals(session
                    .getAttribute("provenienza"))) {
                return mapping.findForward("tornaFascicolo");
            }
            if (session.getAttribute("indietroProtocolliDaProcedimento") == Boolean.TRUE) {
                session.removeAttribute("indietroProtocolliDaProcedimento");
                return mapping.findForward("tornaProcedimento");
            }
        } else if ("fascicoloProtocollo".equals(session
                .getAttribute("provenienza"))) {
            indietroVisibile = true;
            ricercaForm.setIndietroVisibile(indietroVisibile);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("input");
            }
        } else if ("protocolliDaProcedimento".equals(session
                .getAttribute("provenienza"))) {
            indietroVisibile = true;
            ricercaForm.inizializzaForm();
            ricercaForm.setIndietroVisibile(indietroVisibile);
            AlberoUfficiBO.impostaUfficio(utente, ricercaForm, ufficioCompleto);
            impostaUfficio(utente, ricercaForm, ufficioCompleto);
            TitolarioBO.impostaTitolario(ricercaForm, utente.getUfficioInUso(),
                    0);
            String cercaOggetto = (String) request
                    .getAttribute("cercaProtocolliDaProcedimento");
            // ricercaForm.setOggettoProcedimento(cercaOggetto);
            if (request.getAttribute("cercaProtocolliDaProcedimento") == null) {
                ricercaForm.setOggetto(ricercaForm.getOggetto());
            } else {
                ricercaForm.setOggetto(cercaOggetto);
            }
            session.removeAttribute("protocolliDaProcedimento");
            session.removeAttribute("provenienza");
            session.removeAttribute("tornaProcedimento");
            session
                    .setAttribute("elencoProtocolliDaProcedimento",
                            Boolean.TRUE);

            return mapping.findForward("input");

        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        logger.info("Execute RicercaAction");
        session.removeAttribute("indietroProtocolliDaProcedimento ");
        return mapping.getInputForward();

    }

    public static HashMap getParametriRicerca(RicercaForm rForm, Utente utente)
            throws Exception {
        Date dataRegistrazioneDa;
        Date dataRegistrazioneA;
        Date dataDocumentoDa;
        Date dataDocumentoA;
        Date dataRicevutoDa;
        Date dataRicevutoA;
        int numeroProtocolloDa = 0;
        int numeroProtocolloA = 0;
        int annoProtocolloDa = 0;
        int annoProtocolloA = 0;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        HashMap sqlDB = new HashMap();
        sqlDB.clear();
        if (rForm.getDataRegistrazioneDa() != null
                && !"".equals(rForm.getDataRegistrazioneDa())) {
            dataRegistrazioneDa = df.parse(rForm.getDataRegistrazioneDa());
            sqlDB.put("p.DATA_REGISTRAZIONE >= ?", dataRegistrazioneDa);
        }
        if (rForm.getDataRegistrazioneA() != null
                && !"".equals(rForm.getDataRegistrazioneA())) {
            dataRegistrazioneA = new Date(df.parse(
                    rForm.getDataRegistrazioneA()).getTime()
                    + Constants.GIORNO_MILLISECONDS - 1);
            sqlDB.put("p.DATA_REGISTRAZIONE <= ?", dataRegistrazioneA);
        }
        if (rForm.getNumeroProtocolloDa() != null
                && !"".equals(rForm.getNumeroProtocolloDa())) {
            numeroProtocolloDa = Integer
                    .parseInt(rForm.getNumeroProtocolloDa());
            annoProtocolloDa = Integer.parseInt(rForm.getAnnoProtocolloDa());
            sqlDB.put("p.registro_anno_numero >= ?", new Long(ProtocolloVO
                    .getKey(utente.getRegistroInUso(), annoProtocolloDa,
                            numeroProtocolloDa)));
        } else if (rForm.getAnnoProtocolloDa() != null
                && !"".equals(rForm.getAnnoProtocolloDa())) {
            annoProtocolloDa = Integer.parseInt(rForm.getAnnoProtocolloDa());
            sqlDB.put("p.anno_registrazione >= ?",
                    new Integer(annoProtocolloDa));
        }

        if (rForm.getNumeroProtocolloA() != null
                && !"".equals(rForm.getNumeroProtocolloA())) {
            numeroProtocolloA = Integer.parseInt(rForm.getNumeroProtocolloA());
            annoProtocolloA = Integer.parseInt(rForm.getAnnoProtocolloA());

            sqlDB.put("p.registro_anno_numero <= ?", new Long(ProtocolloVO
                    .getKey(utente.getRegistroInUso(), annoProtocolloA,
                            numeroProtocolloA)));
        } else if (rForm.getAnnoProtocolloA() != null
                && !"".equals(rForm.getAnnoProtocolloA())) {
            annoProtocolloA = Integer.parseInt(rForm.getAnnoProtocolloA());
            sqlDB
                    .put("p.anno_registrazione <= ?", new Integer(
                            annoProtocolloA));
        }
        // TODO : gestione Ricerca protocollo interno
        if (rForm.getTipoProtocollo() != null
                && !"".equals(rForm.getTipoProtocollo())) {
            if ("M".equals(rForm.getTipoProtocollo())) {
                sqlDB.put("p.FLAG_TIPO = ? AND FLAG_MOZIONE=1", "U");
            } else if ("U".equals(rForm.getTipoProtocollo())) {
                sqlDB.put("p.FLAG_TIPO = ? AND FLAG_MOZIONE=0", "U");
            } else if (rForm.getMozioneUscita().equals(
                    rForm.getTipoProtocollo())) {
                sqlDB.put("p.FLAG_TIPO = ?", "U");
            } else {
                sqlDB.put("p.FLAG_TIPO = ?", rForm.getTipoProtocollo());
            }
        }
        if (rForm.getStatoProtocollo() != null
                && !"".equals(rForm.getStatoProtocollo())) {
            if (rForm.getTipoProtocollo() != null
                    && rForm.getTipoProtocollo().equals(
                            Parametri.LABEL_MOZIONE_USCITA)
                    && "A".equals(rForm.getStatoProtocollo())) {
                sqlDB.put(
                        "(p.stato_protocollo = 'R' OR p.stato_protocollo = ?)",
                        rForm.getStatoProtocollo());
            } else {
                sqlDB.put("p.stato_protocollo = ?", rForm.getStatoProtocollo());
            }
        }
        if ("on".equalsIgnoreCase(rForm.getRiservato())) {
            sqlDB.put("p.FLAG_RISERVATO = ?", FLAG_PROTOCOLLO_RISERVATO);
        }

        // DATI DOCUMENTO
        if (rForm.getTipoDocumento() != null
                && !Parametri.TUTTI.equalsIgnoreCase(rForm.getTipoDocumento())) {
            sqlDB.put("p.tipo_documento_id = ?", rForm.getTipoDocumento());
        }
        if (rForm.getDataDocumentoDa() != null
                && !"".equals(rForm.getDataDocumentoDa())) {
            dataDocumentoDa = df.parse(rForm.getDataDocumentoDa());
            sqlDB.put("p.DATA_DOCUMENTO >= ?", dataDocumentoDa);
        }
        if (rForm.getDataDocumentoA() != null
                && !"".equals(rForm.getDataDocumentoA())) {
            dataDocumentoA = df.parse(rForm.getDataDocumentoA());
            sqlDB.put("p.DATA_DOCUMENTO <= ?", dataDocumentoA);
        }
        if (rForm.getDataRicevutoDa() != null
                && !"".equals(rForm.getDataRicevutoDa())) {
            dataRicevutoDa = df.parse(rForm.getDataRicevutoDa());
            sqlDB.put("p.DATA_RICEZIONE >= ?", dataRicevutoDa);
        }
        if (rForm.getDataRicevutoA() != null
                && !"".equals(rForm.getDataRicevutoA())) {
            dataRicevutoA = df.parse(rForm.getDataRicevutoA());
            sqlDB.put("p.DATA_RICEZIONE <= ?", dataRicevutoA);
        }
        if (rForm.getOggetto() != null && !"".equals(rForm.getOggetto())) {
            sqlDB.put("UPPER(p.TEXT_OGGETTO) LIKE ?", rForm.getOggetto()
                    .toUpperCase());
        }
        if (rForm.getProgressivoFascicolo() != null
                && !"".equals(rForm.getProgressivoFascicolo())) {
            String strFascicoli = " EXISTS (SELECT * FROM fascicolo_protocolli fasc"
                    + " WHERE fasc.protocollo_id=p.protocollo_id AND fasc.fascicolo_id=?)";
            sqlDB.put(strFascicoli,
                    new Integer(rForm.getProgressivoFascicolo()));
        }

        // ANNOTAZIONE
        if (rForm.getChiaveAnnotazione() != null
                && !"".equals(rForm.getChiaveAnnotazione())) {
            sqlDB.put("upper(p.ANNOTAZIONE_CHIAVE) LIKE ?", rForm
                    .getChiaveAnnotazione().toUpperCase());
        }
        if (rForm.getPosizioneAnnotazione() != null
                && !"".equals(rForm.getPosizioneAnnotazione())) {
            sqlDB.put("upper(p.ANNOTAZIONE_POSIZIONE) LIKE ?", rForm
                    .getPosizioneAnnotazione().toUpperCase());
        }
        if (rForm.getDescrizioneAnnotazione() != null
                && !"".equals(rForm.getDescrizioneAnnotazione())) {
            sqlDB.put("upper(p.ANNOTAZIONE_DESCRIZIONE) LIKE ?", rForm
                    .getDescrizioneAnnotazione().toUpperCase());
        }

        // TITOLARIO
        if (rForm.getTitolario() != null) {
            sqlDB.put("p.titolario_id = ?", rForm.getTitolario().getId());
        }
        // PROTOCOLLATORE
        if (rForm.getUfficioProtRicercaId() > 0) {
            sqlDB.put("p.ufficio_protocollatore_id = ?", new Integer(rForm
                    .getUfficioProtRicercaId()));
        }
        if (rForm.getUtenteProtSelezionatoId() > 0) {
            sqlDB.put("p.utente_protocollatore_id = ?", new Integer(rForm
                    .getUtenteProtSelezionatoId()));
        }
        // TODO : ricerca Protocollo Interno in base al mittente
        if ("I".equals(rForm.getTipoProtocollo())) {
            // MITTENTE protocollo ingresso
            if (rForm.getMittente() != null && !"".equals(rForm.getMittente())) {
                String sqlMittente = "(upper(p.DESC_DENOMINAZIONE_MITTENTE) LIKE '"
                        + rForm.getMittente().toUpperCase()
                        + "%'"
                        + " OR upper(p.DESC_COGNOME_MITTENTE) LIKE ?)";
                sqlDB.put(sqlMittente, rForm.getMittente().toUpperCase());
            }
            // ASSEGNATARIO protocollo ingresso
            if (rForm.getUfficioRicercaId() > 0) {
                if (rForm.getUtenteSelezionatoId() == 0) {
                    String strAssegnatari = " EXISTS (SELECT * FROM protocollo_assegnatari ass"
                            + " WHERE ass.protocollo_id=p.protocollo_id AND ass.ufficio_assegnatario_id=?"
                            + " AND ass.utente_assegnatario_id IS NULL)";
                    sqlDB.put(strAssegnatari, new Integer(rForm
                            .getUfficioRicercaId()));
                } else {
                    String strAssegnatari = " EXISTS (SELECT * FROM protocollo_assegnatari ass"
                            + " WHERE ass.protocollo_id=p.protocollo_id AND ass.ufficio_assegnatario_id="
                            + rForm.getUfficioRicercaId()
                            + " AND ass.utente_assegnatario_id=?)";
                    sqlDB.put(strAssegnatari, new Integer(rForm
                            .getUtenteSelezionatoId()));

                }

            }
        } else if ("U".equals(rForm.getTipoProtocollo())
                || "M".equals(rForm.getTipoProtocollo())
                || Parametri.LABEL_MOZIONE_USCITA.equals(rForm
                        .getTipoProtocollo())) {
            // DESTINATARIO
            if (rForm.getDestinatario() != null
                    && !"".equals(rForm.getDestinatario())
                    && !"I".equals(rForm.getTipoProtocollo())) {
                sqlDB.put("upper(d.destinatario) LIKE ?", rForm
                        .getDestinatario().toUpperCase());
            }
            // MITTENTE protocollo uscita
            if (rForm.getUfficioRicercaId() > 0) {
                sqlDB.put("ufficio_mittente_id=? ", new Integer(rForm
                        .getUfficioRicercaId()));
                if (rForm.getUtenteSelezionatoId() > 0) {
                    sqlDB.put("utente_mittente_id=?", new Integer(rForm
                            .getUtenteSelezionatoId()));
                }
            }

        }
        // Lucene
        if(!"".equals(rForm.getText())) {
        	List<String> documentIds = null;
        	SearcherProtocolloDao searcher = new SearcherProtocolloDao();
        	documentIds = searcher.searchText(rForm.getText());
        	List<Integer> listSql = new ArrayList<Integer>();
        	if(documentIds.size() > 0) {
        		for(int i = 0; i < documentIds.size(); i++) {
        			listSql.add(new Integer(Integer.parseInt(documentIds.get(i))));
        		}
        	}
        	
        	sqlDB.put("p.documento_id IN ", listSql);
        }
        
        return sqlDB;
    }

    // private void impostaTitolario(RicercaForm form, int ufficioId,
    // int titolarioId) {
    // TitolarioDelegate td = TitolarioDelegate.getInstance();
    // form.setTitolario(td.getTitolario(ufficioId, titolarioId));
    // form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId));
    // }

    public static void impostaUfficio(Utente utente, RicercaForm form,
            boolean alberoCompleto) {
        int ufficioId = form.getUfficioProtCorrenteId();
        Organizzazione org = Organizzazione.getInstance();
        Ufficio ufficioRoot = org.getUfficio(utente.getUfficioInUso());
        if (alberoCompleto) { // ufficio centrale
            AreaOrganizzativa aoo = org.getAreaOrganizzativa(ufficioRoot
                    .getValueObject().getAooId());
            ufficioRoot = aoo.getUfficioCentrale();
        }
        Ufficio ufficioCorrente = org.getUfficio(ufficioId);
        if (ufficioCorrente == null) {
            ufficioCorrente = ufficioRoot;
            ufficioId = ufficioRoot.getValueObject().getId().intValue();
        }
        Ufficio uff = ufficioCorrente;
        while (uff != ufficioRoot) {
            if (uff == null) {
                ufficioCorrente = ufficioRoot;
                ufficioId = ufficioCorrente.getValueObject().getId().intValue();
                break;
            }
            uff = uff.getUfficioDiAppartenenza();
        }
        form.setUfficioProtCorrenteId(ufficioId);
        form.setUfficioProtCorrentePath(ufficioCorrente.getPath());
        form.setUfficioProtCorrente(ufficioCorrente.getValueObject());
        List list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUfficiDipendenti().iterator(); i
                .hasNext();) {
            uff = (Ufficio) i.next();
            list.add(uff.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UfficioVO uff1 = (UfficioVO) obj1;
                UfficioVO uff2 = (UfficioVO) obj2;
                return uff1.getDescription().compareToIgnoreCase(
                        uff2.getDescription());
            }
        };
        Collections.sort(list, c);
        form.setUfficiProtDipendenti(list);
    }

    public static void impostaUfficioUtenti(Utente utente, RicercaForm form,
            boolean alberoCompleto) {
        Organizzazione org = Organizzazione.getInstance();
        impostaUfficio(utente, form, alberoCompleto);
        Ufficio ufficioCorrente = org.getUfficio(form
                .getUfficioProtCorrenteId());
        List list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUtenti().iterator(); i.hasNext();) {
            Utente ute = (Utente) i.next();
            list.add(ute.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UtenteVO ute1 = (UtenteVO) obj1;
                UtenteVO ute2 = (UtenteVO) obj2;
                return ute1.getFullName().compareToIgnoreCase(
                        ute2.getFullName());
            }
        };
        Collections.sort(list, c);
        // for (int i = 0; i < list.size(); i++) {
        // UtenteVO utenteVO = (UtenteVO) list.get(i);
        // logger.info("nome Utente: "+utenteVO.getFullName());
        // }

        form.setUtentiProt(list);
    }

}