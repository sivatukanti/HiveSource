// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricsException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public MetricsException(final String message) {
        super(message);
    }
    
    public MetricsException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MetricsException(final Throwable cause) {
        super(cause);
    }
}
