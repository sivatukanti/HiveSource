// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import java.net.InetAddress;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface KrbTransport
{
    void sendMessage(final ByteBuffer p0) throws IOException;
    
    ByteBuffer receiveMessage() throws IOException;
    
    boolean isTcp();
    
    InetAddress getRemoteAddress();
    
    void setAttachment(final Object p0);
    
    Object getAttachment();
    
    void release();
}
