// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
@FunctionalInterface
public interface PathHandle extends Serializable
{
    default byte[] toByteArray() {
        final ByteBuffer bb = this.bytes();
        final byte[] ret = new byte[bb.remaining()];
        bb.get(ret);
        return ret;
    }
    
    ByteBuffer bytes();
    
    boolean equals(final Object p0);
}
