// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.nio;

import org.mortbay.jetty.RetryRequest;
import org.mortbay.thread.Timeout;
import org.mortbay.jetty.Connector;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.jetty.Request;
import org.mortbay.io.EndPoint;
import org.mortbay.log.Log;
import org.mortbay.io.Connection;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.io.nio.SelectChannelEndPoint;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import org.mortbay.io.nio.SelectorManager;
import java.nio.channels.ServerSocketChannel;

public class SelectChannelConnector extends AbstractNIOConnector
{
    protected transient ServerSocketChannel _acceptChannel;
    private long _lowResourcesConnections;
    private long _lowResourcesMaxIdleTime;
    private SelectorManager _manager;
    
    public SelectChannelConnector() {
        this._manager = new SelectorManager() {
            protected SocketChannel acceptChannel(final SelectionKey key) throws IOException {
                final SocketChannel channel = ((ServerSocketChannel)key.channel()).accept();
                if (channel == null) {
                    return null;
                }
                channel.configureBlocking(false);
                final Socket socket = channel.socket();
                AbstractConnector.this.configure(socket);
                return channel;
            }
            
            public boolean dispatch(final Runnable task) throws IOException {
                return SelectChannelConnector.this.getThreadPool().dispatch(task);
            }
            
            protected void endPointClosed(final SelectChannelEndPoint endpoint) {
                AbstractConnector.this.connectionClosed((HttpConnection)endpoint.getConnection());
            }
            
            protected void endPointOpened(final SelectChannelEndPoint endpoint) {
                AbstractConnector.this.connectionOpened((HttpConnection)endpoint.getConnection());
            }
            
            protected Connection newConnection(final SocketChannel channel, final SelectChannelEndPoint endpoint) {
                return SelectChannelConnector.this.newConnection(channel, endpoint);
            }
            
            protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectSet selectSet, final SelectionKey sKey) throws IOException {
                return SelectChannelConnector.this.newEndPoint(channel, selectSet, sKey);
            }
        };
    }
    
    public void accept(final int acceptorID) throws IOException {
        this._manager.doSelect(acceptorID);
    }
    
    public void close() throws IOException {
        synchronized (this) {
            if (this._manager.isRunning()) {
                try {
                    this._manager.stop();
                }
                catch (Exception e) {
                    Log.warn(e);
                }
            }
            if (this._acceptChannel != null) {
                this._acceptChannel.close();
            }
            this._acceptChannel = null;
        }
    }
    
    public void customize(final EndPoint endpoint, final Request request) throws IOException {
        final ConnectorEndPoint cep = (ConnectorEndPoint)endpoint;
        cep.cancelIdle();
        request.setTimeStamp(cep.getSelectSet().getNow());
        super.customize(endpoint, request);
    }
    
    public void persist(final EndPoint endpoint) throws IOException {
        ((ConnectorEndPoint)endpoint).scheduleIdle();
        super.persist(endpoint);
    }
    
    public Object getConnection() {
        return this._acceptChannel;
    }
    
    public boolean getDelaySelectKeyUpdate() {
        return this._manager.isDelaySelectKeyUpdate();
    }
    
    public int getLocalPort() {
        synchronized (this) {
            if (this._acceptChannel == null || !this._acceptChannel.isOpen()) {
                return -1;
            }
            return this._acceptChannel.socket().getLocalPort();
        }
    }
    
    public Continuation newContinuation() {
        return new RetryContinuation();
    }
    
    public void open() throws IOException {
        synchronized (this) {
            if (this._acceptChannel == null) {
                this._acceptChannel = ServerSocketChannel.open();
                this._acceptChannel.socket().setReuseAddress(this.getReuseAddress());
                final InetSocketAddress addr = (this.getHost() == null) ? new InetSocketAddress(this.getPort()) : new InetSocketAddress(this.getHost(), this.getPort());
                this._acceptChannel.socket().bind(addr, this.getAcceptQueueSize());
                this._acceptChannel.configureBlocking(false);
            }
        }
    }
    
    public void setDelaySelectKeyUpdate(final boolean delay) {
        this._manager.setDelaySelectKeyUpdate(delay);
    }
    
    public void setMaxIdleTime(final int maxIdleTime) {
        this._manager.setMaxIdleTime(maxIdleTime);
        super.setMaxIdleTime(maxIdleTime);
    }
    
    public long getLowResourcesConnections() {
        return this._lowResourcesConnections;
    }
    
    public void setLowResourcesConnections(final long lowResourcesConnections) {
        this._lowResourcesConnections = lowResourcesConnections;
    }
    
    public long getLowResourcesMaxIdleTime() {
        return this._lowResourcesMaxIdleTime;
    }
    
    public void setLowResourcesMaxIdleTime(final long lowResourcesMaxIdleTime) {
        this._lowResourcesMaxIdleTime = lowResourcesMaxIdleTime;
        super.setLowResourceMaxIdleTime((int)lowResourcesMaxIdleTime);
    }
    
    public void setLowResourceMaxIdleTime(final int lowResourcesMaxIdleTime) {
        this._lowResourcesMaxIdleTime = lowResourcesMaxIdleTime;
        super.setLowResourceMaxIdleTime(lowResourcesMaxIdleTime);
    }
    
    protected void doStart() throws Exception {
        this._manager.setSelectSets(this.getAcceptors());
        this._manager.setMaxIdleTime(this.getMaxIdleTime());
        this._manager.setLowResourcesConnections(this.getLowResourcesConnections());
        this._manager.setLowResourcesMaxIdleTime(this.getLowResourcesMaxIdleTime());
        this._manager.start();
        this.open();
        this._manager.register(this._acceptChannel);
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key) throws IOException {
        return new ConnectorEndPoint(channel, selectSet, key);
    }
    
    protected Connection newConnection(final SocketChannel channel, final SelectChannelEndPoint endpoint) {
        return new HttpConnection(this, endpoint, this.getServer());
    }
    
    public static class ConnectorEndPoint extends SelectChannelEndPoint
    {
        public ConnectorEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key) {
            super(channel, selectSet, key);
            this.scheduleIdle();
        }
        
        public void close() throws IOException {
            final Connection con = this.getConnection();
            if (con instanceof HttpConnection) {
                final RetryContinuation continuation = (RetryContinuation)((HttpConnection)this.getConnection()).getRequest().getContinuation();
                if (continuation != null && continuation.isPending()) {
                    continuation.reset();
                }
            }
            super.close();
        }
        
        public void undispatch() {
            final Connection con = this.getConnection();
            if (con instanceof HttpConnection) {
                final RetryContinuation continuation = (RetryContinuation)((HttpConnection)this.getConnection()).getRequest().getContinuation();
                if (continuation != null) {
                    Log.debug("continuation {}", continuation);
                    if (continuation.undispatch()) {
                        super.undispatch();
                    }
                }
                else {
                    super.undispatch();
                }
            }
            else {
                super.undispatch();
            }
        }
    }
    
    public static class RetryContinuation extends Timeout.Task implements Continuation, Runnable
    {
        SelectChannelEndPoint _endPoint;
        boolean _new;
        Object _object;
        boolean _pending;
        boolean _resumed;
        boolean _parked;
        RetryRequest _retry;
        long _timeout;
        
        public RetryContinuation() {
            this._endPoint = (SelectChannelEndPoint)HttpConnection.getCurrentConnection().getEndPoint();
            this._new = true;
            this._pending = false;
            this._resumed = false;
            this._parked = false;
        }
        
        public Object getObject() {
            return this._object;
        }
        
        public long getTimeout() {
            return this._timeout;
        }
        
        public boolean isNew() {
            return this._new;
        }
        
        public boolean isPending() {
            return this._pending;
        }
        
        public boolean isResumed() {
            return this._resumed;
        }
        
        public void reset() {
            synchronized (this) {
                this._resumed = false;
                this._pending = false;
                this._parked = false;
            }
            synchronized (this._endPoint.getSelectSet()) {
                this.cancel();
            }
        }
        
        public boolean suspend(final long timeout) {
            boolean resumed = false;
            synchronized (this) {
                resumed = this._resumed;
                this._resumed = false;
                this._new = false;
                if (!this._pending && !resumed && timeout >= 0L) {
                    this._pending = true;
                    this._parked = false;
                    this._timeout = timeout;
                    if (this._retry == null) {
                        this._retry = new RetryRequest();
                    }
                    throw this._retry;
                }
                this._resumed = false;
                this._pending = false;
                this._parked = false;
            }
            synchronized (this._endPoint.getSelectSet()) {
                this.cancel();
            }
            return resumed;
        }
        
        public void resume() {
            boolean redispatch = false;
            synchronized (this) {
                if (this._pending && !this.isExpired()) {
                    this._resumed = true;
                    redispatch = this._parked;
                    this._parked = false;
                }
            }
            if (redispatch) {
                final SelectorManager.SelectSet selectSet = this._endPoint.getSelectSet();
                synchronized (selectSet) {
                    this.cancel();
                }
                this._endPoint.scheduleIdle();
                selectSet.addChange(this);
                selectSet.wakeup();
            }
        }
        
        public void expire() {
            boolean redispatch = false;
            synchronized (this) {
                redispatch = (this._parked && this._pending && !this._resumed);
                this._parked = false;
            }
            if (redispatch) {
                this._endPoint.scheduleIdle();
                this._endPoint.getSelectSet().addChange(this);
                this._endPoint.getSelectSet().wakeup();
            }
        }
        
        public void run() {
            this._endPoint.run();
        }
        
        public boolean undispatch() {
            boolean redispatch = false;
            synchronized (this) {
                if (!this._pending) {
                    return true;
                }
                redispatch = (this.isExpired() || this._resumed);
                this._parked = !redispatch;
            }
            if (redispatch) {
                this._endPoint.scheduleIdle();
                this._endPoint.getSelectSet().addChange(this);
            }
            else if (this._timeout > 0L) {
                this._endPoint.getSelectSet().scheduleTimeout(this, this._timeout);
            }
            this._endPoint.getSelectSet().wakeup();
            return false;
        }
        
        public void setObject(final Object object) {
            this._object = object;
        }
        
        public String toString() {
            synchronized (this) {
                return "RetryContinuation@" + this.hashCode() + (this._new ? ",new" : "") + (this._pending ? ",pending" : "") + (this._resumed ? ",resumed" : "") + (this.isExpired() ? ",expired" : "") + (this._parked ? ",parked" : "");
            }
        }
    }
}
