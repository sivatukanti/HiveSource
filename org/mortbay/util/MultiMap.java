// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MultiMap extends HashMap implements Cloneable
{
    public MultiMap() {
    }
    
    public MultiMap(final int size) {
        super(size);
    }
    
    public MultiMap(final Map map) {
        super(map.size() * 3 / 2);
        this.putAll(map);
    }
    
    public List getValues(final Object name) {
        return LazyList.getList(super.get(name), true);
    }
    
    public Object getValue(final Object name, final int i) {
        final Object l = super.get(name);
        if (i == 0 && LazyList.size(l) == 0) {
            return null;
        }
        return LazyList.get(l, i);
    }
    
    public String getString(final Object name) {
        final Object l = super.get(name);
        switch (LazyList.size(l)) {
            case 0: {
                return null;
            }
            case 1: {
                final Object o = LazyList.get(l, 0);
                return (o == null) ? null : o.toString();
            }
            default: {
                final StringBuffer values = new StringBuffer(128);
                synchronized (values) {
                    for (int i = 0; i < LazyList.size(l); ++i) {
                        final Object e = LazyList.get(l, i);
                        if (e != null) {
                            if (values.length() > 0) {
                                values.append(',');
                            }
                            values.append(e.toString());
                        }
                    }
                    return values.toString();
                }
                break;
            }
        }
    }
    
    public Object get(final Object name) {
        final Object l = super.get(name);
        switch (LazyList.size(l)) {
            case 0: {
                return null;
            }
            case 1: {
                final Object o = LazyList.get(l, 0);
                return o;
            }
            default: {
                return LazyList.getList(l, true);
            }
        }
    }
    
    public Object put(final Object name, final Object value) {
        return super.put(name, LazyList.add(null, value));
    }
    
    public Object putValues(final Object name, final List values) {
        return super.put(name, values);
    }
    
    public Object putValues(final Object name, final String[] values) {
        Object list = null;
        for (int i = 0; i < values.length; ++i) {
            list = LazyList.add(list, values[i]);
        }
        return this.put(name, list);
    }
    
    public void add(final Object name, final Object value) {
        final Object lo = super.get(name);
        final Object ln = LazyList.add(lo, value);
        if (lo != ln) {
            super.put(name, ln);
        }
    }
    
    public void addValues(final Object name, final List values) {
        final Object lo = super.get(name);
        final Object ln = LazyList.addCollection(lo, values);
        if (lo != ln) {
            super.put(name, ln);
        }
    }
    
    public void addValues(final Object name, final String[] values) {
        final Object lo = super.get(name);
        final Object ln = LazyList.addCollection(lo, Arrays.asList(values));
        if (lo != ln) {
            super.put(name, ln);
        }
    }
    
    public boolean removeValue(final Object name, final Object value) {
        Object ln;
        final Object lo = ln = super.get(name);
        final int s = LazyList.size(lo);
        if (s > 0) {
            ln = LazyList.remove(lo, value);
            if (ln == null) {
                super.remove(name);
            }
            else {
                super.put(name, ln);
            }
        }
        return LazyList.size(ln) != s;
    }
    
    public void putAll(final Map m) {
        final Iterator i = m.entrySet().iterator();
        final boolean multi = m instanceof MultiMap;
        while (i.hasNext()) {
            final Map.Entry entry = i.next();
            if (multi) {
                super.put(entry.getKey(), LazyList.clone(entry.getValue()));
            }
            else {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    public Map toStringArrayMap() {
        final HashMap map = new HashMap(this.size() * 3 / 2);
        for (final Map.Entry entry : super.entrySet()) {
            final Object l = entry.getValue();
            final String[] a = LazyList.toStringArray(l);
            map.put(entry.getKey(), a);
        }
        return map;
    }
    
    public Object clone() {
        final MultiMap mm = (MultiMap)super.clone();
        for (final Map.Entry entry : mm.entrySet()) {
            entry.setValue(LazyList.clone(entry.getValue()));
        }
        return mm;
    }
}
