// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import java.util.HashMap;
import java.util.Map;

public enum SaslQOP
{
    AUTH("auth"), 
    AUTH_INT("auth-int"), 
    AUTH_CONF("auth-conf");
    
    public final String saslQop;
    private static final Map<String, SaslQOP> STR_TO_ENUM;
    
    private SaslQOP(final String saslQop) {
        this.saslQop = saslQop;
    }
    
    @Override
    public String toString() {
        return this.saslQop;
    }
    
    public static SaslQOP fromString(String str) {
        if (str != null) {
            str = str.toLowerCase();
        }
        final SaslQOP saslQOP = SaslQOP.STR_TO_ENUM.get(str);
        if (saslQOP == null) {
            throw new IllegalArgumentException("Unknown auth type: " + str + " Allowed values are: " + SaslQOP.STR_TO_ENUM.keySet());
        }
        return saslQOP;
    }
    
    static {
        STR_TO_ENUM = new HashMap<String, SaslQOP>();
        for (final SaslQOP saslQop : values()) {
            SaslQOP.STR_TO_ENUM.put(saslQop.toString(), saslQop);
        }
    }
}
