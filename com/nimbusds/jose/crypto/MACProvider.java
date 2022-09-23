// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.StandardCharset;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Set;

abstract class MACProvider extends BaseJWSProvider
{
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    private final byte[] secret;
    
    static {
        final Set<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.HS256);
        algs.add(JWSAlgorithm.HS384);
        algs.add(JWSAlgorithm.HS512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)algs);
    }
    
    protected static String getJCAAlgorithmName(final JWSAlgorithm alg) throws JOSEException {
        if (alg.equals(JWSAlgorithm.HS256)) {
            return "HMACSHA256";
        }
        if (alg.equals(JWSAlgorithm.HS384)) {
            return "HMACSHA384";
        }
        if (alg.equals(JWSAlgorithm.HS512)) {
            return "HMACSHA512";
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, MACProvider.SUPPORTED_ALGORITHMS));
    }
    
    protected MACProvider(final byte[] secret, final Set<JWSAlgorithm> supportedAlgs) throws KeyLengthException {
        super(supportedAlgs);
        if (secret.length < 32) {
            throw new KeyLengthException("The secret length must be at least 256 bits");
        }
        this.secret = secret;
    }
    
    public SecretKey getSecretKey() {
        return new SecretKeySpec(this.secret, "MAC");
    }
    
    public byte[] getSecret() {
        return this.secret;
    }
    
    public String getSecretString() {
        return new String(this.secret, StandardCharset.UTF_8);
    }
}
