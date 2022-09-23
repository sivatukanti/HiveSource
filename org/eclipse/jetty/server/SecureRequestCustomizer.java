// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.util.ssl.X509;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpHeader;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Logger;

public class SecureRequestCustomizer implements HttpConfiguration.Customizer
{
    private static final Logger LOG;
    public static final String CACHED_INFO_ATTR;
    private String sslSessionAttribute;
    private boolean _sniHostCheck;
    private long _stsMaxAge;
    private boolean _stsIncludeSubDomains;
    private HttpField _stsField;
    
    public SecureRequestCustomizer() {
        this(true);
    }
    
    public SecureRequestCustomizer(@Name("sniHostCheck") final boolean sniHostCheck) {
        this(sniHostCheck, -1L, false);
    }
    
    public SecureRequestCustomizer(@Name("sniHostCheck") final boolean sniHostCheck, @Name("stsMaxAgeSeconds") final long stsMaxAgeSeconds, @Name("stsIncludeSubdomains") final boolean stsIncludeSubdomains) {
        this.sslSessionAttribute = "org.eclipse.jetty.servlet.request.ssl_session";
        this._stsMaxAge = -1L;
        this._sniHostCheck = sniHostCheck;
        this._stsMaxAge = stsMaxAgeSeconds;
        this._stsIncludeSubDomains = stsIncludeSubdomains;
        this.formatSTS();
    }
    
    public boolean isSniHostCheck() {
        return this._sniHostCheck;
    }
    
    public void setSniHostCheck(final boolean sniHostCheck) {
        this._sniHostCheck = sniHostCheck;
    }
    
    public long getStsMaxAge() {
        return this._stsMaxAge;
    }
    
    public void setStsMaxAge(final long stsMaxAgeSeconds) {
        this._stsMaxAge = stsMaxAgeSeconds;
        this.formatSTS();
    }
    
    public void setStsMaxAge(final long period, final TimeUnit units) {
        this._stsMaxAge = units.toSeconds(period);
        this.formatSTS();
    }
    
    public boolean isStsIncludeSubDomains() {
        return this._stsIncludeSubDomains;
    }
    
    public void setStsIncludeSubDomains(final boolean stsIncludeSubDomains) {
        this._stsIncludeSubDomains = stsIncludeSubDomains;
        this.formatSTS();
    }
    
    private void formatSTS() {
        if (this._stsMaxAge < 0L) {
            this._stsField = null;
        }
        else {
            this._stsField = new PreEncodedHttpField(HttpHeader.STRICT_TRANSPORT_SECURITY, String.format("max-age=%d%s", this._stsMaxAge, this._stsIncludeSubDomains ? "; includeSubDomains" : ""));
        }
    }
    
    @Override
    public void customize(final Connector connector, final HttpConfiguration channelConfig, final Request request) {
        if (request.getHttpChannel().getEndPoint() instanceof SslConnection.DecryptedEndPoint) {
            if (request.getHttpURI().getScheme() == null) {
                request.setScheme(HttpScheme.HTTPS.asString());
            }
            final SslConnection.DecryptedEndPoint ssl_endp = (SslConnection.DecryptedEndPoint)request.getHttpChannel().getEndPoint();
            final SslConnection sslConnection = ssl_endp.getSslConnection();
            final SSLEngine sslEngine = sslConnection.getSSLEngine();
            this.customize(sslEngine, request);
        }
        if (HttpScheme.HTTPS.is(request.getScheme())) {
            this.customizeSecure(request);
        }
    }
    
    protected void customizeSecure(final Request request) {
        request.setSecure(true);
        if (this._stsField != null) {
            request.getResponse().getHttpFields().add(this._stsField);
        }
    }
    
    protected void customize(final SSLEngine sslEngine, final Request request) {
        request.setScheme(HttpScheme.HTTPS.asString());
        final SSLSession sslSession = sslEngine.getSession();
        if (this._sniHostCheck) {
            final String name = request.getServerName();
            final X509 x509 = (X509)sslSession.getValue("org.eclipse.jetty.util.ssl.snix509");
            if (x509 != null && !x509.matches(name)) {
                SecureRequestCustomizer.LOG.warn("Host {} does not match SNI {}", name, x509);
                throw new BadMessageException(400, "Host does not match SNI");
            }
            if (SecureRequestCustomizer.LOG.isDebugEnabled()) {
                SecureRequestCustomizer.LOG.debug("Host {} matched SNI {}", name, x509);
            }
        }
        try {
            final String cipherSuite = sslSession.getCipherSuite();
            CachedInfo cachedInfo = (CachedInfo)sslSession.getValue(SecureRequestCustomizer.CACHED_INFO_ATTR);
            Integer keySize;
            X509Certificate[] certs;
            String idStr;
            if (cachedInfo != null) {
                keySize = cachedInfo.getKeySize();
                certs = cachedInfo.getCerts();
                idStr = cachedInfo.getIdStr();
            }
            else {
                keySize = SslContextFactory.deduceKeyLength(cipherSuite);
                certs = SslContextFactory.getCertChain(sslSession);
                final byte[] bytes = sslSession.getId();
                idStr = TypeUtil.toHexString(bytes);
                cachedInfo = new CachedInfo(keySize, certs, idStr);
                sslSession.putValue(SecureRequestCustomizer.CACHED_INFO_ATTR, cachedInfo);
            }
            if (certs != null) {
                request.setAttribute("javax.servlet.request.X509Certificate", certs);
            }
            request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
            request.setAttribute("javax.servlet.request.key_size", keySize);
            request.setAttribute("javax.servlet.request.ssl_session_id", idStr);
            final String sessionAttribute = this.getSslSessionAttribute();
            if (sessionAttribute != null && !sessionAttribute.isEmpty()) {
                request.setAttribute(sessionAttribute, sslSession);
            }
        }
        catch (Exception e) {
            SecureRequestCustomizer.LOG.warn("EXCEPTION ", e);
        }
    }
    
    public void setSslSessionAttribute(final String attribute) {
        this.sslSessionAttribute = attribute;
    }
    
    public String getSslSessionAttribute() {
        return this.sslSessionAttribute;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }
    
    static {
        LOG = Log.getLogger(SecureRequestCustomizer.class);
        CACHED_INFO_ATTR = CachedInfo.class.getName();
    }
    
    private static class CachedInfo
    {
        private final X509Certificate[] _certs;
        private final Integer _keySize;
        private final String _idStr;
        
        CachedInfo(final Integer keySize, final X509Certificate[] certs, final String idStr) {
            this._keySize = keySize;
            this._certs = certs;
            this._idStr = idStr;
        }
        
        X509Certificate[] getCerts() {
            return this._certs;
        }
        
        Integer getKeySize() {
            return this._keySize;
        }
        
        String getIdStr() {
            return this._idStr;
        }
    }
}
