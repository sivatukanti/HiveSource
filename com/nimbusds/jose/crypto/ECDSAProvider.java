// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Set;

abstract class ECDSAProvider extends BaseJWSProvider
{
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    
    static {
        final Set<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.ES256);
        algs.add(JWSAlgorithm.ES384);
        algs.add(JWSAlgorithm.ES512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)algs);
    }
    
    protected ECDSAProvider(final JWSAlgorithm alg) throws JOSEException {
        super(new HashSet<JWSAlgorithm>(Collections.singletonList(alg)));
        if (!ECDSAProvider.SUPPORTED_ALGORITHMS.contains(alg)) {
            throw new JOSEException("Unsupported EC DSA algorithm: " + alg);
        }
    }
    
    public JWSAlgorithm supportedECDSAAlgorithm() {
        return this.supportedJWSAlgorithms().iterator().next();
    }
}
