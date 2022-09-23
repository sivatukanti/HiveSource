// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.nio.channels.WritePendingException;
import java.nio.channels.ReadPendingException;
import org.eclipse.jetty.util.Callback;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;

public class ProxyConnectionFactory extends AbstractConnectionFactory
{
    private static final Logger LOG;
    private final String _next;
    
    public ProxyConnectionFactory() {
        super("proxy");
        this._next = null;
    }
    
    public ProxyConnectionFactory(final String nextProtocol) {
        super("proxy");
        this._next = nextProtocol;
    }
    
    @Override
    public Connection newConnection(final Connector connector, final EndPoint endp) {
        String next = this._next;
        if (next == null) {
            final Iterator<String> i = connector.getProtocols().iterator();
            while (i.hasNext()) {
                final String p = i.next();
                if (this.getProtocol().equalsIgnoreCase(p)) {
                    next = i.next();
                    break;
                }
            }
        }
        return new ProxyConnection(endp, connector, next);
    }
    
    static {
        LOG = Log.getLogger(ProxyConnectionFactory.class);
    }
    
    public static class ProxyConnection extends AbstractConnection
    {
        private final int[] __size;
        private final Connector _connector;
        private final String _next;
        private final StringBuilder _builder;
        private final String[] _field;
        private int _fields;
        private int _length;
        
        protected ProxyConnection(final EndPoint endp, final Connector connector, final String next) {
            super(endp, connector.getExecutor());
            this.__size = new int[] { 29, 23, 21, 13, 5, 3, 1 };
            this._builder = new StringBuilder();
            this._field = new String[6];
            this._connector = connector;
            this._next = next;
        }
        
        @Override
        public void onOpen() {
            super.onOpen();
            this.fillInterested();
        }
        
        @Override
        public void onFillable() {
            Label_0533: {
                try {
                    ByteBuffer buffer = null;
                    while (true) {
                        final int size = Math.max(1, this.__size[this._fields] - this._builder.length());
                        if (buffer == null || buffer.capacity() != size) {
                            buffer = BufferUtil.allocate(size);
                        }
                        else {
                            BufferUtil.clear(buffer);
                        }
                        final int fill = this.getEndPoint().fill(buffer);
                        if (fill < 0) {
                            this.getEndPoint().shutdownOutput();
                            return;
                        }
                        if (fill == 0) {
                            this.fillInterested();
                            return;
                        }
                        this._length += fill;
                        if (this._length >= 108) {
                            ProxyConnectionFactory.LOG.warn("PROXY line too long {} for {}", this._length, this.getEndPoint());
                            this.close();
                            return;
                        }
                        while (buffer.hasRemaining()) {
                            final byte b = buffer.get();
                            if (this._fields < 6) {
                                if (b == 32 || (b == 13 && this._fields == 5)) {
                                    this._field[this._fields++] = this._builder.toString();
                                    this._builder.setLength(0);
                                }
                                else {
                                    if (b < 32) {
                                        ProxyConnectionFactory.LOG.warn("Bad character {} for {}", b & 0xFF, this.getEndPoint());
                                        this.close();
                                        return;
                                    }
                                    this._builder.append((char)b);
                                }
                            }
                            else {
                                if (b != 10) {
                                    ProxyConnectionFactory.LOG.warn("Bad CRLF for {}", this.getEndPoint());
                                    this.close();
                                    return;
                                }
                                if (!"PROXY".equals(this._field[0])) {
                                    ProxyConnectionFactory.LOG.warn("Not PROXY protocol for {}", this.getEndPoint());
                                    this.close();
                                    return;
                                }
                                final InetSocketAddress remote = new InetSocketAddress(this._field[2], Integer.parseInt(this._field[4]));
                                final InetSocketAddress local = new InetSocketAddress(this._field[3], Integer.parseInt(this._field[5]));
                                final ConnectionFactory connectionFactory = this._connector.getConnectionFactory(this._next);
                                if (connectionFactory == null) {
                                    ProxyConnectionFactory.LOG.info("Next protocol '{}' for {}", this._next, this.getEndPoint());
                                    this.close();
                                    return;
                                }
                                final EndPoint endPoint = new ProxyEndPoint(this.getEndPoint(), remote, local);
                                final Connection newConnection = connectionFactory.newConnection(this._connector, endPoint);
                                endPoint.upgrade(newConnection);
                                break Label_0533;
                            }
                        }
                    }
                }
                catch (Throwable x) {
                    ProxyConnectionFactory.LOG.warn("PROXY error for " + this.getEndPoint(), x);
                    this.close();
                }
            }
        }
    }
    
    public static class ProxyEndPoint implements EndPoint
    {
        private final EndPoint _endp;
        private final InetSocketAddress _remote;
        private final InetSocketAddress _local;
        
        public ProxyEndPoint(final EndPoint endp, final InetSocketAddress remote, final InetSocketAddress local) {
            this._endp = endp;
            this._remote = remote;
            this._local = local;
        }
        
        @Override
        public boolean isOptimizedForDirectBuffers() {
            return this._endp.isOptimizedForDirectBuffers();
        }
        
        @Override
        public InetSocketAddress getLocalAddress() {
            return this._local;
        }
        
        @Override
        public InetSocketAddress getRemoteAddress() {
            return this._remote;
        }
        
        @Override
        public boolean isOpen() {
            return this._endp.isOpen();
        }
        
        @Override
        public long getCreatedTimeStamp() {
            return this._endp.getCreatedTimeStamp();
        }
        
        @Override
        public void shutdownOutput() {
            this._endp.shutdownOutput();
        }
        
        @Override
        public boolean isOutputShutdown() {
            return this._endp.isOutputShutdown();
        }
        
        @Override
        public boolean isInputShutdown() {
            return this._endp.isInputShutdown();
        }
        
        @Override
        public void close() {
            this._endp.close();
        }
        
        @Override
        public int fill(final ByteBuffer buffer) throws IOException {
            return this._endp.fill(buffer);
        }
        
        @Override
        public boolean flush(final ByteBuffer... buffer) throws IOException {
            return this._endp.flush(buffer);
        }
        
        @Override
        public Object getTransport() {
            return this._endp.getTransport();
        }
        
        @Override
        public long getIdleTimeout() {
            return this._endp.getIdleTimeout();
        }
        
        @Override
        public void setIdleTimeout(final long idleTimeout) {
            this._endp.setIdleTimeout(idleTimeout);
        }
        
        @Override
        public void fillInterested(final Callback callback) throws ReadPendingException {
            this._endp.fillInterested(callback);
        }
        
        @Override
        public boolean tryFillInterested(final Callback callback) {
            return this._endp.tryFillInterested(callback);
        }
        
        @Override
        public boolean isFillInterested() {
            return this._endp.isFillInterested();
        }
        
        @Override
        public void write(final Callback callback, final ByteBuffer... buffers) throws WritePendingException {
            this._endp.write(callback, buffers);
        }
        
        @Override
        public Connection getConnection() {
            return this._endp.getConnection();
        }
        
        @Override
        public void setConnection(final Connection connection) {
            this._endp.setConnection(connection);
        }
        
        @Override
        public void onOpen() {
            this._endp.onOpen();
        }
        
        @Override
        public void onClose() {
            this._endp.onClose();
        }
        
        @Override
        public void upgrade(final Connection newConnection) {
            this._endp.upgrade(newConnection);
        }
    }
}
