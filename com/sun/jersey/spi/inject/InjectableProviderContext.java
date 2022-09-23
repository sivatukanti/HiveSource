// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import java.util.List;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.annotation.Annotation;

public interface InjectableProviderContext
{
    boolean isAnnotationRegistered(final Class<? extends Annotation> p0, final Class<?> p1);
    
    boolean isInjectableProviderRegistered(final Class<? extends Annotation> p0, final Class<?> p1, final ComponentScope p2);
    
     <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> p0, final ComponentContext p1, final A p2, final C p3, final ComponentScope p4);
    
     <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> p0, final ComponentContext p1, final A p2, final C p3, final List<ComponentScope> p4);
    
     <A extends Annotation, C> InjectableScopePair getInjectableWithScope(final Class<? extends Annotation> p0, final ComponentContext p1, final A p2, final C p3, final List<ComponentScope> p4);
    
    public static final class InjectableScopePair
    {
        public final Injectable i;
        public final ComponentScope cs;
        
        public InjectableScopePair(final Injectable i, final ComponentScope cs) {
            this.i = i;
            this.cs = cs;
        }
    }
}
