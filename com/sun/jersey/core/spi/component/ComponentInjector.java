// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Type;
import java.util.Iterator;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import javax.ws.rs.Path;
import javax.ws.rs.HttpMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.spi.inject.InjectableProviderContext;

public class ComponentInjector<T>
{
    protected final InjectableProviderContext ipc;
    protected final Class<T> c;
    
    public ComponentInjector(final InjectableProviderContext ipc, final Class<T> c) {
        this.ipc = ipc;
        this.c = c;
    }
    
    public void inject(final T t) {
        final AnnotatedContext aoc = new AnnotatedContext();
        for (Class oClass = this.c; oClass != Object.class; oClass = oClass.getSuperclass()) {
            for (final Field f : oClass.getDeclaredFields()) {
                aoc.setAccessibleObject(f);
                final Annotation[] as = f.getAnnotations();
                aoc.setAnnotations(as);
                boolean missingDependency = false;
                for (final Annotation a : as) {
                    final Injectable i = this.ipc.getInjectable(a.annotationType(), aoc, a, f.getGenericType(), ComponentScope.UNDEFINED_SINGLETON);
                    if (i != null) {
                        missingDependency = false;
                        this.setFieldValue(t, f, i.getValue());
                        break;
                    }
                    if (this.ipc.isAnnotationRegistered(a.annotationType(), f.getGenericType().getClass())) {
                        missingDependency = true;
                    }
                }
                if (missingDependency) {
                    Errors.missingDependency(f);
                }
            }
        }
        final MethodList ml = new MethodList(this.c.getMethods());
        int methodIndex = 0;
        for (final AnnotatedMethod m : ml.hasNotMetaAnnotation(HttpMethod.class).hasNotAnnotation(Path.class).hasNumParams(1).hasReturnType(Void.TYPE).nameStartsWith("set")) {
            final Annotation[] as = m.getAnnotations();
            aoc.setAccessibleObject(m.getMethod());
            aoc.setAnnotations(as);
            final Type gpt = m.getGenericParameterTypes()[0];
            boolean missingDependency2 = false;
            for (final Annotation a2 : as) {
                final Injectable j = this.ipc.getInjectable(a2.annotationType(), aoc, a2, gpt, ComponentScope.UNDEFINED_SINGLETON);
                if (j != null) {
                    missingDependency2 = false;
                    this.setMethodValue(t, m, j.getValue());
                    break;
                }
                if (this.ipc.isAnnotationRegistered(a2.annotationType(), gpt.getClass())) {
                    missingDependency2 = true;
                }
            }
            if (missingDependency2) {
                Errors.missingDependency(m.getMethod(), methodIndex);
            }
            ++methodIndex;
        }
    }
    
    private void setFieldValue(final Object resource, final Field f, final Object value) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    f.set(resource, value);
                    return null;
                }
                catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    
    private void setMethodValue(final Object o, final AnnotatedMethod m, final Object value) {
        try {
            m.getMethod().invoke(o, value);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
