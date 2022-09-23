// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.security.PrivilegedAction;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import java.lang.annotation.Annotation;
import com.sun.jersey.core.reflection.MethodList;
import java.util.HashSet;
import java.util.ArrayList;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.reflect.Method;
import java.util.List;

public class ComponentDestructor
{
    private final List<Method> preDestroys;
    
    public ComponentDestructor(final Class c) {
        this.preDestroys = getPreDestroyMethods(c);
    }
    
    private static List<Method> getPreDestroyMethods(final Class c) {
        final Class preDestroyClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PreDestroy"));
        final List<Method> list = new ArrayList<Method>();
        final HashSet<String> names = new HashSet<String>();
        if (preDestroyClass != null) {
            final MethodList methodList = new MethodList(c, true);
            for (final AnnotatedMethod m : methodList.hasAnnotation((Class<Annotation>)preDestroyClass).hasNumParams(0).hasReturnType(Void.TYPE)) {
                final Method method = m.getMethod();
                if (names.add(method.getName())) {
                    AccessController.doPrivileged((PrivilegedAction<Object>)ReflectionHelper.setAccessibleMethodPA(method));
                    list.add(method);
                }
            }
        }
        return list;
    }
    
    public void destroy(final Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (final Method preDestroy : this.preDestroys) {
            preDestroy.invoke(o, new Object[0]);
        }
    }
}
