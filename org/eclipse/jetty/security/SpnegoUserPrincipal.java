// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.B64Code;
import java.security.Principal;

public class SpnegoUserPrincipal implements Principal
{
    private final String _name;
    private byte[] _token;
    private String _encodedToken;
    
    public SpnegoUserPrincipal(final String name, final String encodedToken) {
        this._name = name;
        this._encodedToken = encodedToken;
    }
    
    public SpnegoUserPrincipal(final String name, final byte[] token) {
        this._name = name;
        this._token = token;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public byte[] getToken() {
        if (this._token == null) {
            this._token = B64Code.decode(this._encodedToken);
        }
        return this._token;
    }
    
    public String getEncodedToken() {
        if (this._encodedToken == null) {
            this._encodedToken = new String(B64Code.encode(this._token, true));
        }
        return this._encodedToken;
    }
}
