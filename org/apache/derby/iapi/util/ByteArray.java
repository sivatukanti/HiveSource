// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public final class ByteArray
{
    private byte[] array;
    private int offset;
    private int length;
    
    public ByteArray(final byte[] array, final int offset, final int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }
    
    public ByteArray(final byte[] array) {
        this(array, 0, array.length);
    }
    
    public ByteArray() {
    }
    
    public void setBytes(final byte[] array) {
        this.array = array;
        this.offset = 0;
        this.length = array.length;
    }
    
    public void setBytes(final byte[] array, final int length) {
        this.array = array;
        this.offset = 0;
        this.length = length;
    }
    
    public void setBytes(final byte[] array, final int offset, final int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }
    
    public boolean equals(final Object o) {
        if (o instanceof ByteArray) {
            final ByteArray byteArray = (ByteArray)o;
            return equals(this.array, this.offset, this.length, byteArray.array, byteArray.offset, byteArray.length);
        }
        return false;
    }
    
    public int hashCode() {
        final byte[] array = this.array;
        int length = this.length;
        for (int i = 0; i < this.length; ++i) {
            length += array[i + this.offset];
        }
        return length;
    }
    
    public final byte[] getArray() {
        return this.array;
    }
    
    public final int getOffset() {
        return this.offset;
    }
    
    public final int getLength() {
        return this.length;
    }
    
    public final void setLength(final int length) {
        this.length = length;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        this.length = int1;
        final int n = int1;
        this.offset = 0;
        objectInput.readFully(this.array = new byte[n], 0, n);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.length);
        objectOutput.write(this.array, this.offset, this.length);
    }
    
    private static boolean equals(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) {
        if (n2 != n4) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (array[i + n] != array2[i + n3]) {
                return false;
            }
        }
        return true;
    }
}
