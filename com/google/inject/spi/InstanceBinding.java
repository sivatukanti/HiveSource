// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.util.Set;
import com.google.inject.Binding;

public interface InstanceBinding<T> extends Binding<T>, HasDependencies
{
    T getInstance();
    
    Set<InjectionPoint> getInjectionPoints();
}
