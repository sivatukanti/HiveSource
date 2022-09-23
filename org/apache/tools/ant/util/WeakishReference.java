// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.lang.ref.WeakReference;

public class WeakishReference
{
    private WeakReference weakref;
    
    WeakishReference(final Object reference) {
        this.weakref = new WeakReference((T)reference);
    }
    
    public Object get() {
        return this.weakref.get();
    }
    
    public static WeakishReference createReference(final Object object) {
        return new WeakishReference(object);
    }
    
    public static class HardReference extends WeakishReference
    {
        public HardReference(final Object object) {
            super(object);
        }
    }
}
