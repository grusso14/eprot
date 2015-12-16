package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ScaricoForm;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
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

public class ScaricoAction extends Action {

    static Logger logger = Logger.getLogger(ScaricoAction.class.getName());

    public final static String PROTOCOLLI_ASSEGNATI = "A";

    public final static String PROTOCOLLI_AGLI_ATTI = "A";

    public final static String PROTOCOLLI_IN_LAVORAZIONE = "N";

    public final static String PROTOCOLLI_IN_RISPOSTA = "R";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession(true);
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
        ScaricoForm scarico = (ScaricoForm) form;
        Date dataProtocolloDa = null;
        Date dataProtocolloA = null;
        int numeroProtocolloDa = 0;
        int numeroProtocolloA = 0;
        int annoProtocolloDa = 0;
        int annoProtocolloA = 0;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        // String tipoUtenteUfficio = request.getParameter("tipoUtenteUfficio");
        String tipoUtenteUfficio = "T"; // tipo utente
        if (request.getParameter("dataRegistrazioneDa") != null
                && !"".equals(request.getParameter("dataRegistrazioneDa"))) {
            dataProtocolloDa = df.parse(request
                    .getParameter("dataRegistrazioneDa"));
        }
        if (request.getParameter("dataRegistrazioneA") != null
                && !"".equals(request.getParameter("dataRegistrazioneA"))) {
            dataProtocolloA = df.parse(request
                    .getParameter("dataRegistrazioneA"));
        }
        if (request.getParameter("numeroProtocolloDa") != null
                && !"".equals(request.getParameter("numeroProtocolloDa"))) {
            numeroProtocolloDa = Integer.parseInt(request
                    .getParameter("numeroProtocolloDa"));
        }
        if (request.getParameter("numeroProtocolloA") != null
                && !"".equals(request.getParameter("numeroProtocolloA"))) {
            numeroProtocolloA = Integer.parseInt(request
                    .getParameter("numeroProtocolloA"));
        }
        if (request.getParameter("annoProtocolloDa") != null
                && !"".equals(request.getParameter("annoProtocolloDa"))) {
            annoProtocolloDa = Integer.parseInt(request
                    .getParameter("annoProtocolloDa"));
        }
        if (request.getParameter("annoProtocolloA") != null
                && !"".equals(request.getParameter("annoProtocolloA"))) {
            annoProtocolloA = Integer.parseInt(request
                    .getParameter("annoProtocolloA"));
        }

