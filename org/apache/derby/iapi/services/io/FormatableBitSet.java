// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.derby.iapi.util.ReuseFactory;

public final class FormatableBitSet implements Formatable, Cloneable
{
    private byte[] value;
    private byte bitsInLastByte;
    private transient int lengthAsBits;
    
    private final void checkPosition(final int i) {
        if (i < 0 || this.lengthAsBits <= i) {
            throw new IllegalArgumentException("Bit position " + i + " is outside the legal range");
        }
    }
    
    private static int udiv8(final int n) {
        return n >> 3;
    }
    
    private static byte umod8(final int n) {
        return (byte)(n & 0x7);
    }
    
    private static int umul8(final int n) {
        return n << 3;
    }
    
    public FormatableBitSet() {
        this.value = ReuseFactory.getZeroLenByteArray();
    }
    
    public FormatableBitSet(final int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Bit set size " + i + " is not allowed");
        }
        this.initializeBits(i);
    }
    
    private void initializeBits(final int lengthAsBits) {
        this.value = new byte[numBytesFromBits(lengthAsBits)];
        this.bitsInLastByte = numBitsInLastByte(lengthAsBits);
        this.lengthAsBits = lengthAsBits;
    }
    
    public FormatableBitSet(final byte[] value) {
        this.value = value;
        this.bitsInLastByte = 8;
        this.lengthAsBits = this.calculateLength(value.length);
    }
    
    public FormatableBitSet(final FormatableBitSet set) {
        this.bitsInLastByte = set.bitsInLastByte;
        this.lengthAsBits = set.lengthAsBits;
        final int numBytesFromBits = numBytesFromBits(set.lengthAsBits);
        this.value = new byte[numBytesFromBits];
        if (numBytesFromBits > 0) {
            System.arraycopy(set.value, 0, this.value, 0, numBytesFromBits);
        }
    }
    
    public Object clone() {
        return new FormatableBitSet(this);
    }
    
    public boolean invariantHolds() {
        if (this.lengthAsBits > this.value.length * 8) {
            return false;
        }
        final int n = (this.lengthAsBits - 1) / 8;
        if (this.bitsInLastByte != this.lengthAsBits - 8 * n) {
            return false;
        }
        if (this.value.length == 0) {
            return true;
        }
        byte b = (byte)(this.value[n] << this.bitsInLastByte);
        for (int i = n + 1; i < this.value.length; ++i) {
            b |= this.value[i];
        }
        return b == 0;
    }
    
    public int getLengthInBytes() {
        return numBytesFromBits(this.lengthAsBits);
    }
    
    public int getLength() {
        return this.lengthAsBits;
    }
    
    private int calculateLength(final int n) {
        if (n == 0) {
            return 0;
        }
        return (n - 1) * 8 + this.bitsInLastByte;
    }
    
    public int size() {
        return this.getLength();
    }
    
    public byte[] getByteArray() {
        final int lengthInBytes = this.getLengthInBytes();
        if (this.value.length != lengthInBytes) {
            final byte[] value = new byte[lengthInBytes];
            System.arraycopy(this.value, 0, value, 0, lengthInBytes);
            this.value = value;
        }
        return this.value;
    }
    
    public void grow(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Bit set cannot grow from " + this.lengthAsBits + " to " + n + " bits");
        }
        if (n <= this.lengthAsBits) {
            return;
        }
        final int numBytesFromBits = numBytesFromBits(n);
        if (numBytesFromBits > this.value.length) {
            final byte[] value = new byte[numBytesFromBits];
            System.arraycopy(this.value, 0, value, 0, this.getLengthInBytes());
            this.value = value;
        }
        this.bitsInLastByte = numBitsInLastByte(n);
        this.lengthAsBits = n;
    }
    
    public void shrink(final int n) {
        if (n < 0 || n > this.lengthAsBits) {
            throw new IllegalArgumentException("Bit set cannot shrink from " + this.lengthAsBits + " to " + n + " bits");
        }
        final int numBytesFromBits = numBytesFromBits(n);
        this.bitsInLastByte = numBitsInLastByte(n);
        this.lengthAsBits = n;
        for (int i = numBytesFromBits; i < this.value.length; ++i) {
            this.value[i] = 0;
        }
        if (numBytesFromBits > 0) {
            final byte[] value = this.value;
            final int n2 = numBytesFromBits - 1;
            value[n2] &= (byte)(65280 >> this.bitsInLastByte);
        }
    }
    
    public boolean equals(final Object o) {
        if (o instanceof FormatableBitSet) {
            final FormatableBitSet set = (FormatableBitSet)o;
            return this.getLength() == set.getLength() && this.compare(set) == 0;
        }
        return false;
    }
    
    public int compare(final FormatableBitSet set) {
        byte[] value;
        int lengthInBytes;
        int lengthInBytes2;
        int n;
        int n2;
        for (value = set.value, lengthInBytes = set.getLengthInBytes(), lengthInBytes2 = this.getLengthInBytes(), n = 0, n2 = 0; n < lengthInBytes && n2 < lengthInBytes2 && value[n] == this.value[n2]; ++n, ++n2) {}
        if (n == lengthInBytes && n2 == lengthInBytes2) {
            if (this.getLength() == set.getLength()) {
                return 0;
            }
            return (set.getLength() < this.getLength()) ? 1 : -1;
        }
        else {
            if (n == lengthInBytes) {
                return 1;
            }
            if (n2 == lengthInBytes2) {
                return -1;
            }
            return ((this.value[n2] & 0xFF) > (value[n] & 0xFF)) ? 1 : -1;
        }
    }
    
    public int hashCode() {
        int n = 0;
        int n2 = 0;
        for (int lengthInBytes = this.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            n ^= (this.value[i] & 0xFF) << n2;
            n2 += 8;
            if (32 <= n2) {
                n2 = 0;
            }
        }
        return n;
    }
    
    public final boolean isSet(final int n) {
        this.checkPosition(n);
        return (this.value[udiv8(n)] & 128 >> umod8(n)) != 0x0;
    }
    
    public final boolean get(final int n) {
        return this.isSet(n);
    }
    
    public void set(final int n) {
        this.checkPosition(n);
        final int udiv8 = udiv8(n);
        final byte umod8 = umod8(n);
        final byte[] value = this.value;
        final int n2 = udiv8;
        value[n2] |= (byte)(128 >> umod8);
    }
    
    public void clear(final int n) {
        this.checkPosition(n);
        final int udiv8 = udiv8(n);
        final byte umod8 = umod8(n);
        final byte[] value = this.value;
        final int n2 = udiv8;
        value[n2] &= (byte)~(128 >> umod8);
    }
    
    public void clear() {
        for (int lengthInBytes = this.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            this.value[i] = 0;
        }
    }
    
    private static int numBytesFromBits(final int n) {
        return n + 7 >> 3;
    }
    
    private static byte numBitsInLastByte(final int n) {
        if (n == 0) {
            return 0;
        }
        final byte umod8 = umod8(n);
        return (byte)((umod8 != 0) ? umod8 : 8);
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer(this.getLength() * 8 * 3);
        buffer.append("{");
        int n = 1;
        for (int i = 0; i < this.getLength(); ++i) {
            if (this.isSet(i)) {
                if (n == 0) {
                    buffer.append(", ");
                }
                n = 0;
                buffer.append(i);
            }
        }
        buffer.append("}");
        return new String(buffer);
    }
    
    public static int maxBitsForSpace(final int n) {
        return (n - 4) * 8;
    }
    
    private static byte firstSet(final byte b) {
        if ((b & 0x80) != 0x0) {
            return 0;
        }
        if ((b & 0x40) != 0x0) {
            return 1;
        }
        if ((b & 0x20) != 0x0) {
            return 2;
        }
        if ((b & 0x10) != 0x0) {
            return 3;
        }
        if ((b & 0x8) != 0x0) {
            return 4;
        }
        if ((b & 0x4) != 0x0) {
            return 5;
        }
        if ((b & 0x2) != 0x0) {
            return 6;
        }
        return 7;
    }
    
    public int anySetBit() {
        for (int lengthInBytes = this.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            final byte b = this.value[i];
            if (b != 0) {
                return umul8(i) + firstSet(b);
            }
        }
        return -1;
    }
    
    public int anySetBit(int n) {
        if (++n >= this.lengthAsBits) {
            return -1;
        }
        int i = udiv8(n);
        final byte b = (byte)(this.value[i] << umod8(n));
        if (b != 0) {
            return n + firstSet(b);
        }
        final int lengthInBytes = this.getLengthInBytes();
        ++i;
        while (i < lengthInBytes) {
            final byte b2 = this.value[i];
            if (b2 != 0) {
                return umul8(i) + firstSet(b2);
            }
            ++i;
        }
        return -1;
    }
    
    public void or(final FormatableBitSet set) {
        if (set == null) {
            return;
        }
        final int length = set.getLength();
        if (length > this.getLength()) {
            this.grow(length);
        }
        for (int lengthInBytes = set.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            final byte[] value = this.value;
            final int n = i;
            value[n] |= set.value[i];
        }
    }
    
    public void and(final FormatableBitSet set) {
        if (set == null) {
            this.clear();
            return;
        }
        final int length = set.getLength();
        if (length > this.getLength()) {
            this.grow(length);
        }
        int lengthInBytes;
        int i;
        for (lengthInBytes = set.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            final byte[] value = this.value;
            final int n = i;
            value[n] &= set.value[i];
        }
        while (i < this.getLengthInBytes()) {
            this.value[i] = 0;
            ++i;
        }
    }
    
    public void xor(final FormatableBitSet set) {
        if (set == null) {
            return;
        }
        final int length = set.getLength();
        if (length > this.getLength()) {
            this.grow(length);
        }
        for (int lengthInBytes = set.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            final byte[] value = this.value;
            final int n = i;
            value[n] ^= set.value[i];
        }
    }
    
    public int getNumBitsSet() {
        int n = 0;
        for (int lengthInBytes = this.getLengthInBytes(), i = 0; i < lengthInBytes; ++i) {
            final byte b = this.value[i];
            final byte b2 = (byte)(b - (b >> 1 & 0x55));
            final byte b3 = (byte)((b2 & 0x33) + (b2 >> 2 & 0x33));
            n += (byte)((b3 & 0x7) + (b3 >> 4));
        }
        return n;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.getLength());
        final int lengthInBytes = this.getLengthInBytes();
        if (lengthInBytes > 0) {
            objectOutput.write(this.value, 0, lengthInBytes);
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        final int int1 = objectInput.readInt();
        objectInput.readFully(this.value = new byte[numBytesFromBits(int1)]);
        this.bitsInLastByte = numBitsInLastByte(int1);
        this.lengthAsBits = int1;
    }
    
    public int getTypeFormatId() {
        return 269;
    }
}
