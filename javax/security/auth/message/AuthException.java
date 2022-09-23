// 
// Decompiled by Procyon v0.5.36
// 

package javax.security.auth.message;

import javax.security.auth.login.LoginException;

public class AuthException extends LoginException
{
    public AuthException() {
    }
    
    public AuthException(final String msg) {
        super(msg);
    }
}
