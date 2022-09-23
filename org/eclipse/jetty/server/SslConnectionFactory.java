// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.Objects;
import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class SslConnectionFactory extends AbstractConnectionFactory
{
    private final SslContextFactory _sslContextFactory;
    private final String _nextProtocol;
    
    public SslConnectionFactory() {
        this(HttpVersion.HTTP_1_1.asString());
    }
    
    public SslConnectionFactory(@Name("next") final String nextProtocol) {
        this(null, nextProtocol);
    }
    
    public SslConnectionFactory(@Name("sslContextFactory") final SslContextFactory factory, @Name("next") final String nextProtocol) {
        super("SSL");
        this._sslContextFactory = ((factory == null) ? new SslContextFactory() : factory);
        this._nextProtocol = nextProtocol;
        this.addBean(this._sslContextFactory);
    }
    
    public SslContextFactory getSslContextFactory() {
        return this._sslContextFactory;
    }
    
    public String getNextProtocol() {
        return this._nextProtocol;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        final SSLEngine engine = this._sslContextFactory.newSSLEngine();
        engine.setUseClientMode(false);
        final SSLSession session = engine.getSession();
        if (session.getPacketBufferSize() > this.getInputBufferSize()) {
            this.setInputBufferSize(session.getPacketBufferSize());
        }
    }
    
    @Override
    public Connection newConnection(final Connector connector, final EndPoint endPoint) {
        final SSLEngine engine = this._sslContextFactory.newSSLEngine(endPoint.getRemoteAddress());
        engine.setUseClientMode(false);
        final SslConnection sslConnection = this.newSslConnection(connector, endPoint, engine);
        sslConnection.setRenegotiationAllowed(this._sslContextFactory.isRenegotiationAllowed());
        sslConnection.setRenegotiationLimit(this._sslContextFactory.getRenegotiationLimit());
        this.configure(sslConnection, connector, endPoint);
        final ConnectionFactory next = connector.getConnectionFactory(this._nextProtocol);
        final EndPoint decryptedEndPoint = sslConnection.getDecryptedEndPoint();
        final Connection connection = next.newConnection(connector, decryptedEndPoint);
        decryptedEndPoint.setConnection(connection);
        return sslConnection;
    }
    
    protected SslConnection newSslConnection(final Connector connector, final EndPoint endPoint, final SSLEngine engine) {
        return new SslConnection(connector.getByteBufferPool(), connector.getExecutor(), endPoint, engine);
    }
    
    @Override
    protected AbstractConnection configure(final AbstractConnection connection, final Connector connector, final EndPoint endPoint) {
        if (connection instanceof SslConnection) {
            final SslConnection sslConnection = (SslConnection)connection;
            if (connector instanceof ContainerLifeCycle) {
                final ContainerLifeCycle container = (ContainerLifeCycle)connector;
                final Collection<SslHandshakeListener> beans = container.getBeans(SslHandshakeListener.class);
                final SslConnection obj = sslConnection;
                Objects.requireNonNull(obj);
                beans.forEach(obj::addHandshakeListener);
            }
            final Collection<SslHandshakeListener> beans2 = this.getBeans(SslHandshakeListener.class);
            final SslConnection obj2 = sslConnection;
            Objects.requireNonNull(obj2);
            beans2.forEach(obj2::addHandshakeListener);
        }
        return super.configure(connection, connector, endPoint);
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s->%s}", this.getClass().getSimpleName(), this.hashCode(), this.getProtocol(), this._nextProtocol);
    }
}
