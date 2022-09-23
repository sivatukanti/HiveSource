// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContainerNotFoundException extends YarnException
{
    private static final long serialVersionUID = 8694608L;
    
    public ContainerNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public ContainerNotFoundException(final String message) {
        super(message);
    }
    
    public ContainerNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
