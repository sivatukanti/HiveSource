// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import javax.crypto.SecretKey;
import java.util.Collection;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import java.util.Set;
import java.security.PrivateKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWEDecrypter;

@ThreadSafe
public class RSADecrypter extends RSACryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    private final PrivateKey privateKey;
    private Exception cekDecryptionException;
    
    public RSADecrypter(final PrivateKey privateKey) {
        this(privateKey, null);
    }
    
    public RSADecrypter(final RSAKey rsaJWK) throws JOSEException {
        this.critPolicy = new CriticalHeaderParamsDeferral();
        if (!rsaJWK.isPrivate()) {
            throw new JOSEException("The RSA JWK doesn't contain a private part");
        }
        this.privateKey = rsaJWK.toPrivateKey();
    }
    
    public RSADecrypter(final PrivateKey privateKey, final Set<String> defCritHeaders) {
        this.critPolicy = new CriticalHeaderParamsDeferral();
        if (privateKey == null) {
            throw new IllegalArgumentException("The private RSA key must not be null");
        }
        if (!privateKey.getAlgorithm().equalsIgnoreCase("RSA")) {
            throw new IllegalArgumentException("The private key algorithm must be RSA");
        }
        this.privateKey = privateKey;
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
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
        SecretKey cek;
        if (alg.equals(JWEAlgorithm.RSA1_5)) {
            final int keyLength = header.getEncryptionMethod().cekBitLength();
            final SecretKey randomCEK = ContentCryptoProvider.generateCEK(header.getEncryptionMethod(), this.getJCAContext().getSecureRandom());
            try {
                cek = RSA1_5.decryptCEK(this.privateKey, encryptedKey.decode(), keyLength, this.getJCAContext().getKeyEncryptionProvider());
                if (cek == null) {
                    cek = randomCEK;
                }
            }
            catch (Exception e) {
                this.cekDecryptionException = e;
                cek = randomCEK;
            }
            this.cekDecryptionException = null;
        }
        else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
            cek = RSA_OAEP.decryptCEK(this.privateKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        }
        else {
            if (!alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, RSADecrypter.SUPPORTED_ALGORITHMS));
            }
            cek = RSA_OAEP_256.decryptCEK(this.privateKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        }
        return ContentCryptoProvider.decrypt(header, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
    }
    
    public Exception getCEKDecryptionException() {
        return this.cekDecryptionException;
    }
}
