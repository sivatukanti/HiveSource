// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import com.google.common.base.Objects;

public class UsernamePassword
{
    private String username;
    private String password;
    
    public UsernamePassword(final String username, final String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof UsernamePassword) {
            final UsernamePassword that = (UsernamePassword)obj;
            return Objects.equal(this.username, that.getUsername()) && Objects.equal(this.password, that.getPassword());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.username, this.password);
    }
}
