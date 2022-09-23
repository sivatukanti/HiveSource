// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import org.apache.hadoop.io.ByteBufferPool;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface HasEnhancedByteBufferAccess
{
    ByteBuffer read(final ByteBufferPool p0, final int p1, final EnumSet<ReadOption> p2) throws IOException, UnsupportedOperationException;
    
    void releaseBuffer(final ByteBuffer p0);
}
