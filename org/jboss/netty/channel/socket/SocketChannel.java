// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.Channel;

public interface SocketChannel extends Channel
{
    SocketChannelConfig getConfig();
    
    InetSocketAddress getLocalAddress();
    
    InetSocketAddress getRemoteAddress();
}
