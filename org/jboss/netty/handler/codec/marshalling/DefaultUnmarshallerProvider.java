// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Unmarshaller;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.MarshallerFactory;

public class DefaultUnmarshallerProvider implements UnmarshallerProvider
{
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public DefaultUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }
    
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        return this.factory.createUnmarshaller(this.config);
    }
}
