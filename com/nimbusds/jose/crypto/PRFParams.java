// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import com.nimbusds.jose.JWEAlgorithm;
import java.security.Provider;
import net.jcip.annotations.Immutable;

@Immutable
final class PRFParams
{
    private final String jcaMacAlg;
    private final Provider macProvider;
    private final int dkLen;
    
    public PRFParams(final String jcaMacAlg, final Provider macProvider, final int dkLen) {
        this.jcaMacAlg = jcaMacAlg;
        this.macProvider = macProvider;
        this.dkLen = dkLen;
    }
    
    public String getMACAlgorithm() {
        return this.jcaMacAlg;
    }
    
    public Provider getMacProvider() {
        return this.macProvider;
    }
    
    public int getDerivedKeyByteLength() {
        return this.dkLen;
    }
    
    public static PRFParams resolve(final JWEAlgorithm alg, final Provider macProvider) throws JOSEException {
        String jcaMagAlg;
        int dkLen;
        if (JWEAlgorithm.PBES2_HS256_A128KW.equals(alg)) {
            jcaMagAlg = "HmacSHA256";
            dkLen = 16;
        }
        else if (JWEAlgorithm.PBES2_HS384_A192KW.equals(alg)) {
            jcaMagAlg = "HmacSHA384";
            dkLen = 24;
        }
        else {
            if (!JWEAlgorithm.PBES2_HS512_A256KW.equals(alg)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, PasswordBasedCryptoProvider.SUPPORTED_ALGORITHMS));
            }
            jcaMagAlg = "HmacSHA512";
            dkLen = 32;
        }
        return new PRFParams(jcaMagAlg, macProvider, dkLen);
    }
}
