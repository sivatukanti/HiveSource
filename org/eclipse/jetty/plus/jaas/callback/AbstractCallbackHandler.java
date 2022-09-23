// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.callback;

import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

public abstract class AbstractCallbackHandler implements CallbackHandler
{
    protected String _userName;
    protected Object _credential;
    
    public void setUserName(final String userName) {
        this._userName = userName;
    }
    
    public String getUserName() {
        return this._userName;
    }
    
    public void setCredential(final Object credential) {
        this._credential = credential;
    }
    
    public Object getCredential() {
        return this._credential;
    }
    
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    }
}
