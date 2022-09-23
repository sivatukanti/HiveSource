// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import parquet.column.Encoding;
import java.util.Set;
import parquet.schema.PrimitiveType;
import parquet.column.statistics.Statistics;

class LongColumnChunkMetaData extends ColumnChunkMetaData
{
    private final long firstDataPageOffset;
    private final long dictionaryPageOffset;
    private final long valueCount;
    private final long totalSize;
    private final long totalUncompressedSize;
    private final Statistics statistics;
    
    LongColumnChunkMetaData(final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final CompressionCodecName codec, final Set<Encoding> encodings, final Statistics statistics, final long firstDataPageOffset, final long dictionaryPageOffset, final long valueCount, final long totalSize, final long totalUncompressedSize) {
        super(ColumnChunkProperties.get(path, type, codec, encodings));
        this.firstDataPageOffset = firstDataPageOffset;
        this.dictionaryPageOffset = dictionaryPageOffset;
        this.valueCount = valueCount;
        this.totalSize = totalSize;
        this.totalUncompressedSize = totalUncompressedSize;
        this.statistics = statistics;
    }
    
    @Override
    public long getFirstDataPageOffset() {
        return this.firstDataPageOffset;
    }
    
    @Override
    public long getDictionaryPageOffset() {
        return this.dictionaryPageOffset;
    }
    
    @Override
    public long getValueCount() {
        return this.valueCount;
    }
    
    @Override
    public long getTotalUncompressedSize() {
        return this.totalUncompressedSize;
    }
    
    @Override
    public long getTotalSize() {
        return this.totalSize;
    }
    
    @Override
    public Statistics getStatistics() {
        return this.statistics;
    }
}
