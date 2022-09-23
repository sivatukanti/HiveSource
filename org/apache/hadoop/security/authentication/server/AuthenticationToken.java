// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.util.AuthToken;

public class AuthenticationToken extends AuthToken
{
    public static final AuthenticationToken ANONYMOUS;
    
    private AuthenticationToken() {
    }
    
    private AuthenticationToken(final AuthToken token) {
        super(token.getUserName(), token.getName(), token.getType());
        this.setMaxInactives(token.getMaxInactives());
        this.setExpires(token.getExpires());
    }
    
    public AuthenticationToken(final String userName, final String principal, final String type) {
        super(userName, principal, type);
    }
    
    @Override
    public void setMaxInactives(final long maxInactives) {
        if (this != AuthenticationToken.ANONYMOUS) {
            super.setMaxInactives(maxInactives);
        }
    }
    
    @Override
    public void setExpires(final long expires) {
        if (this != AuthenticationToken.ANONYMOUS) {
            super.setExpires(expires);
        }
    }
    
    @Override
    public boolean isExpired() {
        return super.isExpired();
    }
    
    public static AuthenticationToken parse(final String tokenStr) throws AuthenticationException {
        return new AuthenticationToken(AuthToken.parse(tokenStr));
    }
    
    static {
        ANONYMOUS = new AuthenticationToken();
    }
}
