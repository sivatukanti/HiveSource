// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class StringKeyStringValueIgnoreCaseMultivaluedMap extends StringKeyIgnoreCaseMultivaluedMap<String>
{
    public StringKeyStringValueIgnoreCaseMultivaluedMap() {
    }
    
    public StringKeyStringValueIgnoreCaseMultivaluedMap(final StringKeyStringValueIgnoreCaseMultivaluedMap that) {
        super(that);
    }
    
    public void putSingleObject(final String key, final Object value) {
        final List<String> l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value.toString());
        }
        else {
            l.add("");
        }
    }
    
    public void addObject(final String key, final Object value) {
        final List<String> l = this.getList(key);
        if (value != null) {
            l.add(value.toString());
        }
        else {
            l.add("");
        }
    }
    
    public <A> List<A> get(final String key, final Class<A> type) {
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        ArrayList<A> l = null;
        final List<String> values = ((KeyComparatorLinkedHashMap<K, List<String>>)this).get(key);
        if (values != null) {
            l = new ArrayList<A>();
            for (final String value : values) {
                try {
                    l.add(c.newInstance(value));
                }
                catch (Exception ex2) {
                    l.add(null);
                }
            }
        }
        return l;
    }
    
    public <A> A getFirst(final String key, final Class<A> type) {
        final String value = this.getFirst(key);
        if (value == null) {
            return null;
        }
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        A retVal = null;
        try {
            retVal = c.newInstance(value);
        }
        catch (Exception ex2) {}
        return retVal;
    }
    
    public <A> A getFirst(final String key, final A defaultValue) {
        final String value = this.getFirst(key);
        if (value == null) {
            return defaultValue;
        }
        final Class<A> type = (Class<A>)defaultValue.getClass();
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        A retVal = defaultValue;
        try {
            retVal = c.newInstance(value);
        }
        catch (Exception ex2) {}
        return retVal;
    }
}
