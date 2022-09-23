// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import org.apache.hadoop.mapreduce.OutputCommitter;
import parquet.Preconditions;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import parquet.hadoop.codec.CodecConfig;
import parquet.column.ParquetProperties;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.hadoop.util.ConfigurationUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.JobContext;
import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapreduce.Job;
import parquet.hadoop.api.WriteSupport;
import parquet.Log;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ParquetOutputFormat<T> extends FileOutputFormat<Void, T>
{
    private static final Log LOG;
    public static final String BLOCK_SIZE = "parquet.block.size";
    public static final String PAGE_SIZE = "parquet.page.size";
    public static final String COMPRESSION = "parquet.compression";
    public static final String WRITE_SUPPORT_CLASS = "parquet.write.support.class";
    public static final String DICTIONARY_PAGE_SIZE = "parquet.dictionary.page.size";
    public static final String ENABLE_DICTIONARY = "parquet.enable.dictionary";
    public static final String VALIDATION = "parquet.validation";
    public static final String WRITER_VERSION = "parquet.writer.version";
    public static final String ENABLE_JOB_SUMMARY = "parquet.enable.summary-metadata";
    public static final String MEMORY_POOL_RATIO = "parquet.memory.pool.ratio";
    public static final String MIN_MEMORY_ALLOCATION = "parquet.memory.min.chunk.size";
    private WriteSupport<T> writeSupport;
    private ParquetOutputCommitter committer;
    private static MemoryManager memoryManager;
    
    public static void setWriteSupportClass(final Job job, final Class<?> writeSupportClass) {
        ContextUtil.getConfiguration((JobContext)job).set("parquet.write.support.class", writeSupportClass.getName());
    }
    
    public static void setWriteSupportClass(final JobConf job, final Class<?> writeSupportClass) {
        job.set("parquet.write.support.class", writeSupportClass.getName());
    }
    
    public static Class<?> getWriteSupportClass(final Configuration configuration) {
        final String className = configuration.get("parquet.write.support.class");
        if (className == null) {
            return null;
        }
        final Class<?> writeSupportClass = ConfigurationUtil.getClassFromConfig(configuration, "parquet.write.support.class", WriteSupport.class);
        return writeSupportClass;
    }
    
    public static void setBlockSize(final Job job, final int blockSize) {
        ContextUtil.getConfiguration((JobContext)job).setInt("parquet.block.size", blockSize);
    }
    
    public static void setPageSize(final Job job, final int pageSize) {
        ContextUtil.getConfiguration((JobContext)job).setInt("parquet.page.size", pageSize);
    }
    
    public static void setDictionaryPageSize(final Job job, final int pageSize) {
        ContextUtil.getConfiguration((JobContext)job).setInt("parquet.dictionary.page.size", pageSize);
    }
    
    public static void setCompression(final Job job, final CompressionCodecName compression) {
        ContextUtil.getConfiguration((JobContext)job).set("parquet.compression", compression.name());
    }
    
    public static void setEnableDictionary(final Job job, final boolean enableDictionary) {
        ContextUtil.getConfiguration((JobContext)job).setBoolean("parquet.enable.dictionary", enableDictionary);
    }
    
    public static boolean getEnableDictionary(final JobContext jobContext) {
        return getEnableDictionary(ContextUtil.getConfiguration(jobContext));
    }
    
    public static int getBlockSize(final JobContext jobContext) {
        return getBlockSize(ContextUtil.getConfiguration(jobContext));
    }
    
    public static int getPageSize(final JobContext jobContext) {
        return getPageSize(ContextUtil.getConfiguration(jobContext));
    }
    
    public static int getDictionaryPageSize(final JobContext jobContext) {
        return getDictionaryPageSize(ContextUtil.getConfiguration(jobContext));
    }
    
    public static CompressionCodecName getCompression(final JobContext jobContext) {
        return getCompression(ContextUtil.getConfiguration(jobContext));
    }
    
    public static boolean isCompressionSet(final JobContext jobContext) {
        return isCompressionSet(ContextUtil.getConfiguration(jobContext));
    }
    
    public static void setValidation(final JobContext jobContext, final boolean validating) {
        setValidation(ContextUtil.getConfiguration(jobContext), validating);
    }
    
    public static boolean getValidation(final JobContext jobContext) {
        return getValidation(ContextUtil.getConfiguration(jobContext));
    }
    
    public static boolean getEnableDictionary(final Configuration configuration) {
        return configuration.getBoolean("parquet.enable.dictionary", true);
    }
    
    @Deprecated
    public static int getBlockSize(final Configuration configuration) {
        return configuration.getInt("parquet.block.size", 134217728);
    }
    
    public static long getLongBlockSize(final Configuration configuration) {
        return configuration.getLong("parquet.block.size", 134217728L);
    }
    
    public static int getPageSize(final Configuration configuration) {
        return configuration.getInt("parquet.page.size", 1048576);
    }
    
    public static int getDictionaryPageSize(final Configuration configuration) {
        return configuration.getInt("parquet.dictionary.page.size", 1048576);
    }
    
    public static ParquetProperties.WriterVersion getWriterVersion(final Configuration configuration) {
        final String writerVersion = configuration.get("parquet.writer.version", ParquetProperties.WriterVersion.PARQUET_1_0.toString());
        return ParquetProperties.WriterVersion.fromString(writerVersion);
    }
    
    public static CompressionCodecName getCompression(final Configuration configuration) {
        return CodecConfig.getParquetCompressionCodec(configuration);
    }
    
    public static boolean isCompressionSet(final Configuration configuration) {
        return CodecConfig.isParquetCompressionSet(configuration);
    }
    
    public static void setValidation(final Configuration configuration, final boolean validating) {
        configuration.setBoolean("parquet.validation", validating);
    }
    
    public static boolean getValidation(final Configuration configuration) {
        return configuration.getBoolean("parquet.validation", false);
    }
    
    private CompressionCodecName getCodec(final TaskAttemptContext taskAttemptContext) {
        return CodecConfig.from(taskAttemptContext).getCodec();
    }
    
    public <S extends WriteSupport<T>> ParquetOutputFormat(final S writeSupport) {
        this.writeSupport = writeSupport;
    }
    
    public <S extends WriteSupport<T>> ParquetOutputFormat() {
    }
    
    public RecordWriter<Void, T> getRecordWriter(final TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        final Configuration conf = ContextUtil.getConfiguration((JobContext)taskAttemptContext);
        final CompressionCodecName codec = this.getCodec(taskAttemptContext);
        final String extension = codec.getExtension() + ".parquet";
        final Path file = this.getDefaultWorkFile(taskAttemptContext, extension);
        return this.getRecordWriter(conf, file, codec);
    }
    
    public RecordWriter<Void, T> getRecordWriter(final TaskAttemptContext taskAttemptContext, final Path file) throws IOException, InterruptedException {
        return this.getRecordWriter(ContextUtil.getConfiguration((JobContext)taskAttemptContext), file, this.getCodec(taskAttemptContext));
    }
    
    public RecordWriter<Void, T> getRecordWriter(final Configuration conf, final Path file, final CompressionCodecName codec) throws IOException, InterruptedException {
        final WriteSupport<T> writeSupport = this.getWriteSupport(conf);
        final CodecFactory codecFactory = new CodecFactory(conf);
        final long blockSize = getLongBlockSize(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Parquet block size to " + blockSize);
        }
        final int pageSize = getPageSize(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Parquet page size to " + pageSize);
        }
        final int dictionaryPageSize = getDictionaryPageSize(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Parquet dictionary page size to " + dictionaryPageSize);
        }
        final boolean enableDictionary = getEnableDictionary(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Dictionary is " + (enableDictionary ? "on" : "off"));
        }
        final boolean validating = getValidation(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Validation is " + (validating ? "on" : "off"));
        }
        final ParquetProperties.WriterVersion writerVersion = getWriterVersion(conf);
        if (Log.INFO) {
            ParquetOutputFormat.LOG.info("Writer version is: " + writerVersion);
        }
        final WriteSupport.WriteContext init = writeSupport.init(conf);
        final ParquetFileWriter w = new ParquetFileWriter(conf, init.getSchema(), file);
        w.start();
        final float maxLoad = conf.getFloat("parquet.memory.pool.ratio", 0.95f);
        final long minAllocation = conf.getLong("parquet.memory.min.chunk.size", 1048576L);
        if (ParquetOutputFormat.memoryManager == null) {
            ParquetOutputFormat.memoryManager = new MemoryManager(maxLoad, minAllocation);
        }
        else if (ParquetOutputFormat.memoryManager.getMemoryPoolRatio() != maxLoad) {
            ParquetOutputFormat.LOG.warn("The configuration parquet.memory.pool.ratio has been set. It should not be reset by the new value: " + maxLoad);
        }
        return (RecordWriter<Void, T>)new ParquetRecordWriter(w, (WriteSupport<Object>)writeSupport, init.getSchema(), init.getExtraMetaData(), blockSize, pageSize, codecFactory.getCompressor(codec, pageSize), dictionaryPageSize, enableDictionary, validating, writerVersion, ParquetOutputFormat.memoryManager);
    }
    
    public WriteSupport<T> getWriteSupport(final Configuration configuration) {
        if (this.writeSupport != null) {
            return this.writeSupport;
        }
        final Class<?> writeSupportClass = getWriteSupportClass(configuration);
        try {
            return (WriteSupport<T>)Preconditions.checkNotNull(writeSupportClass, "writeSupportClass").newInstance();
        }
        catch (InstantiationException e) {
            throw new BadConfigurationException("could not instantiate write support class: " + writeSupportClass, e);
        }
        catch (IllegalAccessException e2) {
            throw new BadConfigurationException("could not instantiate write support class: " + writeSupportClass, e2);
        }
    }
    
    public OutputCommitter getOutputCommitter(final TaskAttemptContext context) throws IOException {
        if (this.committer == null) {
            final Path output = getOutputPath((JobContext)context);
            this.committer = new ParquetOutputCommitter(output, context);
        }
        return (OutputCommitter)this.committer;
    }
    
    static MemoryManager getMemoryManager() {
        return ParquetOutputFormat.memoryManager;
    }
    
    static {
        LOG = Log.getLog(ParquetOutputFormat.class);
    }
}
