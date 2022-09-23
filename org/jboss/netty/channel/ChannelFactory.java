// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.util.ExternalResourceReleasable;

public interface ChannelFactory extends ExternalResourceReleasable
{
    Channel newChannel(final ChannelPipeline p0);
    
    void shutdown();
    
    void releaseExternalResources();
}
