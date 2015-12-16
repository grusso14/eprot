/*
 * Created on 13-mag-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.util.firma;

import it.finsiel.siged.exception.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Almaviva sud
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class VerificaFirma {

    public X509CRL leggiCRL(String crlFile) {
        try {
            InputStream inStream = new FileInputStream(crlFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509CRL crl = (X509CRL) cf.generateCRL(inStream);
            inStream.close();
            return crl;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CRLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public X509CRLEntry getRevokedCertificate(BigInteger serialNumber,
            X509CRL crl) {
        X509CRLEntry entry = null;
        entry = crl.getRevokedCertificate(serialNumber);
        return entry;
    }

    public static void getCertificateInfo(String cerFile) {
        try {
            InputStream inStream = new FileInputStream(cerFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf
                    .generateCertificate(inStream);
            inStream.close();
            // System.out.println(cert.getIssuerDN());
            String[] urls = getCRLDistributionPoints(cert);
            /*
             * if (urls != null && urls.length > 0) { for (int i = 0; i <
             * urls.length; i++) { System.out.println("CRL " + i + " : " +
             * urls[i]); } } else { System.out.println("Nessuna CRL trovata!"); }
             */
        } catch (Exception e) {
            ;
        }
    }

    public static void getCertificateFromP7M() {
        try {
            FileInputStream fis = new FileInputStream(
                    "C:\\Documents and Settings\\p_finsiel\\Desktop\\Verifica Firma\\LISTACER_20050302.zip.p7m");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection c = cf.generateCertificates(fis);
            Iterator i = c.iterator();
            while (i.hasNext()) {
                X509Certificate cert = (X509Certificate) i.next();
                System.out.println("Serial#:" + cert.getSerialNumber());
                System.out.println("Issuer#:" + cert.getIssuerDN().getName());
                byte[] idBytes = cert
                        .getExtensionValue(X509Extensions.AuthorityKeyIdentifier
                                .getId());
                DERInteger dInt = new DERInteger(idBytes);
                System.out.println(cert.getIssuerDN() + " - Authority Key Id="
                        + dInt.getValue().toString());
                idBytes = cert
                        .getExtensionValue(X509Extensions.SubjectKeyIdentifier
                                .getId());
                dInt = new DERInteger(idBytes);
                System.out.println(" - Subject Key Id="
                        + dInt.getValue().toString());

            }
            fis.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void saveContentFromP7M(String p7mFile, String destFile) {
        try {
            Security.addProvider(new BouncyCastleProvider());
            OutputStream os = new FileOutputStream(destFile);
            InputStream p7mIs = new FileInputStream(p7mFile);
            CMSSignedData cms = new CMSSignedData(p7mIs);
            cms.getSignedContent().write(os);
            p7mIs.close();
            os.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Verifica l'integrità del file p7m, calcola l'impronta del file firmato e
     * la compara con quella di verifica, solitamente contenuta nella
     * segnatura.xml
     */
    public static boolean verificaIntegritaFileFirmato(InputStream p7mIs,
            String improntaDiVerifica) throws ValidationException {
        // --extract original content
        // originContent = getContentFromP7M()
        // --calculate message digest
        // impronta = getImprontaFromFile(originContent)
        // --verifica dell'uguaglianza delle impronte: tra quella calcolata dal
        // file firmato e quella fornita nella segnatura
        return true;
    }

    /*
     * Verifica "forte" di un file firmato di tipo P7M. Controlli eseguiti: - Il
     * file ha almeno un Signer - Il file ha almeno un certificato per ogni
     * Signer trovato - Verifica 'debole' per ogni firma apposta sul file:
     * controlla che il "message digest" ottenuto dal file e' stato codificato
     * (ecco cos'è una firma!) utilizzando la chiave primaria corrispondente a
     * quella pubblica (inclusa nel file). Verifica di tipo: SHA-1 a chiavi
     * simmetriche. - Il file
     */

    public static boolean verificaP7M(InputStream p7mIs)
            throws ValidationException {
        try {
            Security.addProvider(new BouncyCastleProvider());
            CMSSignedData cms = new CMSSignedData(p7mIs);
            CertStore certs = cms.getCertificatesAndCRLs("Collection", "BC");
            SignerInformationStore signers = cms.getSignerInfos();
            Collection c = signers.getSigners();
            Iterator it = c.iterator();
            // controlliamo tutti i soggetti che hanno firmato il file
            if (!c.isEmpty()) {
                while (it.hasNext()) {
                    // in un file PKCS#7 il soggetto viene identificato in base
                    // all'ente CA che ha rilasciato il certificato e il numero
                    // progressivo ad esso associato (univoco per CA)
                    SignerInformation signerInfo = (SignerInformation) it
                            .next();
                    SignerId signerID = signerInfo.getSID();
                    String issuerDN = signerID.getIssuerAsString();
                    String certSerialNumber = signerID.getSerialNumber()
                            .toString();
                    System.out.println("Signer certificate: S/N "
                            + certSerialNumber + " issued by " + issuerDN);
                    // leggo i certificati all'interno del file firmato per ogni
                    // soggetto che ha firmato.
                    // se non sono presenti il file viene considerato non
                    // valido.
                    // Nota: di norma il certificato andrebbe reperito in modo
                    // diverso, non e' obbligatorio includerlo.
                    Collection certCollection = certs.getCertificates(signerID);
                    Iterator certIter = certCollection.iterator();
                    if (!certCollection.isEmpty()) {
                        while (certIter.hasNext()) {
                            X509Certificate x509Cert = (X509Certificate) certIter
                                    .next();
                            // verifica semplice
                            if (!signerInfo.verify(x509Cert, "BC")) {
                                throw new ValidationException(
                                        "Verifica debole: Firma non valida per '"
                                                + x509Cert
                                                        .getSubjectX500Principal()
                                                        .getName() + "'.");
                            } else {
                                // controllo sulle CRL
                                // TODO: aggiungere controllo e spostare la
                                // classe da questo package.
                            }
                        }
                    } else {
                        throw new ValidationException(

                        "Nessun certificato trovato per il soggetto:'" + "'.");
                    }

                }
            } else {
                throw new ValidationException(
                        "Nessuna firma trovata all'interno del file.");
            }
            return true;
        } catch (CertificateExpiredException e) {
            throw new ValidationException("Certificato scaduto.");
        } catch (CertificateNotYetValidException e) {
            throw new ValidationException("Certificato non ancora valido.");
        } catch (NoSuchAlgorithmException e) {
            throw new ValidationException(
                    "Algoritimo di codifica non supportato.\n" + e.getMessage());
        } catch (NoSuchProviderException e) {
            throw new ValidationException(
                    "Provider di Certificato non trovato.");
        } catch (CertStoreException e) {
            throw new ValidationException(
                    "Errore nella lettura dei certificati dal file.");
        } catch (CMSException e) {
            throw new ValidationException(
                    "Errore nella lettura del contenuto firmato.\nPKCS#7 non valido.");
        }
    }

    public static String[] getCRLDistributionPoints(X509Certificate cert)
            throws IOException {

        byte[] extBytes = cert
                .getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
        boolean[] idBool = cert.getIssuerUniqueID();
        byte[] idBytes = cert
                .getExtensionValue(X509Extensions.SubjectKeyIdentifier.getId());
        DERInteger dInt = new DERInteger(idBytes);
        System.out.println(cert.getIssuerDN() + " - Authority Key Id="
                + dInt.getValue().toString());
        ASN1InputStream ansiIs = new ASN1InputStream(new ByteArrayInputStream(
                extBytes));
        DERObject derObj = ansiIs.readObject();
        DEROctetString dos = (DEROctetString) derObj;
        byte[] ansiOctets = dos.getOctets();
        ASN1InputStream ansiIsOc = new ASN1InputStream(
                new ByteArrayInputStream(ansiOctets));
        DERObject derObj2 = ansiIsOc.readObject();
        Vector urls = getDERValue(derObj2);
        if (urls == null)
            return null;
        return (String[]) urls.toArray(new String[0]);
    }

    private static void dumpInfos(File directory) {

        if (directory.exists() && directory.isDirectory()) {
            File[] fileArray = directory.listFiles();

            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].isDirectory()) {
                    dumpInfos(fileArray[i]);
                } else {
                    getCertificateInfo(fileArray[i].getAbsolutePath());
                }
            }
        }

    }

    // ========== util
    private static Vector getDERValue(DERObject derObj) {
        if (derObj instanceof DERSequence) {
            Vector ret = new Vector();
            DERSequence seq = (DERSequence) derObj;
            Enumeration en = seq.getObjects();
            while (en.hasMoreElements()) {
                DERObject nestedObj = (DERObject) en.nextElement();
                Vector appo = getDERValue(nestedObj);
                ret.addAll(appo);
            }
            return ret;
        }
        if (derObj instanceof DERTaggedObject) {
            DERTaggedObject derTag = (DERTaggedObject) derObj;
            if (derTag.isExplicit() && !derTag.isEmpty()) {
                DERObject nestedObj = derTag.getObject();
                Vector ret = getDERValue(nestedObj);
                return ret;
            } else {
                DEROctetString derOct = (DEROctetString) derTag.getObject();
                String val = new String(derOct.getOctets());
                Vector ret = new Vector();
                ret.add(val);
                return ret;
            }
        }
        return null;
    }

    // ===========

    public static void main(String[] args) {

    }
}
