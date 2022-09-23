// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.concurrent.Future;
import java.io.IOException;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("AbstractNetworkConnector")
public abstract class AbstractNetworkConnector extends AbstractConnector implements NetworkConnector
{
    private volatile String _host;
    private volatile int _port;
    
    public AbstractNetworkConnector(final Server server, final Executor executor, final Scheduler scheduler, final ByteBufferPool pool, final int acceptors, final ConnectionFactory... factories) {
        super(server, executor, scheduler, pool, acceptors, factories);
        this._port = 0;
    }
    
    public void setHost(final String host) {
        this._host = host;
    }
    
    @ManagedAttribute("The network interface this connector binds to as an IP address or a hostname.  If null or 0.0.0.0, then bind to all interfaces.")
    @Override
    public String getHost() {
        return this._host;
    }
    
    public void setPort(final int port) {
        this._port = port;
    }
    
    @ManagedAttribute("Port this connector listens on. If set the 0 a random port is assigned which may be obtained with getLocalPort()")
    @Override
    public int getPort() {
        return this._port;
    }
    
    @Override
    public int getLocalPort() {
        return -1;
    }
    
    @Override
    protected void doStart() throws Exception {
        this.open();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this.close();
        super.doStop();
    }
    
    @Override
    public void open() throws IOException {
    }
    
    @Override
    public void close() {
        this.interruptAcceptors();
    }
    
    @Override
    public Future<Void> shutdown() {
        this.close();
        return super.shutdown();
    }
    
    @Override
    protected boolean isAccepting() {
        return super.isAccepting() && this.isOpen();
    }
    
    @Override
    public String toString() {
        return String.format("%s{%s:%d}", super.toString(), (this.getHost() == null) ? "0.0.0.0" : this.getHost(), (this.getLocalPort() <= 0) ? this.getPort() : this.getLocalPort());
    }
}
