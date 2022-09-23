// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;
import com.nimbusds.jose.JOSEException;
import javax.crypto.SecretKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.util.StandardCharset;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWEEncrypter;

@ThreadSafe
public class PasswordBasedEncrypter extends PasswordBasedCryptoProvider implements JWEEncrypter
{
    public static final int MIN_SALT_LENGTH = 8;
    private final int saltLength;
    public static final int MIN_RECOMMENDED_ITERATION_COUNT = 1000;
    private final int iterationCount;
    
    public PasswordBasedEncrypter(final byte[] password, final int saltLength, final int iterationCount) {
        super(password);
        if (saltLength < 8) {
            throw new IllegalArgumentException("The minimum salt length (p2s) is 8 bytes");
        }
        this.saltLength = saltLength;
        if (iterationCount < 1000) {
            throw new IllegalArgumentException("The minimum recommended iteration count (p2c) is 1000");
        }
        this.iterationCount = iterationCount;
    }
    
    public PasswordBasedEncrypter(final String password, final int saltLength, final int iterationCount) {
        this(password.getBytes(StandardCharset.UTF_8), saltLength, iterationCount);
    }
    
    @Override
    public JWECryptoParts encrypt(final JWEHeader header, final byte[] clearText) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        final EncryptionMethod enc = header.getEncryptionMethod();
        final byte[] salt = new byte[this.saltLength];
        this.getJCAContext().getSecureRandom().nextBytes(salt);
        final byte[] formattedSalt = PBKDF2.formatSalt(alg, salt);
        final PRFParams prfParams = PRFParams.resolve(alg, this.getJCAContext().getMACProvider());
        final SecretKey psKey = PBKDF2.deriveKey(this.getPassword(), formattedSalt, this.iterationCount, prfParams);
        final JWEHeader updatedHeader = new JWEHeader.Builder(header).pbes2Salt(Base64URL.encode(salt)).pbes2Count(this.iterationCount).build();
        final SecretKey cek = ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
        final Base64URL encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, psKey, this.getJCAContext().getKeyEncryptionProvider()));
        return ContentCryptoProvider.encrypt(updatedHeader, clearText, cek, encryptedKey, this.getJCAContext());
    }
    
    public int getSaltLength() {
        return this.saltLength;
    }
    
    public int getIterationCount() {
        return this.iterationCount;
    }
}
