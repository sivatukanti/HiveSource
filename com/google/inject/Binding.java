// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Element;

public interface Binding<T> extends Element
{
    Key<T> getKey();
    
    Provider<T> getProvider();
    
     <V> V acceptTargetVisitor(final BindingTargetVisitor<? super T, V> p0);
    
     <V> V acceptScopingVisitor(final BindingScopingVisitor<V> p0);
}
