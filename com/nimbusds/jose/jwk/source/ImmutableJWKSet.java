// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWK;
import java.util.List;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import net.jcip.annotations.Immutable;
import com.nimbusds.jose.proc.SecurityContext;

@Immutable
public class ImmutableJWKSet<C extends SecurityContext> implements JWKSource<C>
{
    private final JWKSet jwkSet;
    
    public ImmutableJWKSet(final JWKSet jwkSet) {
        if (jwkSet == null) {
            throw new IllegalArgumentException("The JWK set must not be null");
        }
        this.jwkSet = jwkSet;
    }
    
    public JWKSet getJWKSet() {
        return this.jwkSet;
    }
    
    @Override
    public List<JWK> get(final JWKSelector jwkSelector, final C context) {
        return jwkSelector.select(this.jwkSet);
    }
}
