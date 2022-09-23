// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import java.util.UUID;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import org.apache.hive.service.cli.thrift.TProtocolVersion;

public class SessionHandle extends Handle
{
    private final TProtocolVersion protocol;
    
    public SessionHandle(final TProtocolVersion protocol) {
        this.protocol = protocol;
    }
    
    public SessionHandle(final TSessionHandle tSessionHandle) {
        this(tSessionHandle, TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1);
    }
    
    public SessionHandle(final TSessionHandle tSessionHandle, final TProtocolVersion protocol) {
        super(tSessionHandle.getSessionId());
        this.protocol = protocol;
    }
    
    public UUID getSessionId() {
        return this.getHandleIdentifier().getPublicId();
    }
    
    public TSessionHandle toTSessionHandle() {
        final TSessionHandle tSessionHandle = new TSessionHandle();
        tSessionHandle.setSessionId(this.getHandleIdentifier().toTHandleIdentifier());
        return tSessionHandle;
    }
    
    public TProtocolVersion getProtocolVersion() {
        return this.protocol;
    }
    
    @Override
    public String toString() {
        return "SessionHandle [" + this.getHandleIdentifier() + "]";
    }
}
