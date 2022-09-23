// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FileAlreadyExistsException extends IOException
{
    public FileAlreadyExistsException() {
    }
    
    public FileAlreadyExistsException(final String msg) {
        super(msg);
    }
}
