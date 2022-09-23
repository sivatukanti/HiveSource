// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.ssl;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.Buffer;
import javax.net.ssl.SSLException;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import org.eclipse.jetty.util.log.Log;
import javax.net.ssl.SSLContext;
import java.net.ServerSocket;
import org.eclipse.jetty.io.RuntimeIOException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.eclipse.jetty.io.bio.SocketEndPoint;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import java.io.IOException;
import java.net.Socket;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.bio.SocketConnector;

public class SslSocketConnector extends SocketConnector implements SslConnector
{
    private static final Logger LOG;
    private final SslContextFactory _sslContextFactory;
    private int _handshakeTimeout;
    
    public SslSocketConnector() {
        this(new SslContextFactory(SslContextFactory.DEFAULT_KEYSTORE_PATH));
        this.setSoLingerTime(30000);
    }
    
    public SslSocketConnector(final SslContextFactory sslContextFactory) {
        this._handshakeTimeout = 0;
        this._sslContextFactory = sslContextFactory;
    }
    
    public boolean isAllowRenegotiate() {
        return this._sslContextFactory.isAllowRenegotiate();
    }
    
    public void setAllowRenegotiate(final boolean allowRenegotiate) {
        this._sslContextFactory.setAllowRenegotiate(allowRenegotiate);
    }
    
    @Override
    public void accept(final int acceptorID) throws IOException, InterruptedException {
        final Socket socket = this._serverSocket.accept();
        this.configure(socket);
        final ConnectorEndPoint connection = new SslConnectorEndPoint(socket);
        connection.dispatch();
    }
    
    protected void configure(final Socket socket) throws IOException {
        super.configure(socket);
    }
    
