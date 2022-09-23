// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.lookup;

import java.util.Iterator;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

class InterpolatorStringLookup extends AbstractStringLookup
{
    private static final char PREFIX_SEPARATOR = ':';
    private final StringLookup defaultStringLookup;
    private final Map<String, StringLookup> stringLookupMap;
    
    InterpolatorStringLookup() {
        this((Map<String, Object>)null);
    }
    
     <V> InterpolatorStringLookup(final Map<String, V> defaultMap) {
        this(MapStringLookup.on((defaultMap == null) ? new HashMap<String, V>() : defaultMap));
    }
    
    InterpolatorStringLookup(final StringLookup defaultStringLookup) {
        this(new HashMap<String, StringLookup>(), defaultStringLookup, true);
    }
    
    InterpolatorStringLookup(final Map<String, StringLookup> stringLookupMap, final StringLookup defaultStringLookup, final boolean addDefaultLookups) {
        this.defaultStringLookup = defaultStringLookup;
        this.stringLookupMap = new HashMap<String, StringLookup>(stringLookupMap.size());
        for (final Map.Entry<String, StringLookup> entry : stringLookupMap.entrySet()) {
            this.stringLookupMap.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }
        if (addDefaultLookups) {
            this.stringLookupMap.put("sys", SystemPropertyStringLookup.INSTANCE);
            this.stringLookupMap.put("env", EnvironmentVariableStringLookup.INSTANCE);
            this.stringLookupMap.put("java", JavaPlatformStringLookup.INSTANCE);
            this.stringLookupMap.put("date", DateStringLookup.INSTANCE);
            this.stringLookupMap.put("localhost", LocalHostStringLookup.INSTANCE);
        }
    }
    
    public Map<String, StringLookup> getStringLookupMap() {
        return this.stringLookupMap;
    }
    
    @Override
    public String lookup(String var) {
        if (var == null) {
            return null;
        }
        final int prefixPos = var.indexOf(58);
        if (prefixPos >= 0) {
            final String prefix = var.substring(0, prefixPos).toLowerCase(Locale.ROOT);
            final String name = var.substring(prefixPos + 1);
            final StringLookup lookup = this.stringLookupMap.get(prefix);
            String value = null;
            if (lookup != null) {
                value = lookup.lookup(name);
            }
            if (value != null) {
                return value;
            }
            var = var.substring(prefixPos + 1);
        }
        if (this.defaultStringLookup != null) {
            return this.defaultStringLookup.lookup(var);
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [stringLookupMap=" + this.stringLookupMap + ", defaultStringLookup=" + this.defaultStringLookup + "]";
    }
}
