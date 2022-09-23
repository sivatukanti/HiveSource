// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class SocksInitResponseDecoder extends ReplayingDecoder<State>
{
    private SocksMessage.ProtocolVersion version;
    private SocksMessage.AuthScheme authScheme;
    private SocksResponse msg;
    
    public SocksInitResponseDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_RESPONSE;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksMessage.ProtocolVersion.fromByte(buffer.readByte());
                if (this.version != SocksMessage.ProtocolVersion.SOCKS5) {
                    break;
                }
                this.checkpoint(State.READ_PREFFERED_AUTH_TYPE);
            }
            case READ_PREFFERED_AUTH_TYPE: {
                this.authScheme = SocksMessage.AuthScheme.fromByte(buffer.readByte());
                this.msg = new SocksInitResponse(this.authScheme);
                break;
            }
        }
        ctx.getPipeline().remove(this);
        return this.msg;
    }
    
    public enum State
    {
        CHECK_PROTOCOL_VERSION, 
        READ_PREFFERED_AUTH_TYPE;
    }
}
