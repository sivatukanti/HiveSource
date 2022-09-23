// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.Map;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.util.log.Logger;

public abstract class NegotiatingClientConnection extends AbstractConnection
{
    private static final Logger LOG;
    private final SSLEngine engine;
    private final ClientConnectionFactory connectionFactory;
    private final Map<String, Object> context;
    private volatile boolean completed;
    
    protected NegotiatingClientConnection(final EndPoint endp, final Executor executor, final SSLEngine sslEngine, final ClientConnectionFactory connectionFactory, final Map<String, Object> context) {
        super(endp, executor);
        this.engine = sslEngine;
        this.connectionFactory = connectionFactory;
        this.context = context;
    }
    
    protected SSLEngine getSSLEngine() {
        return this.engine;
    }
    
    protected void completed() {
        this.completed = true;
    }
    
    @Override
    public void onOpen() {
        super.onOpen();
        try {
            this.getEndPoint().flush(BufferUtil.EMPTY_BUFFER);
            if (this.completed) {
                this.replaceConnection();
            }
            else {
                this.fillInterested();
            }
        }
        catch (IOException x) {
            this.close();
            throw new RuntimeIOException(x);
        }
    }
    
    @Override
    public void onFillable() {
        int filled;
        do {
            filled = this.fill();
            if (filled == 0 && !this.completed) {
                this.fillInterested();
            }
        } while (filled > 0 && !this.completed);
        if (this.completed) {
            this.replaceConnection();
        }
    }
    
    private int fill() {
        try {
            return this.getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
        }
        catch (IOException x) {
            NegotiatingClientConnection.LOG.debug(x);
            this.close();
            return -1;
        }
    }
    
    private void replaceConnection() {
        final EndPoint endPoint = this.getEndPoint();
        try {
            endPoint.upgrade(this.connectionFactory.newConnection(endPoint, this.context));
        }
        catch (Throwable x) {
            NegotiatingClientConnection.LOG.debug(x);
            this.close();
        }
    }
    
    @Override
    public void close() {
        this.getEndPoint().shutdownOutput();
        super.close();
    }
    
    static {
        LOG = Log.getLogger(NegotiatingClientConnection.class);
    }
}
