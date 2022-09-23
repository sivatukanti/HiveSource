// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

public interface NioSelectorPool
{
    void rebuildSelectors();
    
    void shutdown();
}
