// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface Compressor
{
    void setInput(final byte[] p0, final int p1, final int p2);
    
    boolean needsInput();
    
    void setDictionary(final byte[] p0, final int p1, final int p2);
    
    long getBytesRead();
    
    long getBytesWritten();
    
    void finish();
    
    boolean finished();
    
    int compress(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void reset();
    
    void end();
    
    void reinit(final Configuration p0);
}
