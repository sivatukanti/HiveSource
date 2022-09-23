// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;
import javax.net.ssl.SSLSocket;
import org.mortbay.io.bio.SocketEndPoint;
import org.mortbay.jetty.Request;
import org.mortbay.io.EndPoint;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import org.mortbay.resource.Resource;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLException;
import java.security.Security;
import org.mortbay.log.Log;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import org.mortbay.jetty.bio.SocketConnector;

public class SslSocketConnector extends SocketConnector
{
    static final String CACHED_INFO_ATTR;
    public static final String DEFAULT_KEYSTORE;
    public static final String KEYPASSWORD_PROPERTY = "jetty.ssl.keypassword";
    public static final String PASSWORD_PROPERTY = "jetty.ssl.password";
    private String[] _excludeCipherSuites;
    private String _keystore;
    private String _keystoreType;
    private boolean _needClientAuth;
    private transient Password _password;
    private transient Password _keyPassword;
    private transient Password _trustPassword;
    private String _protocol;
    private String _provider;
    private String _secureRandomAlgorithm;
    private String _sslKeyManagerFactoryAlgorithm;
    private String _sslTrustManagerFactoryAlgorithm;
    private String _truststore;
    private String _truststoreType;
    private boolean _wantClientAuth;
    private int _handshakeTimeout;
    private boolean _allowRenegotiate;
    
