// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class SocksInitRequestDecoder extends ReplayingDecoder<State>
{
    private final List<SocksMessage.AuthScheme> authSchemes;
    private SocksMessage.ProtocolVersion version;
    private byte authSchemeNum;
    private SocksRequest msg;
    
    public SocksInitRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
        this.authSchemes = new ArrayList<SocksMessage.AuthScheme>();
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksMessage.ProtocolVersion.fromByte(buffer.readByte());
                if (this.version != SocksMessage.ProtocolVersion.SOCKS5) {
                    break;
                }
                this.checkpoint(State.READ_AUTH_SCHEMES);
            }
            case READ_AUTH_SCHEMES: {
                this.authSchemes.clear();
                this.authSchemeNum = buffer.readByte();
                for (int i = 0; i < this.authSchemeNum; ++i) {
                    this.authSchemes.add(SocksMessage.AuthScheme.fromByte(buffer.readByte()));
                }
                this.msg = new SocksInitRequest(this.authSchemes);
                break;
            }
        }
        ctx.getPipeline().remove(this);
        return this.msg;
    }
    
    enum State
    {
        CHECK_PROTOCOL_VERSION, 
        READ_AUTH_SCHEMES;
    }
}
