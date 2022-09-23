// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.spi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.security.Credential;

public class UserInfo
{
    private String _userName;
    private Credential _credential;
    private List<String> _roleNames;
    
    public UserInfo(final String userName, final Credential credential, final List<String> roleNames) {
        this._userName = userName;
        this._credential = credential;
        this._roleNames = new ArrayList<String>();
        if (roleNames != null) {
            this._roleNames.addAll(roleNames);
        }
    }
    
    public String getUserName() {
        return this._userName;
    }
    
    public List<String> getRoleNames() {
        return new ArrayList<String>(this._roleNames);
    }
    
    public boolean checkCredential(final Object suppliedCredential) {
        return this._credential.check(suppliedCredential);
    }
    
    protected Credential getCredential() {
        return this._credential;
    }
}
