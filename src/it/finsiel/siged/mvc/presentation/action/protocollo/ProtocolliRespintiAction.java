package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolliRespintiForm;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class ProtocolliRespintiAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ProtocolliRespintiAction.class
            .getName());

    // --------------------------------------------------------- Public Methods
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession(true); // we create one if does
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
        ProtocolliRespintiForm riassegnazione = (ProtocolliRespintiForm) form;
        Date dataProtocolloDa = null;
        Date dataProtocolloA = null;
        int numeroProtocolloDa = 0;
        int numeroProtocolloA = 0;
        int annoProtocolloDa = 0;
        int annoProtocolloA = 0;

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
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
            logger.info(" Creating new riassegnazioneAction");
            form = new ProtocolliRespintiForm();
            session.setAttribute(mapping.getAttribute(), form);
        }

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        if (request.getParameter("btnCerca") != null) {
            riassegnazione.setProtocolloRifiutato(null);
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);
            int maxRighe = Integer.parseInt(bundle
                    .getMessage("protocollo.max.righe.lista"));
            int contaRighe = delegate.contaProtocolliRespinti(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataProtocolloDa, dataProtocolloA);
            if (contaRighe == 0) {
                errors.add("nessun_dato", new ActionMessage("nessun_dato", "",
                        ""));
            } else if (contaRighe <= maxRighe) {
                riassegnazione.setProtocolliRifiutati(delegate
                        .getProtocolliRespinti(utente, annoProtocolloDa,
                                annoProtocolloA, numeroProtocolloDa,
                                numeroProtocolloA, dataProtocolloDa,
                                dataProtocolloA));
                return (mapping.findForward("input"));

            } else {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + contaRighe,
                        "protocolli rifiutati", "" + maxRighe));
            }

            // riassegnazione.setProtocolliRifiutati(delegate
            // .getProtocolliRespinti(utente, annoProtocolloDa,
            // annoProtocolloA, numeroProtocolloDa,
            // numeroProtocolloA, dataProtocolloDa,
            // dataProtocolloA));
            // return (mapping.findForward("input"));
        } else if (request.getParameter("btnRiassegna") != null) {
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloRifiutato")));
            return mapping.findForward("riassegna");

        } else if (request.getParameter("protocolloSelezionato") != null) {
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloSelezionato")));
            return (mapping.findForward("visualizzaProtocolloIngresso"));
        }

        // TODO: link DOC
        else if (request.getParameter("downloadDocprotocolloSelezionato") != null) {
            Integer id = new Integer(Integer.parseInt(request
                    .getParameter("downloadDocprotocolloSelezionato")));
            ReportProtocolloView prot = (ReportProtocolloView) riassegnazione
                    .getProtocolliRifiutati().get(id);
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
            return (mapping.findForward("input"));
        }

        logger.info("Execute riassegnazioneAction");
        riassegnazione.setProtocolloRifiutato(null);
        riassegnazione.setProtocolliRifiutati(null);

        return (mapping.findForward("input")); // 
    }

}
