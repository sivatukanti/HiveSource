// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.netty.channel.ChannelHandlerContext;

public interface MarshallerProvider
{
    Marshaller getMarshaller(final ChannelHandlerContext p0) throws Exception;
}
