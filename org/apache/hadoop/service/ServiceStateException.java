// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.ExitCodeProvider;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ServiceStateException extends RuntimeException implements ExitCodeProvider
{
    private static final long serialVersionUID = 1110000352259232646L;
    private int exitCode;
    
    public ServiceStateException(final String message) {
        this(message, null);
    }
    
    public ServiceStateException(final String message, final Throwable cause) {
        super(message, cause);
        if (cause instanceof ExitCodeProvider) {
            this.exitCode = ((ExitCodeProvider)cause).getExitCode();
        }
        else {
            this.exitCode = 57;
        }
    }
    
    public ServiceStateException(final int exitCode, final String message, final Throwable cause) {
        this(message, cause);
        this.exitCode = exitCode;
    }
    
    public ServiceStateException(final Throwable cause) {
        super(cause);
    }
    
    @Override
    public int getExitCode() {
        return this.exitCode;
    }
    
    public static RuntimeException convert(final Throwable fault) {
        if (fault instanceof RuntimeException) {
            return (RuntimeException)fault;
        }
        return new ServiceStateException(fault);
    }
    
    public static RuntimeException convert(final String text, final Throwable fault) {
        if (fault instanceof RuntimeException) {
            return (RuntimeException)fault;
        }
        return new ServiceStateException(text, fault);
    }
}
