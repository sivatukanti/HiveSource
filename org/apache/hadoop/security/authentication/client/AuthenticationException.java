// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

public class AuthenticationException extends Exception
{
    static final long serialVersionUID = 0L;
    
    public AuthenticationException(final Throwable cause) {
        super(cause);
    }
    
    public AuthenticationException(final String msg) {
        super(msg);
    }
    
    public AuthenticationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
