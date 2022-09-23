// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import net.jcip.annotations.Immutable;

@Immutable
final class AuthenticatedCipherText
{
    private final byte[] cipherText;
    private final byte[] authenticationTag;
    
    public AuthenticatedCipherText(final byte[] cipherText, final byte[] authenticationTag) {
        if (cipherText == null) {
            throw new IllegalArgumentException("The cipher text must not be null");
        }
        this.cipherText = cipherText;
        if (authenticationTag == null) {
            throw new IllegalArgumentException("The authentication tag must not be null");
        }
        this.authenticationTag = authenticationTag;
    }
    
    public byte[] getCipherText() {
        return this.cipherText;
    }
    
    public byte[] getAuthenticationTag() {
        return this.authenticationTag;
    }
}
