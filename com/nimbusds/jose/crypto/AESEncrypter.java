// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.util.Container;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.KeyLengthException;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWEEncrypter;

@ThreadSafe
public class AESEncrypter extends AESCryptoProvider implements JWEEncrypter
{
    public AESEncrypter(final SecretKey kek) throws KeyLengthException {
        super(kek);
    }
    
    public AESEncrypter(final byte[] keyBytes) throws KeyLengthException {
        this(new SecretKeySpec(keyBytes, "AES"));
    }
    
    public AESEncrypter(final OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }
    
    @Override
    public JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        AlgFamily algFamily;
        if (alg.equals(JWEAlgorithm.A128KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        }
        else if (alg.equals(JWEAlgorithm.A192KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        }
        else if (alg.equals(JWEAlgorithm.A256KW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256KW encryption");
            }
            algFamily = AlgFamily.AESKW;
        }
        else if (alg.equals(JWEAlgorithm.A128GCMKW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 128) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 128 bits for A128GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        }
        else if (alg.equals(JWEAlgorithm.A192GCMKW)) {
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 192) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 192 bits for A192GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        }
        else {
            if (!alg.equals(JWEAlgorithm.A256GCMKW)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, AESEncrypter.SUPPORTED_ALGORITHMS));
            }
            if (ByteUtils.safeBitLength(this.getKey().getEncoded()) != 256) {
                throw new KeyLengthException("The Key Encryption Key (KEK) length must be 256 bits for A256GCMKW encryption");
            }
            algFamily = AlgFamily.AESGCMKW;
        }
        final EncryptionMethod enc = header.getEncryptionMethod();
        final SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        Base64URL encryptedKey;
        JWEHeader updatedHeader;
        if (AlgFamily.AESKW.equals(algFamily)) {
            encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, this.getKey(), this.getJCAContext().getKeyEncryptionProvider()));
            updatedHeader = header;
        }
        else {
            if (!AlgFamily.AESGCMKW.equals(algFamily)) {
                throw new JOSEException("Unexpected JWE algorithm: " + alg);
            }
            final Container<byte[]> keyIV = new Container<byte[]>(AESGCM.generateIV(this.getJCAContext().getSecureRandom()));
            final AuthenticatedCipherText authCiphCEK = AESGCMKW.encryptCEK(cek, keyIV, this.getKey(), this.getJCAContext().getKeyEncryptionProvider());
            encryptedKey = Base64URL.encode(authCiphCEK.getCipherText());
            updatedHeader = new JWEHeader.Builder(header).iv(Base64URL.encode(keyIV.get())).authTag(Base64URL.encode(authCiphCEK.getAuthenticationTag())).build();
        }
        return ContentCryptoProvider.encrypt(updatedHeader, clearText, cek, encryptedKey, this.getJCAContext());
    }
    
    private enum AlgFamily
    {
        AESKW("AESKW", 0), 
        AESGCMKW("AESGCMKW", 1);
        
        private AlgFamily(final String name, final int ordinal) {
        }
    }
}
