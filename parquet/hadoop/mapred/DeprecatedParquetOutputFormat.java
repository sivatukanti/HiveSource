// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.mapred;

import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapred.Reporter;
import parquet.hadoop.ParquetRecordWriter;
import java.io.IOException;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import parquet.hadoop.codec.CodecConfig;
import org.apache.hadoop.mapred.JobConf;
import parquet.hadoop.metadata.CompressionCodecName;
import org.apache.hadoop.conf.Configuration;
import parquet.hadoop.ParquetOutputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;

public class DeprecatedParquetOutputFormat<V> extends FileOutputFormat<Void, V>
{
    protected ParquetOutputFormat<V> realOutputFormat;
    
    public DeprecatedParquetOutputFormat() {
        this.realOutputFormat = new ParquetOutputFormat<V>();
    }
    
    public static void setWriteSupportClass(final Configuration configuration, final Class<?> writeSupportClass) {
        configuration.set("parquet.write.support.class", writeSupportClass.getName());
    }
    
    public static void setBlockSize(final Configuration configuration, final int blockSize) {
        configuration.setInt("parquet.block.size", blockSize);
    }
    
    public static void setPageSize(final Configuration configuration, final int pageSize) {
        configuration.setInt("parquet.page.size", pageSize);
    }
    
    public static void setCompression(final Configuration configuration, final CompressionCodecName compression) {
        configuration.set("parquet.compression", compression.name());
    }
    
    public static void setEnableDictionary(final Configuration configuration, final boolean enableDictionary) {
        configuration.setBoolean("parquet.enable.dictionary", enableDictionary);
    }
    
    public static void setAsOutputFormat(final JobConf jobConf) {
        jobConf.setOutputFormat((Class)DeprecatedParquetOutputFormat.class);
        jobConf.setOutputCommitter((Class)MapredParquetOutputCommitter.class);
    }
    
    private CompressionCodecName getCodec(final JobConf conf) {
        return CodecConfig.from(conf).getCodec();
    }
    
    private static Path getDefaultWorkFile(final JobConf conf, final String name, final String extension) {
        final String file = getUniqueName(conf, name) + extension;
        return new Path(getWorkOutputPath(conf), file);
    }
    
    public RecordWriter<Void, V> getRecordWriter(final FileSystem fs, final JobConf conf, final String name, final Progressable progress) throws IOException {
        return (RecordWriter<Void, V>)new RecordWriterWrapper(this.realOutputFormat, fs, conf, name, progress);
    }
    
    private class RecordWriterWrapper implements RecordWriter<Void, V>
    {
        private ParquetRecordWriter<V> realWriter;
        
        public RecordWriterWrapper(final ParquetOutputFormat<V> realOutputFormat, final FileSystem fs, final JobConf conf, final String name, final Progressable progress) throws IOException {
            final CompressionCodecName codec = DeprecatedParquetOutputFormat.this.getCodec(conf);
            final String extension = codec.getExtension() + ".parquet";
            final Path file = getDefaultWorkFile(conf, name, extension);
            try {
                this.realWriter = (ParquetRecordWriter<V>)(ParquetRecordWriter)realOutputFormat.getRecordWriter((Configuration)conf, file, codec);
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
        
        public void close(final Reporter reporter) throws IOException {
            try {
                this.realWriter.close(null);
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
        
        public void write(final Void key, final V value) throws IOException {
            try {
                this.realWriter.write(key, value);
            }
            catch (InterruptedException e) {
                Thread.interrupted();
                throw new IOException(e);
            }
        }
    }
}
