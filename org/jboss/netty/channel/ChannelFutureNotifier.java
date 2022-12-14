// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public final class ChannelFutureNotifier implements ChannelFutureListener
{
    private final ChannelFuture future;
    
    public ChannelFutureNotifier(final ChannelFuture future) {
        this.future = future;
    }
    
    public void operationComplete(final ChannelFuture cf) throws Exception {
        if (cf.isSuccess()) {
            this.future.setSuccess();
        }
        else {
            this.future.setFailure(cf.getCause());
        }
    }
}
