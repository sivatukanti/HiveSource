// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.net.InetSocketAddress;
import java.io.Closeable;

public abstract class TServerTransport implements Closeable
{
    public abstract void listen() throws TTransportException;
    
    public final TTransport accept() throws TTransportException {
        final TTransport transport = this.acceptImpl();
        if (transport == null) {
            throw new TTransportException("accept() may not return NULL");
        }
        return transport;
    }
    
    public abstract void close();
    
    protected abstract TTransport acceptImpl() throws TTransportException;
    
    public void interrupt() {
    }
    
    public abstract static class AbstractServerTransportArgs<T extends AbstractServerTransportArgs<T>>
    {
        int backlog;
        int clientTimeout;
        InetSocketAddress bindAddr;
        
        public AbstractServerTransportArgs() {
            this.backlog = 0;
            this.clientTimeout = 0;
        }
        
        public T backlog(final int backlog) {
            this.backlog = backlog;
            return (T)this;
        }
        
        public T clientTimeout(final int clientTimeout) {
            this.clientTimeout = clientTimeout;
            return (T)this;
        }
        
        public T port(final int port) {
            this.bindAddr = new InetSocketAddress(port);
            return (T)this;
        }
        
        public T bindAddr(final InetSocketAddress bindAddr) {
            this.bindAddr = bindAddr;
            return (T)this;
        }
    }
}
