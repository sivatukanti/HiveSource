// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ExceptionEvent extends ChannelEvent
{
    Throwable getCause();
}
