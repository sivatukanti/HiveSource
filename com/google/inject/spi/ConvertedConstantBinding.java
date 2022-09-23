// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.util.Set;
import com.google.inject.Key;
import com.google.inject.Binding;

public interface ConvertedConstantBinding<T> extends Binding<T>, HasDependencies
{
    T getValue();
    
    TypeConverterBinding getTypeConverterBinding();
    
    Key<String> getSourceKey();
    
    Set<Dependency<?>> getDependencies();
}
