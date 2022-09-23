// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.inject;

import java.util.List;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.spi.inject.InjectableProviderContext;

public interface ServerInjectableProviderContext extends InjectableProviderContext
{
    boolean isParameterTypeRegistered(final Parameter p0);
    
    Injectable getInjectable(final Parameter p0, final ComponentScope p1);
    
    Injectable getInjectable(final AccessibleObject p0, final Parameter p1, final ComponentScope p2);
    
    InjectableScopePair getInjectableiWithScope(final Parameter p0, final ComponentScope p1);
    
    InjectableScopePair getInjectableiWithScope(final AccessibleObject p0, final Parameter p1, final ComponentScope p2);
    
    List<Injectable> getInjectable(final List<Parameter> p0, final ComponentScope p1);
    
    List<Injectable> getInjectable(final AccessibleObject p0, final List<Parameter> p1, final ComponentScope p2);
}
