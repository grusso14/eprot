/*
 * Created on 18-05
 *
 * 
 */
package it.finsiel.siged.dao.mail;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.business.EmailDelegate;

import java.security.Security;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;

import org.apache.log4j.Logger;

import com.sun.net.ssl.internal.ssl.Provider;

/**
 * @author Almaviva sud
 * 
 */
public class PecEmailUscita {

    static Logger logger = Logger.getLogger(PecEmailUscita.class.getName());

    /**
     * Controlla una casella di posta per presenza di nuovi messaggi email. Per
     * ogni messaggio genera un ProtocolloIngresso.
     * 
     * @param emailSettings
     */
    public static void inviaProtocolliUscita(EmailDelegate delegate, int aooId,
            String host, String port, String username, String password,
            String emailMittente, String authentication, String tempFolder)
            throws NoSuchProviderException, MessagingException, DataException {

        Security.addProvider(new Provider());
        String SSL_FACTORY = "it.finsiel.siged.util.ssl.EprotSSLSocketFactory";
        Properties props = new Properties();
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.port", port);

        Session session = Session.getInstance(props);
        session.setDebug(true);

        Collection msgDaInviare = delegate.getListaMessaggiUscita(aooId);
        Iterator messaggiId = msgDaInviare.iterator();
        while (messaggiId.hasNext()) {
            int id = ((Integer) messaggiId.next()).intValue();

            try {
                delegate.inviaProtocolloEmail(session, id, tempFolder, host,
                        username, password, emailMittente);
            } catch (DataException e) {
                delegate.salvaEmailLog("Invio Protocollo in uscita fallito.",
                        "Errore dati", e.getMessage(),
                        EmailConstants.LOG_EMAIL_USCITA, aooId);
                logger.debug("", e);
            } catch (Exception e) {
                delegate.salvaEmailLog("Invio Protocollo in uscita fallito.",
                        "Errore non specifico", e.getMessage(),
                        EmailConstants.LOG_EMAIL_USCITA, aooId);
                logger.error("", e);
            }

        }
    }
}
