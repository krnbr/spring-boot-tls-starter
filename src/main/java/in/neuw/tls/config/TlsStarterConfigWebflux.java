package in.neuw.tls.config;

import in.neuw.tls.config.properties.AppProperties;
import in.neuw.tls.config.properties.AppTlsProperties;
import in.neuw.tls.service.TlsService;
import in.neuw.tls.service.TlsServiceReactiveImpl;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
@AutoConfiguration
//@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnClass(WebClient.class)
@ConditionalOnProperty(prefix = "app.tls", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({AppProperties.class, AppTlsProperties.class})
public class TlsStarterConfigWebflux {

    private Logger logger = LoggerFactory.getLogger(TlsStarterConfigWebflux.class);

    private final AppTlsProperties appTlsProperties;
    private final AppProperties appProperties;

    public TlsStarterConfigWebflux(final AppTlsProperties appTlsProperties,
                                   final AppProperties appProperties) {
        this.appTlsProperties = appTlsProperties;
        this.appProperties = appProperties;
    }

    @Bean(name = "reactiveSslContext")
    public SslContext defaultSSLContextWebflux() throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        logger.info("defaultSSLContext initialization in progress for reactive project {}", appProperties.getAppName());
        return TlsConfigCompanion.sslContextReactor(appTlsProperties.getMain().getKeyStore(),
                appTlsProperties.getMain().getKeyStorePassword(),
                appTlsProperties.getMain().getTrustStore(),
                appTlsProperties.getMain().getTrustStorePassword());
    }

    @Bean(name = "reactiveSslContextService")
    public TlsService<SslContext> tlsServiceReactive(SslContext defaultSSLContextWebflux) {
        logger.info("tlsServiceReactive initialization in progress for reactive project {}", appProperties.getAppName());
        Map<String, SslContext> sslContexts = new HashMap<>();
        sslContexts.put("main", defaultSSLContextWebflux);
        if (appTlsProperties.getSettings().size() > 0) {
            appTlsProperties.getSettings().entrySet().stream().forEach(e -> {
                try {
                    sslContexts.put(e.getKey(), TlsConfigCompanion.sslContextReactor(e.getValue().getKeyStore(),
                            e.getValue().getKeyStorePassword(),
                            e.getValue().getTrustStore(),
                            e.getValue().getTrustStorePassword()));
                } catch (UnrecoverableKeyException ex) {
                    throw new RuntimeException(ex);
                } catch (CertificateException ex) {
                    throw new RuntimeException(ex);
                } catch (KeyStoreException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return new TlsServiceReactiveImpl(sslContexts);
    }

}
