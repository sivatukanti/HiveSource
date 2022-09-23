// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.ChannelConfig;

public interface SocketChannelConfig extends ChannelConfig
{
    boolean isTcpNoDelay();
    
    void setTcpNoDelay(final boolean p0);
    
    int getSoLinger();
    
    void setSoLinger(final int p0);
    
    int getSendBufferSize();
    
    void setSendBufferSize(final int p0);
    
    int getReceiveBufferSize();
    
    void setReceiveBufferSize(final int p0);
    
    boolean isKeepAlive();
    
    void setKeepAlive(final boolean p0);
    
    int getTrafficClass();
    
    void setTrafficClass(final int p0);
    
    boolean isReuseAddress();
    
    void setReuseAddress(final boolean p0);
    
    void setPerformancePreferences(final int p0, final int p1, final int p2);
}
