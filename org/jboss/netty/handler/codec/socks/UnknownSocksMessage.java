// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public final class UnknownSocksMessage extends SocksMessage
{
    public UnknownSocksMessage() {
        super(MessageType.UNKNOWN);
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer byteBuf) {
    }
}
