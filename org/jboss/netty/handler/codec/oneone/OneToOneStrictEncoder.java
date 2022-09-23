// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.oneone;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelHandlerContext;

public abstract class OneToOneStrictEncoder extends OneToOneEncoder
{
    @Override
    protected boolean doEncode(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        synchronized (ctx.getChannel()) {
            return super.doEncode(ctx, e);
        }
    }
}
