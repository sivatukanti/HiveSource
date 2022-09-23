// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper
{
    private final Map mapping;
    
    public SimpleRemapper(final Map mapping) {
        this.mapping = mapping;
    }
    
    public SimpleRemapper(final String key, final String value) {
        this.mapping = Collections.singletonMap(key, value);
    }
    
    public String mapMethodName(final String str, final String str2, final String str3) {
        final String map = this.map(str + '.' + str2 + str3);
        return (map == null) ? str2 : map;
    }
    
    public String mapFieldName(final String str, final String str2, final String s) {
        final String map = this.map(str + '.' + str2);
        return (map == null) ? str2 : map;
    }
    
    public String map(final String s) {
        return this.mapping.get(s);
    }
}
