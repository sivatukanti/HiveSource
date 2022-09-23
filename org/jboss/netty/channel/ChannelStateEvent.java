// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelStateEvent extends ChannelEvent
{
    ChannelState getState();
    
    Object getValue();
}
