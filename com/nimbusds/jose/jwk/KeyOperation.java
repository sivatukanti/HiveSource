// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jwk;

import java.util.Iterator;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

public enum KeyOperation
{
    SIGN("SIGN", 0, "sign"), 
    VERIFY("VERIFY", 1, "verify"), 
    ENCRYPT("ENCRYPT", 2, "encrypt"), 
    DECRYPT("DECRYPT", 3, "decrypt"), 
    WRAP_KEY("WRAP_KEY", 4, "wrapKey"), 
    UNWRAP_KEY("UNWRAP_KEY", 5, "unwrapKey"), 
    DERIVE_KEY("DERIVE_KEY", 6, "deriveKey"), 
    DERIVE_BITS("DERIVE_BITS", 7, "deriveBits");
    
    private final String identifier;
    
    private KeyOperation(final String name, final int ordinal, final String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("The key operation identifier must not be null");
        }
        this.identifier = identifier;
    }
    
    public String identifier() {
        return this.identifier;
    }
    
    @Override
    public String toString() {
        return this.identifier();
    }
    
    public static Set<KeyOperation> parse(final List<String> sl) throws ParseException {
        if (sl == null) {
            return null;
        }
        final Set<KeyOperation> keyOps = new LinkedHashSet<KeyOperation>();
        for (final String s : sl) {
            if (s == null) {
                continue;
            }
            KeyOperation parsedOp = null;
            KeyOperation[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final KeyOperation op = values[i];
                if (s.equals(op.identifier())) {
                    parsedOp = op;
                    break;
                }
            }
            if (parsedOp == null) {
                throw new ParseException("Invalid JWK operation: " + s, 0);
            }
            keyOps.add(parsedOp);
        }
        return keyOps;
    }
}
