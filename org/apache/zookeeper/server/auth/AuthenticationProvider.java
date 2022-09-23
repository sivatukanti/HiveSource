// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ServerCnxn;

public interface AuthenticationProvider
{
    String getScheme();
    
    KeeperException.Code handleAuthentication(final ServerCnxn p0, final byte[] p1);
    
    boolean matches(final String p0, final String p1);
    
    boolean isAuthenticated();
    
    boolean isValid(final String p0);
}
