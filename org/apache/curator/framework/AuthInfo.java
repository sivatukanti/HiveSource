// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework;

import java.util.Arrays;

public class AuthInfo
{
    final String scheme;
    final byte[] auth;
    
    public AuthInfo(final String scheme, final byte[] auth) {
        this.scheme = scheme;
        this.auth = auth;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public byte[] getAuth() {
        return this.auth;
    }
    
    @Override
    public String toString() {
        return "AuthInfo{scheme='" + this.scheme + '\'' + ", auth=" + Arrays.toString(this.auth) + '}';
    }
}
