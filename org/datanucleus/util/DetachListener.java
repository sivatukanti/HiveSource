// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import javax.jdo.JDODetachedFieldAccessException;

public abstract class DetachListener
{
    private static DetachListener instance;
    
    public static DetachListener getInstance() {
        if (DetachListener.instance == null) {
            synchronized (DetachListener.class) {
                if (DetachListener.instance == null) {
                    DetachListener.instance = new DetachListener() {
                        @Override
                        public void undetachedFieldAccess(final Object instance, final String fieldName) {
                            throw new JDODetachedFieldAccessException("You have just attempted to access field \"" + fieldName + "\" yet this field was not detached when you detached the object." + " Either dont access this field, or detach it when detaching the object.");
                        }
                    };
                }
            }
        }
        return DetachListener.instance;
    }
    
    public static void setInstance(final DetachListener instance) {
        synchronized (DetachListener.class) {
            DetachListener.instance = instance;
        }
    }
    
    public abstract void undetachedFieldAccess(final Object p0, final String p1);
}
