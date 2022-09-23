// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import javax.ws.rs.FormParam;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.inject.Injectable;
import java.util.List;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import javax.ws.rs.core.Context;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;

public class FormDispatchProvider extends AbstractResourceMethodDispatchProvider
{
    public static final String FORM_PROPERTY = "com.sun.jersey.api.representation.form";
    @Context
    private MultivaluedParameterExtractorProvider mpep;
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        if ("GET".equals(abstractResourceMethod.getHttpMethod())) {
            return null;
        }
        return super.create(abstractResourceMethod, invoker);
    }
    
    @Override
    protected InjectableValuesProvider getInjectableValuesProvider(final AbstractResourceMethod abstractResourceMethod) {
        final List<Injectable> is = this.processParameters(abstractResourceMethod);
        if (is == null) {
            return null;
        }
        return new FormParameterProvider(is);
    }
    
    protected MultivaluedParameterExtractorProvider getMultivaluedParameterExtractorProvider() {
        return this.mpep;
    }
    
    private void processForm(final HttpContext context) {
        Form form = context.getProperties().get("com.sun.jersey.api.representation.form");
        if (form == null) {
            form = context.getRequest().getEntity(Form.class);
            context.getProperties().put("com.sun.jersey.api.representation.form", form);
        }
    }
    
    private List<Injectable> processParameters(final AbstractResourceMethod method) {
        if (method.getParameters().isEmpty()) {
            return null;
        }
        boolean hasFormParam = false;
        for (int i = 0; i < method.getParameters().size(); ++i) {
            final Parameter parameter = method.getParameters().get(i);
            if (parameter.getAnnotation() != null) {
                hasFormParam |= (parameter.getAnnotation().annotationType() == FormParam.class);
            }
        }
        if (!hasFormParam) {
            return null;
        }
        return this.getInjectables(method);
    }
    
    protected List<Injectable> getInjectables(final AbstractResourceMethod method) {
        final List<Injectable> is = new ArrayList<Injectable>(method.getParameters().size());
        for (int i = 0; i < method.getParameters().size(); ++i) {
            final Parameter p = method.getParameters().get(i);
            if (Parameter.Source.ENTITY == p.getSource()) {
                if (MultivaluedMap.class.isAssignableFrom(p.getParameterClass())) {
                    is.add(new FormEntityInjectable(p.getParameterClass(), p.getParameterType(), p.getAnnotations()));
                }
                else {
                    is.add(null);
                }
            }
            else {
                final Injectable injectable = this.getInjectableProviderContext().getInjectable(method.getMethod(), p, ComponentScope.PerRequest);
                is.add(injectable);
            }
        }
        return is;
    }
    
    private final class FormParameterProvider extends InjectableValuesProvider
    {
        public FormParameterProvider(final List<Injectable> is) {
            super(is);
        }
        
        @Override
        public Object[] getInjectableValues(final HttpContext context) {
            FormDispatchProvider.this.processForm(context);
            return super.getInjectableValues(context);
        }
    }
    
    private static final class FormEntityInjectable extends AbstractHttpContextInjectable<Object>
    {
        final Class<?> c;
        final Type t;
        final Annotation[] as;
        
        FormEntityInjectable(final Class c, final Type t, final Annotation[] as) {
            this.c = (Class<?>)c;
            this.t = t;
            this.as = as;
        }
        
        @Override
        public Object getValue(final HttpContext context) {
            return context.getProperties().get("com.sun.jersey.api.representation.form");
        }
    }
}
