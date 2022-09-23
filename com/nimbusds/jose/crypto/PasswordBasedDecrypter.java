// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import javax.crypto.SecretKey;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import java.util.Set;
import com.nimbusds.jose.util.StandardCharset;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWEDecrypter;

@ThreadSafe
public class PasswordBasedDecrypter extends PasswordBasedCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    
    public PasswordBasedDecrypter(final byte[] password) {
        super(password);
        this.critPolicy = new CriticalHeaderParamsDeferral();
    }
    
    public PasswordBasedDecrypter(final String password) {
        super(password.getBytes(StandardCharset.UTF_8));
        this.critPolicy = new CriticalHeaderParamsDeferral();
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
        if (header.getPBES2Salt() == null) {
            throw new JOSEException("Missing JWE \"p2s\" header parameter");
        }
        final byte[] salt = header.getPBES2Salt().decode();
        if (header.getPBES2Count() < 1) {
            throw new JOSEException("Missing JWE \"p2c\" header parameter");
        }
        final int iterationCount = header.getPBES2Count();
        this.critPolicy.ensureHeaderPasses(header);
        final JWEAlgorithm alg = header.getAlgorithm();
        final byte[] formattedSalt = PBKDF2.formatSalt(alg, salt);
        final PRFParams prfParams = PRFParams.resolve(alg, this.getJCAContext().getMACProvider());
        final SecretKey psKey = PBKDF2.deriveKey(this.getPassword(), formattedSalt, iterationCount, prfParams);
        final SecretKey cek = AESKW.unwrapCEK(psKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        return ContentCryptoProvider.decrypt(header, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
    }
}
