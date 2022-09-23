// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.inject;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.annotation.Annotation;

public interface InjectableProvider<A extends Annotation, C>
{
    ComponentScope getScope();
    
    Injectable getInjectable(final ComponentContext p0, final A p1, final C p2);
}
