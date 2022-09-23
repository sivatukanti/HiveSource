// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.security.GeneralSecurityException;

public class ServerAuthException extends GeneralSecurityException
{
    public ServerAuthException() {
    }
    
    public ServerAuthException(final String s) {
        super(s);
    }
    
    public ServerAuthException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public ServerAuthException(final Throwable throwable) {
        super(throwable);
    }
}
