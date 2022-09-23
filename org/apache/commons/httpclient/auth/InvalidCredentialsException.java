// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.auth;

public class InvalidCredentialsException extends AuthenticationException
{
    public InvalidCredentialsException() {
    }
    
    public InvalidCredentialsException(final String message) {
        super(message);
    }
    
    public InvalidCredentialsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
