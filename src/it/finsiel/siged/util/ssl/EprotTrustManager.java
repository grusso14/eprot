package it.finsiel.siged.util.ssl;

import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 
 * @author Paolo Spadafora
 */
public class EprotTrustManager implements X509TrustManager {

    // private KeyStore ks;

    // private String pathRoCACERTS;

    public EprotTrustManager() throws KeyStoreException {
    }

    /**
     * 
     * 
     * public EprotTrustManager(String pathRoCACERTS) throws KeyStoreException {
     * ks = KeyStore.getInstance("JKS"); this.pathRoCACERTS = pathRoCACERTS; }
     */

    public void init() throws Exception {
        /*
         * ks.load(new FileInputStream(pathRoCACERTS),
         * "changeit".toCharArray()); Enumeration en = ks.aliases(); while
         * (en.hasMoreElements()) { System.out.println("Cert:" + (String)
         * en.nextElement()); }
         */
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        ;
    }

    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        ;
    }

}
