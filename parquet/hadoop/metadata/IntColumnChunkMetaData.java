// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import parquet.column.Encoding;
import java.util.Set;
import parquet.schema.PrimitiveType;
import parquet.column.statistics.Statistics;

class IntColumnChunkMetaData extends ColumnChunkMetaData
{
    private final int firstDataPage;
    private final int dictionaryPageOffset;
    private final int valueCount;
    private final int totalSize;
    private final int totalUncompressedSize;
    private final Statistics statistics;
    
    IntColumnChunkMetaData(final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final CompressionCodecName codec, final Set<Encoding> encodings, final Statistics statistics, final long firstDataPage, final long dictionaryPageOffset, final long valueCount, final long totalSize, final long totalUncompressedSize) {
        super(ColumnChunkProperties.get(path, type, codec, encodings));
        this.firstDataPage = this.positiveLongToInt(firstDataPage);
        this.dictionaryPageOffset = this.positiveLongToInt(dictionaryPageOffset);
        this.valueCount = this.positiveLongToInt(valueCount);
        this.totalSize = this.positiveLongToInt(totalSize);
        this.totalUncompressedSize = this.positiveLongToInt(totalUncompressedSize);
        this.statistics = statistics;
    }
    
    private int positiveLongToInt(final long value) {
        if (!ColumnChunkMetaData.positiveLongFitsInAnInt(value)) {
            throw new IllegalArgumentException("value should be positive and fit in an int: " + value);
        }
        return (int)(value - 2147483648L);
    }
    
    private long intToPositiveLong(final int value) {
        return value + 2147483648L;
    }
    
    @Override
    public long getFirstDataPageOffset() {
        return this.intToPositiveLong(this.firstDataPage);
    }
    
    @Override
    public long getDictionaryPageOffset() {
        return this.intToPositiveLong(this.dictionaryPageOffset);
    }
    
    @Override
    public long getValueCount() {
        return this.intToPositiveLong(this.valueCount);
    }
    
    @Override
    public long getTotalUncompressedSize() {
        return this.intToPositiveLong(this.totalUncompressedSize);
    }
    
    @Override
    public long getTotalSize() {
        return this.intToPositiveLong(this.totalSize);
    }
    
    @Override
    public Statistics getStatistics() {
        return this.statistics;
    }
}
