// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JCAContext;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Collection;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.JOSEException;
import java.util.Set;
import java.security.interfaces.ECPublicKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWSVerifier;

@ThreadSafe
public class ECDSAVerifier extends ECDSAProvider implements JWSVerifier, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    private final ECPublicKey publicKey;
    
    public ECDSAVerifier(final ECPublicKey publicKey) throws JOSEException {
        this(publicKey, null);
    }
    
    public ECDSAVerifier(final ECKey ecJWK) throws JOSEException {
        this(ecJWK.toECPublicKey());
    }
    
    public ECDSAVerifier(final ECPublicKey publicKey, final Set<String> defCritHeaders) throws JOSEException {
        super(ECDSA.resolveAlgorithm(publicKey));
        this.critPolicy = new CriticalHeaderParamsDeferral();
        this.publicKey = publicKey;
        if (!ECChecks.isPointOnCurve(publicKey, ECKey.Curve.forJWSAlgoritm(this.supportedECDSAAlgorithm()).toECParameterSpec())) {
            throw new JOSEException("Curve / public key parameters mismatch");
        }
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }
    
    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public Set<String> getProcessedCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }
    
    @Override
    public Set<String> getDeferredCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }
    
    @Override
    public boolean verify(final JWSHeader header, final byte[] signedContent, final Base64URL signature) throws JOSEException {
        final JWSAlgorithm alg = header.getAlgorithm();
        if (!this.supportedJWSAlgorithms().contains(alg)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, this.supportedJWSAlgorithms()));
        }
        if (!this.critPolicy.headerPasses(header)) {
            return false;
        }
        final byte[] jwsSignature = signature.decode();
        byte[] derSignature;
        try {
            derSignature = ECDSA.transcodeSignatureToDER(jwsSignature);
        }
        catch (JOSEException ex) {
            return false;
        }
        final Signature sig = ECDSA.getSignerAndVerifier(alg, this.getJCAContext().getProvider());
        try {
            sig.initVerify(this.publicKey);
            sig.update(signedContent);
            return sig.verify(derSignature);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid EC public key: " + e.getMessage(), e);
        }
        catch (SignatureException ex2) {
            return false;
        }
    }
}
