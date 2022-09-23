// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface PositionedReadable
{
    int read(final long p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    void readFully(final long p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    void readFully(final long p0, final byte[] p1) throws IOException;
}
