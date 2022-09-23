// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RpcConstants
{
    public static final int AUTHORIZATION_FAILED_CALL_ID = -1;
    public static final int INVALID_CALL_ID = -2;
    public static final int CONNECTION_CONTEXT_CALL_ID = -3;
    public static final int PING_CALL_ID = -4;
    public static final byte[] DUMMY_CLIENT_ID;
    public static final int INVALID_RETRY_COUNT = -1;
    public static final ByteBuffer HEADER;
    public static final int HEADER_LEN_AFTER_HRPC_PART = 3;
    public static final byte CURRENT_VERSION = 9;
    
    private RpcConstants() {
    }
    
    static {
        DUMMY_CLIENT_ID = new byte[0];
        HEADER = ByteBuffer.wrap("hrpc".getBytes(StandardCharsets.UTF_8));
    }
}