    private static X509Certificate[] getCertChain(final SSLSession sslSession) {
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
            Log.warn("EXCEPTION ", e);
            return null;
        }
    }
    
    public SslSocketConnector() {
        this._excludeCipherSuites = null;
        this._keystore = SslSocketConnector.DEFAULT_KEYSTORE;
        this._keystoreType = "JKS";
        this._needClientAuth = false;
        this._protocol = "TLS";
        this._sslKeyManagerFactoryAlgorithm = ((Security.getProperty("ssl.KeyManagerFactory.algorithm") == null) ? "SunX509" : Security.getProperty("ssl.KeyManagerFactory.algorithm"));
        this._sslTrustManagerFactoryAlgorithm = ((Security.getProperty("ssl.TrustManagerFactory.algorithm") == null) ? "SunX509" : Security.getProperty("ssl.TrustManagerFactory.algorithm"));
        this._truststoreType = "JKS";
        this._wantClientAuth = false;
        this._handshakeTimeout = 0;
        this._allowRenegotiate = false;
    }
    
    public boolean isAllowRenegotiate() {
        return this._allowRenegotiate;
    }
    
    public void setAllowRenegotiate(final boolean allowRenegotiate) {
        this._allowRenegotiate = allowRenegotiate;
    }
    
    public void accept(final int acceptorID) throws IOException, InterruptedException {
        try {
            final Socket socket = this._serverSocket.accept();
            this.configure(socket);
            final Connection connection = new SslConnection(socket);
            connection.dispatch();
        }
        catch (SSLException e) {
            Log.warn(e);
            try {
                this.stop();
            }
            catch (Exception e2) {
                Log.warn(e2);
                throw new IllegalStateException(e2.getMessage());
            }
        }
    }
    
    protected void configure(final Socket socket) throws IOException {
        super.configure(socket);
    }
    
    protected SSLServerSocketFactory createFactory() throws Exception {
        if (this._truststore == null) {
            this._truststore = this._keystore;
            this._truststoreType = this._keystoreType;
        }
        KeyManager[] keyManagers = null;
        InputStream keystoreInputStream = null;
        KeyStore keyStore = null;
        try {
            if (this._keystore != null) {
                keystoreInputStream = Resource.newResource(this._keystore).getInputStream();
            }
            keyStore = KeyStore.getInstance(this._keystoreType);
            keyStore.load(keystoreInputStream, (char[])((this._password == null) ? null : this._password.toString().toCharArray()));
        }
        finally {
            if (keystoreInputStream != null) {
                keystoreInputStream.close();
            }
        }
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this._sslKeyManagerFactoryAlgorithm);
        keyManagerFactory.init(keyStore, (char[])((this._keyPassword == null) ? null : this._keyPassword.toString().toCharArray()));
        keyManagers = keyManagerFactory.getKeyManagers();
        TrustManager[] trustManagers = null;
        InputStream truststoreInputStream = null;
        KeyStore trustStore = null;
        try {
            if (this._truststore != null) {
                truststoreInputStream = Resource.newResource(this._truststore).getInputStream();
            }
            trustStore = KeyStore.getInstance(this._truststoreType);
            trustStore.load(truststoreInputStream, (char[])((this._trustPassword == null) ? null : this._trustPassword.toString().toCharArray()));
        }
        finally {
            if (truststoreInputStream != null) {
                truststoreInputStream.close();
            }
        }
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this._sslTrustManagerFactoryAlgorithm);
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();
        final SecureRandom secureRandom = (this._secureRandomAlgorithm == null) ? null : SecureRandom.getInstance(this._secureRandomAlgorithm);
        final SSLContext context = (this._provider == null) ? SSLContext.getInstance(this._protocol) : SSLContext.getInstance(this._protocol, this._provider);
        context.init(keyManagers, trustManagers, secureRandom);
        return context.getServerSocketFactory();
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        super.customize(endpoint, request);
        request.setScheme("https");
        final SocketEndPoint socket_end_point = (SocketEndPoint)endpoint;
        final SSLSocket sslSocket = (SSLSocket)socket_end_point.getTransport();
        try {
            final SSLSession sslSession = sslSocket.getSession();
            final String cipherSuite = sslSession.getCipherSuite();
            CachedInfo cachedInfo = (CachedInfo)sslSession.getValue(SslSocketConnector.CACHED_INFO_ATTR);
            Integer keySize;
            X509Certificate[] certs;
            if (cachedInfo != null) {
                keySize = cachedInfo.getKeySize();
                certs = cachedInfo.getCerts();
            }
            else {
                keySize = new Integer(ServletSSL.deduceKeyLength(cipherSuite));
                certs = getCertChain(sslSession);
                cachedInfo = new CachedInfo(keySize, certs);
                sslSession.putValue(SslSocketConnector.CACHED_INFO_ATTR, cachedInfo);
            }
            if (certs != null) {
                request.setAttribute("javax.servlet.request.X509Certificate", certs);
            }
            else if (this._needClientAuth) {
                throw new IllegalStateException("no client auth");
            }
            request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
            request.setAttribute("javax.servlet.request.key_size", keySize);
        }
        catch (Exception e) {
            Log.warn("EXCEPTION ", e);
        }
    }
    
    public String[] getExcludeCipherSuites() {
        return this._excludeCipherSuites;
    }
    
    public String getKeystore() {
        return this._keystore;
    }
    
    public String getKeystoreType() {
        return this._keystoreType;
    }
    
    public boolean getNeedClientAuth() {
        return this._needClientAuth;
    }
    
    public String getProtocol() {
        return this._protocol;
    }
    
    public String getProvider() {
        return this._provider;
    }
    
    public String getSecureRandomAlgorithm() {
        return this._secureRandomAlgorithm;
    }
    
    public String getSslKeyManagerFactoryAlgorithm() {
        return this._sslKeyManagerFactoryAlgorithm;
    }
    
    public String getSslTrustManagerFactoryAlgorithm() {
        return this._sslTrustManagerFactoryAlgorithm;
    }
    
    public String getTruststore() {
        return this._truststore;
    }
    
    public String getTruststoreType() {
        return this._truststoreType;
    }
    
    public boolean getWantClientAuth() {
        return this._wantClientAuth;
    }
    
    public boolean isConfidential(final Request request) {
        final int confidentialPort = this.getConfidentialPort();
        return confidentialPort == 0 || confidentialPort == request.getServerPort();
    }
    
    public boolean isIntegral(final Request request) {
        final int integralPort = this.getIntegralPort();
        return integralPort == 0 || integralPort == request.getServerPort();
    }
    
    protected ServerSocket newServerSocket(final String host, final int port, final int backlog) throws IOException {
        SSLServerSocketFactory factory = null;
        SSLServerSocket socket = null;
        try {
            factory = this.createFactory();
            socket = (SSLServerSocket)((host == null) ? factory.createServerSocket(port, backlog) : factory.createServerSocket(port, backlog, InetAddress.getByName(host)));
            if (this._wantClientAuth) {
                socket.setWantClientAuth(this._wantClientAuth);
            }
            if (this._needClientAuth) {
                socket.setNeedClientAuth(this._needClientAuth);
            }
            if (this._excludeCipherSuites != null && this._excludeCipherSuites.length > 0) {
                final List excludedCSList = Arrays.asList(this._excludeCipherSuites);
                String[] enabledCipherSuites = socket.getEnabledCipherSuites();
                final List enabledCSList = new ArrayList(Arrays.asList(enabledCipherSuites));
                for (final String cipherName : excludedCSList) {
                    if (enabledCSList.contains(cipherName)) {
                        enabledCSList.remove(cipherName);
                    }
                }
                enabledCipherSuites = enabledCSList.toArray(new String[enabledCSList.size()]);
                socket.setEnabledCipherSuites(enabledCipherSuites);
            }
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e2) {
            Log.warn(e2.toString());
            Log.debug(e2);
            throw new IOException("!JsseListener: " + e2);
        }
        return socket;
    }
    
    public void setExcludeCipherSuites(final String[] cipherSuites) {
        this._excludeCipherSuites = cipherSuites;
    }
    
    public void setKeyPassword(final String password) {
        this._keyPassword = Password.getPassword("jetty.ssl.keypassword", password, null);
    }
    
    public void setKeystore(final String keystore) {
        this._keystore = keystore;
    }
    
    public void setKeystoreType(final String keystoreType) {
        this._keystoreType = keystoreType;
    }
    
    public void setNeedClientAuth(final boolean needClientAuth) {
        this._needClientAuth = needClientAuth;
    }
    
    public void setPassword(final String password) {
        this._password = Password.getPassword("jetty.ssl.password", password, null);
    }
    
    public void setTrustPassword(final String password) {
        this._trustPassword = Password.getPassword("jetty.ssl.password", password, null);
    }
    
    public void setProtocol(final String protocol) {
        this._protocol = protocol;
    }
    
    public void setProvider(final String _provider) {
        this._provider = _provider;
    }
    
    public void setSecureRandomAlgorithm(final String algorithm) {
        this._secureRandomAlgorithm = algorithm;
    }
    
    public void setSslKeyManagerFactoryAlgorithm(final String algorithm) {
        this._sslKeyManagerFactoryAlgorithm = algorithm;
    }
    
    public void setSslTrustManagerFactoryAlgorithm(final String algorithm) {
        this._sslTrustManagerFactoryAlgorithm = algorithm;
    }
    
    public void setTruststore(final String truststore) {
        this._truststore = truststore;
    }
    
    public void setTruststoreType(final String truststoreType) {
        this._truststoreType = truststoreType;
    }
    
    public void setWantClientAuth(final boolean wantClientAuth) {
        this._wantClientAuth = wantClientAuth;
    }
    
    public void setHandshakeTimeout(final int msec) {
        this._handshakeTimeout = msec;
    }
    
    public int getHandshakeTimeout() {
        return this._handshakeTimeout;
    }
    
    static {
        CACHED_INFO_ATTR = CachedInfo.class.getName();
        DEFAULT_KEYSTORE = System.getProperty("user.home") + File.separator + ".keystore";
    }
    
    private class CachedInfo
    {
        private X509Certificate[] _certs;
        private Integer _keySize;
        
        CachedInfo(final Integer keySize, final X509Certificate[] certs) {
            this._keySize = keySize;
            this._certs = certs;
        }
        
        X509Certificate[] getCerts() {
            return this._certs;
        }
        
        Integer getKeySize() {
            return this._keySize;
        }
    }
    
    public class SslConnection extends Connection
    {
        public SslConnection(final Socket socket) throws IOException {
            super(socket);
        }
        
        public void shutdownOutput() throws IOException {
            this.close();
        }
        
        public void run() {
            try {
                final int handshakeTimeout = SslSocketConnector.this.getHandshakeTimeout();
                final int oldTimeout = this._socket.getSoTimeout();
                if (handshakeTimeout > 0) {
                    this._socket.setSoTimeout(handshakeTimeout);
                }
                final SSLSocket ssl = (SSLSocket)this._socket;
                ssl.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                    boolean handshook = false;
                    
                    public void handshakeCompleted(final HandshakeCompletedEvent event) {
                        if (this.handshook) {
                            if (!SslSocketConnector.this._allowRenegotiate) {
                                Log.warn("SSL renegotiate denied: " + ssl);
                                try {
                                    ssl.close();
                                }
                                catch (IOException e) {
                                    Log.warn(e);
                                }
                            }
                        }
                        else {
                            this.handshook = true;
                        }
                    }
                });
                ssl.startHandshake();
                if (handshakeTimeout > 0) {
                    this._socket.setSoTimeout(oldTimeout);
                }
                super.run();
            }
            catch (SSLException e) {
                Log.warn(e);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
            catch (IOException e3) {
                Log.debug(e3);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
        }
    }
}
