// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.charset.CharsetEncoder;

public final class SocksAuthRequest extends SocksRequest
{
    private static final CharsetEncoder asciiEncoder;
    private static final SubnegotiationVersion SUBNEGOTIATION_VERSION;
    private final String username;
    private final String password;
    
    public SocksAuthRequest(final String username, final String password) {
        super(SocksRequestType.AUTH);
        if (username == null) {
            throw new NullPointerException("username");
        }
        if (password == null) {
            throw new NullPointerException("password");
        }
        if (!SocksAuthRequest.asciiEncoder.canEncode(username) || !SocksAuthRequest.asciiEncoder.canEncode(password)) {
            throw new IllegalArgumentException("username: " + username + " or password: **** values should be in pure ascii");
        }
        if (username.length() > 255) {
            throw new IllegalArgumentException("username: " + username + " exceeds 255 char limit");
        }
        if (password.length() > 255) {
            throw new IllegalArgumentException("password: **** exceeds 255 char limit");
        }
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) throws Exception {
        channelBuffer.writeByte(SocksAuthRequest.SUBNEGOTIATION_VERSION.getByteValue());
        channelBuffer.writeByte(this.username.length());
        channelBuffer.writeBytes(this.username.getBytes("US-ASCII"));
        channelBuffer.writeByte(this.password.length());
        channelBuffer.writeBytes(this.password.getBytes("US-ASCII"));
    }
    
    static {
        asciiEncoder = CharsetUtil.getEncoder(CharsetUtil.US_ASCII);
        SUBNEGOTIATION_VERSION = SubnegotiationVersion.AUTH_PASSWORD;
    }
}
