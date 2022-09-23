// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

public interface Worker extends Runnable
{
    void executeInIoThread(final Runnable p0);
}
