// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.security.KeyPair;
import java.util.Iterator;
import com.nimbusds.jose.JOSEException;
import java.util.LinkedList;
import java.util.Collections;
import java.security.Key;
import java.util.List;

public class KeyConverter
{
    public static List<Key> toJavaKeys(final List<JWK> jwkList) {
        if (jwkList == null) {
            return Collections.emptyList();
        }
        final List<Key> out = new LinkedList<Key>();
        for (final JWK jwk : jwkList) {
            try {
                if (jwk instanceof AssymetricJWK) {
                    final KeyPair keyPair = ((AssymetricJWK)jwk).toKeyPair();
                    out.add(keyPair.getPublic());
                    if (keyPair.getPrivate() == null) {
                        continue;
                    }
                    out.add(keyPair.getPrivate());
                }
                else {
                    if (!(jwk instanceof SecretJWK)) {
                        continue;
                    }
                    out.add(((SecretJWK)jwk).toSecretKey());
                }
            }
            catch (JOSEException ex) {}
        }
        return out;
    }
}
