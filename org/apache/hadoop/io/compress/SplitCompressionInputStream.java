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
public abstract class SplitCompressionInputStream extends CompressionInputStream
{
    private long start;
    private long end;
    
    public SplitCompressionInputStream(final InputStream in, final long start, final long end) throws IOException {
        super(in);
        this.start = start;
        this.end = end;
    }
    
    protected void setStart(final long start) {
        this.start = start;
    }
    
    protected void setEnd(final long end) {
        this.end = end;
    }
    
    public long getAdjustedStart() {
        return this.start;
    }
    
    public long getAdjustedEnd() {
        return this.end;
    }
}
