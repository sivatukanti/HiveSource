// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.EventListener;

public interface ChannelFutureListener extends EventListener
{
    public static final ChannelFutureListener CLOSE = new ChannelFutureListener() {
        public void operationComplete(final ChannelFuture future) {
            future.getChannel().close();
        }
    };
    public static final ChannelFutureListener CLOSE_ON_FAILURE = new ChannelFutureListener() {
        public void operationComplete(final ChannelFuture future) {
            if (!future.isSuccess()) {
                future.getChannel().close();
            }
        }
    };
    
    void operationComplete(final ChannelFuture p0) throws Exception;
}
