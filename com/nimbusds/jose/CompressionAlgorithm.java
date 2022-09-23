// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import net.minidev.json.JSONObject;
import net.jcip.annotations.Immutable;
import java.io.Serializable;
import net.minidev.json.JSONAware;

@Immutable
public final class CompressionAlgorithm implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final CompressionAlgorithm DEF;
    private final String name;
    
    static {
        DEF = new CompressionAlgorithm("DEF");
    }
    
    public CompressionAlgorithm(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("The compression algorithm name must not be null");
        }
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof CompressionAlgorithm && this.toString().equals(object.toString());
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public String toJSONString() {
        return "\"" + JSONObject.escape(this.name) + '\"';
    }
}
