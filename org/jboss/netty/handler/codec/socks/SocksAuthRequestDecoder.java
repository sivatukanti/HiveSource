// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class SocksAuthRequestDecoder extends ReplayingDecoder<State>
{
    private SocksMessage.SubnegotiationVersion version;
    private int fieldLength;
    private String username;
    private String password;
    private SocksRequest msg;
    
    public SocksAuthRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        switch (state) {
            case CHECK_PROTOCOL_VERSION: {
                this.version = SocksMessage.SubnegotiationVersion.fromByte(buffer.readByte());
                if (this.version != SocksMessage.SubnegotiationVersion.AUTH_PASSWORD) {
                    break;
                }
                this.checkpoint(State.READ_USERNAME);
            }
            case READ_USERNAME: {
                this.fieldLength = buffer.readByte();
                this.username = buffer.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.checkpoint(State.READ_PASSWORD);
            }
            case READ_PASSWORD: {
                this.fieldLength = buffer.readByte();
                this.password = buffer.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                this.msg = new SocksAuthRequest(this.username, this.password);
                break;
            }
        }
        ctx.getPipeline().remove(this);
        return this.msg;
    }
    
    enum State
    {
        CHECK_PROTOCOL_VERSION, 
        READ_USERNAME, 
        READ_PASSWORD;
    }
}
