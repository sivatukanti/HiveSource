// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.message;

import java.nio.ByteBuffer;

public class AdminMessage
{
    private AdminMessageType adminMessageType;
    private ByteBuffer messageBuffer;
    
    public AdminMessage(final AdminMessageType adminMessageType) {
        this.adminMessageType = adminMessageType;
    }
    
    public AdminMessageType getAdminMessageType() {
        return this.adminMessageType;
    }
    
    public void setMessageBuffer(final ByteBuffer messageBuffer) {
        this.messageBuffer = messageBuffer;
    }
    
    public ByteBuffer getMessageBuffer() {
        return this.messageBuffer;
    }
    
    public int encodingLength() {
        return this.messageBuffer.limit();
    }
}
