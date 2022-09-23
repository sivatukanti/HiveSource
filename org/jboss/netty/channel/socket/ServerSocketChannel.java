// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.ServerChannel;

public interface ServerSocketChannel extends ServerChannel
{
    ServerSocketChannelConfig getConfig();
    
    InetSocketAddress getLocalAddress();
    
    InetSocketAddress getRemoteAddress();
}
