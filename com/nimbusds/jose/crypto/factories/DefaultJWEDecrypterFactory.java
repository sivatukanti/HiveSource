// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto.factories;

import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.KeyLengthException;
import javax.crypto.SecretKey;
import java.security.interfaces.ECPrivateKey;
import java.security.PrivateKey;
import com.nimbusds.jose.KeyTypeException;
import java.security.interfaces.RSAPrivateKey;
import com.nimbusds.jose.JWEDecrypter;
import java.security.Key;
import com.nimbusds.jose.JWEHeader;
import java.util.Collections;
import com.nimbusds.jose.crypto.PasswordBasedDecrypter;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import java.util.Collection;
import com.nimbusds.jose.crypto.RSADecrypter;
import java.util.LinkedHashSet;
import com.nimbusds.jose.jca.JWEJCAContext;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.proc.JWEDecrypterFactory;

@ThreadSafe
public class DefaultJWEDecrypterFactory implements JWEDecrypterFactory
{
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    private final JWEJCAContext jcaContext;
    
    static {
        final Set<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.addAll(RSADecrypter.SUPPORTED_ALGORITHMS);
        algs.addAll(ECDHDecrypter.SUPPORTED_ALGORITHMS);
        algs.addAll(DirectDecrypter.SUPPORTED_ALGORITHMS);
        algs.addAll(AESDecrypter.SUPPORTED_ALGORITHMS);
        algs.addAll(PasswordBasedDecrypter.SUPPORTED_ALGORITHMS);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet((Set<? extends JWEAlgorithm>)algs);
        final Set<EncryptionMethod> encs = new LinkedHashSet<EncryptionMethod>();
        encs.addAll(RSADecrypter.SUPPORTED_ENCRYPTION_METHODS);
        encs.addAll(ECDHDecrypter.SUPPORTED_ENCRYPTION_METHODS);
        encs.addAll(DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS);
        encs.addAll(AESDecrypter.SUPPORTED_ENCRYPTION_METHODS);
        encs.addAll(PasswordBasedDecrypter.SUPPORTED_ENCRYPTION_METHODS);
        SUPPORTED_ENCRYPTION_METHODS = Collections.unmodifiableSet((Set<? extends EncryptionMethod>)encs);
    }
    
    public DefaultJWEDecrypterFactory() {
        this.jcaContext = new JWEJCAContext();
    }
    
    @Override
    public Set<JWEAlgorithm> supportedJWEAlgorithms() {
        return DefaultJWEDecrypterFactory.SUPPORTED_ALGORITHMS;
    }
    
    @Override
    public Set<EncryptionMethod> supportedEncryptionMethods() {
        return DefaultJWEDecrypterFactory.SUPPORTED_ENCRYPTION_METHODS;
    }
    
    @Override
    public JWEJCAContext getJCAContext() {
        return this.jcaContext;
    }
    
    @Override
    public JWEDecrypter createJWEDecrypter(final JWEHeader header, final Key key) throws JOSEException {
        JWEDecrypter decrypter;
        if (RSADecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) && RSADecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
            if (!(key instanceof RSAPrivateKey)) {
                throw new KeyTypeException(RSAPrivateKey.class);
            }
            final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)key;
            decrypter = new RSADecrypter(rsaPrivateKey);
        }
        else if (ECDHDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) && ECDHDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
            if (!(key instanceof ECPrivateKey)) {
                throw new KeyTypeException(ECPrivateKey.class);
            }
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)key;
            decrypter = new ECDHDecrypter(ecPrivateKey);
        }
        else if (DirectDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) && DirectDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
            if (!(key instanceof SecretKey)) {
                throw new KeyTypeException(SecretKey.class);
            }
            final SecretKey aesKey = (SecretKey)key;
            final DirectDecrypter directDecrypter = new DirectDecrypter(aesKey);
            if (!directDecrypter.supportedEncryptionMethods().contains(header.getEncryptionMethod())) {
                throw new KeyLengthException(header.getEncryptionMethod().cekBitLength(), header.getEncryptionMethod());
            }
            decrypter = directDecrypter;
        }
        else if (AESDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) && AESDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
            if (!(key instanceof SecretKey)) {
                throw new KeyTypeException(SecretKey.class);
            }
            final SecretKey aesKey = (SecretKey)key;
            final AESDecrypter aesDecrypter = new AESDecrypter(aesKey);
            if (!aesDecrypter.supportedJWEAlgorithms().contains(header.getAlgorithm())) {
                throw new KeyLengthException(header.getAlgorithm());
            }
            decrypter = aesDecrypter;
        }
        else {
            if (!PasswordBasedDecrypter.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()) || !PasswordBasedDecrypter.SUPPORTED_ENCRYPTION_METHODS.contains(header.getEncryptionMethod())) {
                throw new JOSEException("Unsupported JWE algorithm or encryption method");
            }
            if (!(key instanceof SecretKey)) {
                throw new KeyTypeException(SecretKey.class);
            }
            final byte[] password = key.getEncoded();
            decrypter = new PasswordBasedDecrypter(password);
        }
        decrypter.getJCAContext().setSecureRandom(this.jcaContext.getSecureRandom());
        decrypter.getJCAContext().setProvider(this.jcaContext.getProvider());
        decrypter.getJCAContext().setKeyEncryptionProvider(this.jcaContext.getKeyEncryptionProvider());
        decrypter.getJCAContext().setMACProvider(this.jcaContext.getMACProvider());
        decrypter.getJCAContext().setContentEncryptionProvider(this.jcaContext.getContentEncryptionProvider());
        return decrypter;
    }
}
