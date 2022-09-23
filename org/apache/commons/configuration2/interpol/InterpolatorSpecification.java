// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

public final class InterpolatorSpecification
{
    private final ConfigurationInterpolator interpolator;
    private final ConfigurationInterpolator parentInterpolator;
    private final Map<String, Lookup> prefixLookups;
    private final Collection<Lookup> defaultLookups;
    
    private InterpolatorSpecification(final Builder builder) {
        this.interpolator = builder.interpolator;
        this.parentInterpolator = builder.parentInterpolator;
        this.prefixLookups = Collections.unmodifiableMap((Map<? extends String, ? extends Lookup>)new HashMap<String, Lookup>(builder.prefixLookups));
        this.defaultLookups = Collections.unmodifiableCollection((Collection<? extends Lookup>)new ArrayList<Lookup>(builder.defLookups));
    }
    
    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator;
    }
    
    public ConfigurationInterpolator getParentInterpolator() {
        return this.parentInterpolator;
    }
    
    public Map<String, Lookup> getPrefixLookups() {
        return this.prefixLookups;
    }
    
    public Collection<Lookup> getDefaultLookups() {
        return this.defaultLookups;
    }
    
    public static class Builder
    {
        private final Map<String, Lookup> prefixLookups;
        private final Collection<Lookup> defLookups;
        private ConfigurationInterpolator interpolator;
        private ConfigurationInterpolator parentInterpolator;
        
        public Builder() {
            this.prefixLookups = new HashMap<String, Lookup>();
            this.defLookups = new LinkedList<Lookup>();
        }
        
        public Builder withPrefixLookup(final String prefix, final Lookup lookup) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix must not be null!");
            }
            checkLookup(lookup);
            this.prefixLookups.put(prefix, lookup);
            return this;
        }
        
        public Builder withPrefixLookups(final Map<String, ? extends Lookup> lookups) {
            if (lookups != null) {
                for (final Map.Entry<String, ? extends Lookup> e : lookups.entrySet()) {
                    this.withPrefixLookup(e.getKey(), (Lookup)e.getValue());
                }
            }
            return this;
        }
        
        public Builder withDefaultLookup(final Lookup lookup) {
            checkLookup(lookup);
            this.defLookups.add(lookup);
            return this;
        }
        
        public Builder withDefaultLookups(final Collection<? extends Lookup> lookups) {
            if (lookups != null) {
                for (final Lookup l : lookups) {
                    this.withDefaultLookup(l);
                }
            }
            return this;
        }
        
        public Builder withInterpolator(final ConfigurationInterpolator ci) {
            this.interpolator = ci;
            return this;
        }
        
        public Builder withParentInterpolator(final ConfigurationInterpolator parent) {
            this.parentInterpolator = parent;
            return this;
        }
        
        public InterpolatorSpecification create() {
            final InterpolatorSpecification spec = new InterpolatorSpecification(this, null);
            this.reset();
            return spec;
        }
        
        public void reset() {
            this.interpolator = null;
            this.parentInterpolator = null;
            this.prefixLookups.clear();
            this.defLookups.clear();
        }
        
        private static void checkLookup(final Lookup lookup) {
            if (lookup == null) {
                throw new IllegalArgumentException("Lookup must not be null!");
            }
        }
    }
}
