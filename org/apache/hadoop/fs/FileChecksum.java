// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Arrays;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class FileChecksum implements Writable
{
    public abstract String getAlgorithmName();
    
    public abstract int getLength();
    
    public abstract byte[] getBytes();
    
    public Options.ChecksumOpt getChecksumOpt() {
        return null;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !(other instanceof FileChecksum)) {
            return false;
        }
        final FileChecksum that = (FileChecksum)other;
        return this.getAlgorithmName().equals(that.getAlgorithmName()) && Arrays.equals(this.getBytes(), that.getBytes());
    }
    
    @Override
    public int hashCode() {
        return this.getAlgorithmName().hashCode() ^ Arrays.hashCode(this.getBytes());
    }
}
