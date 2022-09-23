// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import parquet.column.statistics.Statistics;
import parquet.column.statistics.BooleanStatistics;
import parquet.column.Encoding;
import java.util.Set;
import parquet.schema.PrimitiveType;

public abstract class ColumnChunkMetaData
{
    private final ColumnChunkProperties properties;
    
    @Deprecated
    public static ColumnChunkMetaData get(final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final CompressionCodecName codec, final Set<Encoding> encodings, final long firstDataPage, final long dictionaryPageOffset, final long valueCount, final long totalSize, final long totalUncompressedSize) {
        if (positiveLongFitsInAnInt(firstDataPage) && positiveLongFitsInAnInt(dictionaryPageOffset) && positiveLongFitsInAnInt(valueCount) && positiveLongFitsInAnInt(totalSize) && positiveLongFitsInAnInt(totalUncompressedSize)) {
            return new IntColumnChunkMetaData(path, type, codec, encodings, new BooleanStatistics(), firstDataPage, dictionaryPageOffset, valueCount, totalSize, totalUncompressedSize);
        }
        return new LongColumnChunkMetaData(path, type, codec, encodings, new BooleanStatistics(), firstDataPage, dictionaryPageOffset, valueCount, totalSize, totalUncompressedSize);
    }
    
    public static ColumnChunkMetaData get(final ColumnPath path, final PrimitiveType.PrimitiveTypeName type, final CompressionCodecName codec, final Set<Encoding> encodings, final Statistics statistics, final long firstDataPage, final long dictionaryPageOffset, final long valueCount, final long totalSize, final long totalUncompressedSize) {
        if (positiveLongFitsInAnInt(firstDataPage) && positiveLongFitsInAnInt(dictionaryPageOffset) && positiveLongFitsInAnInt(valueCount) && positiveLongFitsInAnInt(totalSize) && positiveLongFitsInAnInt(totalUncompressedSize)) {
            return new IntColumnChunkMetaData(path, type, codec, encodings, statistics, firstDataPage, dictionaryPageOffset, valueCount, totalSize, totalUncompressedSize);
        }
        return new LongColumnChunkMetaData(path, type, codec, encodings, statistics, firstDataPage, dictionaryPageOffset, valueCount, totalSize, totalUncompressedSize);
    }
    
    public long getStartingPos() {
        final long dictionaryPageOffset = this.getDictionaryPageOffset();
        final long firstDataPageOffset = this.getFirstDataPageOffset();
        if (dictionaryPageOffset > 0L && dictionaryPageOffset < firstDataPageOffset) {
            return dictionaryPageOffset;
        }
        return firstDataPageOffset;
    }
    
    protected static boolean positiveLongFitsInAnInt(final long value) {
        return value >= 0L && value - 2147483648L <= 2147483647L;
    }
    
    protected ColumnChunkMetaData(final ColumnChunkProperties columnChunkProperties) {
        this.properties = columnChunkProperties;
    }
    
    public CompressionCodecName getCodec() {
        return this.properties.getCodec();
    }
    
    public ColumnPath getPath() {
        return this.properties.getPath();
    }
    
    public PrimitiveType.PrimitiveTypeName getType() {
        return this.properties.getType();
    }
    
    public abstract long getFirstDataPageOffset();
    
    public abstract long getDictionaryPageOffset();
    
    public abstract long getValueCount();
    
    public abstract long getTotalUncompressedSize();
    
    public abstract long getTotalSize();
    
    public abstract Statistics getStatistics();
    
    public Set<Encoding> getEncodings() {
        return this.properties.getEncodings();
    }
    
    @Override
    public String toString() {
        return "ColumnMetaData{" + this.properties.toString() + ", " + this.getFirstDataPageOffset() + "}";
    }
}
