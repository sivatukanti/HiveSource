// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import com.nimbusds.jose.JOSEException;
import java.util.Collection;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.Signature;
import java.security.Provider;
import com.nimbusds.jose.JWSAlgorithm;

class RSASSA
{
    protected static Signature getSignerAndVerifier(final JWSAlgorithm alg, final Provider provider) throws JOSEException {
        PSSParameterSpec pssSpec = null;
        String jcaAlg;
        if (alg.equals(JWSAlgorithm.RS256)) {
            jcaAlg = "SHA256withRSA";
        }
        else if (alg.equals(JWSAlgorithm.RS384)) {
            jcaAlg = "SHA384withRSA";
        }
        else if (alg.equals(JWSAlgorithm.RS512)) {
            jcaAlg = "SHA512withRSA";
        }
        else if (alg.equals(JWSAlgorithm.PS256)) {
            jcaAlg = "SHA256withRSAandMGF1";
            pssSpec = new PSSParameterSpec("SHA256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
        }
        else if (alg.equals(JWSAlgorithm.PS384)) {
            jcaAlg = "SHA384withRSAandMGF1";
            pssSpec = new PSSParameterSpec("SHA384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1);
        }
        else {
            if (!alg.equals(JWSAlgorithm.PS512)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, RSASSAProvider.SUPPORTED_ALGORITHMS));
            }
            jcaAlg = "SHA512withRSAandMGF1";
            pssSpec = new PSSParameterSpec("SHA512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1);
        }
        Signature signature;
        try {
            if (provider != null) {
                signature = Signature.getInstance(jcaAlg, provider);
            }
            else {
                signature = Signature.getInstance(jcaAlg);
            }
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Unsupported RSASSA algorithm: " + e.getMessage(), e);
        }
        if (pssSpec != null) {
            try {
                signature.setParameter(pssSpec);
            }
            catch (InvalidAlgorithmParameterException e2) {
                throw new JOSEException("Invalid RSASSA-PSS salt length parameter: " + e2.getMessage(), e2);
            }
        }
        return signature;
    }
    
    private RSASSA() {
    }
}
