// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Set;

abstract class RSASSAProvider extends BaseJWSProvider
{
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    
    static {
        final Set<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.RS256);
        algs.add(JWSAlgorithm.RS384);
        algs.add(JWSAlgorithm.RS512);
        algs.add(JWSAlgorithm.PS256);
        algs.add(JWSAlgorithm.PS384);
        algs.add(JWSAlgorithm.PS512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)algs);
    }
    
    protected RSASSAProvider() {
        super(RSASSAProvider.SUPPORTED_ALGORITHMS);
    }
}
