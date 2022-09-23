// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model;

import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;
import java.util.ArrayList;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import java.util.List;
import java.util.HashMap;

final class ResourceMethodMap extends HashMap<String, List<ResourceMethod>>
{
    public void put(final ResourceMethod method) {
        List<ResourceMethod> l = ((HashMap<K, List<ResourceMethod>>)this).get(method.getHttpMethod());
        if (l == null) {
            l = new ArrayList<ResourceMethod>();
            this.put(method.getHttpMethod(), l);
        }
        l.add(method);
    }
    
    public void sort() {
        for (final Map.Entry<String, List<ResourceMethod>> e : this.entrySet()) {
            Collections.sort(e.getValue(), ResourceMethod.COMPARATOR);
        }
    }
}
