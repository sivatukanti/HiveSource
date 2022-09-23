// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.token;

import org.apache.kerby.kerberos.kerb.type.base.AuthToken;

public class TokenContext
{
    public boolean usingIdToken;
    public AuthToken token;
    
    public TokenContext() {
        this.usingIdToken = true;
        this.token = null;
    }
}
