// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.KeyPairGenerator;
import java.security.spec.ECParameterSpec;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import java.security.interfaces.ECPrivateKey;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import java.security.interfaces.ECPublicKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWEEncrypter;

@ThreadSafe
public class ECDHEncrypter extends ECDHCryptoProvider implements JWEEncrypter
{
    private final ECPublicKey publicKey;
    
    public ECDHEncrypter(final ECPublicKey publicKey) throws JOSEException {
        super(ECKey.Curve.forECParameterSpec(publicKey.getParams()));
        this.publicKey = publicKey;
    }
    
    public ECDHEncrypter(final ECKey ecJWK) throws JOSEException {
        super(ecJWK.getCurve());
        this.publicKey = ecJWK.toECPublicKey();
    }
    
    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }
    
    @Override
    public JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        final ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
        final EncryptionMethod enc = header.getEncryptionMethod();
        final KeyPair ephemeralKeyPair = this.generateEphemeralKeyPair(this.publicKey.getParams());
        final ECPublicKey ephemeralPublicKey = (ECPublicKey)ephemeralKeyPair.getPublic();
        final ECPrivateKey ephemeralPrivateKey = (ECPrivateKey)ephemeralKeyPair.getPrivate();
        final SecretKey Z = ECDH.deriveSharedSecret(this.publicKey, ephemeralPrivateKey, this.getJCAContext().getKeyEncryptionProvider());
        this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
        final SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
        SecretKey cek;
        Base64URL encryptedKey;
        if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
            cek = sharedKey;
            encryptedKey = null;
        }
        else {
            if (!algMode.equals(ECDH.AlgorithmMode.KW)) {
                throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
            }
            cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
            encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, sharedKey, this.getJCAContext().getKeyEncryptionProvider()));
        }
        final JWEHeader updatedHeader = new JWEHeader.Builder(header).ephemeralPublicKey(new ECKey.Builder(this.getCurve(), ephemeralPublicKey).build()).build();
        return ContentCryptoProvider.encrypt(updatedHeader, clearText, cek, encryptedKey, this.getJCAContext());
    }
    
    private KeyPair generateEphemeralKeyPair(final ECParameterSpec ecParameterSpec) throws JOSEException {
        final Provider keProvider = this.getJCAContext().getKeyEncryptionProvider();
        try {
            KeyPairGenerator generator;
            if (keProvider != null) {
                generator = KeyPairGenerator.getInstance("EC", keProvider);
            }
            else {
                generator = KeyPairGenerator.getInstance("EC");
            }
            generator.initialize(ecParameterSpec);
            return generator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex2) {
            final GeneralSecurityException ex;
            final GeneralSecurityException e = ex;
            throw new JOSEException("Couldn't generate ephemeral EC key pair: " + e.getMessage(), e);
        }
    }
}
