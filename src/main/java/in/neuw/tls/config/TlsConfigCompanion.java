package in.neuw.tls.config;

import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
public class TlsConfigCompanion {

    private static final Logger logger = LoggerFactory.getLogger(TlsConfigCompanion.class);

    public static SslContext sslContextReactor(final String keystoreContent,
                                               final String keyStorePassword,
                                               final String truststoreContent,
                                               final String trustStorePassword) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        SslContext sslContext = SslContextBuilder.forClient()
                .clientAuth(ClientAuth.REQUIRE)
                .keyManager(getKeyManagerFactory(keystoreContent, keyStorePassword))
                // the following line is not recommended and commented out
                // .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .trustManager(getTrustManagerFactory(truststoreContent, trustStorePassword))
                .build();
        return sslContext;
    }

    public static KeyManagerFactory getKeyManagerFactory(final String keystoreContent,
                                                         final String keyStorePassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = keyStore(keystoreContent, keyStorePassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf;
    }

    public static TrustManagerFactory getTrustManagerFactory(final String truststoreContent,
                                                             final String trustStorePassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore trustStore = keyStore(truststoreContent, trustStorePassword);
        TrustManagerFactory tmf = FingerprintTrustManagerFactory.getInstance(FingerprintTrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf;
    }

    public static KeyStore keyStore(final String storeContent, final String storePassword) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        var storeType = type(storeContent);
        logger.info("store type is {}", storeType);
        var keyStore = KeyStore.getInstance(storeType);
        var inputStream = new ByteArrayInputStream(getBytesFromBase64String(storeContent));
        keyStore.load(inputStream, storePassword.toCharArray());
        return keyStore;
    }

    public static String type(final String storeContent) {
        if (storeContent.startsWith("/u3+7QAAAA")) {
            return "JKS";
        } else if (storeContent.startsWith("MII")) {
            return "PKCS12";
        } else {
            throw new RuntimeException("store type not recognized");
        }
    }

    /*public static String getMimeType(final String storeContent) throws IOException {
        var decoder = Base64.getMimeDecoder();
        var inputStream = new BufferedInputStream(new ByteArrayInputStream(decoder.decode(storeContent)));
        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        return mimeType;
    }*/

    public static byte[] getBytesFromBase64String(final String base64FileString) {
        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(base64FileString);
            logger.info("The string is a valid Base64-encoded string.");
        } catch (IllegalArgumentException e) {
            logger.info("The string is not a valid Base64-encoded string :--> " + e.getMessage());
            throw new RuntimeException("unable to transform input base64 String");
        }
        return fileBytes;
    }

    public static void main(String[] args) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        var storeContent = "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";
        System.out.println(keyStore(storeContent, "changeit"));
        storeContent = "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";
        System.out.println(keyStore(storeContent, "changeit"));
    }

}
