// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

public interface WrappedChannelBuffer extends ChannelBuffer
{
    ChannelBuffer unwrap();
}
