// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto.factories;

import java.security.interfaces.ECPublicKey;
import com.nimbusds.jose.JOSEException;
import java.security.interfaces.RSAPublicKey;
import com.nimbusds.jose.KeyTypeException;
import javax.crypto.SecretKey;
import com.nimbusds.jose.JWSVerifier;
import java.security.Key;
import com.nimbusds.jose.JWSHeader;
import java.util.Collections;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import java.util.Collection;
import com.nimbusds.jose.crypto.MACVerifier;
import java.util.LinkedHashSet;
import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.proc.JWSVerifierFactory;

@ThreadSafe
public class DefaultJWSVerifierFactory implements JWSVerifierFactory
{
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    private final JCAContext jcaContext;
    
    static {
        final Set<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.addAll(MACVerifier.SUPPORTED_ALGORITHMS);
        algs.addAll(RSASSAVerifier.SUPPORTED_ALGORITHMS);
        algs.addAll(ECDSAVerifier.SUPPORTED_ALGORITHMS);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)algs);
    }
    
    public DefaultJWSVerifierFactory() {
        this.jcaContext = new JCAContext();
    }
    
    @Override
    public Set<JWSAlgorithm> supportedJWSAlgorithms() {
        return DefaultJWSVerifierFactory.SUPPORTED_ALGORITHMS;
    }
    
    @Override
    public JCAContext getJCAContext() {
        return this.jcaContext;
    }
    
    @Override
    public JWSVerifier createJWSVerifier(final JWSHeader header, final Key key) throws JOSEException {
        JWSVerifier verifier;
        if (MACVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
            if (!(key instanceof SecretKey)) {
                throw new KeyTypeException(SecretKey.class);
            }
            final SecretKey macKey = (SecretKey)key;
            verifier = new MACVerifier(macKey);
        }
        else if (RSASSAVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
            if (!(key instanceof RSAPublicKey)) {
                throw new KeyTypeException(RSAPublicKey.class);
            }
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)key;
            verifier = new RSASSAVerifier(rsaPublicKey);
        }
        else {
            if (!ECDSAVerifier.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
                throw new JOSEException("Unsupported JWS algorithm: " + header.getAlgorithm());
            }
            if (!(key instanceof ECPublicKey)) {
                throw new KeyTypeException(ECPublicKey.class);
            }
            final ECPublicKey ecPublicKey = (ECPublicKey)key;
            verifier = new ECDSAVerifier(ecPublicKey);
        }
        verifier.getJCAContext().setSecureRandom(this.jcaContext.getSecureRandom());
        verifier.getJCAContext().setProvider(this.jcaContext.getProvider());
        return verifier;
    }
}
