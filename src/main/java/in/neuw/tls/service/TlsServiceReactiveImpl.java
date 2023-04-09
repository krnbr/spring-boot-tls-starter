package in.neuw.tls.service;

import io.netty.handler.ssl.SslContext;

import java.util.Map;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
public class TlsServiceReactiveImpl extends TlsService<SslContext> {

    public TlsServiceReactiveImpl(Map<String, SslContext> sslContexts) {
        this.sslContexts = sslContexts;
    }

    @Override
    public Map<String, SslContext> getSslContexts() {
        return this.sslContexts;
    }
}
