package in.neuw.tls.service;

import java.util.Map;

/**
 * @author Karanbir Singh
 * @user krnbr
 */
public abstract class TlsService<T> {

    Map<String, T> sslContexts;

    public abstract Map<String, T> getSslContexts();
}
