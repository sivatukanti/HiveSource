// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;

abstract class RSACryptoProvider extends BaseJWEProvider
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    
    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.RSA1_5);
        algs.add(JWEAlgorithm.RSA_OAEP);
        algs.add(JWEAlgorithm.RSA_OAEP_256);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
    }
    
    protected RSACryptoProvider() {
        super(RSACryptoProvider.SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);
    }
}
