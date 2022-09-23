// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.util.Collection;
import java.util.Collections;
import java.lang.reflect.Type;
import java.lang.reflect.Constructor;
import java.util.SortedSet;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.spi.inject.Errors;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.security.PrivilegedAction;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import java.lang.annotation.Annotation;
import com.sun.jersey.core.reflection.MethodList;
import java.util.HashSet;
import java.util.LinkedList;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.Method;
import java.util.List;
import com.sun.jersey.spi.inject.InjectableProviderContext;

public class ComponentConstructor<T>
{
    private final InjectableProviderContext ipc;
    private final Class<T> c;
    private final List<Method> postConstructs;
    private final ComponentInjector<T> ci;
    
    public ComponentConstructor(final InjectableProviderContext ipc, final Class<T> c, final ComponentInjector<T> ci) {
        this.ipc = ipc;
        this.c = c;
        this.ci = ci;
        this.postConstructs = getPostConstructMethods(c);
    }
    
    private static List<Method> getPostConstructMethods(final Class c) {
        final Class postConstructClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PostConstruct"));
        final LinkedList<Method> list = new LinkedList<Method>();
        final HashSet<String> names = new HashSet<String>();
        if (postConstructClass != null) {
            final MethodList methodList = new MethodList(c, true);
            for (final AnnotatedMethod m : methodList.hasAnnotation((Class<Annotation>)postConstructClass).hasNumParams(0).hasReturnType(Void.TYPE)) {
                final Method method = m.getMethod();
                if (names.add(method.getName())) {
                    AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setAccessibleMethodPA(method));
                    list.addFirst(method);
                }
            }
        }
        return list;
    }
    
    public T getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final int modifiers = this.c.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            Errors.nonPublicClass(this.c);
        }
        if (Modifier.isAbstract(modifiers)) {
            if (Modifier.isInterface(modifiers)) {
                Errors.interfaceClass(this.c);
            }
            else {
                Errors.abstractClass(this.c);
            }
        }
        if (this.c.getEnclosingClass() != null && !Modifier.isStatic(modifiers)) {
            Errors.innerClass(this.c);
        }
        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && this.c.getConstructors().length == 0) {
            Errors.nonPublicConstructor(this.c);
        }
        final T t = this._getInstance();
        this.ci.inject(t);
        for (final Method postConstruct : this.postConstructs) {
            postConstruct.invoke(t, new Object[0]);
        }
        return t;
    }
    
    private T _getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final ConstructorInjectablePair<T> cip = this.getConstructor();
        if (cip == null || ((ConstructorInjectablePair<Object>)cip).is.isEmpty()) {
            return this.c.newInstance();
        }
        if (((ConstructorInjectablePair<Object>)cip).is.contains(null)) {
            for (int i = 0; i < ((ConstructorInjectablePair<Object>)cip).is.size(); ++i) {
                if (((ConstructorInjectablePair<Object>)cip).is.get(i) == null) {
                    Errors.missingDependency(((ConstructorInjectablePair<Object>)cip).con, i);
                }
            }
        }
        final Object[] params = new Object[((ConstructorInjectablePair<Object>)cip).is.size()];
        int j = 0;
        for (final Injectable injectable : ((ConstructorInjectablePair<Object>)cip).is) {
            if (injectable != null) {
                params[j++] = injectable.getValue();
            }
        }
        return ((ConstructorInjectablePair<Object>)cip).con.newInstance(params);
    }
    
    private ConstructorInjectablePair<T> getConstructor() {
        if (this.c.getConstructors().length == 0) {
            return null;
        }
        final SortedSet<ConstructorInjectablePair<T>> cs = new TreeSet<ConstructorInjectablePair<T>>((Comparator<? super ConstructorInjectablePair<T>>)new ConstructorComparator());
        final AnnotatedContext aoc = new AnnotatedContext();
        for (final Constructor con : this.c.getConstructors()) {
            final List<Injectable> is = new ArrayList<Injectable>();
            final int ps = con.getParameterTypes().length;
            aoc.setAccessibleObject(con);
            for (int p = 0; p < ps; ++p) {
                final Type pgtype = con.getGenericParameterTypes()[p];
                final Annotation[] as = con.getParameterAnnotations()[p];
                aoc.setAnnotations(as);
                Injectable i = null;
                for (final Annotation a : as) {
                    i = this.ipc.getInjectable(a.annotationType(), aoc, a, pgtype, ComponentScope.UNDEFINED_SINGLETON);
                }
                is.add(i);
            }
            cs.add(new ConstructorInjectablePair<T>(con, (List)is));
        }
        return cs.first();
    }
    
    private static class ConstructorInjectablePair<T>
    {
        private final Constructor<T> con;
        private final List<Injectable> is;
        
        private ConstructorInjectablePair(final Constructor<T> con, final List<Injectable> is) {
            this.con = con;
            this.is = is;
        }
    }
    
    private static class ConstructorComparator<T> implements Comparator<ConstructorInjectablePair<T>>
    {
        @Override
        public int compare(final ConstructorInjectablePair<T> o1, final ConstructorInjectablePair<T> o2) {
            final int p = Collections.frequency(((ConstructorInjectablePair<Object>)o1).is, null) - Collections.frequency(((ConstructorInjectablePair<Object>)o2).is, null);
            if (p != 0) {
                return p;
            }
            return ((ConstructorInjectablePair<Object>)o2).con.getParameterTypes().length - ((ConstructorInjectablePair<Object>)o1).con.getParameterTypes().length;
        }
    }
}
