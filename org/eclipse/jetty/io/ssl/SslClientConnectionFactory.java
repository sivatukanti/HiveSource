// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.ssl;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.Objects;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.io.IOException;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.Connection;
import java.util.Map;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.io.ClientConnectionFactory;

public class SslClientConnectionFactory implements ClientConnectionFactory
{
    public static final String SSL_CONTEXT_FACTORY_CONTEXT_KEY = "ssl.context.factory";
    public static final String SSL_PEER_HOST_CONTEXT_KEY = "ssl.peer.host";
    public static final String SSL_PEER_PORT_CONTEXT_KEY = "ssl.peer.port";
    public static final String SSL_ENGINE_CONTEXT_KEY = "ssl.engine";
    private final SslContextFactory sslContextFactory;
    private final ByteBufferPool byteBufferPool;
    private final Executor executor;
    private final ClientConnectionFactory connectionFactory;
    private boolean allowMissingCloseMessage;
    
    public SslClientConnectionFactory(final SslContextFactory sslContextFactory, final ByteBufferPool byteBufferPool, final Executor executor, final ClientConnectionFactory connectionFactory) {
        this.allowMissingCloseMessage = true;
        this.sslContextFactory = sslContextFactory;
        this.byteBufferPool = byteBufferPool;
        this.executor = executor;
        this.connectionFactory = connectionFactory;
    }
    
    public boolean isAllowMissingCloseMessage() {
        return this.allowMissingCloseMessage;
    }
    
    public void setAllowMissingCloseMessage(final boolean allowMissingCloseMessage) {
        this.allowMissingCloseMessage = allowMissingCloseMessage;
    }
    
    @Override
    public Connection newConnection(final EndPoint endPoint, final Map<String, Object> context) throws IOException {
        final String host = context.get("ssl.peer.host");
        final int port = context.get("ssl.peer.port");
        final SSLEngine engine = this.sslContextFactory.newSSLEngine(host, port);
        engine.setUseClientMode(true);
        context.put("ssl.engine", engine);
        final SslConnection sslConnection = this.newSslConnection(this.byteBufferPool, this.executor, endPoint, engine);
        endPoint.setConnection(sslConnection);
        final EndPoint appEndPoint = sslConnection.getDecryptedEndPoint();
        appEndPoint.setConnection(this.connectionFactory.newConnection(appEndPoint, context));
        this.customize(sslConnection, context);
        return sslConnection;
    }
    
    protected SslConnection newSslConnection(final ByteBufferPool byteBufferPool, final Executor executor, final EndPoint endPoint, final SSLEngine engine) {
        return new SslConnection(byteBufferPool, executor, endPoint, engine);
    }
    
    @Override
    public Connection customize(final Connection connection, final Map<String, Object> context) {
        if (connection instanceof SslConnection) {
            final SslConnection sslConnection = (SslConnection)connection;
            sslConnection.setRenegotiationAllowed(this.sslContextFactory.isRenegotiationAllowed());
            sslConnection.setRenegotiationLimit(this.sslContextFactory.getRenegotiationLimit());
            sslConnection.setAllowMissingCloseMessage(this.isAllowMissingCloseMessage());
            final ContainerLifeCycle connector = context.get("client.connector");
            final Collection<SslHandshakeListener> beans = connector.getBeans(SslHandshakeListener.class);
            final SslConnection obj = sslConnection;
            Objects.requireNonNull(obj);
            beans.forEach(obj::addHandshakeListener);
        }
        return super.customize(connection, context);
    }
}
