// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ServiceFailedException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public ServiceFailedException(final String message) {
        super(message);
    }
    
    public ServiceFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
