// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.util.Set;
import com.google.inject.Provider;
import com.google.inject.Binding;

public interface ProviderInstanceBinding<T> extends Binding<T>, HasDependencies
{
    Provider<? extends T> getProviderInstance();
    
    Set<InjectionPoint> getInjectionPoints();
}
