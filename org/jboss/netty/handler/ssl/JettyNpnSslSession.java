// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import java.security.Principal;
import javax.security.cert.X509Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;

final class JettyNpnSslSession implements SSLSession
{
    private final SSLEngine engine;
    private volatile String applicationProtocol;
    
    JettyNpnSslSession(final SSLEngine engine) {
        this.engine = engine;
    }
    
    void setApplicationProtocol(String applicationProtocol) {
        if (applicationProtocol != null) {
            applicationProtocol = applicationProtocol.replace(':', '_');
        }
        this.applicationProtocol = applicationProtocol;
    }
    
    public String getProtocol() {
        final String protocol = this.unwrap().getProtocol();
        final String applicationProtocol = this.applicationProtocol;
        if (applicationProtocol != null) {
            final StringBuilder buf = new StringBuilder(32);
            if (protocol != null) {
                buf.append(protocol.replace(':', '_'));
                buf.append(':');
            }
            else {
                buf.append("null:");
            }
            buf.append(applicationProtocol);
            return buf.toString();
        }
        if (protocol != null) {
            return protocol.replace(':', '_');
        }
        return null;
    }
    
    private SSLSession unwrap() {
        return this.engine.getSession();
    }
    
    public byte[] getId() {
        return this.unwrap().getId();
    }
    
    public SSLSessionContext getSessionContext() {
        return this.unwrap().getSessionContext();
    }
    
    public long getCreationTime() {
        return this.unwrap().getCreationTime();
    }
    
    public long getLastAccessedTime() {
        return this.unwrap().getLastAccessedTime();
    }
    
    public void invalidate() {
        this.unwrap().invalidate();
    }
    
    public boolean isValid() {
        return this.unwrap().isValid();
    }
    
    public void putValue(final String s, final Object o) {
        this.unwrap().putValue(s, o);
    }
    
    public Object getValue(final String s) {
        return this.unwrap().getValue(s);
    }
    
    public void removeValue(final String s) {
        this.unwrap().removeValue(s);
    }
    
    public String[] getValueNames() {
        return this.unwrap().getValueNames();
    }
    
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return this.unwrap().getPeerCertificates();
    }
    
    public Certificate[] getLocalCertificates() {
        return this.unwrap().getLocalCertificates();
    }
    
    public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return this.unwrap().getPeerCertificateChain();
    }
    
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.unwrap().getPeerPrincipal();
    }
    
    public Principal getLocalPrincipal() {
        return this.unwrap().getLocalPrincipal();
    }
    
    public String getCipherSuite() {
        return this.unwrap().getCipherSuite();
    }
    
    public String getPeerHost() {
        return this.unwrap().getPeerHost();
    }
    
    public int getPeerPort() {
        return this.unwrap().getPeerPort();
    }
    
    public int getPacketBufferSize() {
        return this.unwrap().getPacketBufferSize();
    }
    
    public int getApplicationBufferSize() {
        return this.unwrap().getApplicationBufferSize();
    }
}
