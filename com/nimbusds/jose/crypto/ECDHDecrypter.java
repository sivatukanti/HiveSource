// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.crypto;

import com.nimbusds.jose.jca.JWEJCAContext;
import javax.crypto.SecretKey;
import java.security.interfaces.ECPublicKey;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.crypto.utils.ECChecks;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.JOSEException;
import java.util.Set;
import java.security.interfaces.ECPrivateKey;
import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JWEDecrypter;

public class ECDHDecrypter extends ECDHCryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware
{
    private final ECPrivateKey privateKey;
    private final CriticalHeaderParamsDeferral critPolicy;
    
    public ECDHDecrypter(final ECPrivateKey privateKey) throws JOSEException {
        this(privateKey, null);
    }
    
    public ECDHDecrypter(final ECKey ecJWK) throws JOSEException {
        super(ecJWK.getCurve());
        this.critPolicy = new CriticalHeaderParamsDeferral();
        if (!ecJWK.isPrivate()) {
            throw new JOSEException("The EC JWK doesn't contain a private part");
        }
        this.privateKey = ecJWK.toECPrivateKey();
    }
    
    public ECDHDecrypter(final ECPrivateKey privateKey, final Set<String> defCritHeaders) throws JOSEException {
        super(ECKey.Curve.forECParameterSpec(privateKey.getParams()));
        (this.critPolicy = new CriticalHeaderParamsDeferral()).setDeferredCriticalHeaderParams(defCritHeaders);
        this.privateKey = privateKey;
    }
    
    public ECPrivateKey getPrivateKey() {
        return this.privateKey;
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
    public byte[] decrypt(final JWEHeader header, final Base64URL encryptedKey, final Base64URL iv, final Base64URL cipherText, final Base64URL authTag) throws JOSEException {
        final JWEAlgorithm alg = header.getAlgorithm();
        final ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
        this.critPolicy.ensureHeaderPasses(header);
        final ECKey ephemeralKey = header.getEphemeralPublicKey();
        if (ephemeralKey == null) {
            throw new JOSEException("Missing ephemeral public EC key \"epk\" JWE header parameter");
        }
        final ECPublicKey ephemeralPublicKey = ephemeralKey.toECPublicKey();
        if (!ECChecks.isPointOnCurve(ephemeralPublicKey, this.getPrivateKey())) {
            throw new JOSEException("Invalid ephemeral public EC key: Point(s) not on the expected curve");
        }
        final SecretKey Z = ECDH.deriveSharedSecret(ephemeralPublicKey, this.privateKey, this.getJCAContext().getKeyEncryptionProvider());
        this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
        final SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
        SecretKey cek;
        if (algMode.equals(ECDH.AlgorithmMode.DIRECT)) {
            cek = sharedKey;
        }
        else {
            if (!algMode.equals(ECDH.AlgorithmMode.KW)) {
                throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + algMode);
            }
            if (encryptedKey == null) {
                throw new JOSEException("Missing JWE encrypted key");
            }
            cek = AESKW.unwrapCEK(sharedKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        }
        return ContentCryptoProvider.decrypt(header, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
    }
}
