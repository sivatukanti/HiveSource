// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
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
public class DirectEncrypter extends DirectCryptoProvider implements JWEEncrypter
{
    public DirectEncrypter(final SecretKey key) throws KeyLengthException {
        super(key);
    }
    
    public DirectEncrypter(final byte[] keyBytes) throws KeyLengthException {
        this(new SecretKeySpec(keyBytes, "AES"));
    }
    
    public DirectEncrypter(final OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }
    
    @Override
    public JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        if (!alg.equals(JWEAlgorithm.DIR)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, DirectEncrypter.SUPPORTED_ALGORITHMS));
        }
        final EncryptionMethod enc = header.getEncryptionMethod();
        if (enc.cekBitLength() != ByteUtils.safeBitLength(this.getKey().getEncoded())) {
            throw new KeyLengthException(enc.cekBitLength(), enc);
        }
        final Base64URL encryptedKey = null;
        return ContentCryptoProvider.encrypt(header, clearText, this.getKey(), encryptedKey, this.getJCAContext());
    }
}
