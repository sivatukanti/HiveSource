// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.io.Connection;
import javax.net.ssl.SSLEngineResult;
import org.eclipse.jetty.io.EndPoint;
import java.util.List;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public abstract class NegotiatingServerConnection extends AbstractConnection
{
    private static final Logger LOG;
    private final Connector connector;
    private final SSLEngine engine;
    private final List<String> protocols;
    private final String defaultProtocol;
    private String protocol;
    
    protected NegotiatingServerConnection(final Connector connector, final EndPoint endPoint, final SSLEngine engine, final List<String> protocols, final String defaultProtocol) {
        super(endPoint, connector.getExecutor());
        this.connector = connector;
        this.protocols = protocols;
        this.defaultProtocol = defaultProtocol;
        this.engine = engine;
    }
    
    protected List<String> getProtocols() {
        return this.protocols;
    }
    
    protected String getDefaultProtocol() {
        return this.defaultProtocol;
    }
    
    protected Connector getConnector() {
        return this.connector;
    }
    
    protected SSLEngine getSSLEngine() {
        return this.engine;
    }
    
    protected String getProtocol() {
        return this.protocol;
    }
    
    protected void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    @Override
    public void onOpen() {
        super.onOpen();
        this.fillInterested();
    }
    
    @Override
    public void onFillable() {
        final int filled = this.fill();
        if (filled == 0) {
            if (this.protocol == null) {
                if (this.engine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                    if (NegotiatingServerConnection.LOG.isDebugEnabled()) {
                        NegotiatingServerConnection.LOG.debug("{} could not negotiate protocol, SSLEngine: {}", this, this.engine);
                    }
                    this.close();
                }
                else {
                    this.fillInterested();
                }
            }
            else {
                final ConnectionFactory connectionFactory = this.connector.getConnectionFactory(this.protocol);
                if (connectionFactory == null) {
                    NegotiatingServerConnection.LOG.info("{} application selected protocol '{}', but no correspondent {} has been configured", this, this.protocol, ConnectionFactory.class.getName());
                    this.close();
                }
                else {
                    final EndPoint endPoint = this.getEndPoint();
                    final Connection newConnection = connectionFactory.newConnection(this.connector, endPoint);
                    endPoint.upgrade(newConnection);
                }
            }
        }
        else {
            if (filled >= 0) {
                throw new IllegalStateException();
            }
            if (NegotiatingServerConnection.LOG.isDebugEnabled()) {
                NegotiatingServerConnection.LOG.debug("{} detected close on client side", this);
            }
            this.close();
        }
    }
    
    private int fill() {
        try {
            return this.getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
        }
        catch (IOException x) {
            NegotiatingServerConnection.LOG.debug(x);
            this.close();
            return -1;
        }
    }
    
    @Override
    public void close() {
        this.getEndPoint().shutdownOutput();
        super.close();
    }
    
    static {
        LOG = Log.getLogger(NegotiatingServerConnection.class);
    }
    
    public interface CipherDiscriminator
    {
        boolean isAcceptable(final String p0, final String p1, final String p2);
    }
}
