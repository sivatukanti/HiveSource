// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public class KadminCode
{
    public static ByteBuffer encodeMessage(final AdminMessage adminMessage) {
        final int length = adminMessage.encodingLength();
        final ByteBuffer buffer = ByteBuffer.allocate(length + 4);
        buffer.putInt(length);
        buffer.put(adminMessage.getMessageBuffer());
        buffer.flip();
        return buffer;
    }
    
    public static AdminMessage decodeMessage(final ByteBuffer buffer) throws IOException {
        final int type = buffer.getInt();
        System.out.println("type: " + type);
        final AdminMessageType adminMessageType = AdminMessageType.findType(type);
        AdminMessage adminMessage = null;
        final byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        if (adminMessageType == AdminMessageType.ADD_PRINCIPAL_REQ) {
            adminMessage = new AddPrincipalReq();
            System.out.println("check if decoding right: " + new String(ByteBuffer.wrap(bytes).array()));
        }
        else {
            if (adminMessageType != AdminMessageType.ADD_PRINCIPAL_REP) {
                throw new IOException("Unknown Admin Message Type: " + type);
            }
            adminMessage = new AddPrincipalRep();
            System.out.println("check if decoding right2: " + new String(ByteBuffer.wrap(bytes).array()));
        }
        return adminMessage;
    }
}
