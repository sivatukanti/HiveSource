// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.KeyLengthException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import javax.crypto.SecretKey;
import java.util.Map;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;

abstract class AESCryptoProvider extends BaseJWEProvider
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    public static final Map<Integer, Set<JWEAlgorithm>> COMPATIBLE_ALGORITHMS;
    private final SecretKey kek;
    
    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.A128KW);
        algs.add(JWEAlgorithm.A192KW);
        algs.add(JWEAlgorithm.A256KW);
        algs.add(JWEAlgorithm.A128GCMKW);
        algs.add(JWEAlgorithm.A192GCMKW);
        algs.add(JWEAlgorithm.A256GCMKW);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
        final Map<Integer, Set<JWEAlgorithm>> algsMap = new HashMap<Integer, Set<JWEAlgorithm>>();
        final Set<JWEAlgorithm> bit128Algs = new HashSet<JWEAlgorithm>();
        final Set<JWEAlgorithm> bit192Algs = new HashSet<JWEAlgorithm>();
        final Set<JWEAlgorithm> bit256Algs = new HashSet<JWEAlgorithm>();
        bit128Algs.add(JWEAlgorithm.A128GCMKW);
        bit128Algs.add(JWEAlgorithm.A128KW);
        bit192Algs.add(JWEAlgorithm.A192GCMKW);
        bit192Algs.add(JWEAlgorithm.A192KW);
        bit256Algs.add(JWEAlgorithm.A256GCMKW);
        bit256Algs.add(JWEAlgorithm.A256KW);
        algsMap.put(128, Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)bit128Algs));
        algsMap.put(192, Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)bit192Algs));
        algsMap.put(256, Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)bit256Algs));
        COMPATIBLE_ALGORITHMS = Collections.unmodifiableMap((Map<? extends Integer, ? extends Set<JWEAlgorithm>>)algsMap);
    }
    
    private static Set<JWEAlgorithm> getCompatibleJWEAlgorithms(final int kekLength) throws KeyLengthException {
        final Set<JWEAlgorithm> algs = AESCryptoProvider.COMPATIBLE_ALGORITHMS.get(kekLength);
        if (algs == null) {
            throw new KeyLengthException("The Key Encryption Key length must be 128 bits (16 bytes), 192 bits (24 bytes) or 256 bits (32 bytes)");
        }
        return algs;
    }
    
    protected AESCryptoProvider(final SecretKey kek) throws KeyLengthException {
        super(getCompatibleJWEAlgorithms(ByteUtils.bitLength(kek.getEncoded())), ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);
        this.kek = kek;
    }
    
    public SecretKey getKey() {
        return this.kek;
    }
}
