// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface Seekable
{
    void seek(final long p0) throws IOException;
    
    long getPos() throws IOException;
    
    @InterfaceAudience.Private
    boolean seekToNewSource(final long p0) throws IOException;
}
