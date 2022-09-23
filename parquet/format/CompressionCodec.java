// 
// Decompiled by Procyon v0.5.36
// 

package parquet.format;

import parquet.org.apache.thrift.TEnum;

public enum CompressionCodec implements TEnum
{
    UNCOMPRESSED(0), 
    SNAPPY(1), 
    GZIP(2), 
    LZO(3);
    
    private final int value;
    
    private CompressionCodec(final int value) {
        this.value = value;
    }
    
    @Override
    public int getValue() {
        return this.value;
    }
    
    public static CompressionCodec findByValue(final int value) {
        switch (value) {
            case 0: {
                return CompressionCodec.UNCOMPRESSED;
            }
            case 1: {
                return CompressionCodec.SNAPPY;
            }
            case 2: {
                return CompressionCodec.GZIP;
            }
            case 3: {
                return CompressionCodec.LZO;
            }
            default: {
                return null;
            }
        }
    }
}
