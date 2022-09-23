// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface SplittableCompressionCodec extends CompressionCodec
{
    SplitCompressionInputStream createInputStream(final InputStream p0, final Decompressor p1, final long p2, final long p3, final READ_MODE p4) throws IOException;
    
    public enum READ_MODE
    {
        CONTINUOUS, 
        BYBLOCK;
    }
}
