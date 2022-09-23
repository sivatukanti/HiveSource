// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.execution;

public class ChannelUpstreamEventRunnableFilter implements ChannelEventRunnableFilter
{
    public boolean filter(final ChannelEventRunnable event) {
        return event instanceof ChannelDownstreamEventRunnable;
    }
}
