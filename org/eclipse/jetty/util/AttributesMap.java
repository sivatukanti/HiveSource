// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class AttributesMap implements Attributes
{
    private final AtomicReference<ConcurrentMap<String, Object>> _map;
    
    public AttributesMap() {
        this._map = new AtomicReference<ConcurrentMap<String, Object>>();
    }
    
    public AttributesMap(final AttributesMap attributes) {
        this._map = new AtomicReference<ConcurrentMap<String, Object>>();
        final ConcurrentMap<String, Object> map = attributes.map();
        if (map != null) {
            this._map.set(new ConcurrentHashMap<String, Object>(map));
        }
    }
    
    private ConcurrentMap<String, Object> map() {
        return this._map.get();
    }
    
    private ConcurrentMap<String, Object> ensureMap() {
        while (true) {
            ConcurrentMap<String, Object> map = this.map();
            if (map != null) {
                return map;
            }
            map = new ConcurrentHashMap<String, Object>();
            if (this._map.compareAndSet(null, map)) {
                return map;
            }
        }
    }
    
    @Override
    public void removeAttribute(final String name) {
        final Map<String, Object> map = this.map();
        if (map != null) {
            map.remove(name);
        }
    }
    
    @Override
    public void setAttribute(final String name, final Object attribute) {
        if (attribute == null) {
            this.removeAttribute(name);
        }
        else {
            this.ensureMap().put(name, attribute);
        }
    }
    
    @Override
    public Object getAttribute(final String name) {
        final Map<String, Object> map = this.map();
        return (map == null) ? null : map.get(name);
    }
    
    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.getAttributeNameSet());
    }
    
    public Set<String> getAttributeNameSet() {
        return this.keySet();
    }
    
    public Set<Map.Entry<String, Object>> getAttributeEntrySet() {
        final Map<String, Object> map = this.map();
        return (map == null) ? Collections.emptySet() : map.entrySet();
    }
    
    public static Enumeration<String> getAttributeNamesCopy(final Attributes attrs) {
        if (attrs instanceof AttributesMap) {
            return Collections.enumeration(((AttributesMap)attrs).keySet());
        }
        final List<String> names = new ArrayList<String>();
        names.addAll(Collections.list(attrs.getAttributeNames()));
        return Collections.enumeration(names);
    }
    
    @Override
    public void clearAttributes() {
        final Map<String, Object> map = this.map();
        if (map != null) {
            map.clear();
        }
    }
    
    public int size() {
        final Map<String, Object> map = this.map();
        return (map == null) ? 0 : map.size();
    }
    
    @Override
    public String toString() {
        final Map<String, Object> map = this.map();
        return (map == null) ? "{}" : map.toString();
    }
    
    private Set<String> keySet() {
        final Map<String, Object> map = this.map();
        return (map == null) ? Collections.emptySet() : map.keySet();
    }
    
    public void addAll(final Attributes attributes) {
        final Enumeration<String> e = attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            this.setAttribute(name, attributes.getAttribute(name));
        }
    }
}
