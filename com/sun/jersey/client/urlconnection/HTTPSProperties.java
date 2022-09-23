// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.urlconnection;

import javax.net.ssl.HttpsURLConnection;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;

public class HTTPSProperties
{
    public static final String PROPERTY_HTTPS_PROPERTIES = "com.sun.jersey.client.impl.urlconnection.httpsProperties";
    private HostnameVerifier hostnameVerifier;
    private SSLContext sslContext;
    
    public HTTPSProperties() throws NoSuchAlgorithmException {
        this(null, SSLContext.getInstance("SSL"));
    }
    
    public HTTPSProperties(final HostnameVerifier hv) throws NoSuchAlgorithmException {
        this(hv, SSLContext.getInstance("SSL"));
    }
    
    public HTTPSProperties(final HostnameVerifier hv, final SSLContext c) {
        this.hostnameVerifier = null;
        this.sslContext = null;
        if (c == null) {
            throw new IllegalArgumentException("SSLContext must not be null");
        }
        this.hostnameVerifier = hv;
        this.sslContext = c;
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public SSLContext getSSLContext() {
        return this.sslContext;
    }
    
    public void setConnection(final HttpsURLConnection connection) {
        if (this.hostnameVerifier != null) {
            connection.setHostnameVerifier(this.hostnameVerifier);
        }
        connection.setSSLSocketFactory(this.sslContext.getSocketFactory());
    }
}
