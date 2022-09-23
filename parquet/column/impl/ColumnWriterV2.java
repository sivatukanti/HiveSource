// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import parquet.column.Encoding;
import parquet.bytes.BytesInput;
import parquet.Ints;
import parquet.column.page.DictionaryPage;
import parquet.io.api.Binary;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.bytes.BytesUtils;
import parquet.column.ParquetProperties;
import parquet.column.statistics.Statistics;
import parquet.column.values.ValuesWriter;
import parquet.column.values.rle.RunLengthBitPackingHybridEncoder;
import parquet.column.page.PageWriter;
import parquet.column.ColumnDescriptor;
import parquet.Log;
import parquet.column.ColumnWriter;

final class ColumnWriterV2 implements ColumnWriter
{
    private static final Log LOG;
    private static final boolean DEBUG;
    private static final int MIN_SLAB_SIZE = 64;
    private final ColumnDescriptor path;
    private final PageWriter pageWriter;
    private RunLengthBitPackingHybridEncoder repetitionLevelColumn;
    private RunLengthBitPackingHybridEncoder definitionLevelColumn;
    private ValuesWriter dataColumn;
    private int valueCount;
    private Statistics<?> statistics;
    private long rowsWrittenSoFar;
    
    public ColumnWriterV2(final ColumnDescriptor path, final PageWriter pageWriter, final ParquetProperties parquetProps, final int pageSize) {
        this.rowsWrittenSoFar = 0L;
        this.path = path;
        this.pageWriter = pageWriter;
        this.resetStatistics();
        this.repetitionLevelColumn = new RunLengthBitPackingHybridEncoder(BytesUtils.getWidthFromMaxInt(path.getMaxRepetitionLevel()), 64, pageSize);
        this.definitionLevelColumn = new RunLengthBitPackingHybridEncoder(BytesUtils.getWidthFromMaxInt(path.getMaxDefinitionLevel()), 64, pageSize);
        final int initialSlabSize = CapacityByteArrayOutputStream.initialSlabSizeHeuristic(64, pageSize, 10);
        this.dataColumn = parquetProps.getValuesWriter(path, initialSlabSize, pageSize);
    }
    
    private void log(final Object value, final int r, final int d) {
        ColumnWriterV2.LOG.debug(this.path + " " + value + " r:" + r + " d:" + d);
    }
    
    private void resetStatistics() {
        this.statistics = Statistics.getStatsBasedOnType(this.path.getType());
    }
    
    private void definitionLevel(final int definitionLevel) {
        try {
            this.definitionLevelColumn.writeInt(definitionLevel);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("illegal definition level " + definitionLevel + " for column " + this.path, e);
        }
    }
    
    private void repetitionLevel(final int repetitionLevel) {
        try {
            this.repetitionLevelColumn.writeInt(repetitionLevel);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("illegal repetition level " + repetitionLevel + " for column " + this.path, e);
        }
    }
    
    @Override
    public void writeNull(final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(null, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.statistics.incrementNumNulls();
        ++this.valueCount;
    }
    
    @Override
    public void write(final double value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeDouble(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    @Override
    public void write(final float value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeFloat(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    @Override
    public void write(final Binary value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeBytes(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    @Override
    public void write(final boolean value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeBoolean(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    @Override
    public void write(final int value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeInteger(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    @Override
    public void write(final long value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV2.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevel(repetitionLevel);
        this.definitionLevel(definitionLevel);
        this.dataColumn.writeLong(value);
        this.statistics.updateStats(value);
        ++this.valueCount;
    }
    
    public void finalizeColumnChunk() {
        final DictionaryPage dictionaryPage = this.dataColumn.createDictionaryPage();
        if (dictionaryPage != null) {
            if (ColumnWriterV2.DEBUG) {
                ColumnWriterV2.LOG.debug("write dictionary");
            }
            try {
                this.pageWriter.writeDictionaryPage(dictionaryPage);
            }
            catch (IOException e) {
                throw new ParquetEncodingException("could not write dictionary page for " + this.path, e);
            }
            this.dataColumn.resetDictionary();
        }
    }
    
    public long getCurrentPageBufferedSize() {
        return this.repetitionLevelColumn.getBufferedSize() + this.definitionLevelColumn.getBufferedSize() + this.dataColumn.getBufferedSize();
    }
    
    public long getTotalBufferedSize() {
        return this.repetitionLevelColumn.getBufferedSize() + this.definitionLevelColumn.getBufferedSize() + this.dataColumn.getBufferedSize() + this.pageWriter.getMemSize();
    }
    
    public long allocatedSize() {
        return this.repetitionLevelColumn.getAllocatedSize() + this.definitionLevelColumn.getAllocatedSize() + this.dataColumn.getAllocatedSize() + this.pageWriter.allocatedSize();
    }
    
    public String memUsageString(final String indent) {
        final StringBuilder b = new StringBuilder(indent).append(this.path).append(" {\n");
        b.append(indent).append(" r:").append(this.repetitionLevelColumn.getAllocatedSize()).append(" bytes\n");
        b.append(indent).append(" d:").append(this.definitionLevelColumn.getAllocatedSize()).append(" bytes\n");
        b.append(this.dataColumn.memUsageString(indent + "  data:")).append("\n");
        b.append(this.pageWriter.memUsageString(indent + "  pages:")).append("\n");
        b.append(indent).append(String.format("  total: %,d/%,d", this.getTotalBufferedSize(), this.allocatedSize())).append("\n");
        b.append(indent).append("}\n");
        return b.toString();
    }
    
    public long getRowsWrittenSoFar() {
        return this.rowsWrittenSoFar;
    }
    
    public void writePage(final long rowCount) {
        final int pageRowCount = Ints.checkedCast(rowCount - this.rowsWrittenSoFar);
        this.rowsWrittenSoFar = rowCount;
        if (ColumnWriterV2.DEBUG) {
            ColumnWriterV2.LOG.debug("write page");
        }
        try {
            final BytesInput bytes = this.dataColumn.getBytes();
            final Encoding encoding = this.dataColumn.getEncoding();
            this.pageWriter.writePageV2(pageRowCount, Ints.checkedCast(this.statistics.getNumNulls()), this.valueCount, (this.path.getMaxRepetitionLevel() == 0) ? BytesInput.empty() : this.repetitionLevelColumn.toBytes(), (this.path.getMaxDefinitionLevel() == 0) ? BytesInput.empty() : this.definitionLevelColumn.toBytes(), encoding, bytes, this.statistics);
        }
        catch (IOException e) {
            throw new ParquetEncodingException("could not write page for " + this.path, e);
        }
        this.repetitionLevelColumn.reset();
        this.definitionLevelColumn.reset();
        this.dataColumn.reset();
        this.valueCount = 0;
        this.resetStatistics();
    }
    
    static {
        LOG = Log.getLog(ColumnWriterV2.class);
        DEBUG = Log.DEBUG;
    }
}
