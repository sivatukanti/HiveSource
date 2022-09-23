// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;

public interface Element
{
    Object getSource();
    
     <T> T acceptVisitor(final ElementVisitor<T> p0);
    
    void applyTo(final Binder p0);
}
