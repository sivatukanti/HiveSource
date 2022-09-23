// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AccessControlException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public AccessControlException() {
        super("Permission denied.");
    }
    
    public AccessControlException(final String s) {
        super(s);
    }
    
    public AccessControlException(final Throwable cause) {
        super(cause);
    }
}
