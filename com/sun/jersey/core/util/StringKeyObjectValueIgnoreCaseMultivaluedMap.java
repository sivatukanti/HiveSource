// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class StringKeyObjectValueIgnoreCaseMultivaluedMap extends StringKeyIgnoreCaseMultivaluedMap<Object>
{
    public StringKeyObjectValueIgnoreCaseMultivaluedMap() {
    }
    
    public StringKeyObjectValueIgnoreCaseMultivaluedMap(final StringKeyObjectValueIgnoreCaseMultivaluedMap that) {
        super(that);
    }
    
    public <A> List<A> get(final String key, final Class<A> type) {
        ArrayList<A> l = null;
        final List<Object> values = ((KeyComparatorLinkedHashMap<K, List<Object>>)this).get(key);
        if (values != null) {
            l = new ArrayList<A>();
            for (final Object value : values) {
                if (!type.isInstance(value)) {
                    throw new IllegalArgumentException(type + " is not an instance of " + value.getClass());
                }
                l.add((A)value);
            }
        }
        return l;
    }
    
    public <A> A getFirst(final String key, final Class<A> type) {
        final Object value = this.getFirst(key);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (A)value;
        }
        throw new IllegalArgumentException(type + " is not an instance of " + value.getClass());
    }
    
    public <A> A getFirst(final String key, final A defaultValue) {
        final Object value = this.getFirst(key);
        if (value == null) {
            return defaultValue;
        }
        if (defaultValue.getClass().isInstance(value)) {
            return (A)value;
        }
        throw new IllegalArgumentException(defaultValue.getClass() + " is not an instance of " + value.getClass());
    }
}
