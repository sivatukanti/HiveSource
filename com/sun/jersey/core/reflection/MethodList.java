// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.reflection;

import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.List;

public class MethodList implements Iterable<AnnotatedMethod>
{
    private AnnotatedMethod[] methods;
    
    public MethodList(final Class c) {
        this(c, false);
    }
    
    public MethodList(final Class c, final boolean declaredMethods) {
        this(declaredMethods ? getAllDeclaredMethods(c) : getMethods(c));
    }
    
    private static List<Method> getAllDeclaredMethods(Class c) {
        final List<Method> l = new ArrayList<Method>();
        while (c != null && c != Object.class) {
            l.addAll(Arrays.asList(c.getDeclaredMethods()));
            c = c.getSuperclass();
        }
        return l;
    }
    
    private static List<Method> getMethods(final Class c) {
        return Arrays.asList(c.getMethods());
    }
    
    public MethodList(final List<Method> methods) {
        final List<AnnotatedMethod> l = new ArrayList<AnnotatedMethod>();
        for (final Method m : methods) {
            if (!m.isBridge() && m.getDeclaringClass() != Object.class) {
                l.add(new AnnotatedMethod(m));
            }
        }
        this.methods = new AnnotatedMethod[l.size()];
        this.methods = l.toArray(this.methods);
    }
    
    public MethodList(final Method... methods) {
        final List<AnnotatedMethod> l = new ArrayList<AnnotatedMethod>();
        for (final Method m : methods) {
            if (!m.isBridge() && m.getDeclaringClass() != Object.class) {
                l.add(new AnnotatedMethod(m));
            }
        }
        this.methods = new AnnotatedMethod[l.size()];
        this.methods = l.toArray(this.methods);
    }
    
    public MethodList(final AnnotatedMethod... methods) {
        this.methods = methods;
    }
    
    @Override
    public Iterator<AnnotatedMethod> iterator() {
        return Arrays.asList(this.methods).iterator();
    }
    
    public <T extends Annotation> MethodList isNotPublic() {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return !Modifier.isPublic(m.getMethod().getModifiers());
            }
        });
    }
    
    public <T extends Annotation> MethodList hasNumParams(final int i) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getParameterTypes().length == i;
            }
        });
    }
    
    public <T extends Annotation> MethodList hasReturnType(final Class<?> r) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getMethod().getReturnType() == r;
            }
        });
    }
    
    public <T extends Annotation> MethodList nameStartsWith(final String s) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getMethod().getName().startsWith(s);
            }
        });
    }
    
    public <T extends Annotation> MethodList hasAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getAnnotation((Class<Annotation>)annotation) != null;
            }
        });
    }
    
    public <T extends Annotation> MethodList hasMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                for (final Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation((Class<Annotation>)annotation) != null) {
                        return true;
                    }
                }
                return false;
            }
        });
    }
    
    public <T extends Annotation> MethodList hasNotAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                return m.getAnnotation((Class<Annotation>)annotation) == null;
            }
        });
    }
    
    public <T extends Annotation> MethodList hasNotMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter() {
            @Override
            public boolean keep(final AnnotatedMethod m) {
                for (final Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation((Class<Annotation>)annotation) != null) {
                        return false;
                    }
                }
                return true;
            }
        });
    }
    
    public MethodList filter(final Filter f) {
        final List<AnnotatedMethod> r = new ArrayList<AnnotatedMethod>();
        for (final AnnotatedMethod m : this.methods) {
            if (f.keep(m)) {
                r.add(m);
            }
        }
        return new MethodList((AnnotatedMethod[])r.toArray(new AnnotatedMethod[0]));
    }
    
    public interface Filter
    {
        boolean keep(final AnnotatedMethod p0);
    }
}
