// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.http;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class HttpTunnelingClientSocketChannelFactory implements ClientSocketChannelFactory
{
    private final ChannelSink sink;
    private final ClientSocketChannelFactory clientSocketChannelFactory;
    
    public HttpTunnelingClientSocketChannelFactory(final ClientSocketChannelFactory clientSocketChannelFactory) {
        this.sink = new HttpTunnelingClientSocketPipelineSink();
        this.clientSocketChannelFactory = clientSocketChannelFactory;
    }
    
    public SocketChannel newChannel(final ChannelPipeline pipeline) {
        return new HttpTunnelingClientSocketChannel(this, pipeline, this.sink, this.clientSocketChannelFactory);
    }
    
    public void releaseExternalResources() {
        this.clientSocketChannelFactory.releaseExternalResources();
    }
    
    public void shutdown() {
        this.clientSocketChannelFactory.shutdown();
    }
}
