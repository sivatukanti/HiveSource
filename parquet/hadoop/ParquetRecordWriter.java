// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.io.IOException;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import parquet.Preconditions;
import parquet.column.ParquetProperties;
import java.util.Map;
import parquet.schema.MessageType;
import parquet.hadoop.api.WriteSupport;
import org.apache.hadoop.mapreduce.RecordWriter;

public class ParquetRecordWriter<T> extends RecordWriter<Void, T>
{
    private InternalParquetRecordWriter<T> internalWriter;
    private MemoryManager memoryManager;
    
    @Deprecated
    public ParquetRecordWriter(final ParquetFileWriter w, final WriteSupport<T> writeSupport, final MessageType schema, final Map<String, String> extraMetaData, final int blockSize, final int pageSize, final CodecFactory.BytesCompressor compressor, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion) {
        this.internalWriter = new InternalParquetRecordWriter<T>(w, writeSupport, schema, extraMetaData, blockSize, pageSize, compressor, dictionaryPageSize, enableDictionary, validating, writerVersion);
    }
    
    public ParquetRecordWriter(final ParquetFileWriter w, final WriteSupport<T> writeSupport, final MessageType schema, final Map<String, String> extraMetaData, final long blockSize, final int pageSize, final CodecFactory.BytesCompressor compressor, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion, final MemoryManager memoryManager) {
        this.internalWriter = new InternalParquetRecordWriter<T>(w, writeSupport, schema, extraMetaData, blockSize, pageSize, compressor, dictionaryPageSize, enableDictionary, validating, writerVersion);
        this.memoryManager = Preconditions.checkNotNull(memoryManager, "memoryManager");
        memoryManager.addWriter(this.internalWriter, blockSize);
    }
    
    public void close(final TaskAttemptContext context) throws IOException, InterruptedException {
        this.internalWriter.close();
        if (this.memoryManager != null) {
            this.memoryManager.removeWriter(this.internalWriter);
        }
    }
    
    public void write(final Void key, final T value) throws IOException, InterruptedException {
        this.internalWriter.write(value);
    }
}
