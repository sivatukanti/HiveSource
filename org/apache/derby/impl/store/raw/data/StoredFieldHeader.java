// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.DataInputUtil;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInput;
import java.io.IOException;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.OutputStream;

public final class StoredFieldHeader
{
    private static final int FIELD_INITIAL = 0;
    public static final int FIELD_NULL = 1;
    public static final int FIELD_OVERFLOW = 2;
    private static final int FIELD_NOT_NULLABLE = 4;
    public static final int FIELD_EXTENSIBLE = 8;
    public static final int FIELD_TAGGED = 16;
    protected static final int FIELD_FIXED = 32;
    public static final int FIELD_NONEXISTENT = 5;
    public static final int STORED_FIELD_HEADER_STATUS_SIZE = 1;
    
    public static final boolean isNull(final int n) {
        return (n & 0x1) == 0x1;
    }
    
    public static final boolean isOverflow(final int n) {
        return (n & 0x2) == 0x2;
    }
    
    public static final boolean isNonexistent(final int n) {
        return (n & 0x5) == 0x5;
    }
    
    public static final boolean isExtensible(final int n) {
        return (n & 0x8) == 0x8;
    }
    
    public static final boolean isNullorNonExistent(final int n) {
        return (n & 0x1) != 0x0;
    }
    
    public static final boolean isTagged(final int n) {
        return (n & 0x10) == 0x10;
    }
    
    public static final boolean isFixed(final int n) {
        return (n & 0x20) == 0x20;
    }
    
    public static final boolean isNullable(final int n) {
        return (n & 0x4) == 0x0;
    }
    
    public static final int size(final int n, final int n2, final int n3) {
        if ((n & 0x21) == 0x0) {
            if (n2 <= 63) {
                return 2;
            }
            if (n2 <= 16383) {
                return 3;
            }
            return 5;
        }
        else {
            if ((n & 0x1) != 0x0) {
                return 1;
            }
            return (n3 > 2) ? 5 : 3;
        }
    }
    
    public static final int setInitial() {
        return 0;
    }
    
    public static final int setNull(int n, final boolean b) {
        if (b) {
            n |= 0x1;
        }
        else {
            n &= 0xFFFFFFFE;
        }
        return n;
    }
    
    public static final int setOverflow(int n, final boolean b) {
        if (b) {
            n |= 0x2;
        }
        else {
            n &= 0xFFFFFFFD;
        }
        return n;
    }
    
    public static final int setNonexistent(int n) {
        n |= 0x5;
        return n;
    }
    
    public static final int setExtensible(int n, final boolean b) {
        if (b) {
            n |= 0x8;
        }
        else {
            n &= 0xFFFFFFF7;
        }
        return n;
    }
    
    public static final int setTagged(int n, final boolean b) {
        if (b) {
            n |= 0x10;
        }
        else {
            n &= 0xFFFFFFEF;
        }
        return n;
    }
    
    public static final int setFixed(int n, final boolean b) {
        if (b) {
            n |= 0x20;
        }
        else {
            n &= 0xFFFFFFDF;
        }
        return n;
    }
    
    public static final int write(final OutputStream outputStream, final int n, final int n2, final int n3) throws IOException {
        int n4 = 1;
        outputStream.write(n);
        if (isNull(n)) {
            return n4;
        }
        if (isFixed(n)) {
            if (n3 > 2) {
                for (int i = n3 - CompressedNumber.writeInt(outputStream, n2); i > 0; --i) {
                    outputStream.write(0);
                }
                n4 += n3;
            }
            else {
                outputStream.write(n2 >>> 8 & 0xFF);
                outputStream.write(n2 >>> 0 & 0xFF);
                n4 += 2;
            }
        }
        else {
            n4 += CompressedNumber.writeInt(outputStream, n2);
        }
        return n4;
    }
    
    public static final int readStatus(final ObjectInput objectInput) throws IOException {
        final int read;
        if ((read = objectInput.read()) >= 0) {
            return read;
        }
        throw new EOFException();
    }
    
    public static final int readStatus(final byte[] array, final int n) {
        return array[n];
    }
    
    public static final int readTotalFieldLength(final byte[] array, int n) throws IOException {
        if ((array[n++] & 0x1) == 0x1) {
            return 1;
        }
        final byte b = array[n];
        if ((b & 0xFFFFFFC0) == 0x0) {
            return b + 2;
        }
        if ((b & 0x80) == 0x0) {
            return ((b & 0x3F) << 8 | (array[n + 1] & 0xFF)) + 3;
        }
        return ((b & 0x7F) << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF)) + 5;
    }
    
    public static final int readFieldLengthAndSetStreamPosition(final byte[] array, int position, final int n, final int n2, final ArrayInputStream arrayInputStream) throws IOException {
        if ((n & 0x21) == 0x0) {
            int n3 = array[position++];
            if ((n3 & 0xFFFFFFC0) != 0x0) {
                if ((n3 & 0x80) == 0x0) {
                    n3 = ((n3 & 0x3F) << 8 | (array[position++] & 0xFF));
                }
                else {
                    n3 = ((n3 & 0x7F) << 24 | (array[position++] & 0xFF) << 16 | (array[position++] & 0xFF) << 8 | (array[position++] & 0xFF));
                }
            }
            arrayInputStream.setPosition(position);
            return n3;
        }
        if ((n & 0x1) != 0x0) {
            arrayInputStream.setPosition(position);
            return 0;
        }
        int n4;
        if (n2 <= 2) {
            n4 = ((array[position++] & 0xFF) << 8 | (array[position++] & 0xFF));
        }
        else {
            n4 = array[position];
            if ((n4 & 0xFFFFFFC0) != 0x0) {
                if ((n4 & 0x80) == 0x0) {
                    n4 = ((n4 & 0x3F) << 8 | (array[position + 1] & 0xFF));
                }
                else {
                    n4 = ((n4 & 0x7F) << 24 | (array[position + 1] & 0xFF) << 16 | (array[position + 2] & 0xFF) << 8 | (array[position + 3] & 0xFF));
                }
            }
            position += n2;
        }
        arrayInputStream.setPosition(position);
        return n4;
    }
    
    public static final int readFieldDataLength(final ObjectInput objectInput, final int n, final int n2) throws IOException {
        if ((n & 0x21) == 0x0) {
            return CompressedNumber.readInt(objectInput);
        }
        if ((n & 0x1) != 0x0) {
            return 0;
        }
        int int1;
        if (n2 <= 2) {
            final int read = objectInput.read();
            final int read2 = objectInput.read();
            if ((read | read2) < 0) {
                throw new EOFException();
            }
            int1 = (read << 8) + (read2 << 0);
        }
        else {
            int1 = CompressedNumber.readInt(objectInput);
            final int n3 = n2 - CompressedNumber.sizeInt(int1);
            if (n3 != 0) {
                DataInputUtil.skipFully(objectInput, n3);
            }
        }
        return int1;
    }
    
    public static String toDebugString(final int n) {
        return null;
    }
}
