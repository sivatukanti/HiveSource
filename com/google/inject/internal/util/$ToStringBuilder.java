// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class $ToStringBuilder
{
    final Map<String, Object> map;
    final String name;
    
    public $ToStringBuilder(final Class type) {
        this.map = new LinkedHashMap<String, Object>();
        this.name = type.getSimpleName();
    }
    
    public $ToStringBuilder add(final String name, final Object value) {
        if (this.map.put(name, value) != null) {
            throw new RuntimeException("Duplicate names: " + name);
        }
        return this;
    }
    
    @Override
    public String toString() {
        return this.name + this.map.toString().replace('{', '[').replace('}', ']');
    }
}
