// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public final class SocksAuthResponse extends SocksResponse
{
    private static final SubnegotiationVersion SUBNEGOTIATION_VERSION;
    private final AuthStatus authStatus;
    
    public SocksAuthResponse(final AuthStatus authStatus) {
        super(SocksResponseType.AUTH);
        if (authStatus == null) {
            throw new NullPointerException("authStatus");
        }
        this.authStatus = authStatus;
    }
    
    public AuthStatus getAuthStatus() {
        return this.authStatus;
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(SocksAuthResponse.SUBNEGOTIATION_VERSION.getByteValue());
        channelBuffer.writeByte(this.authStatus.getByteValue());
    }
    
    static {
        SUBNEGOTIATION_VERSION = SubnegotiationVersion.AUTH_PASSWORD;
    }
}
