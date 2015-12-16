package it.finsiel.siged.util.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * 
 * @author Paolo Spadafora
 */
public class EprotSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory factory;

    public EprotSSLSocketFactory() {
        try {
            // EprotTrustManager tManager = new
            // EprotTrustManager("C:\\Sun\\j2sdk1.4.2_02\\jre\\lib\\security\\cacerts");
            EprotTrustManager tManager = new EprotTrustManager();
            // tManager.init();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { tManager },
                    new java.security.SecureRandom());
            factory = (SSLSocketFactory) sslcontext.getSocketFactory();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SocketFactory getDefault() {
        return new EprotSSLSocketFactory();
    }

    public Socket createSocket(Socket socket, String s, int i, boolean flag)
            throws IOException {
        return factory.createSocket(socket, s, i, flag);
    }

    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1,
            int j) throws IOException {
        return factory.createSocket(inaddr, i, inaddr1, j);
    }

    public Socket createSocket(InetAddress inaddr, int i) throws IOException {
        return factory.createSocket(inaddr, i);
    }

    public Socket createSocket(String s, int i, InetAddress inaddr, int j)
            throws IOException {
        return factory.createSocket(s, i, inaddr, j);
    }

    public Socket createSocket(String s, int i) throws IOException {
        return factory.createSocket(s, i);
    }

    public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }
}
