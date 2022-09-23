// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;

public final class SocksCmdResponse extends SocksResponse
{
    private final CmdStatus cmdStatus;
    private final AddressType addressType;
    private static final byte[] IPv4_HOSTNAME_ZEROED;
    private static final byte[] IPv6_HOSTNAME_ZEROED;
    
    public SocksCmdResponse(final CmdStatus cmdStatus, final AddressType addressType) {
        super(SocksResponseType.CMD);
        if (cmdStatus == null) {
            throw new NullPointerException("cmdStatus");
        }
        if (addressType == null) {
            throw new NullPointerException("addressType");
        }
        this.cmdStatus = cmdStatus;
        this.addressType = addressType;
    }
    
    public CmdStatus getCmdStatus() {
        return this.cmdStatus;
    }
    
    public AddressType getAddressType() {
        return this.addressType;
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(this.getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.cmdStatus.getByteValue());
        channelBuffer.writeByte(0);
        channelBuffer.writeByte(this.addressType.getByteValue());
        switch (this.addressType) {
            case IPv4: {
                channelBuffer.writeBytes(SocksCmdResponse.IPv4_HOSTNAME_ZEROED);
                channelBuffer.writeShort(0);
                break;
            }
            case DOMAIN: {
                channelBuffer.writeByte(1);
                channelBuffer.writeByte(0);
                channelBuffer.writeShort(0);
                break;
            }
            case IPv6: {
                channelBuffer.writeBytes(SocksCmdResponse.IPv6_HOSTNAME_ZEROED);
                channelBuffer.writeShort(0);
                break;
            }
        }
    }
    
    static {
        IPv4_HOSTNAME_ZEROED = new byte[] { 0, 0, 0, 0 };
        IPv6_HOSTNAME_ZEROED = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
