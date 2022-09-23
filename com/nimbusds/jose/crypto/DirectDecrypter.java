// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Collection;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import java.util.Set;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.KeyLengthException;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWEDecrypter;

@ThreadSafe
public class DirectDecrypter extends DirectCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    
    public DirectDecrypter(final SecretKey key) throws KeyLengthException {
        super(key);
        this.critPolicy = new CriticalHeaderParamsDeferral();
    }
    
    public DirectDecrypter(final byte[] keyBytes) throws KeyLengthException {
        this(new SecretKeySpec(keyBytes, "AES"));
    }
    
    public DirectDecrypter(final OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }
    
    public DirectDecrypter(final SecretKey key, final Set<String> defCritHeaders) throws KeyLengthException {
        super(key);
        (this.critPolicy = new CriticalHeaderParamsDeferral()).setDeferredCriticalHeaderParams(defCritHeaders);
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
    public byte[] decrypt(final JWEHeader header, final Base64URL encryptedKey, final Base64URL iv, final Base64URL cipherText, final Base64URL authTag) throws JOSEException {
        if (encryptedKey != null) {
            throw new JOSEException("Unexpected present JWE encrypted key");
        }
        if (iv == null) {
            throw new JOSEException("Unexpected present JWE initialization vector (IV)");
        }
        if (authTag == null) {
            throw new JOSEException("Missing JWE authentication tag");
        }
        final JWEAlgorithm alg = header.getAlgorithm();
        if (!alg.equals(JWEAlgorithm.DIR)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, DirectDecrypter.SUPPORTED_ALGORITHMS));
        }
        this.critPolicy.ensureHeaderPasses(header);
        return ContentCryptoProvider.decrypt(header, null, iv, cipherText, authTag, this.getKey(), this.getJCAContext());
    }
}
