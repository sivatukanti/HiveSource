// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

public abstract class SocksResponse extends SocksMessage
{
    private final SocksResponseType socksResponseType;
    
    protected SocksResponse(final SocksResponseType socksResponseType) {
        super(MessageType.RESPONSE);
        if (socksResponseType == null) {
            throw new NullPointerException("socksResponseType");
        }
        this.socksResponseType = socksResponseType;
    }
    
    public SocksResponseType getSocksResponseType() {
        return this.socksResponseType;
    }
    
    public enum SocksResponseType
    {
        INIT, 
        AUTH, 
        CMD, 
        UNKNOWN;
    }
}
