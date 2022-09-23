// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.util.IdentityHashMap;

public class ObjectIdMap extends IdentityHashMap<Object, Object>
{
    public ObjectIdMap() {
        super(16);
    }
    
    public Object findId(final Object pojo) {
        return ((IdentityHashMap<K, Object>)this).get(pojo);
    }
    
    public void insertId(final Object pojo, final Object id) {
        this.put(pojo, id);
    }
}
