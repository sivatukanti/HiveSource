// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.classfile.ClassHolder;
import org.apache.derby.iapi.services.cache.Cacheable;

class VMTypeIdCacheable implements Cacheable
{
    private Object descriptor;
    private Object key;
    
    public void clearIdentity() {
    }
    
    public Object getIdentity() {
        return this.key;
    }
    
    public Cacheable createIdentity(final Object o, final Object o2) {
        return this;
    }
    
    public Cacheable setIdentity(final Object key) {
        this.key = key;
        if (key instanceof String) {
            final String s = (String)key;
            this.descriptor = new Type(s, ClassHolder.convertToInternalDescriptor(s));
        }
        else {
            this.descriptor = ((BCMethodDescriptor)key).buildMethodDescriptor();
        }
        return this;
    }
    
    public void clean(final boolean b) {
    }
    
    public boolean isDirty() {
        return false;
    }
    
    Object descriptor() {
        return this.descriptor;
    }
}
