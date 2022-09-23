// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

public class EncodingUtils
{
    public static final void encodeBigEndian(final int integer, final byte[] buf) {
        encodeBigEndian(integer, buf, 0);
    }
    
    public static final void encodeBigEndian(final int integer, final byte[] buf, final int offset) {
        buf[offset] = (byte)(0xFF & integer >> 24);
        buf[offset + 1] = (byte)(0xFF & integer >> 16);
        buf[offset + 2] = (byte)(0xFF & integer >> 8);
        buf[offset + 3] = (byte)(0xFF & integer);
    }
    
    public static final int decodeBigEndian(final byte[] buf) {
        return decodeBigEndian(buf, 0);
    }
    
    public static final int decodeBigEndian(final byte[] buf, final int offset) {
        return (buf[offset] & 0xFF) << 24 | (buf[offset + 1] & 0xFF) << 16 | (buf[offset + 2] & 0xFF) << 8 | (buf[offset + 3] & 0xFF);
    }
}
