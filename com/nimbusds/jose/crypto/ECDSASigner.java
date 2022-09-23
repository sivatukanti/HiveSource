// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.util.Set;
import com.nimbusds.jose.jca.JCAContext;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Collection;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEException;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.PrivateKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWSSigner;

@ThreadSafe
public class ECDSASigner extends ECDSAProvider implements JWSSigner
{
    private final PrivateKey privateKey;
    
    public ECDSASigner(final ECPrivateKey privateKey) throws JOSEException {
        super(ECDSA.resolveAlgorithm(privateKey));
        this.privateKey = privateKey;
    }
    
    public ECDSASigner(final PrivateKey privateKey, final com.nimbusds.jose.jwk.ECKey.Curve curve) throws JOSEException {
        super(ECDSA.resolveAlgorithm(curve));
        if (!"EC".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be EC");
        }
        this.privateKey = privateKey;
    }
    
    public ECDSASigner(final com.nimbusds.jose.jwk.ECKey ecJWK) throws JOSEException {
        super(ECDSA.resolveAlgorithm(ecJWK.getCurve()));
        if (!ecJWK.isPrivate()) {
            throw new JOSEException("The EC JWK doesn't contain a private part");
        }
        this.privateKey = ecJWK.toPrivateKey();
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    @Override
    public Base64URL sign(final JWSHeader header, final byte[] signingInput) throws JOSEException {
        final JWSAlgorithm alg = header.getAlgorithm();
        if (!this.supportedJWSAlgorithms().contains(alg)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, this.supportedJWSAlgorithms()));
        }
        byte[] jcaSignature;
        try {
            final Signature dsa = ECDSA.getSignerAndVerifier(alg, this.getJCAContext().getProvider());
            dsa.initSign(this.privateKey, this.getJCAContext().getSecureRandom());
            dsa.update(signingInput);
            jcaSignature = dsa.sign();
        }
        catch (InvalidKeyException | SignatureException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException(e.getMessage(), e);
        }
        final int rsByteArrayLength = ECDSA.getSignatureByteArrayLength(header.getAlgorithm());
        final byte[] jwsSignature = ECDSA.transcodeSignatureToConcat(jcaSignature, rsByteArrayLength);
        return Base64URL.encode(jwsSignature);
    }
}
