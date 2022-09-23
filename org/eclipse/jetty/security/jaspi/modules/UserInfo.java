// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import java.util.Arrays;

public class UserInfo
{
    private final String userName;
    private char[] password;
    
    public UserInfo(final String userName, final char[] password) {
        this.userName = userName;
        this.password = password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public void clearPassword() {
        Arrays.fill(this.password, '\0');
        this.password = null;
    }
}
