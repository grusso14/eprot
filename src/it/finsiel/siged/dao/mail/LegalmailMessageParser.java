/*
 * Created on 25-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.dao.mail;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.MessageParsingException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.mvc.vo.posta.PecDaticertVO;
import it.finsiel.siged.mvc.vo.posta.PecDestVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Almaviva sud
 */
public class LegalmailMessageParser {

    static Logger logger = Logger.getLogger(LegalmailMessageParser.class
            .getName());

    public static void parseMessage(MimeMessage msg, MessaggioEmailEntrata pe)
            throws MessageParsingException {

        HashMap allegati = new HashMap(2);

        // ======= P A R S I N G - M E S S A G E ======= //
        try {
            Object content = msg.getContent();
            if (content instanceof Multipart) {
                // parsing messaggion originale
                gestisciMultiPart((MimeMultipart) content, allegati);
                // estrazione dei dati dal file xml di daticert
                PecDaticertVO daticert = getDatiCertDaXML(allegati
                        .get(EmailConstants.DATICERT_XML));
                // Smime smime =
                // getSMimeP7S(allegati.get(EmailConstants.SMIME.P7S));
                if (allegati.containsKey(EmailConstants.DATICERT_XML)) {

                    if (daticert.isValid()) {

                        if (EmailConstants.TIPO_ACCETTAZIONE
                                .equalsIgnoreCase(daticert.getTipo())) {
                            pe.getEmail().setOggetto(daticert.getOggetto());
                        } else if (EmailConstants.TIPO_CONSEGNA
                                .equalsIgnoreCase(daticert.getTipo())) {
                            pe.getEmail().setOggetto(daticert.getOggetto());
                        } else if (EmailConstants.TIPO_POSTA_CERTIFICATA
                                .equalsIgnoreCase(daticert.getTipo())) {
                            MimeMessage eml = (MimeMessage) allegati
                                    .get(EmailConstants.POSTACERT_EML);
                            if (eml != null) {
                                MimeMessageParser.getProtocolloEmail(eml, pe);
                                pe.getEmail().setDataRicezione(
                                        eml.getReceivedDate());
                                pe.getEmail().setDataSpedizione(
                                        eml.getSentDate());
                            } else {
                                throw new MessageParsingException(
                                        "Allegato Email '"
                                                + EmailConstants.POSTACERT_EML
                                                + "' non trovato!");
                            }
                        }
                        pe.setDaticertXML(daticert);
                    } else {
                        throw new MessageParsingException("Il file allegato '"
                                + EmailConstants.DATICERT_XML
                                + "' ha un formato non valido.");
                    }
                } else {
                    //
                    MimeMessage eml = (MimeMessage) allegati
                            .get(EmailConstants.POSTACERT_EML);

                    if (eml != null) {

                        MimeMessageParser.getProtocolloEmail(eml, pe);
                        pe.setTipoEmail(EmailConstants.TIPO_ANOMALIA);
                        pe.getEmail().setDataRicezione(eml.getReceivedDate());
                        pe.getEmail().setDataSpedizione(eml.getSentDate());
                    } else {
                        pe.setTipoEmail(EmailConstants.TIPO_ALTRO);
                        // aggiungere l'intero messaggio nel pe?
                    }
                }
            } else {
                throw new MessageParsingException(
                        "Il messaggio non e' di tipo MIME.");
            }
        } catch (Exception e) {
            throw new MessageParsingException(e.getMessage());
        }
    }

