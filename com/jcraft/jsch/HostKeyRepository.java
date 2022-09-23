// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

public interface HostKeyRepository
{
    public static final int OK = 0;
    public static final int NOT_INCLUDED = 1;
    public static final int CHANGED = 2;
    
    int check(final String p0, final byte[] p1);
    
    void add(final HostKey p0, final UserInfo p1);
    
    void remove(final String p0, final String p1);
    
    void remove(final String p0, final String p1, final byte[] p2);
    
    String getKnownHostsRepositoryID();
    
    HostKey[] getHostKey();
    
    HostKey[] getHostKey(final String p0, final String p1);
}
