// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk.source;

import javax.crypto.SecretKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import net.jcip.annotations.Immutable;
import com.nimbusds.jose.proc.SecurityContext;

@Immutable
public class ImmutableSecret<C extends SecurityContext> extends ImmutableJWKSet<C>
{
    public ImmutableSecret(final byte[] secret) {
        super(new JWKSet(new OctetSequenceKey.Builder(secret).build()));
    }
    
    public ImmutableSecret(final SecretKey secretKey) {
        super(new JWKSet(new OctetSequenceKey.Builder(secretKey).build()));
    }
    
    public byte[] getSecret() {
        return this.getJWKSet().getKeys().get(0).toByteArray();
    }
    
    public SecretKey getSecretKey() {
        return this.getJWKSet().getKeys().get(0).toSecretKey();
    }
}
