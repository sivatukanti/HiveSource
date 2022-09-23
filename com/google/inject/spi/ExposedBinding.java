// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.Binding;

public interface ExposedBinding<T> extends Binding<T>, HasDependencies
{
    PrivateElements getPrivateElements();
    
    void applyTo(final Binder p0);
}
