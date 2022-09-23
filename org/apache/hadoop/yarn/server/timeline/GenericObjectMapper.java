// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ObjectReader;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GenericObjectMapper
{
    private static final byte[] EMPTY_BYTES;
    public static final ObjectReader OBJECT_READER;
    public static final ObjectWriter OBJECT_WRITER;
    
    public static byte[] write(final Object o) throws IOException {
        if (o == null) {
            return GenericObjectMapper.EMPTY_BYTES;
        }
        return GenericObjectMapper.OBJECT_WRITER.writeValueAsBytes(o);
    }
    
    public static Object read(final byte[] b) throws IOException {
        return read(b, 0);
    }
    
    public static Object read(final byte[] b, final int offset) throws IOException {
        if (b == null || b.length == 0) {
            return null;
        }
        return GenericObjectMapper.OBJECT_READER.readValue(b, offset, b.length - offset);
    }
    
    public static byte[] writeReverseOrderedLong(final long l) {
        final byte[] b = new byte[8];
        return writeReverseOrderedLong(l, b, 0);
    }
    
    public static byte[] writeReverseOrderedLong(final long l, final byte[] b, final int offset) {
        b[offset] = (byte)(0x7FL ^ (l >> 56 & 0xFFL));
        for (int i = offset + 1; i < offset + 7; ++i) {
            b[i] = (byte)(0xFFL ^ (l >> 8 * (7 - i) & 0xFFL));
        }
        b[offset + 7] = (byte)(0xFFL ^ (l & 0xFFL));
        return b;
    }
    
    public static long readReverseOrderedLong(final byte[] b, final int offset) {
        long l = b[offset] & 0xFF;
        for (int i = 1; i < 8; ++i) {
            l <<= 8;
            l |= (b[offset + i] & 0xFF);
        }
        return l ^ Long.MAX_VALUE;
    }
    
    static {
        EMPTY_BYTES = new byte[0];
        final ObjectMapper mapper = new ObjectMapper();
        OBJECT_READER = mapper.reader(Object.class);
        OBJECT_WRITER = mapper.writer();
    }
}
