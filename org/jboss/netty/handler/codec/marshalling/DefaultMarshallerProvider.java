// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.MarshallerFactory;

public class DefaultMarshallerProvider implements MarshallerProvider
{
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public DefaultMarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.factory = factory;
        this.config = config;
    }
    
    public Marshaller getMarshaller(final ChannelHandlerContext ctx) throws Exception {
        return this.factory.createMarshaller(this.config);
    }
}
