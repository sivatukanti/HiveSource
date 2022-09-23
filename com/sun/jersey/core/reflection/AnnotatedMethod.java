// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.reflection;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.HttpMethod;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.lang.reflect.AnnotatedElement;

public final class AnnotatedMethod implements AnnotatedElement
{
    private static final Set<Class<? extends Annotation>> METHOD_META_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATIONS;
    private static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS;
    private final Method m;
    private final Method am;
    private final Annotation[] methodAnnotations;
    private final Annotation[][] parameterAnnotations;
    
    private static Set<Class<? extends Annotation>> getSet(final Class<? extends Annotation>... cs) {
        final Set<Class<? extends Annotation>> s = new HashSet<Class<? extends Annotation>>();
        for (final Class<? extends Annotation> c : cs) {
            s.add(c);
        }
        return s;
    }
    
    public AnnotatedMethod(final Method m) {
        this.m = m;
        this.am = findAnnotatedMethod(m);
        if (m.equals(this.am)) {
            this.methodAnnotations = m.getAnnotations();
            this.parameterAnnotations = m.getParameterAnnotations();
        }
        else {
            this.methodAnnotations = mergeMethodAnnotations(m, this.am);
            this.parameterAnnotations = mergeParameterAnnotations(m, this.am);
        }
    }
    
    public Method getMethod() {
        return this.am;
    }
    
    public Annotation[][] getParameterAnnotations() {
        return this.parameterAnnotations.clone();
    }
    
    public Class<?>[] getParameterTypes() {
        return this.am.getParameterTypes();
    }
    
    public TypeVariable<Method>[] getTypeParameters() {
        return this.am.getTypeParameters();
    }
    
    public Type[] getGenericParameterTypes() {
        return this.am.getGenericParameterTypes();
    }
    
    public <T extends Annotation> List<T> getMetaMethodAnnotations(final Class<T> annotation) {
        final List<T> ma = new ArrayList<T>();
        for (final Annotation a : this.methodAnnotations) {
            if (a.annotationType().getAnnotation(annotation) != null) {
                ma.add(a.annotationType().getAnnotation(annotation));
            }
        }
        return ma;
    }
    
    @Override
    public String toString() {
        return this.m.toString();
    }
    
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        for (final Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() == annotationType) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        for (final Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() == annotationType) {
                return annotationType.cast(ma);
            }
        }
        return this.am.getAnnotation(annotationType);
    }
    
    @Override
    public Annotation[] getAnnotations() {
        return this.methodAnnotations.clone();
    }
    
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }
    
    private static Annotation[] mergeMethodAnnotations(final Method m, final Method am) {
        final List<Annotation> al = asList(m.getAnnotations());
        for (final Annotation a : am.getAnnotations()) {
            if (!m.isAnnotationPresent(a.getClass())) {
                al.add(a);
            }
        }
        return al.toArray(new Annotation[0]);
    }
    
    private static Annotation[][] mergeParameterAnnotations(final Method m, final Method am) {
        final Annotation[][] mp = m.getParameterAnnotations();
        final Annotation[][] amp = am.getParameterAnnotations();
        final List<List<Annotation>> ala = new ArrayList<List<Annotation>>();
        for (int i = 0; i < mp.length; ++i) {
            final List<Annotation> al = asList(mp[i]);
            for (final Annotation a : amp[i]) {
                if (!isAnnotatonPresent(a.getClass(), al)) {
                    al.add(a);
                }
            }
            ala.add(al);
        }
        final Annotation[][] paa = new Annotation[mp.length][];
        for (int j = 0; j < mp.length; ++j) {
            paa[j] = ala.get(j).toArray(new Annotation[0]);
        }
        return paa;
    }
    
    private static boolean isAnnotatonPresent(final Class<? extends Annotation> ca, final List<Annotation> la) {
        for (final Annotation a : la) {
            if (ca == a.getClass()) {
                return true;
            }
        }
        return false;
    }
    
    private static Method findAnnotatedMethod(final Method m) {
        final Method am = findAnnotatedMethod(m.getDeclaringClass(), m);
        return (am != null) ? am : m;
    }
    
    private static Method findAnnotatedMethod(final Class<?> c, Method m) {
        if (c == Object.class) {
            return null;
        }
        m = AccessController.doPrivileged(ReflectionHelper.findMethodOnClassPA(c, m));
        if (m == null) {
            return null;
        }
        if (hasAnnotations(m)) {
            return m;
        }
        final Class<?> sc = c.getSuperclass();
        if (sc != null && sc != Object.class) {
            final Method sm = findAnnotatedMethod(sc, m);
            if (sm != null) {
                return sm;
            }
        }
        for (final Class<?> ic : c.getInterfaces()) {
            final Method im = findAnnotatedMethod(ic, m);
            if (im != null) {
                return im;
            }
        }
        return null;
    }
    
    private static boolean hasAnnotations(final Method m) {
        return hasMetaMethodAnnotations(m) || hasMethodAnnotations(m) || hasParameterAnnotations(m);
    }
    
    private static boolean hasMetaMethodAnnotations(final Method m) {
        for (final Class<? extends Annotation> ac : AnnotatedMethod.METHOD_META_ANNOTATIONS) {
            for (final Annotation a : m.getAnnotations()) {
                if (a.annotationType().getAnnotation(ac) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean hasMethodAnnotations(final Method m) {
        for (final Class<? extends Annotation> ac : AnnotatedMethod.METHOD_ANNOTATIONS) {
            if (m.isAnnotationPresent(ac)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasParameterAnnotations(final Method m) {
        for (final Annotation[] arr$2 : m.getParameterAnnotations()) {
            final Annotation[] as = arr$2;
            for (final Annotation a : arr$2) {
                if (AnnotatedMethod.PARAMETER_ANNOTATIONS.contains(a.annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static <T> List<T> asList(final T... ts) {
        final List<T> l = new ArrayList<T>();
        for (final T t : ts) {
            l.add(t);
        }
        return l;
    }
    
    static {
        METHOD_META_ANNOTATIONS = getSet(HttpMethod.class);
        METHOD_ANNOTATIONS = getSet(Path.class, Produces.class, Consumes.class);
        PARAMETER_ANNOTATIONS = getSet(Context.class, Encoded.class, DefaultValue.class, MatrixParam.class, QueryParam.class, CookieParam.class, HeaderParam.class, PathParam.class, FormParam.class);
    }
}
