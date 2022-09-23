// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

public class HttpAuthenticationException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public HttpAuthenticationException(final Throwable cause) {
        super(cause);
    }
    
    public HttpAuthenticationException(final String msg) {
        super(msg);
    }
    
    public HttpAuthenticationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
