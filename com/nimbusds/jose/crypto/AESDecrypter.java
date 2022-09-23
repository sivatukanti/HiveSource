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
import com.nimbusds.jose.jwk.OctetSequenceKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.KeyLengthException;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWEDecrypter;

@ThreadSafe
public class AESDecrypter extends AESCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    
    public AESDecrypter(final SecretKey kek) throws KeyLengthException {
        this(kek, null);
    }
    
    public AESDecrypter(final byte[] keyBytes) throws KeyLengthException {
        this(new SecretKeySpec(keyBytes, "AES"));
    }
    
    public AESDecrypter(final OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }
    
    public AESDecrypter(final SecretKey kek, final Set<String> defCritHeaders) throws KeyLengthException {
        super(kek);
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
        if (encryptedKey == null) {
            throw new JOSEException("Missing JWE encrypted key");
        }
        if (iv == null) {
            throw new JOSEException("Missing JWE initialization vector (IV)");
        }
        if (authTag == null) {
            throw new JOSEException("Missing JWE authentication tag");
        }
        this.critPolicy.ensureHeaderPasses(header);
        final JWEAlgorithm alg = header.getAlgorithm();
        final int keyLength = header.getEncryptionMethod().cekBitLength();
        SecretKey cek;
        if (alg.equals(JWEAlgorithm.A128KW) || alg.equals(JWEAlgorithm.A192KW) || alg.equals(JWEAlgorithm.A256KW)) {
            cek = AESKW.unwrapCEK(this.getKey(), encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        }
        else {
            if (!alg.equals(JWEAlgorithm.A128GCMKW) && !alg.equals(JWEAlgorithm.A192GCMKW) && !alg.equals(JWEAlgorithm.A256GCMKW)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, AESDecrypter.SUPPORTED_ALGORITHMS));
            }
            if (header.getIV() == null) {
                throw new JOSEException("Missing JWE \"iv\" header parameter");
            }
            final byte[] keyIV = header.getIV().decode();
            if (header.getAuthTag() == null) {
                throw new JOSEException("Missing JWE \"tag\" header parameter");
            }
            final byte[] keyTag = header.getAuthTag().decode();
            final AuthenticatedCipherText authEncrCEK = new AuthenticatedCipherText(encryptedKey.decode(), keyTag);
            cek = AESGCMKW.decryptCEK(this.getKey(), keyIV, authEncrCEK, keyLength, this.getJCAContext().getKeyEncryptionProvider());
        }
        return ContentCryptoProvider.decrypt(header, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
    }
}
