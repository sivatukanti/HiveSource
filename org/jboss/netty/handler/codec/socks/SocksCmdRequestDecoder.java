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

public class SocksCmdRequestDecoder extends ReplayingDecoder<State>
{
    private SocksMessage.ProtocolVersion version;
    private int fieldLength;
    private SocksMessage.CmdType cmdType;
    private SocksMessage.AddressType addressType;
    private byte reserved;
    private String host;
    private int port;
    private SocksRequest msg;
    
    public SocksCmdRequestDecoder() {
        super(State.CHECK_PROTOCOL_VERSION);
        this.msg = SocksCommonUtils.UNKNOWN_SOCKS_REQUEST;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        Label_0337: {
            switch (state) {
                case CHECK_PROTOCOL_VERSION: {
                    this.version = SocksMessage.ProtocolVersion.fromByte(buffer.readByte());
                    if (this.version != SocksMessage.ProtocolVersion.SOCKS5) {
                        break;
                    }
                    this.checkpoint(State.READ_CMD_HEADER);
                }
                case READ_CMD_HEADER: {
                    this.cmdType = SocksMessage.CmdType.fromByte(buffer.readByte());
                    this.reserved = buffer.readByte();
                    this.addressType = SocksMessage.AddressType.fromByte(buffer.readByte());
                    this.checkpoint(State.READ_CMD_ADDRESS);
                }
                case READ_CMD_ADDRESS: {
                    switch (this.addressType) {
                        case IPv4: {
                            this.host = SocksCommonUtils.intToIp(buffer.readInt());
                            this.port = buffer.readUnsignedShort();
                            this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                            break Label_0337;
                        }
                        case DOMAIN: {
                            this.fieldLength = buffer.readByte();
                            this.host = buffer.readBytes(this.fieldLength).toString(CharsetUtil.US_ASCII);
                            this.port = buffer.readUnsignedShort();
                            this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                            break Label_0337;
                        }
                        case IPv6: {
                            this.host = SocksCommonUtils.ipv6toStr(buffer.readBytes(16).array());
                            this.port = buffer.readUnsignedShort();
                            this.msg = new SocksCmdRequest(this.cmdType, this.addressType, this.host, this.port);
                            break Label_0337;
                        }
                    }
                    break;
                }
            }
        }
        ctx.getPipeline().remove(this);
        return this.msg;
    }
    
    enum State
    {
        CHECK_PROTOCOL_VERSION, 
        READ_CMD_HEADER, 
        READ_CMD_ADDRESS;
    }
}
