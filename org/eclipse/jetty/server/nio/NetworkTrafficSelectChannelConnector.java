// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.nio;

import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;

@Deprecated
public class NetworkTrafficSelectChannelConnector extends NetworkTrafficServerConnector
{
    public NetworkTrafficSelectChannelConnector(final Server server) {
        super(server);
    }
    
    public NetworkTrafficSelectChannelConnector(final Server server, final ConnectionFactory connectionFactory, final SslContextFactory sslContextFactory) {
        super(server, connectionFactory, sslContextFactory);
    }
    
    public NetworkTrafficSelectChannelConnector(final Server server, final ConnectionFactory connectionFactory) {
        super(server, connectionFactory);
    }
    
    public NetworkTrafficSelectChannelConnector(final Server server, final Executor executor, final Scheduler scheduler, final ByteBufferPool pool, final int acceptors, final int selectors, final ConnectionFactory... factories) {
        super(server, executor, scheduler, pool, acceptors, selectors, factories);
    }
    
    public NetworkTrafficSelectChannelConnector(final Server server, final SslContextFactory sslContextFactory) {
        super(server, sslContextFactory);
    }
}
