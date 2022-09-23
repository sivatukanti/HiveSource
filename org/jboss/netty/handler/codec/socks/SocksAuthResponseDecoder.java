// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class SocksAuthResponseDecoder extends ReplayingDecoder<State>
{
    private SocksMessage.SubnegotiationVersion version;
    private SocksMessage.AuthStatus authStatus;
    private SocksResponse msg;
    
    public SocksAuthResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksMessage.SubnegotiationVersion.fromByte(buffer.readByte());
                if (this.version != SocksMessage.SubnegotiationVersion.AUTH_PASSWORD) {
                    break;
                }
                this.checkpoint(State.READ_AUTH_RESPONSE);
            }
            case READ_AUTH_RESPONSE: {
                this.authStatus = SocksMessage.AuthStatus.fromByte(buffer.readByte());
                this.msg = new SocksAuthResponse(this.authStatus);
                break;
            }
        }
        ctx.getPipeline().remove(this);
        return this.msg;
    }
    
    public enum State
    {
        CHECK_PROTOCOL_VERSION, 
        READ_AUTH_RESPONSE;
    }
}
