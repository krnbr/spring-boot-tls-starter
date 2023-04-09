package in.neuw.tls.config;

import in.neuw.tls.config.properties.AppProperties;
import in.neuw.tls.config.properties.AppTlsProperties;
import in.neuw.tls.service.TlsService;
import in.neuw.tls.service.TlsServiceForServletsImpl;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = SERVLET)
@ConditionalOnProperty(prefix = "app.tls", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AppProperties.class, AppTlsProperties.class})
public class TlsStarterConfigWeb {

    private Logger logger = LoggerFactory.getLogger(TlsStarterConfigWeb.class);

    private final AppTlsProperties appTlsProperties;
    private final AppProperties appProperties;

    public TlsStarterConfigWeb(final AppTlsProperties appTlsProperties,
                               final AppProperties appProperties) {
        this.appTlsProperties = appTlsProperties;
        this.appProperties = appProperties;
    }

    @Bean(name = "sslContext")
    public SSLContext defaultSSLContextServlet() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException {
        logger.info("defaultSSLContext initialization in progress for servlet based project {}", appProperties.getAppName());
        return SSLContexts.custom()
                .loadTrustMaterial(
                        TlsConfigCompanion.keyStore(appTlsProperties.getMain().getTrustStore(),
                                                    appTlsProperties.getMain().getTrustStorePassword()),
                                         null)
                .loadKeyMaterial(
                        TlsConfigCompanion.keyStore(appTlsProperties.getMain().getKeyStore(),
                                                    appTlsProperties.getMain().getKeyStorePassword()),
                                                    appTlsProperties.getMain().getKeyStorePassword().toCharArray())
                .build();
    }

    @Bean(name = "sslContextService")
    public TlsService<SSLContext> tlsServiceServlet(final SSLContext defaultSSLContext) {
        logger.info("tlsServiceServlet initialization in progress for servlet based project {}", appProperties.getAppName());
        Map<String, SSLContext> sslContexts = new HashMap<>();
        sslContexts.put("main", defaultSSLContext);
        // loop in for other ssl contexts creation
        if (appTlsProperties.getSettings().size() > 0) {
            appTlsProperties.getSettings().entrySet().stream().forEach(e -> {
                try {
                    sslContexts.put(e.getKey(), SSLContexts.custom()
                            .loadTrustMaterial(
                                    TlsConfigCompanion.keyStore(e.getValue().getTrustStore(),
                                            e.getValue().getTrustStorePassword()),
                                    null)
                            .loadKeyMaterial(
                                    TlsConfigCompanion.keyStore(e.getValue().getKeyStore(),
                                            e.getValue().getKeyStorePassword()),
                                    e.getValue().getKeyStorePassword().toCharArray())
                            .build());
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                } catch (KeyManagementException ex) {
                    throw new RuntimeException(ex);
                } catch (KeyStoreException ex) {
                    throw new RuntimeException(ex);
                } catch (UnrecoverableKeyException ex) {
                    throw new RuntimeException(ex);
                } catch (CertificateException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return new TlsServiceForServletsImpl(sslContexts);
    }

}
