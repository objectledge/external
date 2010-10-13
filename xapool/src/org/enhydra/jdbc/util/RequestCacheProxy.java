package org.enhydra.jdbc.util;

public class RequestCacheProxy {

    public RequestCacheProxy() { }

    public void reset() {
	RequestCache.getInstance().reset();
    }

}