// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.socks;

public abstract class SocksRequest extends SocksMessage
{
    private final SocksRequestType socksRequestType;
    
    protected SocksRequest(final SocksRequestType socksRequestType) {
        super(MessageType.REQUEST);
        if (socksRequestType == null) {
            throw new NullPointerException("socksRequestType");
        }
        this.socksRequestType = socksRequestType;
    }
    
    public SocksRequestType getSocksRequestType() {
        return this.socksRequestType;
    }
    
    public enum SocksRequestType
    {
        INIT, 
        AUTH, 
        CMD, 
        UNKNOWN;
    }
}
