// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface UserInfo
{
    String getPassphrase();
    
    String getPassword();
    
    boolean promptPassword(final String p0);
    
    boolean promptPassphrase(final String p0);
    
    boolean promptYesNo(final String p0);
    
    void showMessage(final String p0);
}
