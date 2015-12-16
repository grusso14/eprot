package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.digitalsignature.VerificaFirma;
import it.finsiel.siged.exception.CRLNonAggiornataException;
import it.finsiel.siged.exception.CertificatoNonValidoException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.exception.EmailException;
import it.finsiel.siged.exception.FirmaNonValidaException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.business.EmailDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ListaEmailForm;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

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

public class ListaEmailAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(ListaEmailAction.class.getName());

    // --------------------------------------------------------- Public Methods

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        ListaEmailForm listaEmailForm = (ListaEmailForm) form;

        if (listaEmailForm == null) {
            logger.info(" Creating new ListaEmailAction");
            listaEmailForm = new ListaEmailForm();
            request.setAttribute(mapping.getAttribute(), listaEmailForm);
        }

        if (request.getParameter("cancella") != null) {
            errors = listaEmailForm.validate(mapping, request);
            if (errors.isEmpty()) {
                int emailSelezionataId = listaEmailForm.getEmailSelezionataId();
                int emailId = listaEmailForm.getEmailId();
                int id = 0;
                if (emailId != 0)
                    id = emailId;
                else
                    id = emailSelezionataId;

                if (EmailDelegate.getInstance().eliminaEmail(id)) {
                    errors.add("email", new ActionMessage("cancellazione_ok",
                            "", ""));
                }
            }

        } else if (request.getParameter("visualizza") != null) {
            removeTempObject(session);
            errors = listaEmailForm.validate(mapping, request);
            if (errors.isEmpty()) {
                int emailId = new Integer(request
                        .getParameter("emailSelezionataId")).intValue();
                try {

                    MessaggioEmailEntrata messaggio = EmailDelegate
                            .getInstance().getMessaggioEntrata(emailId, utente);
                    session.setAttribute(Constants.TMP_MSG_ENTRATA_OBJ,
                            messaggio);
                    // TODO: validazione segnatura e aggiornamento messaggio in
                    // base
                    // ai valori trovati
                    aggiornaForm(listaEmailForm, messaggio, errors);
                } catch (Exception e) {
                    errors.add("general", new ActionMessage(
                            "error.database.cannotload"));
                }
            }
            // ritorna alla pagina di edit
            saveErrors(request, errors);
            return mapping.findForward("edit");
        } else if (request.getParameter("protocolla") != null) {
            errors = listaEmailForm.validate(mapping, request);
            if (errors.isEmpty()) {
                try {
                    preparaProtocollo(request, listaEmailForm, session, utente);
                    saveToken(request);
                    return (mapping.findForward("protocollazione"));
                } catch (EmailException e) {
                    errors.add("general", new ActionMessage(
                            "protocollo_da_email"));
                }
            }
            MessaggioEmailEntrata msg = (MessaggioEmailEntrata) session
                    .getAttribute(Constants.TMP_MSG_ENTRATA_OBJ);
            listaEmailForm.setAllegatiEmail(msg.getAllegati());
            saveErrors(request, errors);
            return mapping.findForward("edit");
        }

        try {
            listaEmailForm.setListaEmail(EmailDelegate.getInstance()
                    .getMessaggiDaProtocollare(
                            utente.getValueObject().getAooId()));
        } catch (Exception e) {
            errors.add("general",
                    new ActionMessage("error.database.cannotload"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return (mapping.findForward("input"));
    }

    public void aggiornaForm(ListaEmailForm form,
            MessaggioEmailEntrata messaggio, ActionMessages errors) {
        Date dataRic = messaggio.getEmail().getDataRicezione();
        form.setEmailId(messaggio.getEmail().getId().intValue());
        form.setDataRicezione(dataRic == null ? null : DateUtil
                .formattaData(dataRic.getTime()));
        Date dataSped = messaggio.getEmail().getDataSpedizione();
        form.setDataSpedizione(dataSped == null ? null : DateUtil
                .formattaData(dataSped.getTime()));
        form.setOggetto(messaggio.getEmail().getOggetto());
        form.setEmailMittente(messaggio.getEmail().getEmailMittente());
        form.setNomeMittente(messaggio.getEmail().getNomeMittente());

        // form.setDocumentoPrincipaleId()
        Iterator it = messaggio.getAllegati().iterator();
        while (it.hasNext()) {
            DocumentoVO doc = (DocumentoVO) it.next();
            try {
                VerificaFirma.verificaFileFirmato(doc.getPath(), doc
                        .getContentType());
            } catch (DataException e) {
                errors.add("allegati",
                        new ActionMessage("database.cannot.load"));
            } catch (CertificatoNonValidoException e) {
                errors.add("allegati", new ActionMessage(
                        "errore.verificafirma.doc.non_valido", e.getMessage()));
            } catch (FirmaNonValidaException e) {
                errors.add("allegati", new ActionMessage(
                        "errore.verificafirma.doc.non_valido", e.getMessage()));
            } catch (CRLNonAggiornataException e) {
                errors.add("allegati", new ActionMessage(
                        "errore.verificafirma.crl_non_aggiornata"));
            }
        }
        form.setAllegatiEmail(messaggio.getAllegati());
    }

    public void preparaProtocollo(HttpServletRequest request,
            ListaEmailForm form, HttpSession session, Utente utente)
            throws EmailException {

        MessaggioEmailEntrata msg = (MessaggioEmailEntrata) session
                .getAttribute(Constants.TMP_MSG_ENTRATA_OBJ);

        ProtocolloIngresso pi = ProtocolloBO
                .getDefaultProtocolloIngresso(utente);

        try {
            // salvo il testo del messaggio su disco: compatibilità con
            // protocollazione
            File file = File.createTempFile("tmp_prot_ing_", ".att", new File(
                    utente.getValueObject().getTempFolder()));
            InputStream bais = new ByteArrayInputStream(msg.getEmail()
                    .getTestoMessaggio().getBytes());
            OutputStream os = new FileOutputStream(file);
            FileUtil.writeFile(bais, os);
            bais.close();
            os.close();
            DocumentoVO docBody = new DocumentoVO();
            docBody.setContentType("text/plain");
            docBody.setDescrizione("Body");
            docBody.setPath(file.getAbsolutePath());
            docBody.setSize((int) file.length());
            docBody.setFileName("body messaggio.txt");
            docBody.setMustCreateNew(true);
            int docId = form.getDocPrincipaleId();
            boolean bodyAsDocPrincipale = "BODY".equals(form
                    .getTipoDocumentoPrincipale());
            if (bodyAsDocPrincipale) {
                pi.setDocumentoPrincipale(docBody);
            } else {
                pi.allegaDocumento(docBody);
            }
            Iterator iterator = msg.getAllegati().iterator();
            while (iterator.hasNext()) {
                DocumentoVO d = (DocumentoVO) iterator.next();
                d.setMustCreateNew(true);
                if (d.getId().intValue() == docId && !bodyAsDocPrincipale) {
                    pi.setDocumentoPrincipale(d);
                } else {
                    pi.allegaDocumento(d);
                }
            }

            ProtocolloVO protocollo = pi.getProtocollo();
            protocollo.setDataDocumento(DateUtil.toDate(form
                    .getDataSpedizione()));
            protocollo.setDataRicezione(DateUtil
                    .toDate(form.getDataRicezione()));
            protocollo.setOggetto(form.getOggetto());
            protocollo.setDenominazioneMittente(form.getNomeMittente());
            protocollo.setFlagTipoMittente(LookupDelegate.tipiPersona[1]
                    .getTipo());
            protocollo.setMittenteIndirizzo(form.getEmailMittente());
            // utilizzaredato da Segnatura ??
            pi.setProtocollo(protocollo);
            pi.setMessaggioEmailId(msg.getEmail().getId());
            request.setAttribute(Constants.PROTOCOLLO_INGRESSO_DA_EMAIL, pi);
        } catch (Exception e) {
            throw new EmailException("Errore nella generazione del Protocollo");
        }
    }

    public static String salvaFile(String folder, String filename, Object part)
            throws Exception {
        BufferedInputStream bis = new BufferedInputStream(
                new ByteArrayInputStream(((String) part).getBytes()));
        return salvaFile(folder, filename, bis);
    }

    public static String salvaFile(String tempFolder, String filename,
            InputStream input) throws Exception {
        File file = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            if (filename == null) {
                file = File.createTempFile("tmp_", ".email", new File(
                        tempFolder));
            } else {
                String name = new File(filename).getName();
                file = new File(tempFolder + File.separator + name);
            }

            logger.debug("Saving file:" + file.getAbsolutePath());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bis = new BufferedInputStream(input);
            FileUtil.writeFile(bis, bos);
        } catch (FileNotFoundException e) {
            throw new Exception(e);
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            FileUtil.closeOS(bos);
            FileUtil.closeIS(bis);
        }
        return file.getAbsolutePath();
    }

    public void removeTempObject(HttpSession session) {
        session.removeAttribute(Constants.TMP_MSG_ENTRATA_OBJ);
    }
}
