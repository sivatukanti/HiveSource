// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.rangecoder;

import java.io.IOException;
import org.tukaani.xz.CorruptedInputException;
import java.io.DataInputStream;

public final class RangeDecoder extends RangeCoder
{
    private static final int INIT_SIZE = 5;
    private final byte[] buf;
    private int pos;
    private int end;
    private int range;
    private int code;
    
    public RangeDecoder(final int n) {
        this.pos = 0;
        this.end = 0;
        this.range = 0;
        this.code = 0;
        this.buf = new byte[n - 5];
    }
    
    public void prepareInputBuffer(final DataInputStream dataInputStream, final int n) throws IOException {
        if (n < 5) {
            throw new CorruptedInputException();
        }
        if (dataInputStream.readUnsignedByte() != 0) {
            throw new CorruptedInputException();
        }
        this.code = dataInputStream.readInt();
        this.range = -1;
        this.pos = 0;
        this.end = n - 5;
        dataInputStream.readFully(this.buf, 0, this.end);
    }
    
    public boolean isInBufferOK() {
        return this.pos <= this.end;
    }
    
    public boolean isFinished() {
        return this.pos == this.end && this.code == 0;
    }
    
    public void normalize() throws IOException {
        if ((this.range & 0xFF000000) == 0x0) {
            try {
                this.code = (this.code << 8 | (this.buf[this.pos++] & 0xFF));
                this.range <<= 8;
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                throw new CorruptedInputException();
            }
        }
    }
    
    public int decodeBit(final short[] array, final int n) throws IOException {
        this.normalize();
        final short n2 = array[n];
        final int range = (this.range >>> 11) * n2;
        int n3;
        if ((this.code ^ Integer.MIN_VALUE) < (range ^ Integer.MIN_VALUE)) {
            this.range = range;
            array[n] = (short)(n2 + (2048 - n2 >>> 5));
            n3 = 0;
        }
        else {
            this.range -= range;
            this.code -= range;
            array[n] = (short)(n2 - (n2 >>> 5));
            n3 = 1;
        }
        return n3;
    }
    
    public int decodeBitTree(final short[] array) throws IOException {
        int i = 1;
        do {
            i = (i << 1 | this.decodeBit(array, i));
        } while (i < array.length);
        return i - array.length;
    }
    
    public int decodeReverseBitTree(final short[] array) throws IOException {
        int i = 1;
        int n = 0;
        int n2 = 0;
        do {
            final int decodeBit = this.decodeBit(array, i);
            i = (i << 1 | decodeBit);
            n2 |= decodeBit << n++;
        } while (i < array.length);
        return n2;
    }
    
    public int decodeDirectBits(int n) throws IOException {
        int n2 = 0;
        do {
            this.normalize();
            this.range >>>= 1;
            final int n3 = this.code - this.range >>> 31;
            this.code -= (this.range & n3 - 1);
            n2 = (n2 << 1 | 1 - n3);
        } while (--n != 0);
        return n2;
    }
}
