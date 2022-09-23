// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$Type;

public abstract class $ClassInfo
{
    protected $ClassInfo() {
    }
    
    public abstract $Type getType();
    
    public abstract $Type getSuperType();
    
    public abstract $Type[] getInterfaces();
    
    public abstract int getModifiers();
    
    public boolean equals(final Object o) {
        return o != null && o instanceof $ClassInfo && this.getType().equals((($ClassInfo)o).getType());
    }
    
    public int hashCode() {
        return this.getType().hashCode();
    }
    
    public String toString() {
        return this.getType().getClassName();
    }
}
