// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.HadoopIllegalArgumentException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class InvalidPathException extends HadoopIllegalArgumentException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidPathException(final String path) {
        super("Invalid path name " + path);
    }
    
    public InvalidPathException(final String path, final String reason) {
        super("Invalid path " + path + ((reason == null) ? "" : (". (" + reason + ")")));
    }
}
