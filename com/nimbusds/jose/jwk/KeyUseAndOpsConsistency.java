// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

class KeyUseAndOpsConsistency
{
    static Map<KeyUse, Set<KeyOperation>> MAP;
    
    static {
        final Map<KeyUse, Set<KeyOperation>> map = new HashMap<KeyUse, Set<KeyOperation>>();
        map.put(KeyUse.SIGNATURE, new HashSet<KeyOperation>(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY)));
        map.put(KeyUse.ENCRYPTION, new HashSet<KeyOperation>(Arrays.asList(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY)));
        KeyUseAndOpsConsistency.MAP = Collections.unmodifiableMap((Map<? extends KeyUse, ? extends Set<KeyOperation>>)map);
    }
    
    static boolean areConsistent(final KeyUse use, final Set<KeyOperation> ops) {
        return use == null || ops == null || KeyUseAndOpsConsistency.MAP.get(use).containsAll(ops);
    }
}