    @Override
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        super.customize(endpoint, request);
        request.setScheme("https");
        final SocketEndPoint socket_end_point = (SocketEndPoint)endpoint;
        final SSLSocket sslSocket = (SSLSocket)socket_end_point.getTransport();
        final SSLSession sslSession = sslSocket.getSession();
        SslCertificates.customize(sslSession, endpoint, request);
    }
    
    @Deprecated
    public String[] getExcludeCipherSuites() {
        return this._sslContextFactory.getExcludeCipherSuites();
    }
    
    @Deprecated
    public String[] getIncludeCipherSuites() {
        return this._sslContextFactory.getIncludeCipherSuites();
    }
    
    @Deprecated
    public String getKeystore() {
        return this._sslContextFactory.getKeyStorePath();
    }
    
    @Deprecated
    public String getKeystoreType() {
        return this._sslContextFactory.getKeyStoreType();
    }
    
    @Deprecated
    public boolean getNeedClientAuth() {
        return this._sslContextFactory.getNeedClientAuth();
    }
    
    @Deprecated
    public String getProtocol() {
        return this._sslContextFactory.getProtocol();
    }
    
    @Deprecated
    public String getProvider() {
        return this._sslContextFactory.getProvider();
    }
    
    @Deprecated
    public String getSecureRandomAlgorithm() {
        return this._sslContextFactory.getSecureRandomAlgorithm();
    }
    
    @Deprecated
    public String getSslKeyManagerFactoryAlgorithm() {
        return this._sslContextFactory.getSslKeyManagerFactoryAlgorithm();
    }
    
    @Deprecated
    public String getSslTrustManagerFactoryAlgorithm() {
        return this._sslContextFactory.getTrustManagerFactoryAlgorithm();
    }
    
    @Deprecated
    public String getTruststore() {
        return this._sslContextFactory.getTrustStore();
    }
    
    public SslContextFactory getSslContextFactory() {
        return this._sslContextFactory;
    }
    
    @Deprecated
    public String getTruststoreType() {
        return this._sslContextFactory.getTrustStoreType();
    }
    
    @Deprecated
    public boolean getWantClientAuth() {
        return this._sslContextFactory.getWantClientAuth();
    }
    
    public boolean isConfidential(final Request request) {
        final int confidentialPort = this.getConfidentialPort();
        return confidentialPort == 0 || confidentialPort == request.getServerPort();
    }
    
    public boolean isIntegral(final Request request) {
        final int integralPort = this.getIntegralPort();
        return integralPort == 0 || integralPort == request.getServerPort();
    }
    
    @Override
    public void open() throws IOException {
        this._sslContextFactory.checkKeyStore();
        try {
            this._sslContextFactory.start();
        }
        catch (Exception e) {
            throw new RuntimeIOException(e);
        }
        super.open();
    }
    
    @Override
    protected void doStart() throws Exception {
        this._sslContextFactory.checkKeyStore();
        this._sslContextFactory.start();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._sslContextFactory.stop();
        super.doStop();
    }
    
    @Override
    protected ServerSocket newServerSocket(final String host, final int port, final int backlog) throws IOException {
        return this._sslContextFactory.newSslServerSocket(host, port, backlog);
    }
    
    @Deprecated
    public void setExcludeCipherSuites(final String[] cipherSuites) {
        this._sslContextFactory.setExcludeCipherSuites(cipherSuites);
    }
    
    @Deprecated
    public void setIncludeCipherSuites(final String[] cipherSuites) {
        this._sslContextFactory.setIncludeCipherSuites(cipherSuites);
    }
    
    @Deprecated
    public void setKeyPassword(final String password) {
        this._sslContextFactory.setKeyManagerPassword(password);
    }
    
    @Deprecated
    public void setKeystore(final String keystore) {
        this._sslContextFactory.setKeyStorePath(keystore);
    }
    
    @Deprecated
    public void setKeystoreType(final String keystoreType) {
        this._sslContextFactory.setKeyStoreType(keystoreType);
    }
    
    @Deprecated
    public void setNeedClientAuth(final boolean needClientAuth) {
        this._sslContextFactory.setNeedClientAuth(needClientAuth);
    }
    
    @Deprecated
    public void setPassword(final String password) {
        this._sslContextFactory.setKeyStorePassword(password);
    }
    
    @Deprecated
    public void setTrustPassword(final String password) {
        this._sslContextFactory.setTrustStorePassword(password);
    }
    
    @Deprecated
    public void setProtocol(final String protocol) {
        this._sslContextFactory.setProtocol(protocol);
    }
    
    @Deprecated
    public void setProvider(final String provider) {
        this._sslContextFactory.setProvider(provider);
    }
    
    @Deprecated
    public void setSecureRandomAlgorithm(final String algorithm) {
        this._sslContextFactory.setSecureRandomAlgorithm(algorithm);
    }
    
    @Deprecated
    public void setSslKeyManagerFactoryAlgorithm(final String algorithm) {
        this._sslContextFactory.setSslKeyManagerFactoryAlgorithm(algorithm);
    }
    
    @Deprecated
    public void setSslTrustManagerFactoryAlgorithm(final String algorithm) {
        this._sslContextFactory.setTrustManagerFactoryAlgorithm(algorithm);
    }
    
    @Deprecated
    public void setTruststore(final String truststore) {
        this._sslContextFactory.setTrustStore(truststore);
    }
    
    @Deprecated
    public void setTruststoreType(final String truststoreType) {
        this._sslContextFactory.setTrustStoreType(truststoreType);
    }
    
    @Deprecated
    public void setSslContext(final SSLContext sslContext) {
        this._sslContextFactory.setSslContext(sslContext);
    }
    
    @Deprecated
    public SSLContext getSslContext() {
        return this._sslContextFactory.getSslContext();
    }
    
    @Deprecated
    public void setWantClientAuth(final boolean wantClientAuth) {
        this._sslContextFactory.setWantClientAuth(wantClientAuth);
    }
    
    public void setHandshakeTimeout(final int msec) {
        this._handshakeTimeout = msec;
    }
    
    public int getHandshakeTimeout() {
        return this._handshakeTimeout;
    }
    
    @Deprecated
    public String getAlgorithm() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public void setAlgorithm(final String algorithm) {
        throw new UnsupportedOperationException();
    }
    
    static {
        LOG = Log.getLogger(SslSocketConnector.class);
    }
    
    public class SslConnectorEndPoint extends ConnectorEndPoint
    {
        public SslConnectorEndPoint(final Socket socket) throws IOException {
            super(socket);
        }
        
        @Override
        public void shutdownOutput() throws IOException {
            this.close();
        }
        
        @Override
        public void shutdownInput() throws IOException {
            this.close();
        }
        
        @Override
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
                            if (!SslSocketConnector.this._sslContextFactory.isAllowRenegotiate()) {
                                SslSocketConnector.LOG.warn("SSL renegotiate denied: " + ssl, new Object[0]);
                                try {
                                    ssl.close();
                                }
                                catch (IOException e) {
                                    SslSocketConnector.LOG.warn(e);
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
                SslSocketConnector.LOG.debug(e);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SslSocketConnector.LOG.ignore(e2);
                }
            }
            catch (IOException e3) {
                SslSocketConnector.LOG.debug(e3);
                try {
                    this.close();
                }
                catch (IOException e2) {
                    SslSocketConnector.LOG.ignore(e2);
                }
            }
        }
    }
}
