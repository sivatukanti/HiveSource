// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Arrays;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class BBUploadHandle implements UploadHandle
{
    private static final long serialVersionUID = 1775587483L;
    private final byte[] bytes;
    
    private BBUploadHandle(final ByteBuffer byteBuffer) {
        this.bytes = byteBuffer.array();
    }
    
    public static UploadHandle from(final ByteBuffer byteBuffer) {
        return new BBUploadHandle(byteBuffer);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
    
    @Override
    public ByteBuffer bytes() {
        return ByteBuffer.wrap(this.bytes);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof UploadHandle)) {
            return false;
        }
        final UploadHandle o = (UploadHandle)other;
        return this.bytes().equals(o.bytes());
    }
}
