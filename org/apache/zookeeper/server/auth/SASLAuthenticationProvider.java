// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.auth;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ServerCnxn;

public class SASLAuthenticationProvider implements AuthenticationProvider
{
    @Override
    public String getScheme() {
        return "sasl";
    }
    
    @Override
    public KeeperException.Code handleAuthentication(final ServerCnxn cnxn, final byte[] authData) {
        return KeeperException.Code.AUTHFAILED;
    }
    
    @Override
    public boolean matches(final String id, final String aclExpr) {
        if (System.getProperty("zookeeper.superUser") != null) {
            return id.equals(System.getProperty("zookeeper.superUser")) || id.equals(aclExpr);
        }
        return id.equals("super") || id.equals(aclExpr);
    }
    
    @Override
    public boolean isAuthenticated() {
        return true;
    }
    
    @Override
    public boolean isValid(final String id) {
        try {
            new KerberosName(id);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}
