// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce", "YARN" })
@InterfaceStability.Unstable
public class YarnRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -7153142425412203936L;
    
    public YarnRuntimeException(final Throwable cause) {
        super(cause);
    }
    
    public YarnRuntimeException(final String message) {
        super(message);
    }
    
    public YarnRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
