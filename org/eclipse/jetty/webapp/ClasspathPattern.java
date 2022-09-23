// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractList;

public class ClasspathPattern extends AbstractList<String>
{
    private final List<Entry> _entries;
    
    public ClasspathPattern() {
        this._entries = new ArrayList<Entry>();
    }
    
    public ClasspathPattern(final String[] patterns) {
        this._entries = new ArrayList<Entry>();
        this.setAll(patterns);
    }
    
    public ClasspathPattern(final String pattern) {
        this._entries = new ArrayList<Entry>();
        this.add(pattern);
    }
    
    @Override
    public String get(final int index) {
        return this._entries.get(index)._pattern;
    }
    
    @Override
    public String set(final int index, final String element) {
        final Entry e = this._entries.set(index, new Entry(element));
        return (e == null) ? null : e._pattern;
    }
    
    @Override
    public void add(final int index, final String element) {
        this._entries.add(index, new Entry(element));
    }
    
    @Deprecated
    public void addPattern(final String element) {
        this.add(element);
    }
    
    @Override
    public String remove(final int index) {
        final Entry e = this._entries.remove(index);
        return (e == null) ? null : e._pattern;
    }
    
    public boolean remove(final String pattern) {
        int i = this._entries.size();
        while (i-- > 0) {
            if (pattern.equals(this._entries.get(i)._pattern)) {
                this._entries.remove(i);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int size() {
        return this._entries.size();
    }
    
    private void setAll(final String[] classes) {
        this._entries.clear();
        this.addAll(classes);
    }
    
    private void addAll(final String[] classes) {
        if (classes != null) {
            this.addAll(Arrays.asList(classes));
        }
    }
    
    public void prepend(final String[] classes) {
        if (classes != null) {
            int i = 0;
            for (final String c : classes) {
                this.add(i, c);
                ++i;
            }
        }
    }
    
    public void prependPattern(final String pattern) {
        this.add(0, pattern);
    }
    
    public String[] getPatterns() {
        return this.toArray(new String[this._entries.size()]);
    }
    
    public List<String> getClasses() {
        final List<String> list = new ArrayList<String>();
        for (final Entry e : this._entries) {
            if (e._inclusive && !e._package) {
                list.add(e._name);
            }
        }
        return list;
    }
    
    public boolean match(String name) {
        name = name.replace('/', '.');
        for (final Entry entry : this._entries) {
            if (entry == null) {
                continue;
            }
            if (entry._package) {
                if (name.startsWith(entry._name) || ".".equals(entry._pattern)) {
                    return entry._inclusive;
                }
                continue;
            }
            else {
                if (name.equals(entry._name)) {
                    return entry._inclusive;
                }
                if (name.length() > entry._name.length() && '$' == name.charAt(entry._name.length()) && name.startsWith(entry._name)) {
                    return entry._inclusive;
                }
                continue;
            }
        }
        return false;
    }
    
    public void addAfter(final String afterPattern, final String... patterns) {
        if (patterns != null && afterPattern != null) {
            final ListIterator<String> iter = this.listIterator();
            while (iter.hasNext()) {
                final String cc = iter.next();
                if (afterPattern.equals(cc)) {
                    for (int i = 0; i < patterns.length; ++i) {
                        iter.add(patterns[i]);
                    }
                    return;
                }
            }
        }
        throw new IllegalArgumentException("after '" + afterPattern + "' not found in " + this);
    }
    
    public void addBefore(final String beforePattern, final String... patterns) {
        if (patterns != null && beforePattern != null) {
            final ListIterator<String> iter = this.listIterator();
            while (iter.hasNext()) {
                final String cc = iter.next();
                if (beforePattern.equals(cc)) {
                    iter.previous();
                    for (int i = 0; i < patterns.length; ++i) {
                        iter.add(patterns[i]);
                    }
                    return;
                }
            }
        }
        throw new IllegalArgumentException("before '" + beforePattern + "' not found in " + this);
    }
    
    private static class Entry
    {
        public final String _pattern;
        public final String _name;
        public final boolean _inclusive;
        public final boolean _package;
        
        Entry(final String pattern) {
            this._pattern = pattern;
            this._inclusive = !pattern.startsWith("-");
            this._package = pattern.endsWith(".");
            this._name = (this._inclusive ? pattern : pattern.substring(1).trim());
        }
        
        @Override
        public String toString() {
            return this._pattern;
        }
    }
}
