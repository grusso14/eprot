/*
 * Created on 17-gen-2005
 *
 * 
 */
package it.finsiel.siged.dao.mail;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.EmailException;
import it.finsiel.siged.exception.MessageParsingException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.mvc.business.EmailDelegate;

import java.security.Security;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * @author Almaviva sud
 * 
 */
public class PecEmailIngresso {

    static Logger logger = Logger.getLogger(PecEmailIngresso.class.getName());

    /*
     * Direttive: Mi ha contattato Alessandro e mi ha detto che vogliono fare in
     * questo modo per la protocollazione di un documento: - se un documento
     * principale � un documento firmato viene procollato a prescindere dal tipo
     * (pdf, doc, etc...) - se il documento principale � un file Pdf non firmato
     * si appone il timbro - se il documento principale � un file NON Pdf non
     * firmato NON si appone il timbro
     */

    /**
     * Controlla una casella di posta per presenza di nuovi messaggi email. Per
     * ogni messaggio genera un ProtocolloIngresso.
     * 
     * 
     * @param emailSettings
     */
    public static void preparaProtocolliMessaggiIngresso(
            EmailDelegate delegate, int aooId, String host, String port,
            String username, String password, String authentication,
            String tempFolder) throws EmailException {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        String SSL_FACTORY = "it.finsiel.siged.util.ssl.EprotSSLSocketFactory";
        Properties props = new Properties();
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", port);
        props.setProperty("mail.pop3.socketFactory.port", port);

        try {

            int counter = 0;
            while (true) {
                Session session = Session.getInstance(props);
                Store store = session.getStore("pop3");
                //store.connect("pop3s.pec.aruba.it", "flosslab@arubapec.it", "mefad2008");
                store.connect(host, username, password);
                logger.debug("Connessione al server di posta effettuata.");
                Folder folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                Message[] messages = folder.getMessages();
                logger.debug("Trovati " + messages.length + " messaggio/i");
                counter++;
                if (folder.getMessageCount() < 1 || counter > 50)
                    break;

                try {

                    MimeMessage msg = (MimeMessage) messages[0];// primo
                    // messaggio
                    MessaggioEmailEntrata pe = new MessaggioEmailEntrata();
                    pe.getEmail().setAooId(aooId);
                    pe.setTempFolder(tempFolder);

                    if (!msg.isSet(Flags.Flag.DELETED)) {
                        try {
                            LegalmailMessageParser.parseMessage(msg, pe);
                            logger.info(" Tipo email:" + pe.getTipoEmail());
                            logger.info(" Allegati  :"
                                    + pe.getAllegati().size());

                            int retVal = ReturnValues.UNKNOWN;
                            if (EmailConstants.TIPO_POSTA_CERTIFICATA
                                    .equalsIgnoreCase(pe.getTipoEmail())) {
                                retVal = delegate.salvaEmailIngresso(pe);
                            } else if (EmailConstants.TIPO_ANOMALIA
                                    .equalsIgnoreCase(pe.getTipoEmail())) {
                                // gestire diversamente ?
                                retVal = delegate.salvaEmailIngresso(pe);
                            } else if (EmailConstants.TIPO_ACCETTAZIONE
                                    .equalsIgnoreCase(pe.getTipoEmail())
                                    || EmailConstants.TIPO_CONSEGNA
                                            .equalsIgnoreCase(pe.getTipoEmail())
                                    || EmailConstants.TIPO_ALTRO
                                            .equalsIgnoreCase(pe.getTipoEmail())) {
                                retVal = delegate.salvaEmailIngressoLog(pe);
                            }

                            if (retVal == ReturnValues.SAVED) {
                                msg.setFlag(Flags.Flag.DELETED, true);
                            }
                        } catch (MessageParsingException e) {
                            delegate.salvaEmailLog("Messaggio: "
                                    + (msg != null ? (" " + msg.getSubject())
                                            : (" ")),
                                    "Errore Elaborazione Messaggio Email", e
                                            .getMessage(),
                                    EmailConstants.LOG_EMAIL_INGRESSO, aooId);
                            logger.error("", e);
                        } catch (Exception e) {
                            delegate.salvaEmailLog("Messaggio: "
                                    + (msg != null ? (" " + msg.getSubject())
                                            : (" ")), "Errore non specifico", e
                                    .getMessage(),
                                    EmailConstants.LOG_EMAIL_INGRESSO, aooId);
                            logger.error("", e);
                        }

                    }
                } catch (MessagingException e2) {
                    throw new EmailException(
                            "Errore nella lettura/elaborazione dei messaggi di posta elettronica.\nErrore: "
                                    + e2.getMessage());
                }
                // chiudo connessione e cancello envetuali messaggi gi�
                // scaricati
                try {
                    folder.close(true);
                    store.close();
                } catch (MessagingException e) {
                    logger.warn("", e);
                }
            }

        } catch (NoSuchProviderException e1) {
            throw new EmailException("Impossibile contattare l'host:" + host);
        } catch (MessagingException e1) {
        		e1.printStackTrace();
            throw new EmailException(
                    "Impossibile leggere i messaggi dalla cartella Inbox dell'host:"
                            + host+e1.getStackTrace()+"CIAO DANI"+e1.getLocalizedMessage());
        }

    }

    // private SecretKey getMAC() {
    // try {
    // // Generate a key for the HMAC-MD5 keyed-hashing algorithm; see RFC
    // // 2104
    // // In practice, you would save this key.
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
    // SecretKey key = keyGen.generateKey();
    //
    // // Create a MAC object using HMAC-MD5 and initialize with key
    // Mac mac = Mac.getInstance(key.getAlgorithm());
    // mac.init(key);
    //
    // String str = "This message will be digested";
    //
    // // Encode the string into bytes using utf-8 and digest it
    // byte[] utf8 = str.getBytes("UTF8");
    // byte[] digest = mac.doFinal(utf8);
    //
    // // If desired, convert the digest into a string
    // String digestB64 = new sun.misc.BASE64Encoder().encode(digest);
    // } catch (InvalidKeyException e) {
    // } catch (NoSuchAlgorithmException e) {
    // } catch (UnsupportedEncodingException e) {
    // }
    //
    // try {
    // // Generate a key for the HMAC-MD5 keyed-hashing algorithm
    // KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");
    // SecretKey key = keyGen.generateKey();
    //
    // // Generate a key for the HMAC-SHA1 keyed-hashing algorithm
    // keyGen = KeyGenerator.getInstance("HmacSHA1");
    // return key = keyGen.generateKey();
    // } catch (java.security.NoSuchAlgorithmException e) {
    // }
    // return null;
    // }
}
