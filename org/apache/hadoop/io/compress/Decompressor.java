// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface Decompressor
{
    void setInput(final byte[] p0, final int p1, final int p2);
    
    boolean needsInput();
    
    void setDictionary(final byte[] p0, final int p1, final int p2);
    
    boolean needsDictionary();
    
    boolean finished();
    
    int decompress(final byte[] p0, final int p1, final int p2) throws IOException;
    
    int getRemaining();
    
    void reset();
    
    void end();
}
