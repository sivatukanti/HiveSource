// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.core.HttpContext;
import java.lang.reflect.Type;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.model.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import com.sun.jersey.spi.inject.Injectable;
import java.util.List;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.api.model.AbstractResourceMethod;

public class EntityParamDispatchProvider extends AbstractResourceMethodDispatchProvider
{
    @Override
    protected InjectableValuesProvider getInjectableValuesProvider(final AbstractResourceMethod abstractResourceMethod) {
        return new InjectableValuesProvider(this.processParameters(abstractResourceMethod));
    }
    
    private List<Injectable> processParameters(final AbstractResourceMethod method) {
        if (null == method.getParameters() || 0 == method.getParameters().size()) {
            return (List<Injectable>)Collections.emptyList();
        }
        boolean hasEntity = false;
        final List<Injectable> is = new ArrayList<Injectable>(method.getParameters().size());
        for (int i = 0; i < method.getParameters().size(); ++i) {
            final Parameter parameter = method.getParameters().get(i);
            if (Parameter.Source.ENTITY == parameter.getSource()) {
                hasEntity = true;
                is.add(this.processEntityParameter(parameter, method.getMethod().getParameterAnnotations()[i]));
            }
            else {
                is.add(this.getInjectableProviderContext().getInjectable(method.getMethod(), parameter, ComponentScope.PerRequest));
            }
        }
        if (hasEntity) {
            return is;
        }
        if (Collections.frequency(is, null) == 1) {
            final int i = is.lastIndexOf(null);
            final Parameter parameter = method.getParameters().get(i);
            if (Parameter.Source.UNKNOWN == parameter.getSource() && !parameter.isQualified()) {
                final Injectable ij = this.processEntityParameter(parameter, method.getMethod().getParameterAnnotations()[i]);
                is.set(i, ij);
            }
        }
        return is;
    }
    
    private Injectable processEntityParameter(final Parameter parameter, final Annotation[] annotations) {
        return new EntityInjectable(parameter.getParameterClass(), parameter.getParameterType(), annotations);
    }
    
    static final class EntityInjectable extends AbstractHttpContextInjectable<Object>
    {
        final Class<?> c;
        final Type t;
        final Annotation[] as;
        
        EntityInjectable(final Class c, final Type t, final Annotation[] as) {
            this.c = (Class<?>)c;
            this.t = t;
            this.as = as;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            return context.getRequest().getEntity(this.c, this.t, this.as);
        }
    }
}
