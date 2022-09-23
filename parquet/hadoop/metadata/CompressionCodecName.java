// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.metadata;

import parquet.hadoop.codec.CompressionCodecNotSupportedException;
import parquet.format.CompressionCodec;

public enum CompressionCodecName
{
    UNCOMPRESSED((String)null, CompressionCodec.UNCOMPRESSED, ""), 
    SNAPPY("parquet.hadoop.codec.SnappyCodec", CompressionCodec.SNAPPY, ".snappy"), 
    GZIP("org.apache.hadoop.io.compress.GzipCodec", CompressionCodec.GZIP, ".gz"), 
    LZO("com.hadoop.compression.lzo.LzoCodec", CompressionCodec.LZO, ".lzo");
    
    private final String hadoopCompressionCodecClass;
    private final CompressionCodec parquetCompressionCodec;
    private final String extension;
    
    public static CompressionCodecName fromConf(final String name) {
        if (name == null) {
            return CompressionCodecName.UNCOMPRESSED;
        }
        return valueOf(name.toUpperCase());
    }
    
    public static CompressionCodecName fromCompressionCodec(final Class<?> clazz) {
        if (clazz == null) {
            return CompressionCodecName.UNCOMPRESSED;
        }
        final String name = clazz.getName();
        for (final CompressionCodecName codec : values()) {
            if (name.equals(codec.getHadoopCompressionCodecClassName())) {
                return codec;
            }
        }
        throw new CompressionCodecNotSupportedException(clazz);
    }
    
    public static CompressionCodecName fromParquet(final CompressionCodec codec) {
        for (final CompressionCodecName codecName : values()) {
            if (codec.equals(codecName.parquetCompressionCodec)) {
                return codecName;
            }
        }
        throw new IllegalArgumentException("Unknown compression codec " + codec);
    }
    
    private CompressionCodecName(final String hadoopCompressionCodecClass, final CompressionCodec parquetCompressionCodec, final String extension) {
        this.hadoopCompressionCodecClass = hadoopCompressionCodecClass;
        this.parquetCompressionCodec = parquetCompressionCodec;
        this.extension = extension;
    }
    
    public String getHadoopCompressionCodecClassName() {
        return this.hadoopCompressionCodecClass;
    }
    
    public Class getHadoopCompressionCodecClass() {
        final String codecClassName = this.getHadoopCompressionCodecClassName();
        if (codecClassName == null) {
            return null;
        }
        try {
            return Class.forName(codecClassName);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
    
    public CompressionCodec getParquetCompressionCodec() {
        return this.parquetCompressionCodec;
    }
    
    public String getExtension() {
        return this.extension;
    }
}