    /*
     * Effettua le dovute operazioni su un MultiPart del messaggio. Gestendo i
     * possibili oggetti innestati.
     */
    private static void gestisciMultiPart(MimeMultipart multipart,
            HashMap allegati) throws MessagingException, IOException, Exception {
        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            Object content = multipart.getBodyPart(i);
            if (content instanceof Multipart) {
                gestisciMultiPart((MimeMultipart) content, allegati);
            } else if (content instanceof MimeBodyPart) {
                gestisciBodyPart((MimeBodyPart) content, allegati);
            }
        }
    }

    /*
     * Effettua le dovute operazioni su un BodyPart del messaggio. Gestendo i
     * possibili oggetti innestati.
     */
    private static void gestisciBodyPart(MimeBodyPart part, HashMap allegati)
            throws MessagingException, IOException, Exception {
        Object content = part.getContent();
        if (content instanceof MimeMessage
                && EmailConstants.POSTACERT_EML.equalsIgnoreCase(part
                        .getFileName())) {
            gestisciAllegatoEML(part, allegati);
        } else if (content instanceof MimeMultipart) {
            gestisciMultiPart((MimeMultipart) content, allegati);
        } else if (content instanceof InputStream
                && EmailConstants.DATICERT_XML.equalsIgnoreCase(part
                        .getFileName())) {
            gestisciAllegatoXML(part, allegati);
        } else if (content instanceof InputStream
                && EmailConstants.SMIME_P7S
                        .equalsIgnoreCase(part.getFileName())) {
            gestisciAllegatoP7S(part, allegati);
        }
    }

    /*
     * Salva un allegato su disco ed aggiunge un DocumentoVO nel ProtocolloEmail
     * con i relativi dati.
     */

    private static void gestisciAllegatoXML(Part part, HashMap allegati)
            throws MessagingException, IOException {
        System.out.println("gestisciAllegatoBinario:" + part.getFileName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = (InputStream) part.getContent();
        FileUtil.writeFile(is, baos);
        is.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        allegati.put(EmailConstants.DATICERT_XML, bais);
    }

    private static void gestisciAllegatoP7S(Part part, HashMap allegati)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = (InputStream) part.getContent();
        FileUtil.writeFile(is, baos);
        is.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        allegati.put(EmailConstants.SMIME_P7S, bais);
    }

    private static void gestisciAllegatoEML(MimeBodyPart part, HashMap allegati)
            throws MessagingException, IOException {
        logger.info("Allegato EML:" + part.getFileName());
        MimeMessage msg = (MimeMessage) part.getContent();
        allegati.put(part.getFileName(), msg);
    }

    // private static String salvaFile(String tempFolder, InputStream input)
    // throws Exception {
    // File file = null;
    // BufferedOutputStream bos = null;
    // BufferedInputStream bis = null;
    // try {
    // file = File.createTempFile("tmp_", ".email", new File(tempFolder));
    // logger.info("Saving file:" + file.getAbsolutePath());
    // bos = new BufferedOutputStream(new FileOutputStream(file));
    // bis = new BufferedInputStream(input);
    // FileUtil.writeFile(bis, bos);
    // } catch (FileNotFoundException e) {
    // throw new Exception(e);
    // } catch (IOException e) {
    // throw new Exception(e);
    // } finally {
    // FileUtil.closeOS(bos);
    // FileUtil.closeIS(bis);
    // }
    // return file.getAbsolutePath();
    // }

    /*
     * Salva una stringa in un file. @return Path al file appena creato. @param
     * folder La cartella dove salvare il file. @param filename Il nome da
     * assegnare al file. @param part La stringa da salvare nel file.
     */
    // private static String salvaFile(String folder, Object part)
    // throws Exception {
    // BufferedInputStream bis = new BufferedInputStream(
    // new ByteArrayInputStream(((String) part).getBytes()));
    // return salvaFile(folder, bis);
    // }
    private static PecDaticertVO getDatiCertDaXML(Object xmlIs) {
        PecDaticertVO dc = new PecDaticertVO();
        try {
            if (xmlIs == null || !(xmlIs instanceof InputStream))
                return dc;

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse((InputStream) xmlIs);

            NodeList nodes = doc.getChildNodes();

            for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String nodeName = node.getNodeName();

                if ("postacert".equalsIgnoreCase(nodeName)) {
                    NamedNodeMap map = node.getAttributes();
                    dc.setTipo(map.getNamedItem("tipo").getNodeValue());
                    dc.setErrore(map.getNamedItem("errore").getNodeValue());
                    NodeList subNodes = node.getChildNodes();
                    for (int j = 0; subNodes != null
                            && j < subNodes.getLength(); j++) {
                        Node subNode = subNodes.item(j);
                        String subNodeName = subNode.getNodeName();

                        if ("intestazione".equalsIgnoreCase(subNodeName)) {
                            NodeList intNodes = subNode.getChildNodes();
                            for (int k = 0; intNodes != null
                                    && k < intNodes.getLength(); k++) {
                                Node intNode = intNodes.item(k);
                                String intNodeName = intNode.getNodeName();
                                String nodeValue = intNode.hasChildNodes() ? intNode
                                        .getFirstChild().getNodeValue()
                                        : "";
                                if ("mittente".equalsIgnoreCase(intNodeName)) {
                                    dc.setMittente(nodeValue);
                                } else if ("destinatari"
                                        .equalsIgnoreCase(intNodeName)) {
                                    NamedNodeMap attr = intNode.getAttributes();
                                    PecDestVO dest = new PecDestVO();
                                    dest.setTipo(attr.getNamedItem("tipo")
                                            .getNodeValue());
                                    dest.setEmail(nodeValue);
                                    dc.addDestinatario(dest);
                                } else if ("risposte"
                                        .equalsIgnoreCase(intNodeName)) {
                                    dc.setRisposte(nodeValue);
                                } else if ("oggetto"
                                        .equalsIgnoreCase(intNodeName)) {
                                    dc.setOggetto(nodeValue);
                                }
                            }
                        } else if ("dati".equalsIgnoreCase(subNodeName)) {
                            NodeList intNodes = subNode.getChildNodes();
                            for (int k = 0; intNodes != null
                                    && k < intNodes.getLength(); k++) {
                                Node intNode = intNodes.item(k);
                                String intNodeName = intNode.getNodeName();
                                String nodeValue = intNode.hasChildNodes() ? intNode
                                        .getFirstChild().getNodeValue()
                                        : "";
                                if ("identificativo"
                                        .equalsIgnoreCase(intNodeName)) {
                                    dc.setIdentificativo(nodeValue);
                                } else if ("data".equalsIgnoreCase(intNodeName)) {
                                    // NamedNodeMap attr =
                                    // intNode.getAttributes();
                                    // String zona = attr.getNamedItem("zona")
                                    // .getNodeValue();
                                    String giorno = null;
                                    String ora = null;
                                    NodeList dataNodes = intNode
                                            .getChildNodes();
                                    for (int l = 0; dataNodes != null
                                            && l < dataNodes.getLength(); l++) {
                                        Node dataNode = dataNodes.item(l);
                                        if ("giorno".equalsIgnoreCase(dataNode
                                                .getNodeName())) {
                                            giorno = dataNode.getNodeValue();
                                        } else if ("ora"
                                                .equalsIgnoreCase(dataNode
                                                        .getNodeName())) {
                                            ora = dataNode.getNodeValue();
                                        }
                                    }
                                    Date d = DateUtil.getDataOra(giorno + " - "
                                            + ora);
                                    dc.setData(d);
                                } else if ("risposte"
                                        .equalsIgnoreCase(intNodeName)) {
                                    dc.setRisposte(nodeValue);
                                } else if ("oggetto"
                                        .equalsIgnoreCase(intNodeName)) {
                                    dc.setOggetto(nodeValue);
                                } else if ("consegna"
                                        .equalsIgnoreCase(intNodeName)) {
                                    PecDestVO dest = new PecDestVO();
                                    dest.setTipo("");
                                    dest.setEmail(nodeValue);
                                    dc.addDestinatario(dest);
                                }
                            }
                        }
                    }
                }
            }
            dc.setReturnValue(ReturnValues.VALID);
        } catch (NullPointerException e) {
            logger.error("", e);
            dc.setReturnValue(ReturnValues.INVALID);
        } catch (ClassCastException e) {
            logger.error("", e);
            dc.setReturnValue(ReturnValues.INVALID);
        } catch (ParserConfigurationException e) {
            logger.error("", e);
            dc.setReturnValue(ReturnValues.INVALID);
        } catch (SAXException e) {
            logger.error("", e);
            dc.setReturnValue(ReturnValues.INVALID);
        } catch (IOException e) {
            logger.error("", e);
            dc.setReturnValue(ReturnValues.INVALID);
        }
        return dc;
    }

    public static void main(String[] args) {
        try {
            LegalmailMessageParser
                    .getDatiCertDaXML(new FileInputStream(
                            "C:\\Documents and Settings\\p_finsiel\\Desktop\\Protocollazione\\pec\\consegna.xml"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
