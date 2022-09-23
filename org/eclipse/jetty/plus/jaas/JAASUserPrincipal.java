// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import javax.security.auth.login.LoginContext;
import javax.security.auth.Subject;
import java.security.Principal;

public class JAASUserPrincipal implements Principal
{
    private final String _name;
    private final Subject _subject;
    private final LoginContext _loginContext;
    
    public JAASUserPrincipal(final String name, final Subject subject, final LoginContext loginContext) {
        this._name = name;
        this._subject = subject;
        this._loginContext = loginContext;
    }
    
    public String getName() {
        return this._name;
    }
    
    public Subject getSubject() {
        return this._subject;
    }
    
    LoginContext getLoginContext() {
        return this._loginContext;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
