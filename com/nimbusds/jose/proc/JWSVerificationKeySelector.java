// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.KeySourceException;
import java.util.Iterator;
import com.nimbusds.jose.jwk.JWK;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import com.nimbusds.jose.jwk.KeyConverter;
import java.util.LinkedList;
import com.nimbusds.jose.jwk.JWKSelector;
import java.util.Collections;
import java.security.Key;
import java.util.List;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.JWSAlgorithm;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWSVerificationKeySelector<C extends SecurityContext> extends AbstractJWKSelectorWithSource<C> implements JWSKeySelector<C>
{
    private final JWSAlgorithm jwsAlg;
    
    public JWSVerificationKeySelector(final JWSAlgorithm jwsAlg, final JWKSource<C> jwkSource) {
        super(jwkSource);
        if (jwsAlg == null) {
            throw new IllegalArgumentException("The JWS algorithm must not be null");
        }
        this.jwsAlg = jwsAlg;
    }
    
    public JWSAlgorithm getExpectedJWSAlgorithm() {
        return this.jwsAlg;
    }
    
    protected JWKMatcher createJWKMatcher(final JWSHeader jwsHeader) {
        if (!this.getExpectedJWSAlgorithm().equals(jwsHeader.getAlgorithm())) {
            return null;
        }
        if (JWSAlgorithm.Family.RSA.contains(this.getExpectedJWSAlgorithm()) || JWSAlgorithm.Family.EC.contains(this.getExpectedJWSAlgorithm())) {
            return new JWKMatcher.Builder().keyType(KeyType.forAlgorithm(this.getExpectedJWSAlgorithm())).keyID(jwsHeader.getKeyID()).keyUses(KeyUse.SIGNATURE, null).algorithms(this.getExpectedJWSAlgorithm(), null).build();
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(this.getExpectedJWSAlgorithm())) {
            return new JWKMatcher.Builder().keyType(KeyType.forAlgorithm(this.getExpectedJWSAlgorithm())).keyID(jwsHeader.getKeyID()).privateOnly(true).algorithms(this.getExpectedJWSAlgorithm(), null).build();
        }
        return null;
    }
    
    @Override
    public List<Key> selectJWSKeys(final JWSHeader jwsHeader, final C context) throws KeySourceException {
        if (!this.jwsAlg.equals(jwsHeader.getAlgorithm())) {
            return Collections.emptyList();
        }
        final JWKMatcher jwkMatcher = this.createJWKMatcher(jwsHeader);
        if (jwkMatcher == null) {
            return Collections.emptyList();
        }
        final List<JWK> jwkMatches = this.getJWKSource().get(new JWKSelector(jwkMatcher), context);
        final List<Key> sanitizedKeyList = new LinkedList<Key>();
        for (final Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (key instanceof PublicKey || key instanceof SecretKey) {
                sanitizedKeyList.add(key);
            }
        }
        return sanitizedKeyList;
    }
}
