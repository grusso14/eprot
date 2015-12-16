package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.PresaInCaricoForm;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.FileUtil;
import it.finsiel.siged.util.NumberUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class PresaInCaricoAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger
            .getLogger(PresaInCaricoAction.class.getName());

    public final static String PROTOCOLLI_SOSPESI = "S";

    public final static String STATO_TIPO_SCARICO = "N";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
        PresaInCaricoForm presaInCarico = (PresaInCaricoForm) form;
        MessageResources bundle = (MessageResources) request
                .getAttribute(Globals.MESSAGES_KEY);
        int maxRighe = Integer.parseInt(bundle
                .getMessage("protocollo.max.righe.lista"));

        if (form == null) {
            logger.info(" Creating new PresaInCaricoAction");
            form = new PresaInCaricoForm();
            request.setAttribute(mapping.getAttribute(), form);
        }

        Date dataProtocolloDa = null;
        Date dataProtocolloA = null;
        int numeroProtocolloDa = 0;
        int numeroProtocolloA = 0;
        int annoProtocolloDa = 0;
        int annoProtocolloA = 0;

        String tipoUtenteUfficio = presaInCarico.getTipoUtenteUfficio();

        if (!"".equals(presaInCarico.getDataRegistrazioneDa())) {
            dataProtocolloDa = DateUtil.toDate(presaInCarico
                    .getDataRegistrazioneDa());
        }
        if (!"".equals(presaInCarico.getDataRegistrazioneA())) {
            dataProtocolloA = DateUtil.toDate(presaInCarico
                    .getDataRegistrazioneA());
        }
        if (!"".equals(presaInCarico.getNumeroProtocolloDa())) {
            numeroProtocolloDa = NumberUtil.getInt(presaInCarico
                    .getNumeroProtocolloDa());
        }
        if (!"".equals(presaInCarico.getNumeroProtocolloA())) {
            numeroProtocolloA = NumberUtil.getInt(presaInCarico
                    .getNumeroProtocolloA());
        }
        if (!"".equals(presaInCarico.getAnnoProtocolloDa())) {
            annoProtocolloDa = NumberUtil.getInt(presaInCarico
                    .getAnnoProtocolloDa());
        }
        if (!"".equals(presaInCarico.getAnnoProtocolloA())) {
            annoProtocolloA = NumberUtil.getInt(presaInCarico
                    .getAnnoProtocolloA());
        }

        if (presaInCarico.getBtnAccettaSelezionati() != null) {
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            presaInCarico.setBtnAccettaSelezionati(null);
            String[] protocolliAccettati = presaInCarico
                    .getProtocolliSelezionati();
            presaInCarico.setMsgAssegnatarioCompetente(null);
            if (protocolliAccettati != null) {
                ArrayList al = new ArrayList();
                for (int i = 0; i < protocolliAccettati.length; i++) {
                    logger.info(" setProtocolliInCarico: "
                            + protocolliAccettati[i]);
                    if (protocolliAccettati[i] != null) {
                        int protAcc = Integer.parseInt(protocolliAccettati[i]);
                        ProtocolloVO prot = delegate.getProtocolloById(protAcc);
                        if (!prot.getStatoProtocollo().equals(
                                PROTOCOLLI_SOSPESI)) {
                            errors.add("versione_vecchia", new ActionMessage(
                                    "versione_vecchia"));
                            saveErrors(request, errors);
                            return (mapping.findForward("input"));

                        } else {
                            al.add(prot);
                        }
                    }
                }
                delegate.presaInCarico(al, "A", utente);
                presaInCarico.setProtocolliSelezionati(null);
                presaInCarico.removeProtocolliInCarico();
                presaInCarico.setProtocolliInCarico(delegate
                        .getProtocolliAssegnati(utente, annoProtocolloDa,
                                annoProtocolloA, numeroProtocolloDa,
                                numeroProtocolloA, dataProtocolloDa,
                                dataProtocolloA, PROTOCOLLI_SOSPESI,
                                PROTOCOLLI_SOSPESI, tipoUtenteUfficio));
                errors.add("operazione_ok", new ActionMessage("operazione_ok"));
            }
        } else if (presaInCarico.getBtnRespingiSelezionati() != null) {
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            presaInCarico.setBtnRespingiSelezionati(null);
            String[] protocolliRespinti = presaInCarico
                    .getProtocolliSelezionati();
            // presaInCarico.setMsgAssegnatarioCompetente(null);
            if (protocolliRespinti != null) {
                ArrayList al = new ArrayList();
                for (int i = 0; i < protocolliRespinti.length; i++) {
                    logger.info(" setProtocolliInCarico: "
                            + protocolliRespinti[i]);
                    if (protocolliRespinti[i] != null) {
                        int protId = Integer.parseInt(protocolliRespinti[i]);
                        ProtocolloIngresso prot = delegate
                                .getProtocolloIngressoById(protId);
                        if (!prot.getProtocollo().getStatoProtocollo().equals(
                                PROTOCOLLI_SOSPESI)) {
                            errors.add("versione_vecchia", new ActionMessage(
                                    "versione_vecchia"));
                            saveErrors(request, errors);
                            return (mapping.findForward("input"));

                        } else {
                            aggiornaMsgRifiutoAssegnatari(presaInCarico
                                    .getMsgAssegnatarioCompetente(), prot);

                            al.add(prot);

                        }

                    }
                }
                delegate.rifiutaProtocolli(al, utente);
                int numProtocolliAssegnati = delegate.contaProtocolliAssegnati(
                        utente, annoProtocolloDa, annoProtocolloA,
                        numeroProtocolloDa, numeroProtocolloA,
                        dataProtocolloDa, dataProtocolloA, PROTOCOLLI_SOSPESI,
                        PROTOCOLLI_SOSPESI, tipoUtenteUfficio);
                if (numProtocolliAssegnati <= maxRighe) {
                    presaInCarico.removeProtocolliInCarico();
                    presaInCarico.setProtocolliInCarico(delegate
                            .getProtocolliAssegnati(utente, annoProtocolloDa,
                                    annoProtocolloA, numeroProtocolloDa,
                                    numeroProtocolloA, dataProtocolloDa,
                                    dataProtocolloA, PROTOCOLLI_SOSPESI,
                                    PROTOCOLLI_SOSPESI, tipoUtenteUfficio));
                    presaInCarico.setMsgAssegnatarioCompetente(null);
                    errors.add("operazione_ok", new ActionMessage(
                            "operazione_ok"));

                } else {
                    errors.add("controllo.maxrighe", new ActionMessage(
                            "controllo.maxrighe", "protocolli", "" + maxRighe));
                }

            }
        } else if (presaInCarico.getBtnCerca() != null) {
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            presaInCarico.setBtnCerca(null);
            presaInCarico.setProtocolliSelezionati(null);

            int numProtocolliAssegnati = delegate.contaProtocolliAssegnati(
                    utente, annoProtocolloDa, annoProtocolloA,
                    numeroProtocolloDa, numeroProtocolloA, dataProtocolloDa,
                    dataProtocolloA, PROTOCOLLI_SOSPESI, PROTOCOLLI_SOSPESI,
                    tipoUtenteUfficio);
            if (numProtocolliAssegnati == 0) {
                presaInCarico.setProtocolliInCarico(null);
                errors.add("nessun_dato", new ActionMessage("nessun_dato", "",
                        ""));
            } else if (numProtocolliAssegnati <= maxRighe) {

                presaInCarico.setProtocolliInCarico(delegate
                        .getProtocolliAssegnati(utente, annoProtocolloDa,
                                annoProtocolloA, numeroProtocolloDa,
                                numeroProtocolloA, dataProtocolloDa,
                                dataProtocolloA, PROTOCOLLI_SOSPESI,
                                PROTOCOLLI_SOSPESI, tipoUtenteUfficio));
                return (mapping.findForward("input"));
            } else {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + numProtocolliAssegnati,
                        "protocolli", "" + maxRighe));
            }
        } else if (request.getParameter("protocolloSelezionato") != null) {
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloSelezionato")));
            return (mapping.findForward("visualizzaProtocolloIngresso"));
        } else if (request.getParameter("downloadDocprotocolloSelezionato") != null) {
            Integer id = new Integer(Integer.parseInt(request
                    .getParameter("downloadDocprotocolloSelezionato")));
            ReportProtocolloView prot = (ReportProtocolloView) presaInCarico
                    .getProtocolliInCarico().get(id);
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
                        // protocollo esistente prendiamo i dati dal Repository
                        DocumentoDelegate.getInstance().writeDocumentToStream(
                                doc.getId().intValue(), os);
                    } else {
                        // protocollo ancora da sa?vare: i file sono nella
                        // cartella
                        // temporanea
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
            return (mapping.findForward("input"));
        }

        presaInCarico.removeProtocolliInCarico();
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
