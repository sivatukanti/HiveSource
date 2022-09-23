// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JCAContext;
import com.nimbusds.jose.crypto.utils.ConstantTimeUtils;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import javax.crypto.SecretKey;
import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jose.JOSEException;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWSVerifier;

@ThreadSafe
public class MACVerifier extends MACProvider implements JWSVerifier, CriticalHeaderParamsAware
{
    private final CriticalHeaderParamsDeferral critPolicy;
    
    public MACVerifier(final byte[] secret) throws JOSEException {
        this(secret, null);
    }
    
    public MACVerifier(final String secretString) throws JOSEException {
        this(secretString.getBytes(StandardCharset.UTF_8));
    }
    
    public MACVerifier(final SecretKey secretKey) throws JOSEException {
        this(secretKey.getEncoded());
    }
    
    public MACVerifier(final OctetSequenceKey jwk) throws JOSEException {
        this(jwk.toByteArray());
    }
    
    public MACVerifier(final byte[] secret, final Set<String> defCritHeaders) throws JOSEException {
        super(secret, MACVerifier.SUPPORTED_ALGORITHMS);
        (this.critPolicy = new CriticalHeaderParamsDeferral()).setDeferredCriticalHeaderParams(defCritHeaders);
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
    public boolean verify(final JWSHeader header, final byte[] signedContent, final Base64URL signature) throws JOSEException {
        if (!this.critPolicy.headerPasses(header)) {
            return false;
        }
        final String jcaAlg = MACProvider.getJCAAlgorithmName(header.getAlgorithm());
        final byte[] expectedHMAC = HMAC.compute(jcaAlg, this.getSecret(), signedContent, this.getJCAContext().getProvider());
        return ConstantTimeUtils.areEqual(expectedHMAC, signature.decode());
    }
}
