// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.jwk.source.JWKSource;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
abstract class AbstractJWKSelectorWithSource<C extends SecurityContext>
{
    private final JWKSource<C> jwkSource;
    
    public AbstractJWKSelectorWithSource(final JWKSource<C> jwkSource) {
        if (jwkSource == null) {
            throw new IllegalArgumentException("The JWK source must not be null");
        }
        this.jwkSource = jwkSource;
    }
    
    public JWKSource<C> getJWKSource() {
        return this.jwkSource;
    }
}
