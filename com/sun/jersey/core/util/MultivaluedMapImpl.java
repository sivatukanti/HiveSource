// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.LinkedList;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.HashMap;

public class MultivaluedMapImpl extends HashMap<String, List<String>> implements MultivaluedMap<String, String>
{
    static final long serialVersionUID = -6052320403766368902L;
    
    public MultivaluedMapImpl() {
    }
    
    public MultivaluedMapImpl(final MultivaluedMap<String, String> that) {
        for (final Map.Entry<String, List<String>> e : that.entrySet()) {
            ((HashMap<String, ArrayList<String>>)this).put(e.getKey(), new ArrayList<String>(e.getValue()));
        }
    }
    
    @Override
    public final void putSingle(final String key, final String value) {
        final List<String> l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value);
        }
        else {
            l.add("");
        }
    }
    
    @Override
    public final void add(final String key, final String value) {
        final List<String> l = this.getList(key);
        if (value != null) {
            l.add(value);
        }
        else {
            l.add("");
        }
    }
    
    @Override
    public final String getFirst(final String key) {
        final List<String> values = ((HashMap<K, List<String>>)this).get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }
    
    public final void addFirst(final String key, final String value) {
        final List<String> l = this.getList(key);
        if (value != null) {
            l.add(0, value);
        }
        else {
            l.add(0, "");
        }
    }
    
    public final <A> List<A> get(final String key, final Class<A> type) {
        Constructor<A> c = null;
        try {
            c = type.getConstructor(String.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", ex);
        }
        ArrayList<A> l = null;
        final List<String> values = ((HashMap<K, List<String>>)this).get(key);
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
    
    public final void putSingle(final String key, final Object value) {
        final List<String> l = this.getList(key);
        l.clear();
        if (value != null) {
            l.add(value.toString());
        }
        else {
            l.add("");
        }
    }
    
    public final void add(final String key, final Object value) {
        final List<String> l = this.getList(key);
        if (value != null) {
            l.add(value.toString());
        }
        else {
            l.add("");
        }
    }
    
    private List<String> getList(final String key) {
        List<String> l = ((HashMap<K, List<String>>)this).get(key);
        if (l == null) {
            l = new LinkedList<String>();
            this.put(key, l);
        }
        return l;
    }
    
    public final <A> A getFirst(final String key, final Class<A> type) {
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
    
    public final <A> A getFirst(final String key, final A defaultValue) {
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
