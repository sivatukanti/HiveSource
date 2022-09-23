// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class VersionMismatchException extends IOException
{
    private byte expectedVersion;
    private byte foundVersion;
    
    public VersionMismatchException(final byte expectedVersionIn, final byte foundVersionIn) {
        this.expectedVersion = expectedVersionIn;
        this.foundVersion = foundVersionIn;
    }
    
    @Override
    public String toString() {
        return "A record version mismatch occurred. Expecting v" + this.expectedVersion + ", found v" + this.foundVersion;
    }
}
