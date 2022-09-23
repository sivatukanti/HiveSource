// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

public class BadJWSException extends BadJOSEException
{
    public BadJWSException(final String message) {
        super(message);
    }
    
    public BadJWSException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
