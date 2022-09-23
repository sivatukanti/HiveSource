// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.IOException;
import java.io.ObjectOutput;

public final class CharStreamHeaderGenerator implements StreamHeaderGenerator
{
    private static final int MAX_ENCODABLE_LENGTH = 65535;
    
    public boolean expectsCharCount() {
        return false;
    }
    
    public int generateInto(final byte[] array, final int n, final long n2) {
        if (n2 > 0L && n2 <= 65535L) {
            array[n] = (byte)(n2 >>> 8);
            array[n + 1] = (byte)(n2 >>> 0);
        }
        else {
            array[n + 1] = (array[n] = 0);
        }
        return 2;
    }
    
    public int generateInto(final ObjectOutput objectOutput, final long n) throws IOException {
        if (n > 0L && n <= 65535L) {
            objectOutput.writeByte((byte)(n >>> 8));
            objectOutput.writeByte((byte)(n >>> 0));
        }
        else {
            objectOutput.writeByte(0);
            objectOutput.writeByte(0);
        }
        return 2;
    }
    
    public int writeEOF(final byte[] array, final int n, final long n2) {
        if (n2 < 0L || n2 > 65535L) {
            System.arraycopy(CharStreamHeaderGenerator.DERBY_EOF_MARKER, 0, array, n, CharStreamHeaderGenerator.DERBY_EOF_MARKER.length);
            return CharStreamHeaderGenerator.DERBY_EOF_MARKER.length;
        }
        return 0;
    }
    
    public int writeEOF(final ObjectOutput objectOutput, final long n) throws IOException {
        if (n < 0L || n > 65535L) {
            objectOutput.write(CharStreamHeaderGenerator.DERBY_EOF_MARKER);
            return CharStreamHeaderGenerator.DERBY_EOF_MARKER.length;
        }
        return 0;
    }
    
    public int getMaxHeaderLength() {
        return 2;
    }
}
