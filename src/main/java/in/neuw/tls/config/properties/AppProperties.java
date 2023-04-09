package in.neuw.tls.config.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.StringUtils;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
@ConfigurationProperties(prefix = "neuw")
public class AppProperties implements InitializingBean {

    // need to run spring-boot:build-info on child(consuming) project
    @Autowired(required = false)
    BuildProperties buildProperties;

    private Logger logger = LoggerFactory.getLogger(AppProperties.class);

    private String appName;

    public String getAppName() {
        return StringUtils.hasLength(appName) ? appName.trim() : buildProperties != null ? buildProperties.getArtifact() : "APP-NAME-NOT-KNOWN";
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO validate app name or just log it
        logger.info("app properties initialized for {} app", getAppName());
    }
}
