// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import java.util.ArrayList;
import java.util.List;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.lang.reflect.Type;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import java.util.Map;
import com.sun.jersey.spi.inject.InjectableProviderContext;

public class InjectableProviderFactory implements InjectableProviderContext
{
    private final Map<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>> ipm;
    
    public InjectableProviderFactory() {
        this.ipm = new HashMap<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>>();
    }
    
    public final void update(final InjectableProviderFactory ipf) {
        for (final Map.Entry<Class<? extends Annotation>, LinkedList<MetaInjectableProvider>> e : ipf.ipm.entrySet()) {
            this.getList(e.getKey()).addAll(e.getValue());
        }
    }
    
    public final void add(final InjectableProvider ip) {
        final Type[] args = this.getMetaArguments(ip.getClass());
        if (args != null) {
            final MetaInjectableProvider mip = new MetaInjectableProvider(ip, (Class<? extends Annotation>)args[0], (Class<?>)args[1]);
            this.getList(mip.ac).add(mip);
        }
    }
    
    public final void configure(final ProviderServices providerServices) {
        providerServices.getProvidersAndServices(InjectableProvider.class, new ProviderServices.ProviderListener<InjectableProvider>() {
            @Override
            public void onAdd(final InjectableProvider ip) {
                InjectableProviderFactory.this.add(ip);
            }
        });
    }
    
    public final void configureProviders(final ProviderServices providerServices) {
        providerServices.getProviders(InjectableProvider.class, new ProviderServices.ProviderListener<InjectableProvider>() {
            @Override
            public void onAdd(final InjectableProvider ip) {
                InjectableProviderFactory.this.add(ip);
            }
        });
    }
    
    private LinkedList<MetaInjectableProvider> getList(final Class<? extends Annotation> c) {
        LinkedList<MetaInjectableProvider> l = this.ipm.get(c);
        if (l == null) {
            l = new LinkedList<MetaInjectableProvider>();
            this.ipm.put(c, l);
        }
        return l;
    }
    
    private Type[] getMetaArguments(final Class<? extends InjectableProvider> c) {
        for (Class _c = c; _c != Object.class; _c = _c.getSuperclass()) {
            final Type[] arr$;
            final Type[] ts = arr$ = _c.getGenericInterfaces();
            for (final Type t : arr$) {
                if (t instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)t;
                    if (pt.getRawType() == InjectableProvider.class) {
                        final Type[] args = pt.getActualTypeArguments();
                        for (int i = 0; i < args.length; ++i) {
                            args[i] = this.getResolvedType(args[i], c, _c);
                        }
                        if (args[0] instanceof Class && args[1] instanceof Class) {
                            return args;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private Type getResolvedType(final Type t, final Class c, final Class dc) {
        if (t instanceof Class) {
            return t;
        }
        if (t instanceof TypeVariable) {
            final ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
            if (ct != null) {
                return ct.c;
            }
            return t;
        }
        else {
            if (t instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)t;
                return pt.getRawType();
            }
            return t;
        }
    }
    
    private List<MetaInjectableProvider> findInjectableProviders(final Class<? extends Annotation> ac, final Class<?> cc, final ComponentScope s) {
        final List<MetaInjectableProvider> subips = new ArrayList<MetaInjectableProvider>();
        for (final MetaInjectableProvider i : this.getList(ac)) {
            if (s == i.ip.getScope() && i.cc.isAssignableFrom(cc)) {
                subips.add(i);
            }
        }
        return subips;
    }
    
    @Override
    public boolean isAnnotationRegistered(final Class<? extends Annotation> ac, final Class<?> cc) {
        for (final MetaInjectableProvider i : this.getList(ac)) {
            if (i.cc.isAssignableFrom(cc)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isInjectableProviderRegistered(final Class<? extends Annotation> ac, final Class<?> cc, final ComponentScope s) {
        return !this.findInjectableProviders(ac, cc, s).isEmpty();
    }
    
    @Override
    public final <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final ComponentScope s) {
        for (final MetaInjectableProvider mip : this.findInjectableProviders(ac, c.getClass(), s)) {
            final Injectable i = mip.ip.getInjectable(ic, a, c);
            if (i != null) {
                return i;
            }
        }
        return null;
    }
    
    @Override
    public final <A extends Annotation, C> Injectable getInjectable(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final List<ComponentScope> ls) {
        for (final ComponentScope s : ls) {
            final Injectable i = this.getInjectable(ac, ic, a, c, s);
            if (i != null) {
                return i;
            }
        }
        return null;
    }
    
    @Override
    public <A extends Annotation, C> InjectableScopePair getInjectableWithScope(final Class<? extends Annotation> ac, final ComponentContext ic, final A a, final C c, final List<ComponentScope> ls) {
        for (final ComponentScope s : ls) {
            final Injectable i = this.getInjectable(ac, ic, a, c, s);
            if (i != null) {
                return new InjectableScopePair(i, s);
            }
        }
        return null;
    }
    
    private static final class MetaInjectableProvider
    {
        final InjectableProvider ip;
        final Class<? extends Annotation> ac;
        final Class<?> cc;
        
        MetaInjectableProvider(final InjectableProvider ip, final Class<? extends Annotation> ac, final Class<?> cc) {
            this.ip = ip;
            this.ac = ac;
            this.cc = cc;
        }
    }
}
