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
public final class BBPartHandle implements PartHandle
{
    private static final long serialVersionUID = 600719025L;
    private final byte[] bytes;
    
    private BBPartHandle(final ByteBuffer byteBuffer) {
        this.bytes = byteBuffer.array();
    }
    
    public static PartHandle from(final ByteBuffer byteBuffer) {
        return new BBPartHandle(byteBuffer);
    }
    
    @Override
    public ByteBuffer bytes() {
        return ByteBuffer.wrap(this.bytes);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PartHandle)) {
            return false;
        }
        final PartHandle o = (PartHandle)other;
        return this.bytes().equals(o.bytes());
    }
}
