// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public interface ByteBufferPool
{
    ByteBuffer getBuffer(final boolean p0, final int p1);
    
    void putBuffer(final ByteBuffer p0);
}
