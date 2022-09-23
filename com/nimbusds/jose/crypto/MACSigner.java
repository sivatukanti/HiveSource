// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import javax.crypto.SecretKey;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.util.ByteUtils;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import com.nimbusds.jose.JWSAlgorithm;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWSSigner;

@ThreadSafe
public class MACSigner extends MACProvider implements JWSSigner
{
    public static int getMinRequiredSecretLength(final JWSAlgorithm alg) throws JOSEException {
        if (JWSAlgorithm.HS256.equals(alg)) {
            return 256;
        }
        if (JWSAlgorithm.HS384.equals(alg)) {
            return 384;
        }
        if (JWSAlgorithm.HS512.equals(alg)) {
            return 512;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, MACSigner.SUPPORTED_ALGORITHMS));
    }
    
    public static Set<JWSAlgorithm> getCompatibleAlgorithms(final int secretLength) {
        final Set<JWSAlgorithm> hmacAlgs = new LinkedHashSet<JWSAlgorithm>();
        if (secretLength >= 256) {
            hmacAlgs.add(JWSAlgorithm.HS256);
        }
        if (secretLength >= 384) {
            hmacAlgs.add(JWSAlgorithm.HS384);
        }
        if (secretLength >= 512) {
            hmacAlgs.add(JWSAlgorithm.HS512);
        }
        return Collections.unmodifiableSet((Set<? extends JWSAlgorithm>)hmacAlgs);
    }
    
    public MACSigner(final byte[] secret) throws KeyLengthException {
        super(secret, getCompatibleAlgorithms(ByteUtils.bitLength(secret.length)));
    }
    
    public MACSigner(final String secretString) throws KeyLengthException {
        this(secretString.getBytes(StandardCharset.UTF_8));
    }
    
    public MACSigner(final SecretKey secretKey) throws KeyLengthException {
        this(secretKey.getEncoded());
    }
    
    public MACSigner(final OctetSequenceKey jwk) throws KeyLengthException {
        this(jwk.toByteArray());
    }
    
    @Override
    public Base64URL sign(final JWSHeader header, final byte[] signingInput) throws JOSEException {
        final int minRequiredLength = getMinRequiredSecretLength(header.getAlgorithm());
        if (this.getSecret().length < ByteUtils.byteLength(minRequiredLength)) {
            throw new KeyLengthException("The secret length for " + header.getAlgorithm() + " must be at least " + minRequiredLength + " bits");
        }
        final String jcaAlg = MACProvider.getJCAAlgorithmName(header.getAlgorithm());
        final byte[] hmac = HMAC.compute(jcaAlg, this.getSecret(), signingInput, this.getJCAContext().getProvider());
        return Base64URL.encode(hmac);
    }
}
