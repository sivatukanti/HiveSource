// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.buffer.ChannelBufferFactory;
import java.util.Map;

public interface ChannelConfig
{
    void setOptions(final Map<String, Object> p0);
    
    boolean setOption(final String p0, final Object p1);
    
    ChannelBufferFactory getBufferFactory();
    
    void setBufferFactory(final ChannelBufferFactory p0);
    
    ChannelPipelineFactory getPipelineFactory();
    
    void setPipelineFactory(final ChannelPipelineFactory p0);
    
    int getConnectTimeoutMillis();
    
    void setConnectTimeoutMillis(final int p0);
}
