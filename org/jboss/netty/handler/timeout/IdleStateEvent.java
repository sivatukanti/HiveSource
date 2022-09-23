// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.channel.ChannelEvent;

public interface IdleStateEvent extends ChannelEvent
{
    IdleState getState();
    
    long getLastActivityTimeMillis();
}
