// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import parquet.bytes.BytesInput;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.Log;

class BitWriter
{
    private static final Log LOG;
    private static final boolean DEBUG = false;
    private CapacityByteArrayOutputStream baos;
    private int currentByte;
    private int currentBytePosition;
    private static final int[] byteToTrueMask;
    private static final int[] byteToFalseMask;
    private boolean finished;
    
    public BitWriter(final int initialCapacity, final int pageSize) {
        this.currentByte = 0;
        this.currentBytePosition = 0;
        this.finished = false;
        this.baos = new CapacityByteArrayOutputStream(initialCapacity, pageSize);
    }
    
    public void writeBit(final boolean bit) {
        this.currentByte = setBytePosition(this.currentByte, this.currentBytePosition++, bit);
        if (this.currentBytePosition == 8) {
            this.baos.write(this.currentByte);
            this.currentByte = 0;
            this.currentBytePosition = 0;
        }
    }
    
    public void writeByte(final int val) {
        this.currentByte |= (val & 0xFF) << this.currentBytePosition;
        this.baos.write(this.currentByte);
        this.currentByte >>>= 8;
    }
    
    public void writeNBitInteger(int val, final int bitsToWrite) {
        val <<= this.currentBytePosition;
        int upperByte = this.currentBytePosition + bitsToWrite;
        this.currentByte |= val;
        while (upperByte >= 8) {
            this.baos.write(this.currentByte);
            upperByte -= 8;
            this.currentByte >>>= 8;
        }
        this.currentBytePosition = (this.currentBytePosition + bitsToWrite) % 8;
    }
    
    private String toBinary(final int val, final int alignTo) {
        String result;
        for (result = Integer.toBinaryString(val); result.length() < alignTo; result = "0" + result) {}
        return result;
    }
    
    private String toBinary(final int val) {
        return this.toBinary(val, 8);
    }
    
    public BytesInput finish() {
        if (!this.finished && this.currentBytePosition > 0) {
            this.baos.write(this.currentByte);
        }
        this.finished = true;
        return BytesInput.from(this.baos);
    }
    
    public void reset() {
        this.baos.reset();
        this.currentByte = 0;
        this.currentBytePosition = 0;
        this.finished = false;
    }
    
    private static int setBytePosition(int currentByte, final int bitOffset, final boolean newBitValue) {
        if (newBitValue) {
            currentByte |= BitWriter.byteToTrueMask[bitOffset];
        }
        else {
            currentByte &= BitWriter.byteToFalseMask[bitOffset];
        }
        return currentByte;
    }
    
    public void writeUnsignedVarint(int value) {
        while ((value & 0xFFFFFF80) != 0x0L) {
            this.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.writeByte(value & 0x7F);
    }
    
    public int getMemSize() {
        return 32 + (int)this.baos.size();
    }
    
    public int getCapacity() {
        return this.baos.getCapacity();
    }
    
    public String memUsageString(final String prefix) {
        return this.baos.memUsageString(prefix);
    }
    
    static {
        LOG = Log.getLog(BitWriter.class);
        byteToTrueMask = new int[8];
        byteToFalseMask = new int[8];
        int currentMask = 1;
        for (int i = 0; i < BitWriter.byteToTrueMask.length; ++i) {
            BitWriter.byteToTrueMask[i] = currentMask;
            BitWriter.byteToFalseMask[i] = ~currentMask;
            currentMask <<= 1;
        }
    }
}
