// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Unmarshaller;

public class ThreadLocalUnmarshallerProvider implements UnmarshallerProvider
{
    private final ThreadLocal<Unmarshaller> unmarshallers;
    private final MarshallerFactory factory;
    private final MarshallingConfiguration config;
    
    public ThreadLocalUnmarshallerProvider(final MarshallerFactory factory, final MarshallingConfiguration config) {
        this.unmarshallers = new ThreadLocal<Unmarshaller>();
        this.factory = factory;
        this.config = config;
    }
    
    public Unmarshaller getUnmarshaller(final ChannelHandlerContext ctx) throws Exception {
        Unmarshaller unmarshaller = this.unmarshallers.get();
        if (unmarshaller == null) {
            unmarshaller = this.factory.createUnmarshaller(this.config);
            this.unmarshallers.set(unmarshaller);
        }
        return unmarshaller;
    }
}
