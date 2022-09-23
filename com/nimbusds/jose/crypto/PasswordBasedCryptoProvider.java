// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;

abstract class PasswordBasedCryptoProvider extends BaseJWEProvider
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    private final byte[] password;
    
    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.PBES2_HS256_A128KW);
        algs.add(JWEAlgorithm.PBES2_HS384_A192KW);
        algs.add(JWEAlgorithm.PBES2_HS512_A256KW);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
    }
    
    protected PasswordBasedCryptoProvider(final byte[] password) {
        super(PasswordBasedCryptoProvider.SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("The password must not be null or empty");
        }
        this.password = password;
    }
    
    public byte[] getPassword() {
        return this.password;
    }
    
    public String getPasswordString() {
        return new String(this.password, StandardCharset.UTF_8);
    }
}
