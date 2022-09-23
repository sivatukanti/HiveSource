// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import parquet.hadoop.metadata.CompressionCodecName;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.conf.Configuration;
import parquet.Log;

public abstract class CodecConfig
{
    private static final Log LOG;
    
    public abstract boolean isHadoopCompressionSet();
    
    public abstract Class getHadoopOutputCompressorClass(final Class p0);
    
    public abstract Configuration getConfiguration();
    
    public static CodecConfig from(final JobConf jobConf) {
        return new MapredCodecConfig(jobConf);
    }
    
    public static CodecConfig from(final TaskAttemptContext context) {
        return new MapreduceCodecConfig(context);
    }
    
    public static boolean isParquetCompressionSet(final Configuration conf) {
        return conf.get("parquet.compression") != null;
    }
    
    public static CompressionCodecName getParquetCompressionCodec(final Configuration configuration) {
        return CompressionCodecName.fromConf(configuration.get("parquet.compression", CompressionCodecName.UNCOMPRESSED.name()));
    }
    
    public CompressionCodecName getCodec() {
        final Configuration configuration = this.getConfiguration();
        CompressionCodecName codec;
        if (isParquetCompressionSet(configuration)) {
            codec = getParquetCompressionCodec(configuration);
        }
        else if (this.isHadoopCompressionSet()) {
            codec = this.getHadoopCompressionCodec();
        }
        else {
            if (Log.INFO) {
                CodecConfig.LOG.info("Compression set to false");
            }
            codec = CompressionCodecName.UNCOMPRESSED;
        }
        if (Log.INFO) {
            CodecConfig.LOG.info("Compression: " + codec.name());
        }
        return codec;
    }
    
    private CompressionCodecName getHadoopCompressionCodec() {
        CompressionCodecName codec;
        try {
            final Class<?> codecClass = (Class<?>)this.getHadoopOutputCompressorClass(CompressionCodecName.UNCOMPRESSED.getHadoopCompressionCodecClass());
            if (Log.INFO) {
                CodecConfig.LOG.info("Compression set through hadoop codec: " + codecClass.getName());
            }
            codec = CompressionCodecName.fromCompressionCodec(codecClass);
        }
        catch (CompressionCodecNotSupportedException e) {
            if (Log.WARN) {
                CodecConfig.LOG.warn("codec defined in hadoop config is not supported by parquet [" + e.getCodecClass().getName() + "] and will use UNCOMPRESSED", e);
            }
            codec = CompressionCodecName.UNCOMPRESSED;
        }
        catch (IllegalArgumentException e2) {
            if (Log.WARN) {
                CodecConfig.LOG.warn("codec class not found: " + e2.getMessage(), e2);
            }
            codec = CompressionCodecName.UNCOMPRESSED;
        }
        return codec;
    }
    
    static {
        LOG = Log.getLog(CodecConfig.class);
    }
    
    private static class MapreduceCodecConfig extends CodecConfig
    {
        private final TaskAttemptContext context;
        
        public MapreduceCodecConfig(final TaskAttemptContext context) {
            this.context = context;
        }
        
        @Override
        public boolean isHadoopCompressionSet() {
            return FileOutputFormat.getCompressOutput((JobContext)this.context);
        }
        
        @Override
        public Class getHadoopOutputCompressorClass(final Class defaultCodec) {
            return FileOutputFormat.getOutputCompressorClass((JobContext)this.context, defaultCodec);
        }
        
        @Override
        public Configuration getConfiguration() {
            return ContextUtil.getConfiguration((JobContext)this.context);
        }
    }
    
    private static class MapredCodecConfig extends CodecConfig
    {
        private final JobConf conf;
        
        public MapredCodecConfig(final JobConf conf) {
            this.conf = conf;
        }
        
        @Override
        public boolean isHadoopCompressionSet() {
            return org.apache.hadoop.mapred.FileOutputFormat.getCompressOutput(this.conf);
        }
        
        @Override
        public Class getHadoopOutputCompressorClass(final Class defaultCodec) {
            return org.apache.hadoop.mapred.FileOutputFormat.getOutputCompressorClass(this.conf, defaultCodec);
        }
        
        @Override
        public Configuration getConfiguration() {
            return (Configuration)this.conf;
        }
    }
}
