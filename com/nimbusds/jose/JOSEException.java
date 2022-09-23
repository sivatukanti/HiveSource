// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

public class JOSEException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public JOSEException(final String message) {
        super(message);
    }
    
    public JOSEException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
