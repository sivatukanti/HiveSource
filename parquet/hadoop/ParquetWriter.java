// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import parquet.schema.MessageType;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import parquet.hadoop.api.WriteSupport;
import org.apache.hadoop.fs.Path;
import parquet.column.ParquetProperties;
import parquet.hadoop.metadata.CompressionCodecName;
import java.io.Closeable;

public class ParquetWriter<T> implements Closeable
{
    public static final int DEFAULT_BLOCK_SIZE = 134217728;
    public static final int DEFAULT_PAGE_SIZE = 1048576;
    public static final CompressionCodecName DEFAULT_COMPRESSION_CODEC_NAME;
    public static final boolean DEFAULT_IS_DICTIONARY_ENABLED = true;
    public static final boolean DEFAULT_IS_VALIDATING_ENABLED = false;
    public static final ParquetProperties.WriterVersion DEFAULT_WRITER_VERSION;
    private final InternalParquetRecordWriter<T> writer;
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize) throws IOException {
        this(file, writeSupport, compressionCodecName, blockSize, pageSize, true, false);
    }
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize, final boolean enableDictionary, final boolean validating) throws IOException {
        this(file, writeSupport, compressionCodecName, blockSize, pageSize, pageSize, enableDictionary, validating);
    }
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating) throws IOException {
        this(file, writeSupport, compressionCodecName, blockSize, pageSize, dictionaryPageSize, enableDictionary, validating, ParquetWriter.DEFAULT_WRITER_VERSION);
    }
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion) throws IOException {
        this(file, writeSupport, compressionCodecName, blockSize, pageSize, dictionaryPageSize, enableDictionary, validating, writerVersion, new Configuration());
    }
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion, final Configuration conf) throws IOException {
        this(file, ParquetFileWriter.Mode.CREATE, writeSupport, compressionCodecName, blockSize, pageSize, dictionaryPageSize, enableDictionary, validating, writerVersion, conf);
    }
    
    public ParquetWriter(final Path file, final ParquetFileWriter.Mode mode, final WriteSupport<T> writeSupport, final CompressionCodecName compressionCodecName, final int blockSize, final int pageSize, final int dictionaryPageSize, final boolean enableDictionary, final boolean validating, final ParquetProperties.WriterVersion writerVersion, final Configuration conf) throws IOException {
        final WriteSupport.WriteContext writeContext = writeSupport.init(conf);
        final MessageType schema = writeContext.getSchema();
        final ParquetFileWriter fileWriter = new ParquetFileWriter(conf, schema, file, mode);
        fileWriter.start();
        final CodecFactory codecFactory = new CodecFactory(conf);
        final CodecFactory.BytesCompressor compressor = codecFactory.getCompressor(compressionCodecName, 0);
        this.writer = new InternalParquetRecordWriter<T>(fileWriter, writeSupport, schema, writeContext.getExtraMetaData(), blockSize, pageSize, compressor, dictionaryPageSize, enableDictionary, validating, writerVersion);
    }
    
    public ParquetWriter(final Path file, final WriteSupport<T> writeSupport) throws IOException {
        this(file, writeSupport, ParquetWriter.DEFAULT_COMPRESSION_CODEC_NAME, 134217728, 1048576);
    }
    
    public ParquetWriter(final Path file, final Configuration conf, final WriteSupport<T> writeSupport) throws IOException {
        this(file, writeSupport, ParquetWriter.DEFAULT_COMPRESSION_CODEC_NAME, 134217728, 1048576, 1048576, true, false, ParquetWriter.DEFAULT_WRITER_VERSION, conf);
    }
    
    public void write(final T object) throws IOException {
        try {
            this.writer.write(object);
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.writer.close();
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
    
    static {
        DEFAULT_COMPRESSION_CODEC_NAME = CompressionCodecName.UNCOMPRESSED;
        DEFAULT_WRITER_VERSION = ParquetProperties.WriterVersion.PARQUET_1_0;
    }
}
