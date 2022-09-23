// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.util.Set;
import com.nimbusds.jose.jca.JCAContext;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.PrivateKey;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.JWSSigner;

@ThreadSafe
public class RSASSASigner extends RSASSAProvider implements JWSSigner
{
    private final PrivateKey privateKey;
    
    public RSASSASigner(final PrivateKey privateKey) {
        if (!"RSA".equalsIgnoreCase(privateKey.getAlgorithm())) {
            throw new IllegalArgumentException("The private key algorithm must be RSA");
        }
        this.privateKey = privateKey;
    }
    
    public RSASSASigner(final RSAKey rsaJWK) throws JOSEException {
        if (!rsaJWK.isPrivate()) {
            throw new JOSEException("The RSA JWK doesn't contain a private part");
        }
        this.privateKey = rsaJWK.toPrivateKey();
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    @Override
    public Base64URL sign(final JWSHeader header, final byte[] signingInput) throws JOSEException {
        final Signature signer = RSASSA.getSignerAndVerifier(header.getAlgorithm(), this.getJCAContext().getProvider());
        try {
            signer.initSign(this.privateKey);
            signer.update(signingInput);
            return Base64URL.encode(signer.sign());
        }
        catch (InvalidKeyException e) {
            throw new JOSEException("Invalid private RSA key: " + e.getMessage(), e);
        }
        catch (SignatureException e2) {
            throw new JOSEException("RSA signature exception: " + e2.getMessage(), e2);
        }
    }
}
