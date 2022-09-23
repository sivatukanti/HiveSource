// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JCAContext;
import java.util.Collections;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;
import com.nimbusds.jose.JWEProvider;

abstract class BaseJWEProvider implements JWEProvider
{
    private final Set<JWEAlgorithm> algs;
    private final Set<EncryptionMethod> encs;
    private final JWEJCAContext jcaContext;
    
    public BaseJWEProvider(final Set<JWEAlgorithm> algs, final Set<EncryptionMethod> encs) {
        this.jcaContext = new JWEJCAContext();
        if (algs == null) {
            throw new IllegalArgumentException("The supported JWE algorithm set must not be null");
        }
        this.algs = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
        if (encs == null) {
            throw new IllegalArgumentException("The supported encryption methods must not be null");
        }
        this.encs = encs;
    }
    
    @Override
    public Set<JWEAlgorithm> supportedJWEAlgorithms() {
        return this.algs;
    }
    
    @Override
    public Set<EncryptionMethod> supportedEncryptionMethods() {
        return this.encs;
    }
    
    @Override
    public JWEJCAContext getJCAContext() {
        return this.jcaContext;
    }
}
