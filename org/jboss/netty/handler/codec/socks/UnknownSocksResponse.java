// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public final class UnknownSocksResponse extends SocksResponse
{
    public UnknownSocksResponse() {
        super(SocksResponseType.UNKNOWN);
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer buffer) {
    }
}
