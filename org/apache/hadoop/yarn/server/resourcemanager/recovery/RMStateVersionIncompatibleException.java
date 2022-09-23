// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.exceptions.YarnException;

public class RMStateVersionIncompatibleException extends YarnException
{
    private static final long serialVersionUID = 1364408L;
    
    public RMStateVersionIncompatibleException(final Throwable cause) {
        super(cause);
    }
    
    public RMStateVersionIncompatibleException(final String message) {
        super(message);
    }
    
    public RMStateVersionIncompatibleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
