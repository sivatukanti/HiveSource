// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.binder;

public interface ConstantBindingBuilder
{
    void to(final String p0);
    
    void to(final int p0);
    
    void to(final long p0);
    
    void to(final boolean p0);
    
    void to(final double p0);
    
    void to(final float p0);
    
    void to(final short p0);
    
    void to(final char p0);
    
    void to(final byte p0);
    
    void to(final Class<?> p0);
    
     <E extends Enum<E>> void to(final E p0);
}