        if (form == null) {
            logger.info(" Creating new ScaricoAction");
            form = new ScaricoForm();
            session.setAttribute(mapping.getAttribute(), form);
        }

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        if (scarico.getBtnAtti() != null) {
            scarico.setBtnAtti(null);
            String protocolloSelezionato = request
                    .getParameter("protocolloScarico");
            if (protocolloSelezionato != null) {
                ProtocolloVO protocolloVO = delegate.getProtocolloById(Integer
                        .parseInt(protocolloSelezionato));
                delegate.updateScarico(protocolloVO, PROTOCOLLI_AGLI_ATTI,
                        utente);
                scarico.setProtocolloScarico(null);
                scarico.removeProtocolliScarico();
                scarico.setMsgAssegnatarioCompetente(null);
                scarico.setProtocolliScarico(delegate.getProtocolliAssegnati(
                        utente, annoProtocolloDa, annoProtocolloA,
                        numeroProtocolloDa, numeroProtocolloA,
                        dataProtocolloDa, dataProtocolloA,
                        PROTOCOLLI_ASSEGNATI, scarico.getStatoProtocollo(),
                        tipoUtenteUfficio));
                errors.add("operazione_ok", new ActionMessage("operazione_ok"));
            }
        } else if (scarico.getBtnLavorazione() != null) {
            scarico.setBtnLavorazione(null);
            String protocolloSelezionato = request
                    .getParameter("protocolloScarico");
            if (protocolloSelezionato != null) {
                ProtocolloVO protocolloVO = delegate.getProtocolloById(Integer
                        .parseInt(protocolloSelezionato));
                delegate.updateScarico(protocolloVO, PROTOCOLLI_IN_LAVORAZIONE,
                        utente);
                scarico.setProtocolloScarico(null);
                scarico.removeProtocolliScarico();
                scarico.setMsgAssegnatarioCompetente(null);
                scarico.setProtocolliScarico(delegate.getProtocolliAssegnati(
                        utente, annoProtocolloDa, annoProtocolloA,
                        numeroProtocolloDa, numeroProtocolloA,
                        dataProtocolloDa, dataProtocolloA,
                        PROTOCOLLI_ASSEGNATI, scarico.getStatoProtocollo(),
                        tipoUtenteUfficio));
                errors.add("operazione_ok", new ActionMessage("operazione_ok"));
            }
        } else if (scarico.getBtnRisposta() != null) {
            scarico.setBtnRisposta(null);
            if (scarico.getProtocolloScarico() != null) {
                // operazione avvenuta con successo, ripulisco le variabili
                // seleziono l'id del protocollo in ingresso
                Integer protocolloId = new Integer(scarico
                        .getProtocolloScarico());

                if (protocolloId != null) {
                    request.setAttribute("risposta", Boolean.TRUE);
                    request.setAttribute("protocolloId", protocolloId);
                    saveToken(request);
                    return (mapping.findForward("creaProtocolloRisposta"));
                }
            }
        } else if (scarico.getBtnRifiuta() != null) {
            scarico.setBtnRifiuta(null);
            if (scarico.getProtocolloScarico() != null) {
                Integer protocolloId = new Integer(scarico
                        .getProtocolloScarico());
                if (protocolloId != null) {
                    ProtocolloIngresso pi = delegate
                            .getProtocolloIngressoById(Integer.parseInt(scarico
                                    .getProtocolloScarico()));
                    ProtocolloVO protocolloVO = pi.getProtocollo();

                    protocolloVO.setDataScarico(null);
                    if (scarico.getMsgAssegnatarioCompetente() != null
                            && !"".equals(scarico
                                    .getMsgAssegnatarioCompetente().trim())) {
                        aggiornaMsgRifiutoAssegnatari(scarico
                                .getMsgAssegnatarioCompetente(), pi);
                    }

                    delegate.rifiutaProtocollo(pi, "R", "F", utente);

                }
                scarico.setProtocolloScarico(null);
                scarico.removeProtocolliScarico();
                scarico.setMsgAssegnatarioCompetente(null);
                scarico.setProtocolliScarico(delegate.getProtocolliAssegnati(
                        utente, annoProtocolloDa, annoProtocolloA,
                        numeroProtocolloDa, numeroProtocolloA,
                        dataProtocolloDa, dataProtocolloA,
                        PROTOCOLLI_ASSEGNATI, scarico.getStatoProtocollo(),
                        tipoUtenteUfficio));
                errors.add("operazione_ok", new ActionMessage("operazione_ok"));
            }

        } else if (scarico.getBtnCerca() != null) {
            scarico.setBtnCerca(null);
            scarico.setProtocolloScarico(null);
            scarico.setMsgAssegnatarioCompetente(null);

            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            int maxRighe = Integer.parseInt(bundle
                    .getMessage("protocollo.max.righe.lista"));
            int contaRighe = delegate.contaProtocolliAssegnati(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataProtocolloDa, dataProtocolloA,
                    PROTOCOLLI_ASSEGNATI, scarico.getStatoProtocollo(),
                    tipoUtenteUfficio);
            if (contaRighe == 0) {
                errors.add("nessun_dato", new ActionMessage("nessun_dato", "",
                        ""));
            } else if (contaRighe <= maxRighe) {

                scarico.setProtocolliScarico(delegate.getProtocolliAssegnati(
                        utente, annoProtocolloDa, annoProtocolloA,
                        numeroProtocolloDa, numeroProtocolloA,
                        dataProtocolloDa, dataProtocolloA,
                        PROTOCOLLI_ASSEGNATI, scarico.getStatoProtocollo(),
                        tipoUtenteUfficio));
                return (mapping.findForward("input"));

            } else {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + contaRighe,
                        "protocolli scaricati/riassegnati", "" + maxRighe));
            }

            // scarico.setProtocolliScarico(delegate.getProtocolliAssegnati(
            // utente, annoProtocolloDa, annoProtocolloA,
            // numeroProtocolloDa, numeroProtocolloA, dataProtocolloDa,
            // dataProtocolloA, PROTOCOLLI_ASSEGNATI, scarico
            // .getStatoProtocollo(), tipoUtenteUfficio));
            // return (mapping.findForward("input"));
        } else if (request.getParameter("btnRiassegna") != null) {
            request.setAttribute("protocolloId", new Integer(scarico
                    .getProtocolloScarico()));
            return (mapping.findForward("riassegnaProtocollo"));

        } else if (request.getParameter("protocolloSelezionato") != null) {
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloSelezionato")));

            return (mapping.findForward("visualizzaProtocolloIngresso"));
        } else if (request.getParameter("downloadDocprotocolloSelezionato") != null) {

            Integer id = new Integer(Integer.parseInt(request
                    .getParameter("downloadDocprotocolloSelezionato")));
            ReportProtocolloView prot = (ReportProtocolloView) scarico
                    .getProtocolliScarico().get(id);
            InputStream is = null;
            OutputStream os = null;

            try {
                DocumentoVO doc = DocumentoDelegate.getInstance().getDocumento(
                        prot.getDocumentoId());

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

            return null;

        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        scarico.setProtocolliScarico(null);
        logger.info("Execute ScaricoAction");
        return (mapping.findForward("input"));

    }

    private void aggiornaMsgRifiutoAssegnatari(
            String msgAssegnatarioCompetente, ProtocolloIngresso protocollo) {

        Collection assegnatari = protocollo.getAssegnatari();
        if (assegnatari != null) {
            for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
                ;
                if (assegnatario.isCompetente()) {
                    assegnatario
                            .setMsgAssegnatarioCompetente(msgAssegnatarioCompetente);
                    protocollo
                            .setMsgAssegnatarioCompetente(msgAssegnatarioCompetente);

                }
            }
        }
    }
}
