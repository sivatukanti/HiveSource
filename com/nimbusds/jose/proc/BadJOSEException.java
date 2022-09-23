// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

public class BadJOSEException extends Exception
{
    public BadJOSEException(final String message) {
        super(message);
    }
    
    public BadJOSEException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
