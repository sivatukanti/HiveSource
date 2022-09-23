// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;
import javax.crypto.SecretKey;
import com.nimbusds.jose.EncryptionMethod;
import java.util.Collection;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWEEncrypter;

@ThreadSafe
public class RSAEncrypter extends RSACryptoProvider implements JWEEncrypter
{
    private final RSAPublicKey publicKey;
    
    public RSAEncrypter(final RSAPublicKey publicKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("The public RSA key must not be null");
        }
        this.publicKey = publicKey;
    }
    
    public RSAEncrypter(final RSAKey rsaJWK) throws JOSEException {
        this(rsaJWK.toRSAPublicKey());
    }
    
    public RSAPublicKey getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        final EncryptionMethod enc = header.getEncryptionMethod();
        final SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        Base64URL encryptedKey;
        if (alg.equals(JWEAlgorithm.RSA1_5)) {
            encryptedKey = Base64URL.encode(RSA1_5.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
        }
        else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
            encryptedKey = Base64URL.encode(RSA_OAEP.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
        }
        else {
            if (!alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, RSAEncrypter.SUPPORTED_ALGORITHMS));
            }
            encryptedKey = Base64URL.encode(RSA_OAEP_256.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
        }
        return ContentCryptoProvider.encrypt(header, clearText, cek, encryptedKey, this.getJCAContext());
    }
}
