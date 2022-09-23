// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class InvalidPathHandleException extends IOException
{
    private static final long serialVersionUID = 3448423209L;
    
    public InvalidPathHandleException(final String str) {
        super(str);
    }
    
    public InvalidPathHandleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
