// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.ChannelPipeline;

public interface CodecEmbedder<E>
{
    boolean offer(final Object p0);
    
    boolean finish();
    
    E poll();
    
    E peek();
    
    Object[] pollAll();
    
     <T> T[] pollAll(final T[] p0);
    
    int size();
    
    ChannelPipeline getPipeline();
}
