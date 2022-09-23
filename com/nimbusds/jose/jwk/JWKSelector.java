// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWKSelector
{
    private final JWKMatcher matcher;
    
    public JWKSelector(final JWKMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("The JWK matcher must not be null");
        }
        this.matcher = matcher;
    }
    
    public JWKMatcher getMatcher() {
        return this.matcher;
    }
    
    public List<JWK> select(final JWKSet jwkSet) {
        final List<JWK> selectedKeys = new LinkedList<JWK>();
        if (jwkSet == null) {
            return selectedKeys;
        }
        for (final JWK key : jwkSet.getKeys()) {
            if (this.matcher.matches(key)) {
                selectedKeys.add(key);
            }
        }
        return selectedKeys;
    }
}
