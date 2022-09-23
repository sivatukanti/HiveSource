// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import net.minidev.json.JSONObject;
import net.jcip.annotations.Immutable;
import java.io.Serializable;
import net.minidev.json.JSONAware;

@Immutable
public class Algorithm implements JSONAware, Serializable
{
    private static final long serialVersionUID = 1L;
    public static final Algorithm NONE;
    private final String name;
    private final Requirement requirement;
    
    static {
        NONE = new Algorithm("none", Requirement.REQUIRED);
    }
    
    public Algorithm(final String name, final Requirement req) {
        if (name == null) {
            throw new IllegalArgumentException("The algorithm name must not be null");
        }
        this.name = name;
        this.requirement = req;
    }
    
    public Algorithm(final String name) {
        this(name, null);
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final Requirement getRequirement() {
        return this.requirement;
    }
    
    @Override
    public final int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object != null && object instanceof Algorithm && this.toString().equals(object.toString());
    }
    
    @Override
    public final String toString() {
        return this.name;
    }
    
    @Override
    public final String toJSONString() {
        return "\"" + JSONObject.escape(this.name) + '\"';
    }
}
