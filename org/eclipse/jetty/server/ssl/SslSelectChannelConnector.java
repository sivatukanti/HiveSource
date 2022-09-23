// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.ssl;

import org.eclipse.jetty.io.BuffersFactory;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.AsyncEndPoint;
import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.nio.SslConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

public class SslSelectChannelConnector extends SelectChannelConnector implements SslConnector
{
    private final SslContextFactory _sslContextFactory;
    private Buffers _sslBuffers;
    
    public SslSelectChannelConnector() {
        this(new SslContextFactory(SslContextFactory.DEFAULT_KEYSTORE_PATH));
        this.setSoLingerTime(30000);
    }
    
    public SslSelectChannelConnector(final SslContextFactory sslContextFactory) {
        this.addBean(this._sslContextFactory = sslContextFactory);
        this.setUseDirectBuffers(false);
        this.setSoLingerTime(30000);
    }
    
    @Override
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        request.setScheme("https");
        super.customize(endpoint, request);
        final SslConnection.SslEndPoint sslEndpoint = (SslConnection.SslEndPoint)endpoint;
        final SSLEngine sslEngine = sslEndpoint.getSslEngine();
        final SSLSession sslSession = sslEngine.getSession();
        SslCertificates.customize(sslSession, endpoint, request);
    }
    
    @Deprecated
    public boolean isAllowRenegotiate() {
        return this._sslContextFactory.isAllowRenegotiate();
    }
    
    @Deprecated
    public void setAllowRenegotiate(final boolean allowRenegotiate) {
        this._sslContextFactory.setAllowRenegotiate(allowRenegotiate);
    }
    
    @Deprecated
    public String[] getExcludeCipherSuites() {
        return this._sslContextFactory.getExcludeCipherSuites();
    }
    
    @Deprecated
    public void setExcludeCipherSuites(final String[] cipherSuites) {
        this._sslContextFactory.setExcludeCipherSuites(cipherSuites);
    }
    
    @Deprecated
    public String[] getIncludeCipherSuites() {
        return this._sslContextFactory.getIncludeCipherSuites();
    }
    
    @Deprecated
    public void setIncludeCipherSuites(final String[] cipherSuites) {
        this._sslContextFactory.setIncludeCipherSuites(cipherSuites);
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
    public void setKeyPassword(final String password) {
        this._sslContextFactory.setKeyManagerPassword(password);
    }
    
    @Deprecated
    public String getAlgorithm() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public void setAlgorithm(final String algorithm) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public String getProtocol() {
        return this._sslContextFactory.getProtocol();
    }
    
    @Deprecated
    public void setProtocol(final String protocol) {
        this._sslContextFactory.setProtocol(protocol);
    }
    
    @Deprecated
    public void setKeystore(final String keystore) {
        this._sslContextFactory.setKeyStorePath(keystore);
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
    public boolean getWantClientAuth() {
        return this._sslContextFactory.getWantClientAuth();
    }
    
    @Deprecated
    public void setNeedClientAuth(final boolean needClientAuth) {
        this._sslContextFactory.setNeedClientAuth(needClientAuth);
    }
    
    @Deprecated
    public void setWantClientAuth(final boolean wantClientAuth) {
        this._sslContextFactory.setWantClientAuth(wantClientAuth);
    }
    
    @Deprecated
    public void setKeystoreType(final String keystoreType) {
        this._sslContextFactory.setKeyStoreType(keystoreType);
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
    
    @Deprecated
    public String getTruststoreType() {
        return this._sslContextFactory.getTrustStoreType();
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
    
    public SslContextFactory getSslContextFactory() {
        return this._sslContextFactory;
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
    protected AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint) {
        try {
            final SSLEngine engine = this.createSSLEngine(channel);
            final SslConnection connection = this.newSslConnection(endpoint, engine);
            final AsyncConnection delegate = this.newPlainConnection(channel, connection.getSslEndPoint());
            connection.getSslEndPoint().setConnection(delegate);
            connection.setAllowRenegotiate(this._sslContextFactory.isAllowRenegotiate());
            return connection;
        }
        catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
    
    protected AsyncConnection newPlainConnection(final SocketChannel channel, final AsyncEndPoint endPoint) {
        return super.newConnection(channel, endPoint);
    }
    
    protected SslConnection newSslConnection(final AsyncEndPoint endpoint, final SSLEngine engine) {
        return new SslConnection(engine, endpoint);
    }
    
    protected SSLEngine createSSLEngine(final SocketChannel channel) throws IOException {
        SSLEngine engine;
        if (channel != null) {
            final String peerHost = channel.socket().getInetAddress().getHostAddress();
            final int peerPort = channel.socket().getPort();
            engine = this._sslContextFactory.newSslEngine(peerHost, peerPort);
        }
        else {
            engine = this._sslContextFactory.newSslEngine();
        }
        engine.setUseClientMode(false);
        return engine;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._sslContextFactory.checkKeyStore();
        this._sslContextFactory.start();
        final SSLEngine sslEngine = this._sslContextFactory.newSslEngine();
        sslEngine.setUseClientMode(false);
        final SSLSession sslSession = sslEngine.getSession();
        this._sslBuffers = BuffersFactory.newBuffers(this.getUseDirectBuffers() ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT, sslSession.getApplicationBufferSize(), this.getUseDirectBuffers() ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT, sslSession.getApplicationBufferSize(), this.getUseDirectBuffers() ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT, this.getMaxBuffers());
        if (this.getRequestHeaderSize() < sslSession.getApplicationBufferSize()) {
            this.setRequestHeaderSize(sslSession.getApplicationBufferSize());
        }
        if (this.getRequestBufferSize() < sslSession.getApplicationBufferSize()) {
            this.setRequestBufferSize(sslSession.getApplicationBufferSize());
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._sslBuffers = null;
        super.doStop();
    }
    
    public Buffers getSslBuffers() {
        return this._sslBuffers;
    }
}
