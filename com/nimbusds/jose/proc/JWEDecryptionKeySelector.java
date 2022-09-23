// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.proc;

import com.nimbusds.jose.KeySourceException;
import java.util.Iterator;
import com.nimbusds.jose.jwk.JWK;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
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
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWEDecryptionKeySelector<C extends SecurityContext> extends AbstractJWKSelectorWithSource<C> implements JWEKeySelector<C>
{
    private final JWEAlgorithm jweAlg;
    private final EncryptionMethod jweEnc;
    
    public JWEDecryptionKeySelector(final JWEAlgorithm jweAlg, final EncryptionMethod jweEnc, final JWKSource<C> jwkSource) {
        super(jwkSource);
        if (jweAlg == null) {
            throw new IllegalArgumentException("The JWE algorithm must not be null");
        }
        this.jweAlg = jweAlg;
        if (jweEnc == null) {
            throw new IllegalArgumentException("The JWE encryption method must not be null");
        }
        this.jweEnc = jweEnc;
    }
    
    public JWEAlgorithm getExpectedJWEAlgorithm() {
        return this.jweAlg;
    }
    
    public EncryptionMethod getExpectedJWEEncryptionMethod() {
        return this.jweEnc;
    }
    
    protected JWKMatcher createJWKMatcher(final JWEHeader jweHeader) {
        if (!this.getExpectedJWEAlgorithm().equals(jweHeader.getAlgorithm())) {
            return null;
        }
        if (!this.getExpectedJWEEncryptionMethod().equals(jweHeader.getEncryptionMethod())) {
            return null;
        }
        return new JWKMatcher.Builder().keyType(KeyType.forAlgorithm(this.getExpectedJWEAlgorithm())).keyID(jweHeader.getKeyID()).keyUses(KeyUse.ENCRYPTION, null).algorithms(this.getExpectedJWEAlgorithm(), null).build();
    }
    
    @Override
    public List<Key> selectJWEKeys(final JWEHeader jweHeader, final C context) throws KeySourceException {
        if (!this.jweAlg.equals(jweHeader.getAlgorithm()) || !this.jweEnc.equals(jweHeader.getEncryptionMethod())) {
            return Collections.emptyList();
        }
        final JWKMatcher jwkMatcher = this.createJWKMatcher(jweHeader);
        final List<JWK> jwkMatches = this.getJWKSource().get(new JWKSelector(jwkMatcher), context);
        final List<Key> sanitizedKeyList = new LinkedList<Key>();
        for (final Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (key instanceof PrivateKey || key instanceof SecretKey) {
                sanitizedKeyList.add(key);
            }
        }
        return sanitizedKeyList;
    }
}
