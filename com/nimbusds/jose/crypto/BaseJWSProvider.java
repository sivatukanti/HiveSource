// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.util.Collections;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Set;
import com.nimbusds.jose.JWSProvider;

abstract class BaseJWSProvider implements JWSProvider
{
    private final Set<JWSAlgorithm> algs;
    private final JCAContext jcaContext;
    
    public BaseJWSProvider(final Set<JWSAlgorithm> algs) {
        this.jcaContext = new JCAContext();
        if (algs == null) {
            throw new IllegalArgumentException("The supported JWS algorithm set must not be null");
        }
        this.algs = Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)algs);
    }
    
    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return this.algs;
    }
    
    @Override
    public JCAContext getJCAContext() {
        return this.jcaContext;
    }
}
