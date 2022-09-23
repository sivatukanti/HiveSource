// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public final class SocksInitResponse extends SocksResponse
{
    private final AuthScheme authScheme;
    
    public SocksInitResponse(final AuthScheme authScheme) {
        super(SocksResponseType.INIT);
        if (authScheme == null) {
            throw new NullPointerException("authScheme");
        }
        this.authScheme = authScheme;
    }
    
    public AuthScheme getAuthScheme() {
        return this.authScheme;
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(this.getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.authScheme.getByteValue());
    }
}
