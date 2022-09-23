// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.BadJOSEException;

public class BadJWTException extends BadJOSEException
{
    public BadJWTException(final String message) {
        super(message);
    }
    
    public BadJWTException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
