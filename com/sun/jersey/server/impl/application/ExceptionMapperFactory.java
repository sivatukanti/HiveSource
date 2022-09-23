// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import javax.ws.rs.ext.ExceptionMapper;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.util.HashSet;
import java.util.Set;
import com.sun.jersey.spi.container.ExceptionMapperContext;

public class ExceptionMapperFactory implements ExceptionMapperContext
{
    private Set<ExceptionMapperType> emts;
    
    public ExceptionMapperFactory() {
        this.emts = new HashSet<ExceptionMapperType>();
    }
    
    public void init(final ProviderServices providerServices) {
        for (final ExceptionMapper em : providerServices.getProviders(ExceptionMapper.class)) {
            final Class<? extends Throwable> c = this.getExceptionType(em.getClass());
            if (c != null) {
                this.emts.add(new ExceptionMapperType(em, c));
            }
        }
    }
    
    @Override
    public ExceptionMapper find(final Class<? extends Throwable> c) {
        int distance = Integer.MAX_VALUE;
        ExceptionMapper selectedEm = null;
        for (final ExceptionMapperType emt : this.emts) {
            final int d = this.distance(c, emt.c);
            if (d < distance) {
                distance = d;
                selectedEm = emt.em;
                if (distance == 0) {
                    break;
                }
                continue;
            }
        }
        return selectedEm;
    }
    
    private int distance(Class<?> c, final Class<?> emtc) {
        int distance = 0;
        if (!emtc.isAssignableFrom(c)) {
            return Integer.MAX_VALUE;
        }
        while (c != emtc) {
            c = c.getSuperclass();
            ++distance;
        }
        return distance;
    }
    
    private Class<? extends Throwable> getExceptionType(final Class<? extends ExceptionMapper> c) {
        final Class<?> t = (Class<?>)this.getType(c);
        if (Throwable.class.isAssignableFrom(t)) {
            return (Class<? extends Throwable>)t;
        }
        return null;
    }
    
    private Class getType(final Class<? extends ExceptionMapper> c) {
        for (Class _c = c; _c != Object.class; _c = _c.getSuperclass()) {
            final Type[] arr$;
            final Type[] ts = arr$ = _c.getGenericInterfaces();
            for (final Type t : arr$) {
                if (t instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)t;
                    if (pt.getRawType() == ExceptionMapper.class) {
                        return this.getResolvedType(pt.getActualTypeArguments()[0], c, _c);
                    }
                }
            }
        }
        return null;
    }
    
    private Class getResolvedType(final Type t, final Class c, final Class dc) {
        if (t instanceof Class) {
            return (Class)t;
        }
        if (t instanceof TypeVariable) {
            final ReflectionHelper.ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
            if (ct != null) {
                return ct.c;
            }
            return null;
        }
        else {
            if (t instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)t;
                return (Class)pt.getRawType();
            }
            return null;
        }
    }
    
    private static class ExceptionMapperType
    {
        ExceptionMapper em;
        Class<? extends Throwable> c;
        
        public ExceptionMapperType(final ExceptionMapper em, final Class<? extends Throwable> c) {
            this.em = em;
            this.c = c;
        }
    }
}
