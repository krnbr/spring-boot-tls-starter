package in.neuw.tls.service;

import javax.net.ssl.SSLContext;
import java.util.Map;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
public class TlsServiceForServletsImpl extends TlsService<SSLContext> {

    public TlsServiceForServletsImpl(Map<String, SSLContext> sslContexts) {
        this.sslContexts = sslContexts;
    }

    @Override
    public Map<String, SSLContext> getSslContexts() {
        return this.sslContexts;
    }

}
