// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.listener.InstanceLifecycleListener;

public class LifecycleListenerForClass
{
    private final Class[] classes;
    private final InstanceLifecycleListener listener;
    
    public LifecycleListenerForClass(final InstanceLifecycleListener listener, final Class[] classes) {
        this.classes = classes;
        this.listener = listener;
    }
    
    public InstanceLifecycleListener getListener() {
        return this.listener;
    }
    
    public Class[] getClasses() {
        return this.classes;
    }
    
    public boolean forClass(final Class cls) {
        if (this.classes == null) {
            return true;
        }
        for (int i = 0; i < this.classes.length; ++i) {
            if (this.classes[i].isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }
    
    LifecycleListenerForClass mergeClasses(final Class[] extraClasses) {
        if (this.classes == null) {
            return this;
        }
        if (extraClasses == null) {
            return new LifecycleListenerForClass(this.listener, null);
        }
        final Class[] allClasses = new Class[this.classes.length + extraClasses.length];
        System.arraycopy(this.classes, 0, allClasses, 0, this.classes.length);
        System.arraycopy(extraClasses, 0, allClasses, this.classes.length, extraClasses.length);
        return new LifecycleListenerForClass(this.listener, allClasses);
    }
    
    static Class[] canonicaliseClasses(final Class[] classes) {
        if (classes == null) {
            return null;
        }
        int count = 0;
        for (final Class c : classes) {
            if (c == null) {
                ++count;
            }
        }
        final Class[] result = new Class[classes.length - count];
        int pos = 0;
        for (final Class c2 : classes) {
            if (c2 != null) {
                result[pos++] = c2;
            }
        }
        return result;
    }
}
