// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import com.google.inject.Key;
import java.util.Set;
import com.google.inject.Injector;
import java.util.List;

public interface PrivateElements extends Element
{
    List<Element> getElements();
    
    Injector getInjector();
    
    Set<Key<?>> getExposedKeys();
    
    Object getExposedSource(final Key<?> p0);
}
