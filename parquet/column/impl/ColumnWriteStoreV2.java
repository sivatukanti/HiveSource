// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import java.util.Arrays;
import java.util.Set;
import parquet.column.ColumnWriter;
import parquet.column.page.PageWriter;
import java.util.Iterator;
import java.util.Collections;
import java.util.TreeMap;
import parquet.column.ParquetProperties;
import parquet.column.page.PageWriteStore;
import parquet.schema.MessageType;
import java.util.Collection;
import parquet.column.ColumnDescriptor;
import java.util.Map;
import parquet.column.ColumnWriteStore;

public class ColumnWriteStoreV2 implements ColumnWriteStore
{
    private static final int MINIMUM_RECORD_COUNT_FOR_CHECK = 100;
    private static final int MAXIMUM_RECORD_COUNT_FOR_CHECK = 10000;
    private static final float THRESHOLD_TOLERANCE_RATIO = 0.1f;
    private final Map<ColumnDescriptor, ColumnWriterV2> columns;
    private final Collection<ColumnWriterV2> writers;
    private long rowCount;
    private long rowCountForNextSizeCheck;
    private final long thresholdTolerance;
    private int pageSizeThreshold;
    
    public ColumnWriteStoreV2(final MessageType schema, final PageWriteStore pageWriteStore, final int pageSizeThreshold, final ParquetProperties parquetProps) {
        this.rowCountForNextSizeCheck = 100L;
        this.pageSizeThreshold = pageSizeThreshold;
        this.thresholdTolerance = (long)(pageSizeThreshold * 0.1f);
        final Map<ColumnDescriptor, ColumnWriterV2> mcolumns = new TreeMap<ColumnDescriptor, ColumnWriterV2>();
        for (final ColumnDescriptor path : schema.getColumns()) {
            final PageWriter pageWriter = pageWriteStore.getPageWriter(path);
            mcolumns.put(path, new ColumnWriterV2(path, pageWriter, parquetProps, pageSizeThreshold));
        }
        this.columns = Collections.unmodifiableMap((Map<? extends ColumnDescriptor, ? extends ColumnWriterV2>)mcolumns);
        this.writers = this.columns.values();
    }
    
    @Override
    public ColumnWriter getColumnWriter(final ColumnDescriptor path) {
        return this.columns.get(path);
    }
    
    public Set<ColumnDescriptor> getColumnDescriptors() {
        return this.columns.keySet();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<ColumnDescriptor, ColumnWriterV2> entry : this.columns.entrySet()) {
            sb.append(Arrays.toString(entry.getKey().getPath())).append(": ");
            sb.append(entry.getValue().getTotalBufferedSize()).append(" bytes");
            sb.append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public long getAllocatedSize() {
        long total = 0L;
        for (final ColumnWriterV2 memColumn : this.columns.values()) {
            total += memColumn.allocatedSize();
        }
        return total;
    }
    
    @Override
    public long getBufferedSize() {
        long total = 0L;
        for (final ColumnWriterV2 memColumn : this.columns.values()) {
            total += memColumn.getTotalBufferedSize();
        }
        return total;
    }
    
    @Override
    public void flush() {
        for (final ColumnWriterV2 memColumn : this.columns.values()) {
            final long rows = this.rowCount - memColumn.getRowsWrittenSoFar();
            if (rows > 0L) {
                memColumn.writePage(this.rowCount);
            }
            memColumn.finalizeColumnChunk();
        }
    }
    
    @Override
    public String memUsageString() {
        final StringBuilder b = new StringBuilder("Store {\n");
        for (final ColumnWriterV2 memColumn : this.columns.values()) {
            b.append(memColumn.memUsageString(" "));
        }
        b.append("}\n");
        return b.toString();
    }
    
    @Override
    public void endRecord() {
        ++this.rowCount;
        if (this.rowCount >= this.rowCountForNextSizeCheck) {
            this.sizeCheck();
        }
    }
    
    private void sizeCheck() {
        long minRecordToWait = Long.MAX_VALUE;
        for (final ColumnWriterV2 writer : this.writers) {
            final long usedMem = writer.getCurrentPageBufferedSize();
            final long rows = this.rowCount - writer.getRowsWrittenSoFar();
            long remainingMem = this.pageSizeThreshold - usedMem;
            if (remainingMem <= this.thresholdTolerance) {
                writer.writePage(this.rowCount);
                remainingMem = this.pageSizeThreshold;
            }
            final long rowsToFillPage = (usedMem == 0L) ? 10000L : ((long)(float)rows / usedMem * remainingMem);
            if (rowsToFillPage < minRecordToWait) {
                minRecordToWait = rowsToFillPage;
            }
        }
        if (minRecordToWait == Long.MAX_VALUE) {
            minRecordToWait = 100L;
        }
        this.rowCountForNextSizeCheck = this.rowCount + Math.min(Math.max(minRecordToWait / 2L, 100L), 10000L);
    }
}
