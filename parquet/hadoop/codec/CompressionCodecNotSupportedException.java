// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

public class CompressionCodecNotSupportedException extends RuntimeException
{
    private final Class codecClass;
    
    public CompressionCodecNotSupportedException(final Class codecClass) {
        super("codec not supported: " + codecClass.getName());
        this.codecClass = codecClass;
    }
    
    public Class getCodecClass() {
        return this.codecClass;
    }
}
