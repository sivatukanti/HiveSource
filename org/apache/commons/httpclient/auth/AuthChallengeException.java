// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.auth;

public class AuthChallengeException extends AuthenticationException
{
    public AuthChallengeException() {
    }
    
    public AuthChallengeException(final String message) {
        super(message);
    }
    
    public AuthChallengeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
