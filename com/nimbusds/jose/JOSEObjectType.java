// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import net.minidev.json.JSONObject;
import net.jcip.annotations.Immutable;
import java.io.Serializable;
import net.minidev.json.JSONAware;

@Immutable
public final class JOSEObjectType implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final JOSEObjectType JOSE;
    public static final JOSEObjectType JOSE_JSON;
    public static final JOSEObjectType JWT;
    private final String type;
    
    static {
        JOSE = new JOSEObjectType("JOSE");
        JOSE_JSON = new JOSEObjectType("JOSE+JSON");
        JWT = new JOSEObjectType("JWT");
    }
    
    public JOSEObjectType(final String type) {
        if (type == null) {
            throw new IllegalArgumentException("The object type must not be null");
        }
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof JOSEObjectType && this.toString().equals(object.toString());
    }
    
    @Override
    public String toString() {
        return this.type;
    }
    
    @Override
    public String toJSONString() {
        return "\"" + JSONObject.escape(this.type) + '\"';
    }
}
