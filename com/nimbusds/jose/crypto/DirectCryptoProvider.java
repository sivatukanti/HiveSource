// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.KeyLengthException;
import java.util.Collections;
import java.util.LinkedHashSet;
import javax.crypto.SecretKey;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;

abstract class DirectCryptoProvider extends BaseJWEProvider
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    private final SecretKey cek;
    
    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.DIR);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
    }
    
    private static Set<EncryptionMethod> getCompatibleEncryptionMethods(final int cekLength) throws KeyLengthException {
        final Set<EncryptionMethod> encs = ContentCryptoProvider.COMPATIBLE_ENCRYPTION_METHODS.get(cekLength);
        if (encs == null) {
            throw new KeyLengthException("The Content Encryption Key length must be 128 bits (16 bytes), 192 bits (24 bytes), 256 bits (32 bytes), 384 bits (48 bytes) or 512 bites (64 bytes)");
        }
        return encs;
    }
    
    protected DirectCryptoProvider(final SecretKey cek) throws KeyLengthException {
        super(DirectCryptoProvider.SUPPORTED_ALGORITHMS, getCompatibleEncryptionMethods(ByteUtils.bitLength(cek.getEncoded())));
        this.cek = cek;
    }
    
    public SecretKey getKey() {
        return this.cek;
    }
}
