// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ClientId
{
    public static final int BYTE_LENGTH = 16;
    private static final int shiftWidth = 8;
    
    public static byte[] getClientId() {
        final UUID uuid = UUID.randomUUID();
        final ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }
    
    public static String toString(final byte[] clientId) {
        if (clientId == null || clientId.length == 0) {
            return "";
        }
        Preconditions.checkArgument(clientId.length == 16);
        final long msb = getMsb(clientId);
        final long lsb = getLsb(clientId);
        return new UUID(msb, lsb).toString();
    }
    
    public static long getMsb(final byte[] clientId) {
        long msb = 0L;
        for (int i = 0; i < 8; ++i) {
            msb = (msb << 8 | (long)(clientId[i] & 0xFF));
        }
        return msb;
    }
    
    public static long getLsb(final byte[] clientId) {
        long lsb = 0L;
        for (int i = 8; i < 16; ++i) {
            lsb = (lsb << 8 | (long)(clientId[i] & 0xFF));
        }
        return lsb;
    }
    
    public static byte[] toBytes(final String id) {
        if (id == null || "".equals(id)) {
            return new byte[0];
        }
        final UUID uuid = UUID.fromString(id);
        final ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
        buf.putLong(uuid.getMostSignificantBits());
        buf.putLong(uuid.getLeastSignificantBits());
        return buf.array();
    }
}
