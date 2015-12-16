package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.integration.EmailDAO;
import it.finsiel.siged.mvc.vo.posta.CodaInvioVO;
import it.finsiel.siged.mvc.vo.posta.EmailVO;
import it.finsiel.siged.mvc.vo.posta.PecDestVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class EmailDelegate implements ComponentStatus {

    private static Logger logger = Logger.getLogger(EmailDelegate.class
            .getName());

    private int status;

    private EmailDAO emailDAO = null;

    private ServletConfig config = null;

    private static EmailDelegate delegate = null;

    private EmailDelegate() {
        try {
            if (emailDAO == null) {
                emailDAO = (EmailDAO) DAOFactory
                        .getDAO(Constants.EMAIL_DAO_CLASS);
                logger.debug("EmailDAO instantiated:"
                        + Constants.EMAIL_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            status = STATUS_ERROR;
            logger.error("", e);
        }

    }

    public static EmailDelegate getInstance() {
        if (delegate == null)
            delegate = new EmailDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.ANNOTAZIONE_DELEGATE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int s) {
        this.status = s;
    }

    public boolean eliminaEmail(int emailId) throws Exception {
        JDBCManager jdbcMan = null;
        boolean cancellata = false;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();

            emailDAO.eliminaEmail(connection, emailId);
            connection.commit();
            cancellata = true;
        } catch (SQLException e) {
            jdbcMan.rollback(connection);
            logger.error("", e);
        } catch (DataException e) {
            jdbcMan.rollback(connection);
            logger.error("", e);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellata;
    }

    public boolean eliminaEmailLog(String[] ids) {
        JDBCManager jdbcMan = null;
        boolean cancellati = false;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();

            emailDAO.eliminaEmailLog(connection, ids);
            connection.commit();
            cancellati = true;
        } catch (DataException e) {
            jdbcMan.rollback(connection);
            logger.error("", e);
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.error("", e);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellati;
    }

    public void salvaMessaggioPerInvio(Connection connection, int id,
            int aooId, int protocolloId, Collection destinatari)
            throws DataException {

        emailDAO.salvaMessaggioPerInvio(connection, id, aooId, protocolloId,
                destinatari);
    }

    public Collection getDestinatariMessaggioUscita(int msgId)
            throws DataException {
        return emailDAO.getDestinatariMessaggioUscita(msgId);
    }

    public Collection getListaMessaggiUscita(int aooId) throws DataException {
        return emailDAO.getListaMessaggiUscita(aooId);
    }

    public Collection getListaLog(int aooId, int tipoLog) throws DataException {
        return emailDAO.getListaLog(aooId, tipoLog);
    }

    public void segnaMessaggioComeInviato(int msgId) throws DataException {
        emailDAO.segnaMessaggioComeInviato(msgId);
    }

    public void inviaProtocolloEmail(Session session, int id,
            String tempFolder, String host, String username, String password,
            String mittenteEmail) throws DataException {
        MimeMessage messaggio = null;
        int numeroProtocollo = 0;
        try {
            // get msg
            CodaInvioVO rec = emailDAO.getMessaggioDaInviare(id);
            if (rec != null)
                numeroProtocollo = rec.getNumeroProtocollo();
            if (ReturnValues.FOUND == rec.getReturnValue()) {
                Collection destinatari = getDestinatariMessaggioUscita(id);
                if (!destinatari.isEmpty()) {

                    ProtocolloUscita pu = ProtocolloDelegate.getInstance()
                            .getProtocolloUscitaById(rec.getProtocolloId());
                    if (pu != null) {
                        messaggio = new MimeMessage(session);
                        MimeMultipart multipart = new MimeMultipart();

                        // oggetto
                        messaggio.setSubject(ProtocolloBO.getTimbro(
                                Organizzazione.getInstance(), pu
                                        .getProtocollo()));
                        // body
                        MimeBodyPart messageBody = new MimeBodyPart();
                        messageBody.setContent(pu.getProtocollo().getOggetto(),
                                "text/plain");
                        multipart.addBodyPart(messageBody);
                        // mittente
                        InternetAddress addressFrom = new InternetAddress(
                                mittenteEmail);
                        messaggio.setFrom(addressFrom);
                        // destinatari
                        Iterator dest = destinatari.iterator();
                        while (dest.hasNext()) {
                            PecDestVO d = (PecDestVO) dest.next();
                            messaggio.addRecipient(
                                    MimeMessage.RecipientType.TO,
                                    new InternetAddress(d.getEmail(), d
                                            .getNominativo()));
                        }

                        // segnatura : allegato
                        MimeBodyPart messageSegn = new MimeBodyPart();
                        messageSegn.setContent(ProtocolloBO.getSignature(pu),
                                "text/plain");
                        messageSegn.setDisposition(Part.ATTACHMENT);
                        messageSegn.setFileName("segnatura.xml");
                        multipart.addBodyPart(messageSegn);
                        // documento principale : allegato
                        DocumentoVO mainDoc = pu.getDocumentoPrincipale();
                        Map allegati = pu.getAllegati();
                        if (mainDoc != null && mainDoc.getId() != null)
                            allegati.put(mainDoc.getId(), mainDoc);
                        // documenti : allegati
                        DocumentoVO[] docs = (DocumentoVO[]) allegati.values()
                                .toArray(new DocumentoVO[0]);
                        for (int i = 0; docs != null && i < docs.length; i++) {
                            // Segnatura.dtd: i nomi dei file devono essere
                            // univoci?
                            // devono essere gli stessi nomi se allegati al
                            // messaggio?
                            // oppure possono prendere il nome della
                            // descrizione?
                            DocumentoVO doc = docs[i];
                            // String fileName = doc.getPath();
                            // String desc = doc.getDescrizione();
                            File tempFile = File.createTempFile("temp_",
                                    ".attachment", new File(tempFolder));
                            doc.setPath(tempFile.getAbsolutePath());
                            FileOutputStream os = new FileOutputStream(tempFile);
                            DocumentoDelegate.getInstance()
                                    .writeDocumentToStream(
                                            doc.getId().intValue(), os);
                            os.close();
                            MimeBodyPart messageBodyPart = new MimeBodyPart();

                            FileDataSource source = new FileDataSource(tempFile);

                            messageBodyPart.setDataHandler(new DataHandler(
                                    source));
                            messageBodyPart.setFileName(doc.getFileName());
                            multipart.addBodyPart(messageBodyPart);
                        }

                        messaggio.setContent(multipart);
                        messaggio.saveChanges();
                        // sending message
                        // messaggio.writeTo(new FileOutputStream("C:/" + id+
                        // ".eml"));

                        Transport transport = session.getTransport("smtp");
                        transport.connect(host, username, password);
                        transport.sendMessage(messaggio, messaggio
                                .getAllRecipients());
                        delegate.segnaMessaggioComeInviato(id);

                        // clean-up temp files
                        for (int i = 0; docs != null && i < docs.length; i++) {
                            FileUtil.deleteFile(docs[i].getPath());
                        }

                    } else {
                        throw new DataException(
                                "Errore nella lettura dei dati. Protocollo Numero:"
                                        + numeroProtocollo);
                    }
                } else {
                    throw new DataException(
                            "Il messaggio non ha destinatari. Protocollo Numero:"
                                    + numeroProtocollo);
                }
            } else {
                throw new DataException(
                        "Messaggio non trovato sulla base dati. Protocollo Numero:"
                                + numeroProtocollo);
            }
        } catch (AddressException e) {
            logger.debug("", e);
            throw new DataException(
                    "Errore nell'invio del messaggio - Protocollo Numero:"
                            + numeroProtocollo + " Messaggio Id=" + id + "\n"
                            + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.debug("", e);
            throw new DataException(
                    "Errore nell'invio del messaggio - Protocollo Numero:"
                            + numeroProtocollo + " Messaggio Id=" + id + "\n"
                            + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.debug("", e);
            throw new DataException(
                    "Errore nell'invio del messaggio - Protocollo Numero:"
                            + numeroProtocollo + " Messaggio Id=" + id + "\n"
                            + e.getMessage());
        } catch (MessagingException e) {
            logger.debug("", e);
            throw new DataException(
                    "Errore nell'invio del messaggio - Protocollo Numero:"
                            + numeroProtocollo + " Messaggio Id=" + id + "\n"
                            + e.getMessage());
        } catch (IOException e) {
            logger.debug("", e);
            throw new DataException(
                    "Errore nell'invio del messaggio - Protocollo Numero:"
                            + numeroProtocollo + " Messaggio Id=" + id + "\n"
                            + e.getMessage());
        }
    }

    public int salvaEmailIngresso(MessaggioEmailEntrata email) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        IdentificativiDelegate idDelegate = IdentificativiDelegate
                .getInstance();
        int retVal = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();

            int emailId = idDelegate.getNextId(connection,
                    NomiTabelle.EMAIL_INGRESSO);

            // salva email
            EmailVO emailVO = email.getEmail();
            emailVO.setId(emailId);
            emailDAO.salvaEmail(emailVO, connection);

            // salva allegati
            Iterator iterator = email.getAllegati().iterator();
            while (iterator.hasNext()) {
                DocumentoVO allegato = (DocumentoVO) iterator.next();
                allegato.setId(idDelegate.getNextId(connection,
                        NomiTabelle.EMAIL_INGRESSO_ALLEGATI));
                emailDAO.salvaAllegato(connection, allegato, emailId);
            }

            connection.commit();
            retVal = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return retVal;
    }

    public int salvaEmailIngressoLog(MessaggioEmailEntrata email) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        IdentificativiDelegate idDelegate = IdentificativiDelegate
                .getInstance();
        int retVal = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            // salva email
            email.getEmail().setId(
                    idDelegate.getNextId(connection,
                            NomiTabelle.EMAIL_INGRESSO_LOGS));
            emailDAO.salvaEmailLog(email, connection);

            connection.commit();
            retVal = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return retVal;
    }

    public int salvaEmailLog(String oggetto, String tipo, String errore,
            int tipoLog, int aooId) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        IdentificativiDelegate idDelegate = IdentificativiDelegate
                .getInstance();
        int retVal = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            emailDAO.salvaEmailLog(idDelegate.getNextId(connection,
                    NomiTabelle.EMAIL_INGRESSO_LOGS), oggetto, tipo, errore,
                    connection, tipoLog, aooId);
            connection.commit();
            retVal = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio EmailIngresso fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return retVal;
    }

    public void aggiornaStatoEmailIngresso(Connection connection,
            int messaggioId, int stato) throws DataException {
        emailDAO.aggiornaStatoEmailIngresso(connection, messaggioId, stato);
    }

    public MessaggioEmailEntrata getMessaggioEntrata(int emailId, Utente utente)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        MessaggioEmailEntrata msg = new MessaggioEmailEntrata();
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();

            msg.setEmail(emailDAO.getEmailEntrata(connection, emailId));

            String tempFolder = utente.getValueObject().getTempFolder();
            File tempFile = null;
            OutputStream os = null;
            msg.setAllegati(emailDAO.getAllegatiEmailEntrata(connection,
                    emailId));
            Iterator it = msg.getAllegati().iterator();
            while (it.hasNext()) {
                DocumentoVO doc = (DocumentoVO) it.next();
                tempFile = File.createTempFile("msg_email_", ".att", new File(
                        tempFolder));
                os = new FileOutputStream(tempFile.getAbsolutePath());
                emailDAO.writeDocumentoToStream(connection, doc.getId()
                        .intValue(), os);
                os.close();
                doc.setPath(tempFile.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }

        return msg;
    }

    public Collection getMessaggiDaProtocollare(int aooId) throws DataException {
        return emailDAO.getMessaggiDaProtocollare(aooId);
    }

}