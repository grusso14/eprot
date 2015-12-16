/*
*
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
*
* This file is part of e-prot 1.1 software.
* e-prot 1.1 is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
* Version: e-prot 1.1
*/

package it.flosslab.mvc.presentation.action.protocollo.helper.file;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.digitalsignature.VerificaFirma;
import it.finsiel.siged.exception.CRLNonAggiornataException;
import it.finsiel.siged.exception.CertificatoNonValidoException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.exception.FirmaNonValidaException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.DocumentoDelegate;
import it.finsiel.siged.mvc.presentation.action.protocollo.ProtocolloAction;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

public class ProtocolloFileUtility {
	 
	static Logger logger = Logger.getLogger(ProtocolloAction.class.getName());
	
	public static  void uploadDocumentoPrincipale(ProtocolloForm form,
            HttpServletRequest request, ActionMessages errors) {
        FormFile file = form.getFilePrincipaleUpload();
        String fileName = file.getFileName();
        String contentType = file.getContentType();
        int size = file.getFileSize();

        // controllo: l'utente avrebbe potuto premere il pulsante senza
        // selezionare un file
        if (size > 0 && !"".equals(fileName)) {
            // salva il file in uno temporaneo e ritorna il nome dello
            // stesso
            String tempFilePath = FileUtil.leggiFormFilePrincipale(form,
                    request, errors);
            String impronta = FileUtil.calcolaDigest(tempFilePath, errors);
            String previousExtension = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
             
            if(tempFilePath.endsWith(".pdf")){
            	fileName = fileName.replace(previousExtension, "pdf");
            	contentType = "application/pdf";
            }
            if (errors.isEmpty()) {
                try {
                    VerificaFirma
                            .verificaFileFirmato(tempFilePath, contentType);
                } catch (DataException e) {
                    errors.add("documentoPrincipale", new ActionMessage(
                            "database.cannot.load", e.getMessage()));
                } catch (CertificatoNonValidoException e) {
                    errors.add("documentoPrincipale", new ActionMessage(
                            "errore.verificafirma.doc.non_valido", e
                                    .getMessage()));
                } catch (FirmaNonValidaException e) {
                    errors.add("documentoPrincipale", new ActionMessage(
                            "errore.verificafirma.doc.non_valido", e
                                    .getMessage()));
                } catch (CRLNonAggiornataException e) {
                    errors.add("documentoPrincipale", new ActionMessage(
                            "errore.verificafirma.crl_non_aggiornata"));
                }
                String username = ((Utente) request.getSession().getAttribute(
                        Constants.UTENTE_KEY)).getValueObject().getUsername();
                DocumentoVO documento = new DocumentoVO();
                documento.setDescrizione(null);
                documento.setFileName(fileName);
                documento.setPath(tempFilePath);
                documento.setImpronta(impronta);
                documento.setSize(size);
                documento.setContentType(contentType);
                documento.setRowCreatedTime(new Date(System.currentTimeMillis()));
                documento.setRowUpdatedTime(new Date(System.currentTimeMillis()));
                documento.setRowCreatedUser(username);
                documento.setRowUpdatedUser(username);
                form.setDocumentoPrincipale(documento);
            }
        }

    }

    public static void uploadFile(ProtocolloForm form, HttpServletRequest request,
            ActionMessages errors) {
        String username = ((Utente) request.getSession().getAttribute(
                Constants.UTENTE_KEY)).getValueObject().getUsername();
        String descrizione = form.getNomeFileUpload();
        FormFile file = form.getFormFileUpload();
        String fileName = file.getFileName();
        if ("".equals(descrizione))
            descrizione = fileName;
        String contentType = file.getContentType();
        int size = file.getFileSize();
        // controllo: l'utente avrebbe potuto premere il pulsante senza
        // selezionare un file
        if (size > 0 && !"".equals(fileName)) {
            // salva il file in uno temporaneo e ritorna il nome dello stesso
            String tempFilePath = FileUtil.leggiFormFile(form, request, errors);
            String impronta = FileUtil.calcolaDigest(tempFilePath, errors);
            if (errors.isEmpty()) {
                try {
                    VerificaFirma.verificaFileFirmato(tempFilePath, contentType);
                } catch (DataException e) {
                    errors.add("allegati", new ActionMessage(
                            "database.cannot.load"));
                } catch (CertificatoNonValidoException e) {
                    errors.add("allegati", new ActionMessage("errore.verificafirma.doc.non_valido", e.getMessage()));
                } catch (FirmaNonValidaException e) {
                    errors.add("allegati", new ActionMessage("errore.verificafirma.doc.non_valido", e.getMessage()));
                } catch (CRLNonAggiornataException e) {
                    errors.add("allegati", new ActionMessage(
                            "errore.verificafirma.crl_non_aggiornata"));
                }
                DocumentoVO documento = new DocumentoVO();
                documento.setDescrizione(descrizione);
                documento.setFileName(fileName);
                documento.setImpronta(impronta);
                documento.setPath(tempFilePath);
                documento.setSize(size);
                documento.setContentType(contentType);
                documento
                        .setRowCreatedTime(new Date(System.currentTimeMillis()));
                documento
                        .setRowUpdatedTime(new Date(System.currentTimeMillis()));
                documento.setRowCreatedUser(username);
                documento.setRowUpdatedUser(username);
                form.allegaDocumento(documento);
                form.setNomeFileUpload("");
            }
        }
    }

   
    /**
	 * @param doc
	 * @param response
	 * @return
	 */
	public static ActionMessages downloadFile(DocumentoVO doc,
			HttpServletResponse response) {
		InputStream is = null;
        OutputStream os = null;
        ActionMessages errors = new ActionMessages();
        try {

            if (doc != null) {
                os = response.getOutputStream();
                response.setContentType(doc.getContentType());
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + doc.getFileName());
                response.setHeader("Cache-control", "");
                if (doc.getId() != null && !doc.isMustCreateNew()) { 
                    DocumentoDelegate.getInstance().writeDocumentToStream(
                            doc.getId().intValue(), os);
                } else { // protocollo ancora da salvare: i file sono nella
                    // cartella temporanea
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
		return errors;
	}

    
}
