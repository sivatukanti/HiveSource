// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.buffer.ChannelBuffer;
import java.net.IDN;
import org.jboss.netty.util.NetUtil;
import org.jboss.netty.util.internal.DetectionUtil;

public final class SocksCmdRequest extends SocksRequest
{
    private final CmdType cmdType;
    private final AddressType addressType;
    private final String host;
    private final int port;
    
    public SocksCmdRequest(final CmdType cmdType, final AddressType addressType, final String host, final int port) {
        super(SocksRequestType.CMD);
        if (DetectionUtil.javaVersion() < 6) {
            throw new IllegalStateException("Only supported with Java version 6+");
        }
        if (cmdType == null) {
            throw new NullPointerException("cmdType");
        }
        if (addressType == null) {
            throw new NullPointerException("addressType");
        }
        if (host == null) {
            throw new NullPointerException("host");
        }
        switch (addressType) {
            case IPv4: {
                if (!NetUtil.isValidIpV4Address(host)) {
                    throw new IllegalArgumentException(host + " is not a valid IPv4 address");
                }
                break;
            }
            case DOMAIN: {
                if (IDN.toASCII(host).length() > 255) {
                    throw new IllegalArgumentException(host + " IDN: " + IDN.toASCII(host) + " exceeds 255 char limit");
                }
                break;
            }
            case IPv6: {
                if (!NetUtil.isValidIpV6Address(host)) {
                    throw new IllegalArgumentException(host + " is not a valid IPv6 address");
                }
                break;
            }
        }
        if (port <= 0 || port >= 65536) {
            throw new IllegalArgumentException(port + " is not in bounds 0 < x < 65536");
        }
        this.cmdType = cmdType;
        this.addressType = addressType;
        this.host = IDN.toASCII(host);
        this.port = port;
    }
    
    public CmdType getCmdType() {
        return this.cmdType;
    }
    
    public AddressType getAddressType() {
        return this.addressType;
    }
    
    public String getHost() {
        return IDN.toUnicode(this.host);
    }
    
    public int getPort() {
        return this.port;
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) throws Exception {
        channelBuffer.writeByte(this.getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.cmdType.getByteValue());
        channelBuffer.writeByte(0);
        channelBuffer.writeByte(this.addressType.getByteValue());
        switch (this.addressType) {
            case IPv4: {
                channelBuffer.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                channelBuffer.writeShort(this.port);
                break;
            }
            case DOMAIN: {
                channelBuffer.writeByte(this.host.length());
                channelBuffer.writeBytes(this.host.getBytes("US-ASCII"));
                channelBuffer.writeShort(this.port);
                break;
            }
            case IPv6: {
                channelBuffer.writeBytes(NetUtil.createByteArrayFromIpAddressString(this.host));
                channelBuffer.writeShort(this.port);
                break;
            }
        }
    }
}
