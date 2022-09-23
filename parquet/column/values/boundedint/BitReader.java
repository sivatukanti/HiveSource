// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import parquet.io.ParquetDecodingException;
import java.io.IOException;

class BitReader
{
    private int currentByte;
    private int currentPosition;
    private byte[] buf;
    private int currentBufferPosition;
    private static final int[] byteGetValueMask;
    private static final int[] readMask;
    private int endBufferPosistion;
    
    BitReader() {
        this.currentByte = 0;
        this.currentPosition = 8;
        this.currentBufferPosition = 0;
    }
    
    public void prepare(final byte[] buf, final int offset, final int length) {
        this.buf = buf;
        this.endBufferPosistion = offset + length;
        this.currentByte = 0;
        this.currentPosition = 8;
        this.currentBufferPosition = offset;
    }
    
    private static boolean extractBit(final int val, final int bit) {
        return (val & BitReader.byteGetValueMask[bit]) != 0x0;
    }
    
    public int readNBitInteger(final int bitsPerValue) {
        int bits = bitsPerValue + this.currentPosition;
        int currentValue = this.currentByte >>> this.currentPosition;
        int toShift = 8 - this.currentPosition;
        while (bits >= 8) {
            this.currentByte = this.getNextByte();
            currentValue |= this.currentByte << toShift;
            toShift += 8;
            bits -= 8;
        }
        currentValue &= BitReader.readMask[bitsPerValue];
        this.currentPosition = (bitsPerValue + this.currentPosition) % 8;
        return currentValue;
    }
    
    private int getNextByte() {
        if (this.currentBufferPosition < this.endBufferPosistion) {
            return this.buf[this.currentBufferPosition++] & 0xFF;
        }
        return 0;
    }
    
    public boolean readBit() throws IOException {
        if (this.currentPosition == 8) {
            this.currentByte = this.getNextByte();
            this.currentPosition = 0;
        }
        return extractBit(this.currentByte, this.currentPosition++);
    }
    
    public int readByte() {
        this.currentByte |= this.getNextByte() << 8;
        final int value = this.currentByte >>> this.currentPosition & 0xFF;
        this.currentByte >>>= 8;
        return value;
    }
    
    public int readUnsignedVarint() throws IOException {
        int value = 0;
        int i = 0;
        int b;
        while (((b = this.readByte()) & 0x80) != 0x0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 35) {
                throw new ParquetDecodingException("Variable length quantity is too long");
            }
        }
        return value | b << i;
    }
    
    static {
        byteGetValueMask = new int[8];
        readMask = new int[32];
        int currentMask = 1;
        for (int i = 0; i < BitReader.byteGetValueMask.length; ++i) {
            BitReader.byteGetValueMask[i] = currentMask;
            currentMask <<= 1;
        }
        currentMask = 0;
        for (int i = 0; i < BitReader.readMask.length; ++i) {
            BitReader.readMask[i] = currentMask;
            currentMask <<= 1;
            ++currentMask;
        }
    }
}
