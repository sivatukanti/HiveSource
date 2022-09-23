// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.values.fallback;

import parquet.io.api.Binary;
import parquet.column.page.DictionaryPage;
import parquet.column.Encoding;
import parquet.column.values.RequiresFallback;
import parquet.bytes.BytesInput;

public class FallbackValuesWriter<I extends parquet.column.values.ValuesWriter, F extends ValuesWriter> extends ValuesWriter
{
    public final I initialWriter;
    public final F fallBackWriter;
    private boolean fellBackAlready;
    private ValuesWriter currentWriter;
    private boolean initialUsedAndHadDictionary;
    private long rawDataByteSize;
    private boolean firstPage;
    
    public static <I extends parquet.column.values.ValuesWriter, F extends ValuesWriter> FallbackValuesWriter<I, F> of(final I initialWriter, final F fallBackWriter) {
        return new FallbackValuesWriter<I, F>(initialWriter, fallBackWriter);
    }
    
    public FallbackValuesWriter(final I initialWriter, final F fallBackWriter) {
        this.fellBackAlready = false;
        this.initialUsedAndHadDictionary = false;
        this.rawDataByteSize = 0L;
        this.firstPage = true;
        this.initialWriter = initialWriter;
        this.fallBackWriter = fallBackWriter;
        this.currentWriter = (ValuesWriter)initialWriter;
    }
    
    @Override
    public long getBufferedSize() {
        return this.rawDataByteSize;
    }
    
    @Override
    public BytesInput getBytes() {
        if (!this.fellBackAlready && this.firstPage) {
            final BytesInput bytes = ((ValuesWriter)this.initialWriter).getBytes();
            if (((RequiresFallback)this.initialWriter).isCompressionSatisfying(this.rawDataByteSize, bytes.size())) {
                return bytes;
            }
            this.fallBack();
        }
        return this.currentWriter.getBytes();
    }
    
    @Override
    public Encoding getEncoding() {
        final Encoding encoding = this.currentWriter.getEncoding();
        if (!this.fellBackAlready && !this.initialUsedAndHadDictionary) {
            this.initialUsedAndHadDictionary = encoding.usesDictionary();
        }
        return encoding;
    }
    
    @Override
    public void reset() {
        this.rawDataByteSize = 0L;
        this.firstPage = false;
        this.currentWriter.reset();
    }
    
    @Override
    public DictionaryPage createDictionaryPage() {
        if (this.initialUsedAndHadDictionary) {
            return ((ValuesWriter)this.initialWriter).createDictionaryPage();
        }
        return this.currentWriter.createDictionaryPage();
    }
    
    @Override
    public void resetDictionary() {
        if (this.initialUsedAndHadDictionary) {
            ((ValuesWriter)this.initialWriter).resetDictionary();
        }
        else {
            this.currentWriter.resetDictionary();
        }
        this.currentWriter = (ValuesWriter)this.initialWriter;
        this.fellBackAlready = false;
        this.initialUsedAndHadDictionary = false;
        this.firstPage = true;
    }
    
    @Override
    public long getAllocatedSize() {
        return this.currentWriter.getAllocatedSize();
    }
    
    @Override
    public String memUsageString(final String prefix) {
        return String.format("%s FallbackValuesWriter{\n%s\n%s\n%s}\n", prefix, ((ValuesWriter)this.initialWriter).memUsageString(prefix + " initial:"), this.fallBackWriter.memUsageString(prefix + " fallback:"), prefix);
    }
    
    private void checkFallback() {
        if (!this.fellBackAlready && ((RequiresFallback)this.initialWriter).shouldFallBack()) {
            this.fallBack();
        }
    }
    
    private void fallBack() {
        this.fellBackAlready = true;
        ((RequiresFallback)this.initialWriter).fallBackAllValuesTo(this.fallBackWriter);
        this.currentWriter = this.fallBackWriter;
    }
    
    @Override
    public void writeByte(final int value) {
        ++this.rawDataByteSize;
        this.currentWriter.writeByte(value);
        this.checkFallback();
    }
    
    @Override
    public void writeBytes(final Binary v) {
        this.rawDataByteSize += v.length() + 4;
        this.currentWriter.writeBytes(v);
        this.checkFallback();
    }
    
    @Override
    public void writeInteger(final int v) {
        this.rawDataByteSize += 4L;
        this.currentWriter.writeInteger(v);
        this.checkFallback();
    }
    
    @Override
    public void writeLong(final long v) {
        this.rawDataByteSize += 8L;
        this.currentWriter.writeLong(v);
        this.checkFallback();
    }
    
    @Override
    public void writeFloat(final float v) {
        this.rawDataByteSize += 4L;
        this.currentWriter.writeFloat(v);
        this.checkFallback();
    }
    
    @Override
    public void writeDouble(final double v) {
        this.rawDataByteSize += 8L;
        this.currentWriter.writeDouble(v);
        this.checkFallback();
    }
}
