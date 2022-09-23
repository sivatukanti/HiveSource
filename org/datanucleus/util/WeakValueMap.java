// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class WeakValueMap extends ReferenceValueMap
{
    public WeakValueMap() {
    }
    
    public WeakValueMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public WeakValueMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public WeakValueMap(final Map m) {
        super(m);
    }
    
    @Override
    protected ValueReference newValueReference(final Object key, final Object value, final ReferenceQueue queue) {
        return new WeakValueReference(key, value, queue);
    }
    
    private static class WeakValueReference extends WeakReference implements ValueReference
    {
        private final Object key;
        
        WeakValueReference(final Object key, final Object value, final ReferenceQueue q) {
            super(value, q);
            this.key = key;
        }
        
        @Override
        public Object getKey() {
            return this.key;
        }
    }
}
