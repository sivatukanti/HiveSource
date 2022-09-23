// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.util;

import org.apache.kerby.asn1.Tag;
import java.nio.ByteBuffer;

public final class Asn1Util
{
    private Asn1Util() {
    }
    
    public static int lengthOfBodyLength(final int bodyLength) {
        int length = 1;
        if (bodyLength > 127) {
            for (int payload = bodyLength; payload != 0; payload >>= 8, ++length) {}
        }
        return length;
    }
    
    public static int lengthOfTagLength(int tagNo) {
        int length = 1;
        if (tagNo >= 31) {
            if (tagNo < 128) {
                ++length;
            }
            else {
                ++length;
                do {
                    tagNo >>= 7;
                    ++length;
                } while (tagNo > 127);
            }
        }
        return length;
    }
    
    public static void encodeTag(final ByteBuffer buffer, final Tag tag) {
        final int flags = tag.tagFlags();
        int tagNo = tag.tagNo();
        if (tagNo < 31) {
            buffer.put((byte)(flags | tagNo));
        }
        else {
            buffer.put((byte)(flags | 0x1F));
            if (tagNo < 128) {
                buffer.put((byte)tagNo);
            }
            else {
                final byte[] tmpBytes = new byte[5];
                int iPut = tmpBytes.length;
                tmpBytes[--iPut] = (byte)(tagNo & 0x7F);
                do {
                    tagNo >>= 7;
                    tmpBytes[--iPut] = (byte)((tagNo & 0x7F) | 0x80);
                } while (tagNo > 127);
                buffer.put(tmpBytes, iPut, tmpBytes.length - iPut);
            }
        }
    }
    
    public static void encodeLength(final ByteBuffer buffer, final int bodyLength) {
        if (bodyLength < 128) {
            buffer.put((byte)bodyLength);
        }
        else {
            int length = 0;
            for (int payload = bodyLength; payload != 0; payload >>= 8, ++length) {}
            buffer.put((byte)(length | 0x80));
            int payload = bodyLength;
            for (int i = length - 1; i >= 0; --i) {
                buffer.put((byte)(payload >> i * 8));
            }
        }
    }
    
    public static byte[] readAllLeftBytes(final ByteBuffer buffer) {
        final byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
