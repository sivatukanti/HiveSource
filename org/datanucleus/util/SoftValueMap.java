// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class SoftValueMap extends ReferenceValueMap
{
    public SoftValueMap() {
    }
    
    public SoftValueMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public SoftValueMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public SoftValueMap(final Map m) {
        super(m);
    }
    
    @Override
    protected ValueReference newValueReference(final Object key, final Object value, final ReferenceQueue queue) {
        return new SoftValueReference(key, value, queue);
    }
    
    private static class SoftValueReference extends SoftReference implements ValueReference
    {
        private final Object key;
        
        SoftValueReference(final Object key, final Object value, final ReferenceQueue q) {
            super(value, q);
            this.key = key;
        }
        
        @Override
        public Object getKey() {
            return this.key;
        }
    }
}
