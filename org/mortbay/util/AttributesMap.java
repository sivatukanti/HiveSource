// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AttributesMap implements Attributes
{
    Map _map;
    
    public AttributesMap() {
        this._map = new HashMap();
    }
    
    public AttributesMap(final Map map) {
        this._map = map;
    }
    
    public void removeAttribute(final String name) {
        this._map.remove(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        if (attribute == null) {
            this._map.remove(name);
        }
        else {
            this._map.put(name, attribute);
        }
    }
    
    public Object getAttribute(final String name) {
        return this._map.get(name);
    }
    
    public Enumeration getAttributeNames() {
        return Collections.enumeration((Collection<Object>)this._map.keySet());
    }
    
    public static Enumeration getAttributeNamesCopy(final Attributes attrs) {
        if (attrs instanceof AttributesMap) {
            return Collections.enumeration((Collection<Object>)((AttributesMap)attrs)._map.keySet());
        }
        final ArrayList names = new ArrayList();
        final Enumeration e = attrs.getAttributeNames();
        while (e.hasMoreElements()) {
            names.add(e.nextElement());
        }
        return Collections.enumeration((Collection<Object>)names);
    }
    
    public void clearAttributes() {
        this._map.clear();
    }
    
    public String toString() {
        return this._map.toString();
    }
}
