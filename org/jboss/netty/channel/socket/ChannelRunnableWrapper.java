// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.DefaultChannelFuture;

public class ChannelRunnableWrapper extends DefaultChannelFuture implements Runnable
{
    private final Runnable task;
    private boolean started;
    
    public ChannelRunnableWrapper(final Channel channel, final Runnable task) {
        super(channel, true);
        this.task = task;
    }
    
    public void run() {
        synchronized (this) {
            if (this.isCancelled()) {
                return;
            }
            this.started = true;
        }
        try {
            this.task.run();
            this.setSuccess();
        }
        catch (Throwable t) {
            this.setFailure(t);
        }
    }
    
    @Override
    public synchronized boolean cancel() {
        return !this.started && super.cancel();
    }
}
