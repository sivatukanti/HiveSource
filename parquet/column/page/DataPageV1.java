// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import parquet.Ints;
import parquet.column.Encoding;
import parquet.column.statistics.Statistics;
import parquet.bytes.BytesInput;

public class DataPageV1 extends DataPage
{
    private final BytesInput bytes;
    private final Statistics<?> statistics;
    private final Encoding rlEncoding;
    private final Encoding dlEncoding;
    private final Encoding valuesEncoding;
    
    public DataPageV1(final BytesInput bytes, final int valueCount, final int uncompressedSize, final Statistics<?> stats, final Encoding rlEncoding, final Encoding dlEncoding, final Encoding valuesEncoding) {
        super(Ints.checkedCast(bytes.size()), uncompressedSize, valueCount);
        this.bytes = bytes;
        this.statistics = stats;
        this.rlEncoding = rlEncoding;
        this.dlEncoding = dlEncoding;
        this.valuesEncoding = valuesEncoding;
    }
    
    public BytesInput getBytes() {
        return this.bytes;
    }
    
    public Statistics<?> getStatistics() {
        return this.statistics;
    }
    
    public Encoding getDlEncoding() {
        return this.dlEncoding;
    }
    
    public Encoding getRlEncoding() {
        return this.rlEncoding;
    }
    
    public Encoding getValueEncoding() {
        return this.valuesEncoding;
    }
    
    @Override
    public String toString() {
        return "Page [bytes.size=" + this.bytes.size() + ", valueCount=" + this.getValueCount() + ", uncompressedSize=" + this.getUncompressedSize() + "]";
    }
    
    @Override
    public <T> T accept(final Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
