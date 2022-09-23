// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.model;

public class PathValue
{
    private String value;
    
    public PathValue(final String path) {
        this.value = path;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + ((null == this.getValue()) ? "null" : ("\"" + this.getValue() + "\"")) + ")";
    }
}
