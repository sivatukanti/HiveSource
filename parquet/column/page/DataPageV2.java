// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.page;

import parquet.Ints;
import parquet.column.statistics.Statistics;
import parquet.column.Encoding;
import parquet.bytes.BytesInput;

public class DataPageV2 extends DataPage
{
    private final int rowCount;
    private final int nullCount;
    private final BytesInput repetitionLevels;
    private final BytesInput definitionLevels;
    private final Encoding dataEncoding;
    private final BytesInput data;
    private final Statistics<?> statistics;
    private final boolean isCompressed;
    
    public static DataPageV2 uncompressed(final int rowCount, final int nullCount, final int valueCount, final BytesInput repetitionLevels, final BytesInput definitionLevels, final Encoding dataEncoding, final BytesInput data, final Statistics<?> statistics) {
        return new DataPageV2(rowCount, nullCount, valueCount, repetitionLevels, definitionLevels, dataEncoding, data, Ints.checkedCast(repetitionLevels.size() + definitionLevels.size() + data.size()), statistics, false);
    }
    
    public static DataPageV2 compressed(final int rowCount, final int nullCount, final int valueCount, final BytesInput repetitionLevels, final BytesInput definitionLevels, final Encoding dataEncoding, final BytesInput data, final int uncompressedSize, final Statistics<?> statistics) {
        return new DataPageV2(rowCount, nullCount, valueCount, repetitionLevels, definitionLevels, dataEncoding, data, uncompressedSize, statistics, true);
    }
    
    public DataPageV2(final int rowCount, final int nullCount, final int valueCount, final BytesInput repetitionLevels, final BytesInput definitionLevels, final Encoding dataEncoding, final BytesInput data, final int uncompressedSize, final Statistics<?> statistics, final boolean isCompressed) {
        super(Ints.checkedCast(repetitionLevels.size() + definitionLevels.size() + data.size()), uncompressedSize, valueCount);
        this.rowCount = rowCount;
        this.nullCount = nullCount;
        this.repetitionLevels = repetitionLevels;
        this.definitionLevels = definitionLevels;
        this.dataEncoding = dataEncoding;
        this.data = data;
        this.statistics = statistics;
        this.isCompressed = isCompressed;
    }
    
    public int getRowCount() {
        return this.rowCount;
    }
    
    public int getNullCount() {
        return this.nullCount;
    }
    
    public BytesInput getRepetitionLevels() {
        return this.repetitionLevels;
    }
    
    public BytesInput getDefinitionLevels() {
        return this.definitionLevels;
    }
    
    public Encoding getDataEncoding() {
        return this.dataEncoding;
    }
    
    public BytesInput getData() {
        return this.data;
    }
    
    public Statistics<?> getStatistics() {
        return this.statistics;
    }
    
    public boolean isCompressed() {
        return this.isCompressed;
    }
    
    @Override
    public <T> T accept(final Visitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "Page V2 [dl size=" + this.definitionLevels.size() + ", " + "rl size=" + this.repetitionLevels.size() + ", " + "data size=" + this.data.size() + ", " + "data enc=" + this.dataEncoding + ", " + "valueCount=" + this.getValueCount() + ", " + "rowCount=" + this.getRowCount() + ", " + "is compressed=" + this.isCompressed + ", " + "uncompressedSize=" + this.getUncompressedSize() + "]";
    }
}
