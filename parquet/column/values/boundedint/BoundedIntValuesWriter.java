// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.boundedint;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import parquet.io.ParquetEncodingException;
import parquet.Log;
import parquet.column.values.ValuesWriter;

class BoundedIntValuesWriter extends ValuesWriter
{
    private static final Log LOG;
    private int currentValue;
    private int currentValueCt;
    private boolean currentValueIsRepeated;
    private boolean thereIsABufferedValue;
    private int shouldRepeatThreshold;
    private int bitsPerValue;
    private BitWriter bitWriter;
    private boolean isFirst;
    private static final int[] byteToTrueMask;
    
    public BoundedIntValuesWriter(final int bound, final int initialCapacity, final int pageSize) {
        this.currentValue = -1;
        this.currentValueCt = -1;
        this.currentValueIsRepeated = false;
        this.thereIsABufferedValue = false;
        this.shouldRepeatThreshold = 0;
        this.isFirst = true;
        if (bound == 0) {
            throw new ParquetEncodingException("Value bound cannot be 0. Use DevNullColumnWriter instead.");
        }
        this.bitWriter = new BitWriter(initialCapacity, pageSize);
        this.bitsPerValue = (int)Math.ceil(Math.log(bound + 1) / Math.log(2.0));
        this.shouldRepeatThreshold = (this.bitsPerValue + 9) / (1 + this.bitsPerValue);
        if (Log.DEBUG) {
            BoundedIntValuesWriter.LOG.debug("init column with bit width of " + this.bitsPerValue + " and repeat threshold of " + this.shouldRepeatThreshold);
        }
    }
    
    @Override
    public long getBufferedSize() {
        return 32 + ((this.bitWriter == null) ? 0 : this.bitWriter.getMemSize());
    }
    
    @Override
    public BytesInput getBytes() {
        this.serializeCurrentValue();
        final BytesInput buf = this.bitWriter.finish();
        if (Log.DEBUG) {
            BoundedIntValuesWriter.LOG.debug("writing a buffer of size " + buf.size() + " + 4 bytes");
        }
        return BytesInput.concat(BytesInput.fromInt((int)buf.size()), buf);
    }
    
    @Override
    public void reset() {
        this.currentValue = -1;
        this.currentValueCt = -1;
        this.currentValueIsRepeated = false;
        this.thereIsABufferedValue = false;
        this.isFirst = true;
        this.bitWriter.reset();
    }
    
    @Override
    public void writeInteger(final int val) {
        if (this.currentValue == val) {
            ++this.currentValueCt;
            if (!this.currentValueIsRepeated && this.currentValueCt >= this.shouldRepeatThreshold) {
                this.currentValueIsRepeated = true;
            }
        }
        else {
            if (!this.isFirst) {
                this.serializeCurrentValue();
            }
            else {
                this.isFirst = false;
            }
            this.newCurrentValue(val);
        }
    }
    
    private void serializeCurrentValue() {
        if (this.thereIsABufferedValue) {
            if (this.currentValueIsRepeated) {
                this.bitWriter.writeBit(true);
                this.bitWriter.writeNBitInteger(this.currentValue, this.bitsPerValue);
                this.bitWriter.writeUnsignedVarint(this.currentValueCt);
            }
            else {
                for (int i = 0; i < this.currentValueCt; ++i) {
                    this.bitWriter.writeBit(false);
                    this.bitWriter.writeNBitInteger(this.currentValue, this.bitsPerValue);
                }
            }
        }
        this.thereIsABufferedValue = false;
    }
    
    private void newCurrentValue(final int val) {
        this.currentValue = val;
        this.currentValueCt = 1;
        this.currentValueIsRepeated = false;
        this.thereIsABufferedValue = true;
    }
    
    @Override
    public long getAllocatedSize() {
        return this.bitWriter.getCapacity();
    }
    
    @Override
    public Encoding getEncoding() {
        return Encoding.RLE;
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return this.bitWriter.memUsageString(prefix);
    }
    
    static {
        LOG = Log.getLog(BoundedIntValuesWriter.class);
        byteToTrueMask = new int[8];
        int currentMask = 1;
        for (int i = 0; i < BoundedIntValuesWriter.byteToTrueMask.length; ++i) {
            BoundedIntValuesWriter.byteToTrueMask[i] = currentMask;
            currentMask <<= 1;
        }
    }
}
