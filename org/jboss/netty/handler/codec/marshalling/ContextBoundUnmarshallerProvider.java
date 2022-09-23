// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Unmarshaller;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.MarshallerFactory;

public class ContextBoundUnmarshallerProvider extends DefaultUnmarshallerProvider
{
    public ContextBoundUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        super(factory, config);
    }
    
    @Override
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        Unmarshaller unmarshaller = (Unmarshaller)ctx.getAttachment();
        if (unmarshaller == null) {
            unmarshaller = super.getUnmarshaller(ctx);
            ctx.setAttachment(unmarshaller);
        }
        return unmarshaller;
    }
}
