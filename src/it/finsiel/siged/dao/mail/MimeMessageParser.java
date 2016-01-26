/*

 */
package it.finsiel.siged.dao.mail;

import it.finsiel.siged.exception.MessageParsingException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.util.FileUtil;
import it.finsiel.siged.util.HtmlParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Almaviva sud
 * 
 */
public class MimeMessageParser {

    static Logger logger = Logger.getLogger(MimeMessageParser.class.getName());

    // ========= ENTRATA ============= //
    public static void getProtocolloEmail(MimeMessage msg,
            MessaggioEmailEntrata pe) throws MessageParsingException {

        String subj = "";
        String from = "";

        try {
            // getting general infos
            if (msg.getFrom() != null && msg.getFrom().length > 0) {
                from = ((InternetAddress) msg.getFrom()[0]).getAddress();
                pe.getEmail().setEmailMittente(from);
                pe.getEmail().setNomeMittente(
                        ((InternetAddress) msg.getFrom()[0]).getPersonal());
            }
            subj = msg.getSubject();
            pe.getEmail().setOggetto(subj);
            pe.getEmail().setDataSpedizione(msg.getSentDate());
            pe.getEmail().setDataRicezione(msg.getReceivedDate());
            // ======= P A R S I N G - M E S S A G E ======= //
            Object content = msg.getContent();
            if (content instanceof Multipart) {
                gestisciMultiPart((MimeMultipart) content, pe);
            } else if (content instanceof Part) {
                gestisciPart((Part) content, pe);
            } else if (content instanceof String
                    && msg.isMimeType("text/plain")) {
                gestisciBodyAsText((String) content, pe);
            } else if (content instanceof String && msg.isMimeType("text/html")) {
                gestisciBodyAsText(HtmlParser
                        .restoreSpecialCharacters(HtmlParser
                                .stripHtmlTags((String) content)), pe);
            } else {
                logger.warn("MimeMessage part di tipo non gestito:" + content);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new MessageParsingException(
                    "Errore nell'elaborazione del messaggio.\nErrore:"
                            + e.getMessage());
        }
    }

    /*
     * Effettua le dovute operazioni su un Part del messaggio. Gestendo i
     * possibili oggetti innestati.
     */
    private static void gestisciPart(Part part, MessaggioEmailEntrata pe)
            throws MessagingException, IOException {
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
                && part.getContent() instanceof String) {
            gestisciAllegatoTesto(part, pe);
        } else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
                && part.getContent() instanceof InputStream) {
            gestisciAllegato(part, pe);
        } else if (Part.INLINE.equalsIgnoreCase(part.getDisposition())
                && part.getContent() instanceof String) {
            if (part.isMimeType("text/html"))
                gestisciBodyAsText(HtmlParser
                        .restoreSpecialCharacters(HtmlParser
                                .stripHtmlTags((String) part.getContent())), pe);
            else
                gestisciBodyAsText((String) part.getContent(), pe);
        } else {
            logger.warn("Part di tipo sconosciuta:" + part.getClass());
        }
    }

    /*
     * Effettua le dovute operazioni su un MultiPart del messaggio. Gestendo i
     * possibili oggetti innestati.
     */
    private static void gestisciMultiPart(MimeMultipart multipart,
            MessaggioEmailEntrata pe) throws MessagingException, IOException {
        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            Object content = multipart.getBodyPart(i);
            if (content instanceof Multipart) {
                gestisciMultiPart((MimeMultipart) content, pe);
            } else if (content instanceof MimeBodyPart) {
                gestisciBodyPart((MimeBodyPart) content, pe);
            } else {
                gestisciPart((Part) content, pe);
            }
        }
    }

    private static void gestisciBodyPart(MimeBodyPart part,
            MessaggioEmailEntrata pe) throws MessagingException, IOException {
        Object content = part.getContent();
        if (content instanceof MimeMessage) {
            gestisciAllegato(part, pe);
        } else if (content instanceof MimeMultipart) {
            gestisciMultiPart((MimeMultipart) content, pe);
        } else if (content instanceof InputStream) {
            gestisciAllegato(part, pe);
        } else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())
                && content instanceof String) {
            gestisciAllegatoTesto(part, pe);
        } else if (part.isMimeType("text/plain")) {
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                gestisciAllegato(part, pe);
            } else if (content instanceof String) {
                gestisciPlainTextPart((String) content, pe);
            } else {
                logger
                        .warn("Parte del BodyPart di tipo non gestito:"
                                + content);
            }
        } else if (part.isMimeType("text/html")) {
            // come allegato o come pe.getEmail().getTestMessaggio() ?
            gestisciBodyAsText(HtmlParser.restoreSpecialCharacters(HtmlParser
                    .stripHtmlTags((String) content)), pe);
        } else if (content instanceof String) {
            gestisciPlainTextPart((String) content, pe);
        }

    }

    /*
     * Gestisce il "body" del messaggio. Crea un file di tipo txt su disco e
     * ritorna un DocumentoVO che contiene tutte le referenze a tale parte del
     * messaggio.
     */
    private static void gestisciPlainTextPart(String part,
            MessaggioEmailEntrata pe) {
        pe.getEmail().setTestoMessaggio(part);
    }

    /*
     * Gestisce il "body" del messaggio. Sostituisce la versione di tipo testo,
     * se presente, con una versione testo ma estratta dalla versione HTML del
     * body. Di norma la versione testo del messaggio body dovrebbe essere
     * presente, ma alcuni client di email impostano solo quella html, il codice
     * gestisce entrambi i casi.
     */
    private static void gestisciBodyAsText(String part, MessaggioEmailEntrata pe) {
        pe.getEmail().setTestoMessaggio(part);
        pe.getEmail().setContentType("text/plain");
    }

    /*
     * Salva un allegato su disco ed aggiunge un DocumentoVO nel ProtocolloEmail
     * con i relativi dati.
     */

    private static void gestisciAllegato(Part part, MessaggioEmailEntrata pe)
            throws MessagingException, IOException {

        File tmpAtt = null;
        logger.info("Allegato:" + part.getFileName());
        tmpAtt = File.createTempFile("tmp_att_", ".email", new File(pe
                .getTempFolder()));

        FileUtil.writeFile((InputStream) part.getContent(), tmpAtt
                .getAbsolutePath());
        DocumentoVO doc = new DocumentoVO();
        doc.setContentType(part.getContentType());
        doc.setDescrizione(part.getFileName());
        doc.setPath(tmpAtt.getAbsolutePath());
        doc.setSize((int) tmpAtt.length());
        doc.setFileName(part.getFileName());
        pe.addAllegato(doc);

    }

    /*
     * Salva un allegato di tipo String su disco ed aggiunge un DocumentoVO nel
     * ProtocolloEmail con i relativi dati.
     */
    private static void gestisciAllegatoTesto(Part part,
            MessaggioEmailEntrata pe) throws IOException, MessagingException {
        logger.info("Allegato Testo:" + part.getFileName());

        if (isSegnatura(part)) {
            pe.getEmail().setSegnatura((String) part.getContent());
        } else {
            File tmpAtt = new File(salvaFile(pe.getTempFolder(), part
                    .getFileName(), (String) part.getContent()));
            DocumentoVO doc = new DocumentoVO();
            doc.setContentType(part.getContentType());
            doc.setDescrizione(part.getFileName());
            doc.setPath(tmpAtt.getAbsolutePath());
            doc.setSize((int) tmpAtt.length());
            doc.setFileName(part.getFileName());
            pe.addAllegato(doc);
        }

    }

    private static String salvaFile(String tempFolder, String filename,
            InputStream input) throws IOException {
        File file = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            file = File.createTempFile("tmp_", ".email", new File(tempFolder));

            logger.debug("Saving file:" + file.getAbsolutePath());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bis = new BufferedInputStream(input);
            FileUtil.writeFile(bis, bos);
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } finally {
            FileUtil.closeOS(bos);
            FileUtil.closeIS(bis);
        }
        return file.getAbsolutePath();
    }

    /*
     * Salva una stringa in un file. @return Path al file appena creato. @param
     * folder La cartella dove salvare il file. @param filename Il nome da
     * assegnare al file. @param part La stringa da salvare nel file.
     */
    private static String salvaFile(String folder, String filename, Object part)
            throws IOException {
        BufferedInputStream bis = new BufferedInputStream(
                new ByteArrayInputStream(((String) part).getBytes()));
        return salvaFile(folder, filename, bis);
    }

    private static boolean isSegnatura(Part part) throws IOException,
            MessagingException {
        boolean ret = false;
        try {
            String xml = (String) part.getContent();
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new InputSource(
                    new StringReader(xml)));
            NodeList nodes = doc.getChildNodes();
            for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String nodeName = node.getNodeName();
                logger.info("node:" + nodeName);
                // un xml è di segnatura quando ha nella lista dei nodi sotto il
                // root(documento)
                // un nodo di nome "Segantura"
                if ("Segnatura".equalsIgnoreCase(nodeName)) {
                    ret = true;
                    break;
                }
            }
        } catch (FactoryConfigurationError e) {
            ;
        } catch (ParserConfigurationException e) {
            ;
        } catch (SAXException e) {
            ;
        }
        return ret;
    }

}