package in.neuw.tls.config.properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
@ConfigurationProperties(prefix = "app.tls")
public class AppTlsProperties implements InitializingBean {

    private boolean enabled;

    // the intention is for child/consuming projects which are only requiring to have one single common tls setting
    private TlsProperties main;

    // the intention is for child/consuming projects which have requirement for more than one tls setting
    private Map<String, TlsProperties> settings;

    public TlsProperties getMain() {
        return main;
    }

    public void setMain(TlsProperties main) {
        this.main = main;
    }

    public Map<String, TlsProperties> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, TlsProperties> settings) {
        this.settings = settings;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // validate the properties and accordingly some settings and set some companion properties for bean creation
        if (getSettings().size() > 0) {
            if (getSettings().containsKey("main")) {
                throw new IllegalArgumentException("the property with name 'main' is supposed to be already default ssl config, change the name to something other than 'main'");
            }
        }
    }
}
