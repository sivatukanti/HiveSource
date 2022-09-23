// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class NotInMountpointException extends UnsupportedOperationException
{
    final String msg;
    
    public NotInMountpointException(final Path path, final String operation) {
        this.msg = operation + " on path `" + path + "' is not within a mount point";
    }
    
    public NotInMountpointException(final String operation) {
        this.msg = operation + " on empty path is invalid";
    }
    
    @Override
    public String getMessage() {
        return this.msg;
    }
}
