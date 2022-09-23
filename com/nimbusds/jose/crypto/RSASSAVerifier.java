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
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import java.util.Set;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWSVerifier;

@ThreadSafe
public class RSASSAVerifier extends RSASSAProvider implements JWSVerifier, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    private final RSAPublicKey publicKey;
    
    public RSASSAVerifier(final RSAPublicKey publicKey) {
        this(publicKey, null);
    }
    
    public RSASSAVerifier(final RSAKey rsaJWK) throws JOSEException {
        this(rsaJWK.toRSAPublicKey(), null);
    }
    
    public RSASSAVerifier(final RSAPublicKey publicKey, final Set<String> defCritHeaders) {
        this.critPolicy = new CriticalHeaderParamsDeferral();
        if (publicKey == null) {
            throw new IllegalArgumentException("The public RSA key must not be null");
        }
        this.publicKey = publicKey;
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }
    
    public RSAPublicKey getPublicKey() {
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
        if (!this.critPolicy.headerPasses(header)) {
            return false;
        }
        final Signature verifier = RSASSA.getSignerAndVerifier(header.getAlgorithm(), this.getJCAContext().getProvider());
        try {
            verifier.initVerify(this.publicKey);
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid public RSA key: " + e.getMessage(), e);
        }
        try {
            verifier.update(signedContent);
            return verifier.verify(signature.decode());
        }
        catch (SignatureException ex) {
            return false;
        }
    }
}
