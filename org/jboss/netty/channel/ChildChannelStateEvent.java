// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChildChannelStateEvent extends ChannelEvent
{
    Channel getChannel();
    
    Channel getChildChannel();
}
