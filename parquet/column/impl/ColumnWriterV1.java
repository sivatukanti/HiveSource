// 
// Decompiled by Procyon v0.5.36
// 

package parquet.column.impl;

import parquet.column.page.DictionaryPage;
import java.io.IOException;
import parquet.io.ParquetEncodingException;
import parquet.bytes.BytesInput;
import parquet.io.api.Binary;
import parquet.bytes.CapacityByteArrayOutputStream;
import parquet.column.ParquetProperties;
import parquet.column.statistics.Statistics;
import parquet.column.values.ValuesWriter;
import parquet.column.page.PageWriter;
import parquet.column.ColumnDescriptor;
import parquet.Log;
import parquet.column.ColumnWriter;

final class ColumnWriterV1 implements ColumnWriter
{
    private static final Log LOG;
    private static final boolean DEBUG;
    private static final int INITIAL_COUNT_FOR_SIZE_CHECK = 100;
    private static final int MIN_SLAB_SIZE = 64;
    private final ColumnDescriptor path;
    private final PageWriter pageWriter;
    private final long pageSizeThreshold;
    private ValuesWriter repetitionLevelColumn;
    private ValuesWriter definitionLevelColumn;
    private ValuesWriter dataColumn;
    private int valueCount;
    private int valueCountForNextSizeCheck;
    private Statistics statistics;
    
    public ColumnWriterV1(final ColumnDescriptor path, final PageWriter pageWriter, final int pageSizeThreshold, final int dictionaryPageSizeThreshold, final boolean enableDictionary, final ParquetProperties.WriterVersion writerVersion) {
        this.path = path;
        this.pageWriter = pageWriter;
        this.pageSizeThreshold = pageSizeThreshold;
        this.valueCountForNextSizeCheck = 100;
        this.resetStatistics();
        final ParquetProperties parquetProps = new ParquetProperties(dictionaryPageSizeThreshold, writerVersion, enableDictionary);
        this.repetitionLevelColumn = ParquetProperties.getColumnDescriptorValuesWriter(path.getMaxRepetitionLevel(), 64, pageSizeThreshold);
        this.definitionLevelColumn = ParquetProperties.getColumnDescriptorValuesWriter(path.getMaxDefinitionLevel(), 64, pageSizeThreshold);
        final int initialSlabSize = CapacityByteArrayOutputStream.initialSlabSizeHeuristic(64, pageSizeThreshold, 10);
        this.dataColumn = parquetProps.getValuesWriter(path, initialSlabSize, pageSizeThreshold);
    }
    
    private void log(final Object value, final int r, final int d) {
        ColumnWriterV1.LOG.debug(this.path + " " + value + " r:" + r + " d:" + d);
    }
    
    private void resetStatistics() {
        this.statistics = Statistics.getStatsBasedOnType(this.path.getType());
    }
    
    private void accountForValueWritten() {
        ++this.valueCount;
        if (this.valueCount > this.valueCountForNextSizeCheck) {
            final long memSize = this.repetitionLevelColumn.getBufferedSize() + this.definitionLevelColumn.getBufferedSize() + this.dataColumn.getBufferedSize();
            if (memSize > this.pageSizeThreshold) {
                this.valueCountForNextSizeCheck = this.valueCount / 2;
                this.writePage();
            }
            else {
                this.valueCountForNextSizeCheck = (int)(this.valueCount + this.valueCount * (float)this.pageSizeThreshold / memSize) / 2 + 1;
            }
        }
    }
    
    private void updateStatisticsNumNulls() {
        this.statistics.incrementNumNulls();
    }
    
    private void updateStatistics(final int value) {
        this.statistics.updateStats(value);
    }
    
    private void updateStatistics(final long value) {
        this.statistics.updateStats(value);
    }
    
    private void updateStatistics(final float value) {
        this.statistics.updateStats(value);
    }
    
    private void updateStatistics(final double value) {
        this.statistics.updateStats(value);
    }
    
    private void updateStatistics(final Binary value) {
        this.statistics.updateStats(value);
    }
    
    private void updateStatistics(final boolean value) {
        this.statistics.updateStats(value);
    }
    
    private void writePage() {
        if (ColumnWriterV1.DEBUG) {
            ColumnWriterV1.LOG.debug("write page");
        }
        try {
            this.pageWriter.writePage(BytesInput.concat(this.repetitionLevelColumn.getBytes(), this.definitionLevelColumn.getBytes(), this.dataColumn.getBytes()), this.valueCount, this.statistics, this.repetitionLevelColumn.getEncoding(), this.definitionLevelColumn.getEncoding(), this.dataColumn.getEncoding());
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
    
    @Override
    public void writeNull(final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(null, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.updateStatisticsNumNulls();
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final double value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeDouble(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final float value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeFloat(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final Binary value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeBytes(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final boolean value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeBoolean(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final int value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeInteger(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    @Override
    public void write(final long value, final int repetitionLevel, final int definitionLevel) {
        if (ColumnWriterV1.DEBUG) {
            this.log(value, repetitionLevel, definitionLevel);
        }
        this.repetitionLevelColumn.writeInteger(repetitionLevel);
        this.definitionLevelColumn.writeInteger(definitionLevel);
        this.dataColumn.writeLong(value);
        this.updateStatistics(value);
        this.accountForValueWritten();
    }
    
    public void flush() {
        if (this.valueCount > 0) {
            this.writePage();
        }
        final DictionaryPage dictionaryPage = this.dataColumn.createDictionaryPage();
        if (dictionaryPage != null) {
            if (ColumnWriterV1.DEBUG) {
                ColumnWriterV1.LOG.debug("write dictionary");
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
    
    public long getBufferedSizeInMemory() {
        return this.repetitionLevelColumn.getBufferedSize() + this.definitionLevelColumn.getBufferedSize() + this.dataColumn.getBufferedSize() + this.pageWriter.getMemSize();
    }
    
    public long allocatedSize() {
        return this.repetitionLevelColumn.getAllocatedSize() + this.definitionLevelColumn.getAllocatedSize() + this.dataColumn.getAllocatedSize() + this.pageWriter.allocatedSize();
    }
    
    public String memUsageString(final String indent) {
        final StringBuilder b = new StringBuilder(indent).append(this.path).append(" {\n");
        b.append(this.repetitionLevelColumn.memUsageString(indent + "  r:")).append("\n");
        b.append(this.definitionLevelColumn.memUsageString(indent + "  d:")).append("\n");
        b.append(this.dataColumn.memUsageString(indent + "  data:")).append("\n");
        b.append(this.pageWriter.memUsageString(indent + "  pages:")).append("\n");
        b.append(indent).append(String.format("  total: %,d/%,d", this.getBufferedSizeInMemory(), this.allocatedSize())).append("\n");
        b.append(indent).append("}\n");
        return b.toString();
    }
    
    static {
        LOG = Log.getLog(ColumnWriterV1.class);
        DEBUG = Log.DEBUG;
    }
}
