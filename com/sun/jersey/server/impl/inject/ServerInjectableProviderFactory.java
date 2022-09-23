// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.inject;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.AnnotatedContext;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;

public final class ServerInjectableProviderFactory extends InjectableProviderFactory implements ServerInjectableProviderContext
{
    @Override
    public boolean isParameterTypeRegistered(final Parameter p) {
        return p.getAnnotation() != null && (this.isAnnotationRegistered(p.getAnnotation().annotationType(), p.getClass()) || this.isAnnotationRegistered(p.getAnnotation().annotationType(), p.getParameterType().getClass()));
    }
    
    @Override
    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(final Parameter p, final ComponentScope s) {
        return this.getInjectableiWithScope(null, p, s);
    }
    
    @Override
    public InjectableProviderContext.InjectableScopePair getInjectableiWithScope(final AccessibleObject ao, final Parameter p, final ComponentScope s) {
        if (p.getAnnotation() == null) {
            return null;
        }
        final ComponentContext ic = new AnnotatedContext(ao, p.getAnnotations());
        if (s != ComponentScope.PerRequest) {
            return this.getInjectableWithScope(p.getAnnotation().annotationType(), ic, p.getAnnotation(), p.getParameterType(), ComponentScope.UNDEFINED_SINGLETON);
        }
        final Injectable i = this.getInjectable(p.getAnnotation().annotationType(), ic, p.getAnnotation(), p, ComponentScope.PerRequest);
        if (i != null) {
            return new InjectableProviderContext.InjectableScopePair(i, ComponentScope.PerRequest);
        }
        return this.getInjectableWithScope(p.getAnnotation().annotationType(), ic, p.getAnnotation(), p.getParameterType(), ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
    }
    
    @Override
    public Injectable getInjectable(final Parameter p, final ComponentScope s) {
        return this.getInjectable(null, p, s);
    }
    
    @Override
    public Injectable getInjectable(final AccessibleObject ao, final Parameter p, final ComponentScope s) {
        final InjectableProviderContext.InjectableScopePair isp = this.getInjectableiWithScope(ao, p, s);
        if (isp == null) {
            return null;
        }
        return isp.i;
    }
    
    @Override
    public List<Injectable> getInjectable(final List<Parameter> ps, final ComponentScope s) {
        return this.getInjectable(null, ps, s);
    }
    
    @Override
    public List<Injectable> getInjectable(final AccessibleObject ao, final List<Parameter> ps, final ComponentScope s) {
        final List<Injectable> is = new ArrayList<Injectable>();
        for (final Parameter p : ps) {
            is.add(this.getInjectable(ao, p, s));
        }
        return is;
    }
}
