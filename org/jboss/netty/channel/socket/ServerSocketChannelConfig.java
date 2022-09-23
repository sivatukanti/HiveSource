// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.ChannelConfig;

public interface ServerSocketChannelConfig extends ChannelConfig
{
    int getBacklog();
    
    void setBacklog(final int p0);
    
    boolean isReuseAddress();
    
    void setReuseAddress(final boolean p0);
    
    int getReceiveBufferSize();
    
    void setReceiveBufferSize(final int p0);
    
    void setPerformancePreferences(final int p0, final int p1, final int p2);
}
