// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class ChecksumException extends IOException
{
    private static final long serialVersionUID = 1L;
    private long pos;
    
    public ChecksumException(final String description, final long pos) {
        super(description);
        this.pos = pos;
    }
    
    public long getPos() {
        return this.pos;
    }
}
