// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.ssl;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.util.log.Logger;

public class SslCertificates
{
    private static final Logger LOG;
    static final String CACHED_INFO_ATTR;
    
    public static X509Certificate[] getCertChain(final SSLSession sslSession) {
        try {
            final javax.security.cert.X509Certificate[] javaxCerts = sslSession.getPeerCertificateChain();
            if (javaxCerts == null || javaxCerts.length == 0) {
                return null;
            }
            final int length = javaxCerts.length;
            final X509Certificate[] javaCerts = new X509Certificate[length];
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            for (int i = 0; i < length; ++i) {
                final byte[] bytes = javaxCerts[i].getEncoded();
                final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                javaCerts[i] = (X509Certificate)cf.generateCertificate(stream);
            }
            return javaCerts;
        }
        catch (SSLPeerUnverifiedException pue) {
            return null;
        }
        catch (Exception e) {
            SslCertificates.LOG.warn("EXCEPTION ", e);
            return null;
        }
    }
    
    public static void customize(final SSLSession sslSession, final EndPoint endpoint, final Request request) throws IOException {
        request.setScheme("https");
        try {
            final String cipherSuite = sslSession.getCipherSuite();
            CachedInfo cachedInfo = (CachedInfo)sslSession.getValue(SslCertificates.CACHED_INFO_ATTR);
            Integer keySize;
            X509Certificate[] certs;
            String idStr;
            if (cachedInfo != null) {
                keySize = cachedInfo.getKeySize();
                certs = cachedInfo.getCerts();
                idStr = cachedInfo.getIdStr();
            }
            else {
                keySize = new Integer(ServletSSL.deduceKeyLength(cipherSuite));
                certs = getCertChain(sslSession);
                final byte[] bytes = sslSession.getId();
                idStr = TypeUtil.toHexString(bytes);
                cachedInfo = new CachedInfo(keySize, certs, idStr);
                sslSession.putValue(SslCertificates.CACHED_INFO_ATTR, cachedInfo);
            }
            if (certs != null) {
                request.setAttribute("javax.servlet.request.X509Certificate", certs);
            }
            request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
            request.setAttribute("javax.servlet.request.key_size", keySize);
            request.setAttribute("javax.servlet.request.ssl_session_id", idStr);
        }
        catch (Exception e) {
            SslCertificates.LOG.warn("EXCEPTION ", e);
        }
    }
    
    static {
        LOG = Log.getLogger(SslCertificates.class);
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
