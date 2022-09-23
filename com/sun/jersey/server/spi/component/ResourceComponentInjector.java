// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.spi.component;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractSetterMethod;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.util.Iterator;
import java.util.Map;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.spi.inject.Injectable;
import java.util.HashMap;
import com.sun.jersey.api.model.AbstractField;
import java.util.List;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import java.lang.reflect.Method;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.reflect.Field;

public final class ResourceComponentInjector
{
    private Field[] singletonFields;
    private Object[] singletonFieldValues;
    private Field[] perRequestFields;
    private AbstractHttpContextInjectable<?>[] perRequestFieldInjectables;
    private Method[] singletonSetters;
    private Object[] singletonSetterValues;
    private Method[] perRequestSetters;
    private AbstractHttpContextInjectable<?>[] perRequestSetterInjectables;
    
    public ResourceComponentInjector(final ServerInjectableProviderContext ipc, final ComponentScope s, final AbstractResource resource) {
        this.processFields(ipc, s, resource.getFields());
        this.processSetters(ipc, s, resource.getSetterMethods());
    }
    
    public boolean hasInjectableArtifacts() {
        return this.singletonFields.length > 0 || this.perRequestFields.length > 0 || this.singletonSetters.length > 0 || this.perRequestSetters.length > 0;
    }
    
    private void processFields(final ServerInjectableProviderContext ipc, final ComponentScope s, final List<AbstractField> fields) {
        final Map<Field, Injectable<?>> singletons = new HashMap<Field, Injectable<?>>();
        final Map<Field, Injectable<?>> perRequest = new HashMap<Field, Injectable<?>>();
        for (final AbstractField af : fields) {
            final Parameter p = af.getParameters().get(0);
            final InjectableProviderContext.InjectableScopePair isp = ipc.getInjectableiWithScope(af.getField(), p, s);
            if (isp != null) {
                this.configureField(af.getField());
                if (s == ComponentScope.PerRequest && isp.cs != ComponentScope.Singleton) {
                    perRequest.put(af.getField(), isp.i);
                }
                else {
                    singletons.put(af.getField(), isp.i);
                }
            }
            else {
                if (!ipc.isParameterTypeRegistered(p)) {
                    continue;
                }
                Errors.missingDependency(af.getField());
            }
        }
        int size = singletons.entrySet().size();
        this.singletonFields = new Field[size];
        this.singletonFieldValues = new Object[size];
        int i = 0;
        for (final Map.Entry<Field, Injectable<?>> e : singletons.entrySet()) {
            this.singletonFields[i] = e.getKey();
            this.singletonFieldValues[i++] = e.getValue().getValue();
        }
        size = perRequest.entrySet().size();
        this.perRequestFields = new Field[size];
        this.perRequestFieldInjectables = (AbstractHttpContextInjectable<?>[])new AbstractHttpContextInjectable[size];
        i = 0;
        for (final Map.Entry<Field, Injectable<?>> e : perRequest.entrySet()) {
            this.perRequestFields[i] = e.getKey();
            this.perRequestFieldInjectables[i++] = AbstractHttpContextInjectable.transform(e.getValue());
        }
    }
    
    private void configureField(final Field f) {
        if (!f.isAccessible()) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    f.setAccessible(true);
                    return null;
                }
            });
        }
    }
    
    private void processSetters(final ServerInjectableProviderContext ipc, final ComponentScope s, final List<AbstractSetterMethod> setterMethods) {
        final Map<Method, Injectable<?>> singletons = new HashMap<Method, Injectable<?>>();
        final Map<Method, Injectable<?>> perRequest = new HashMap<Method, Injectable<?>>();
        int methodIndex = 0;
        for (final AbstractSetterMethod sm : setterMethods) {
            final Parameter p = sm.getParameters().get(0);
            final InjectableProviderContext.InjectableScopePair isp = ipc.getInjectableiWithScope(sm.getMethod(), p, s);
            if (isp != null) {
                if (s == ComponentScope.PerRequest && isp.cs != ComponentScope.Singleton) {
                    perRequest.put(sm.getMethod(), isp.i);
                }
                else {
                    singletons.put(sm.getMethod(), isp.i);
                }
            }
            else if (ipc.isParameterTypeRegistered(p)) {
                Errors.missingDependency(sm.getMethod(), methodIndex);
            }
            ++methodIndex;
        }
        int size = singletons.entrySet().size();
        this.singletonSetters = new Method[size];
        this.singletonSetterValues = new Object[size];
        int i = 0;
        for (final Map.Entry<Method, Injectable<?>> e : singletons.entrySet()) {
            this.singletonSetters[i] = e.getKey();
            this.singletonSetterValues[i++] = e.getValue().getValue();
        }
        size = perRequest.entrySet().size();
        this.perRequestSetters = new Method[size];
        this.perRequestSetterInjectables = (AbstractHttpContextInjectable<?>[])new AbstractHttpContextInjectable[size];
        i = 0;
        for (final Map.Entry<Method, Injectable<?>> e : perRequest.entrySet()) {
            this.perRequestSetters[i] = e.getKey();
            this.perRequestSetterInjectables[i++] = AbstractHttpContextInjectable.transform(e.getValue());
        }
    }
    
    public void inject(final HttpContext c, final Object o) {
        int i = 0;
        for (final Field f : this.singletonFields) {
            try {
                f.set(o, this.singletonFieldValues[i++]);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException(ex);
            }
        }
        i = 0;
        for (final Field f : this.perRequestFields) {
            try {
                f.set(o, this.perRequestFieldInjectables[i++].getValue(c));
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException(ex);
            }
        }
        i = 0;
        for (final Method m : this.singletonSetters) {
            try {
                m.invoke(o, this.singletonSetterValues[i++]);
            }
            catch (Exception ex2) {
                throw new ContainerException(ex2);
            }
        }
        i = 0;
        for (final Method m : this.perRequestSetters) {
            try {
                m.invoke(o, this.perRequestSetterInjectables[i++].getValue(c));
            }
            catch (Exception ex2) {
                throw new ContainerException(ex2);
            }
        }
    }
}
