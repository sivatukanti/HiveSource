// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Arrays;
import parquet.column.page.PageWriter;
import java.util.Set;
import parquet.column.ColumnWriter;
import java.util.TreeMap;
import parquet.column.ParquetProperties;
import parquet.column.page.PageWriteStore;
import parquet.column.ColumnDescriptor;
import java.util.Map;
import parquet.column.ColumnWriteStore;

public class ColumnWriteStoreV1 implements ColumnWriteStore
{
    private final Map<ColumnDescriptor, ColumnWriterV1> columns;
    private final PageWriteStore pageWriteStore;
    private final int pageSizeThreshold;
    private final int dictionaryPageSizeThreshold;
    private final boolean enableDictionary;
    private final ParquetProperties.WriterVersion writerVersion;
    
    public ColumnWriteStoreV1(final PageWriteStore pageWriteStore, final int pageSizeThreshold, final int dictionaryPageSizeThreshold, final boolean enableDictionary, final ParquetProperties.WriterVersion writerVersion) {
        this.columns = new TreeMap<ColumnDescriptor, ColumnWriterV1>();
        this.pageWriteStore = pageWriteStore;
        this.pageSizeThreshold = pageSizeThreshold;
        this.dictionaryPageSizeThreshold = dictionaryPageSizeThreshold;
        this.enableDictionary = enableDictionary;
        this.writerVersion = writerVersion;
    }
    
    @Override
    public ColumnWriter getColumnWriter(final ColumnDescriptor path) {
        ColumnWriterV1 column = this.columns.get(path);
        if (column == null) {
            column = this.newMemColumn(path);
            this.columns.put(path, column);
        }
        return column;
    }
    
    public Set<ColumnDescriptor> getColumnDescriptors() {
        return this.columns.keySet();
    }
    
    private ColumnWriterV1 newMemColumn(final ColumnDescriptor path) {
        final PageWriter pageWriter = this.pageWriteStore.getPageWriter(path);
        return new ColumnWriterV1(path, pageWriter, this.pageSizeThreshold, this.dictionaryPageSizeThreshold, this.enableDictionary, this.writerVersion);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<ColumnDescriptor, ColumnWriterV1> entry : this.columns.entrySet()) {
            sb.append(Arrays.toString(entry.getKey().getPath())).append(": ");
            sb.append(entry.getValue().getBufferedSizeInMemory()).append(" bytes");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public long getAllocatedSize() {
        final Collection<ColumnWriterV1> values = this.columns.values();
        long total = 0L;
        for (final ColumnWriterV1 memColumn : values) {
            total += memColumn.allocatedSize();
        }
        return total;
    }
    
    @Override
    public long getBufferedSize() {
        final Collection<ColumnWriterV1> values = this.columns.values();
        long total = 0L;
        for (final ColumnWriterV1 memColumn : values) {
            total += memColumn.getBufferedSizeInMemory();
        }
        return total;
    }
    
    @Override
    public String memUsageString() {
        final StringBuilder b = new StringBuilder("Store {\n");
        final Collection<ColumnWriterV1> values = this.columns.values();
        for (final ColumnWriterV1 memColumn : values) {
            b.append(memColumn.memUsageString(" "));
        }
        b.append("}\n");
        return b.toString();
    }
    
    public long maxColMemSize() {
        final Collection<ColumnWriterV1> values = this.columns.values();
        long max = 0L;
        for (final ColumnWriterV1 memColumn : values) {
            max = Math.max(max, memColumn.getBufferedSizeInMemory());
        }
        return max;
    }
    
    @Override
    public void flush() {
        final Collection<ColumnWriterV1> values = this.columns.values();
        for (final ColumnWriterV1 memColumn : values) {
            memColumn.flush();
        }
    }
    
    @Override
    public void endRecord() {
    }
}
