// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

import java.util.Iterator;
import org.jboss.netty.buffer.ChannelBuffer;
import java.util.Collections;
import java.util.List;

public final class SocksInitRequest extends SocksRequest
{
    private final List<AuthScheme> authSchemes;
    
    public SocksInitRequest(final List<AuthScheme> authSchemes) {
        super(SocksRequestType.INIT);
        if (authSchemes == null) {
            throw new NullPointerException("authSchemes");
        }
        this.authSchemes = authSchemes;
    }
    
    public List<AuthScheme> getAuthSchemes() {
        return Collections.unmodifiableList((List<? extends AuthScheme>)this.authSchemes);
    }
    
    @Override
    public void encodeAsByteBuf(final ChannelBuffer channelBuffer) {
        channelBuffer.writeByte(this.getProtocolVersion().getByteValue());
        channelBuffer.writeByte(this.authSchemes.size());
        for (final AuthScheme authScheme : this.authSchemes) {
            channelBuffer.writeByte(authScheme.getByteValue());
        }
    }
}
