// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.io.IOException;
import javax.security.auth.login.LoginContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.security.Principal;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
class User implements Principal
{
    private final String fullName;
    private final String shortName;
    private volatile UserGroupInformation.AuthenticationMethod authMethod;
    private volatile LoginContext login;
    private volatile long lastLogin;
    
    public User(final String name) {
        this(name, null, null);
    }
    
    public User(final String name, final UserGroupInformation.AuthenticationMethod authMethod, final LoginContext login) {
        this.authMethod = null;
        this.login = null;
        this.lastLogin = 0L;
        try {
            this.shortName = new HadoopKerberosName(name).getShortName();
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("Illegal principal name " + name + ": " + ioe.toString(), ioe);
        }
        this.fullName = name;
        this.authMethod = authMethod;
        this.login = login;
    }
    
    @Override
    public String getName() {
        return this.fullName;
    }
    
    public String getShortName() {
        return this.shortName;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o != null && this.getClass() == o.getClass() && this.fullName.equals(((User)o).fullName) && this.authMethod == ((User)o).authMethod);
    }
    
    @Override
    public int hashCode() {
        return this.fullName.hashCode();
    }
    
    @Override
    public String toString() {
        return this.fullName;
    }
    
    public void setAuthenticationMethod(final UserGroupInformation.AuthenticationMethod authMethod) {
        this.authMethod = authMethod;
    }
    
    public UserGroupInformation.AuthenticationMethod getAuthenticationMethod() {
        return this.authMethod;
    }
    
    public LoginContext getLogin() {
        return this.login;
    }
    
    public void setLogin(final LoginContext login) {
        this.login = login;
    }
    
    public void setLastLogin(final long time) {
        this.lastLogin = time;
    }
    
    public long getLastLogin() {
        return this.lastLogin;
    }
}
