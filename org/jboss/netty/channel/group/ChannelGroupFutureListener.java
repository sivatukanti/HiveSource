// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import java.util.EventListener;

public interface ChannelGroupFutureListener extends EventListener
{
    void operationComplete(final ChannelGroupFuture p0) throws Exception;
}
