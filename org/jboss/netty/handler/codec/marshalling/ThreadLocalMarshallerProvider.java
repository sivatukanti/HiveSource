// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshaller;

public class ThreadLocalMarshallerProvider implements MarshallerProvider
{
    private final ThreadLocal<Marshaller> marshallers;
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public ThreadLocalMarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.marshallers = new ThreadLocal<Marshaller>();
        this.factory = factory;
        this.config = config;
    }
    
    public Marshaller getMarshaller(final ChannelHandlerContext ctx) throws Exception {
        Marshaller marshaller = this.marshallers.get();
        if (marshaller == null) {
            marshaller = this.factory.createMarshaller(this.config);
            this.marshallers.set(marshaller);
        }
        return marshaller;
    }
}
