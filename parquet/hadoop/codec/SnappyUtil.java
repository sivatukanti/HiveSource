// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import parquet.Preconditions;

public class SnappyUtil
{
    public static void validateBuffer(final byte[] buffer, final int off, final int len) {
        Preconditions.checkNotNull(buffer, "buffer");
        Preconditions.checkArgument(off >= 0 && len >= 0 && off <= buffer.length - len, "Invalid buffer offset or length: buffer.length=%s off=%s len=%s", buffer.length, off, len);
    }
}
