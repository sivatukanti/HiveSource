// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.io.IOException;
import java.util.HashMap;
import parquet.io.MessageColumnIO;
import parquet.io.ColumnIOFactory;
import parquet.column.page.PageWriteStore;
import parquet.Preconditions;
import parquet.column.ColumnWriteStore;
import parquet.column.ParquetProperties;
import java.util.Map;
import parquet.schema.MessageType;
import parquet.hadoop.api.WriteSupport;
import parquet.Log;

class InternalParquetRecordWriter<T>
{
    private static final Log LOG;
    private static final int MINIMUM_RECORD_COUNT_FOR_CHECK = 100;
    private static final int MAXIMUM_RECORD_COUNT_FOR_CHECK = 10000;
    private final ParquetFileWriter parquetFileWriter;
    private final WriteSupport<T> writeSupport;
    private final MessageType schema;
    private final Map<String, String> extraMetaData;
    private final long rowGroupSize;
    private long rowGroupSizeThreshold;
    private final int pageSize;
    private final CodecFactory.BytesCompressor compressor;
    private final boolean validating;
    private final ParquetProperties parquetProperties;
    private long recordCount;
    private long recordCountForNextMemCheck;
    private ColumnWriteStore columnStore;
    private ColumnChunkPageWriteStore pageStore;
    
    public InternalParquetRecordWriter(final ParquetFileWriter parquetFileWriter, final WriteSupport<T> writeSupport, final MessageType schema, final Map<String, String> extraMetaData, final long rowGroupSize, final int pageSize, final CodecFactory.BytesCompressor compressor, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion) {
        this.recordCount = 0L;
        this.recordCountForNextMemCheck = 100L;
        this.parquetFileWriter = parquetFileWriter;
        this.writeSupport = Preconditions.checkNotNull(writeSupport, "writeSupport");
        this.schema = schema;
        this.extraMetaData = extraMetaData;
        this.rowGroupSize = rowGroupSize;
        this.rowGroupSizeThreshold = rowGroupSize;
        this.pageSize = pageSize;
        this.compressor = compressor;
        this.validating = validating;
        this.parquetProperties = new ParquetProperties(dictionaryPageSize, writerVersion, enableDictionary);
        this.initStore();
    }
    
    private void initStore() {
        this.pageStore = new ColumnChunkPageWriteStore(this.compressor, this.schema, this.pageSize);
        this.columnStore = this.parquetProperties.newColumnWriteStore(this.schema, this.pageStore, this.pageSize);
        final MessageColumnIO columnIO = new ColumnIOFactory(this.validating).getColumnIO(this.schema);
        this.writeSupport.prepareForWrite(columnIO.getRecordWriter(this.columnStore));
    }
    
    public void close() throws IOException, InterruptedException {
        this.flushRowGroupToStore();
        final WriteSupport.FinalizedWriteContext finalWriteContext = this.writeSupport.finalizeWrite();
        final Map<String, String> finalMetadata = new HashMap<String, String>(this.extraMetaData);
        finalMetadata.putAll(finalWriteContext.getExtraMetaData());
        this.parquetFileWriter.end(finalMetadata);
    }
    
    public void write(final T value) throws IOException, InterruptedException {
        this.writeSupport.write(value);
        ++this.recordCount;
        this.checkBlockSizeReached();
    }
    
    private void checkBlockSizeReached() throws IOException {
        if (this.recordCount >= this.recordCountForNextMemCheck) {
            final long memSize = this.columnStore.getBufferedSize();
            if (memSize > this.rowGroupSizeThreshold) {
                InternalParquetRecordWriter.LOG.info(String.format("mem size %,d > %,d: flushing %,d records to disk.", memSize, this.rowGroupSizeThreshold, this.recordCount));
                this.flushRowGroupToStore();
                this.initStore();
                this.recordCountForNextMemCheck = Math.min(Math.max(100L, this.recordCount / 2L), 10000L);
            }
            else {
                final float recordSize = memSize / (float)this.recordCount;
                this.recordCountForNextMemCheck = Math.min(Math.max(100L, (this.recordCount + (long)(this.rowGroupSizeThreshold / recordSize)) / 2L), this.recordCount + 10000L);
                if (Log.DEBUG) {
                    InternalParquetRecordWriter.LOG.debug(String.format("Checked mem at %,d will check again at: %,d ", this.recordCount, this.recordCountForNextMemCheck));
                }
            }
        }
    }
    
    private void flushRowGroupToStore() throws IOException {
        InternalParquetRecordWriter.LOG.info(String.format("Flushing mem columnStore to file. allocated memory: %,d", this.columnStore.getAllocatedSize()));
        if (this.columnStore.getAllocatedSize() > 3L * this.rowGroupSizeThreshold) {
            InternalParquetRecordWriter.LOG.warn("Too much memory used: " + this.columnStore.memUsageString());
        }
        if (this.recordCount > 0L) {
            this.parquetFileWriter.startBlock(this.recordCount);
            this.columnStore.flush();
            this.pageStore.flushToFileWriter(this.parquetFileWriter);
            this.recordCount = 0L;
            this.parquetFileWriter.endBlock();
        }
        this.columnStore = null;
        this.pageStore = null;
    }
    
    long getRowGroupSizeThreshold() {
        return this.rowGroupSizeThreshold;
    }
    
    void setRowGroupSizeThreshold(final long rowGroupSizeThreshold) {
        this.rowGroupSizeThreshold = rowGroupSizeThreshold;
    }
    
    MessageType getSchema() {
        return this.schema;
    }
    
    static {
        LOG = Log.getLog(InternalParquetRecordWriter.class);
    }
}
