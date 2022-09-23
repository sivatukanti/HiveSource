// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.callback;

import javax.security.auth.Subject;
import org.eclipse.jetty.util.security.Credential;
import javax.security.auth.callback.Callback;

public class CredentialValidationCallback implements Callback
{
    private Credential _credential;
    private boolean _result;
    private Subject _subject;
    private String _userName;
    
    public CredentialValidationCallback(final Subject subject, final String userName, final Credential credential) {
        this._subject = subject;
        this._userName = userName;
        this._credential = credential;
    }
    
    public Credential getCredential() {
        return this._credential;
    }
    
    public void clearCredential() {
        this._credential = null;
    }
    
    public boolean getResult() {
        return this._result;
    }
    
    public Subject getSubject() {
        return this._subject;
    }
    
    public String getUsername() {
        return this._userName;
    }
    
    public void setResult(final boolean result) {
        this._result = result;
    }
}
