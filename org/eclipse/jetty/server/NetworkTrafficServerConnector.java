// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;
import org.eclipse.jetty.io.NetworkTrafficSelectChannelEndPoint;
import org.eclipse.jetty.io.SelectChannelEndPoint;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.io.ManagedSelector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.io.NetworkTrafficListener;
import java.util.List;

public class NetworkTrafficServerConnector extends ServerConnector
{
    private final List<NetworkTrafficListener> listeners;
    
    public NetworkTrafficServerConnector(final Server server) {
        this(server, null, null, null, 0, 0, new ConnectionFactory[] { new HttpConnectionFactory() });
    }
    
    public NetworkTrafficServerConnector(final Server server, final ConnectionFactory connectionFactory, final SslContextFactory sslContextFactory) {
        super(server, sslContextFactory, new ConnectionFactory[] { connectionFactory });
        this.listeners = new CopyOnWriteArrayList<NetworkTrafficListener>();
    }
    
    public NetworkTrafficServerConnector(final Server server, final ConnectionFactory connectionFactory) {
        super(server, new ConnectionFactory[] { connectionFactory });
        this.listeners = new CopyOnWriteArrayList<NetworkTrafficListener>();
    }
    
    public NetworkTrafficServerConnector(final Server server, final Executor executor, final Scheduler scheduler, final ByteBufferPool pool, final int acceptors, final int selectors, final ConnectionFactory... factories) {
        super(server, executor, scheduler, pool, acceptors, selectors, factories);
        this.listeners = new CopyOnWriteArrayList<NetworkTrafficListener>();
    }
    
    public NetworkTrafficServerConnector(final Server server, final SslContextFactory sslContextFactory) {
        super(server, sslContextFactory);
        this.listeners = new CopyOnWriteArrayList<NetworkTrafficListener>();
    }
    
    public void addNetworkTrafficListener(final NetworkTrafficListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeNetworkTrafficListener(final NetworkTrafficListener listener) {
        this.listeners.remove(listener);
    }
    
    @Override
    protected SelectChannelEndPoint newEndPoint(final SocketChannel channel, final ManagedSelector selectSet, final SelectionKey key) throws IOException {
        final NetworkTrafficSelectChannelEndPoint endPoint = new NetworkTrafficSelectChannelEndPoint(channel, selectSet, key, this.getScheduler(), this.getIdleTimeout(), this.listeners);
        return endPoint;
    }
}
