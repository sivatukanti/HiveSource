// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

import org.apache.commons.lang3.text.StrLookup;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.text.StrSubstitutor;
import java.util.List;
import java.util.Map;

public class ConfigurationInterpolator
{
    private static final char PREFIX_SEPARATOR = ':';
    private static final String VAR_START = "${";
    private static final String VAR_END = "}";
    private static final Map<String, Lookup> DEFAULT_PREFIX_LOOKUPS;
    private final Map<String, Lookup> prefixLookups;
    private final List<Lookup> defaultLookups;
    private final StrSubstitutor substitutor;
    private volatile ConfigurationInterpolator parentInterpolator;
    
    public ConfigurationInterpolator() {
        this.prefixLookups = new ConcurrentHashMap<String, Lookup>();
        this.defaultLookups = new CopyOnWriteArrayList<Lookup>();
        this.substitutor = this.initSubstitutor();
    }
    
    public static ConfigurationInterpolator fromSpecification(final InterpolatorSpecification spec) {
        if (spec == null) {
            throw new IllegalArgumentException("InterpolatorSpecification must not be null!");
        }
        return (spec.getInterpolator() != null) ? spec.getInterpolator() : createInterpolator(spec);
    }
    
    public static Map<String, Lookup> getDefaultPrefixLookups() {
        return ConfigurationInterpolator.DEFAULT_PREFIX_LOOKUPS;
    }
    
    public static Lookup nullSafeLookup(Lookup lookup) {
        if (lookup == null) {
            lookup = DummyLookup.INSTANCE;
        }
        return lookup;
    }
    
    public Map<String, Lookup> getLookups() {
        return new HashMap<String, Lookup>(this.prefixLookups);
    }
    
    public void registerLookup(final String prefix, final Lookup lookup) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix for lookup object must not be null!");
        }
        if (lookup == null) {
            throw new IllegalArgumentException("Lookup object must not be null!");
        }
        this.prefixLookups.put(prefix, lookup);
    }
    
    public void registerLookups(final Map<String, ? extends Lookup> lookups) {
        if (lookups != null) {
            this.prefixLookups.putAll(lookups);
        }
    }
    
    public boolean deregisterLookup(final String prefix) {
        return this.prefixLookups.remove(prefix) != null;
    }
    
    public Set<String> prefixSet() {
        return Collections.unmodifiableSet((Set<? extends String>)this.prefixLookups.keySet());
    }
    
    public List<Lookup> getDefaultLookups() {
        return new ArrayList<Lookup>(this.defaultLookups);
    }
    
    public void addDefaultLookup(final Lookup defaultLookup) {
        this.defaultLookups.add(defaultLookup);
    }
    
    public void addDefaultLookups(final Collection<? extends Lookup> lookups) {
        if (lookups != null) {
            this.defaultLookups.addAll(lookups);
        }
    }
    
    public boolean removeDefaultLookup(final Lookup lookup) {
        return this.defaultLookups.remove(lookup);
    }
    
    public void setParentInterpolator(final ConfigurationInterpolator parentInterpolator) {
        this.parentInterpolator = parentInterpolator;
    }
    
    public ConfigurationInterpolator getParentInterpolator() {
        return this.parentInterpolator;
    }
    
    public boolean isEnableSubstitutionInVariables() {
        return this.substitutor.isEnableSubstitutionInVariables();
    }
    
    public void setEnableSubstitutionInVariables(final boolean f) {
        this.substitutor.setEnableSubstitutionInVariables(f);
    }
    
    public Object interpolate(final Object value) {
        if (value instanceof String) {
            final String strValue = (String)value;
            if (looksLikeSingleVariable(strValue)) {
                final Object resolvedValue = this.resolveSingleVariable(strValue);
                if (resolvedValue != null && !(resolvedValue instanceof String)) {
                    return resolvedValue;
                }
            }
            return this.substitutor.replace(strValue);
        }
        return value;
    }
    
    public Object resolve(final String var) {
        if (var == null) {
            return null;
        }
        final int prefixPos = var.indexOf(58);
        if (prefixPos >= 0) {
            final String prefix = var.substring(0, prefixPos);
            final String name = var.substring(prefixPos + 1);
            final Object value = this.fetchLookupForPrefix(prefix).lookup(name);
            if (value != null) {
                return value;
            }
        }
        for (final Lookup l : this.defaultLookups) {
            final Object value = l.lookup(var);
            if (value != null) {
                return value;
            }
        }
        final ConfigurationInterpolator parent = this.getParentInterpolator();
        if (parent != null) {
            return this.getParentInterpolator().resolve(var);
        }
        return null;
    }
    
    protected Lookup fetchLookupForPrefix(final String prefix) {
        return nullSafeLookup(this.prefixLookups.get(prefix));
    }
    
    private StrSubstitutor initSubstitutor() {
        return new StrSubstitutor(new StrLookup<Object>() {
            @Override
            public String lookup(final String key) {
                final Object result = ConfigurationInterpolator.this.resolve(key);
                return (result != null) ? result.toString() : null;
            }
        });
    }
    
    private Object resolveSingleVariable(final String strValue) {
        return this.resolve(extractVariableName(strValue));
    }
    
    private static boolean looksLikeSingleVariable(final String strValue) {
        return strValue.startsWith("${") && strValue.endsWith("}");
    }
    
    private static String extractVariableName(final String strValue) {
        return strValue.substring("${".length(), strValue.length() - "}".length());
    }
    
    private static ConfigurationInterpolator createInterpolator(final InterpolatorSpecification spec) {
        final ConfigurationInterpolator ci = new ConfigurationInterpolator();
        ci.addDefaultLookups(spec.getDefaultLookups());
        ci.registerLookups(spec.getPrefixLookups());
        ci.setParentInterpolator(spec.getParentInterpolator());
        return ci;
    }
    
    static {
        final Map<String, Lookup> lookups = new HashMap<String, Lookup>();
        for (final DefaultLookups l : DefaultLookups.values()) {
            lookups.put(l.getPrefix(), l.getLookup());
        }
        DEFAULT_PREFIX_LOOKUPS = Collections.unmodifiableMap((Map<? extends String, ? extends Lookup>)lookups);
    }
}
