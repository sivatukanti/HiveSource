// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.ejb;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import javax.ejb.Local;
import java.util.Collection;
import java.util.Arrays;
import java.lang.annotation.Annotation;
import javax.ejb.Remote;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.lang.reflect.Method;
import com.sun.jersey.api.model.AbstractResource;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchFactory;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public class EJBRequestDispatcherProvider implements ResourceMethodDispatchProvider
{
    @Context
    ResourceMethodCustomInvokerDispatchFactory rdFactory;
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        final AbstractResource declaringResource = abstractResourceMethod.getDeclaringResource();
        if (this.isSessionBean(declaringResource)) {
            final Class<?> resourceClass = declaringResource.getResourceClass();
            final Method javaMethod = abstractResourceMethod.getMethod();
            for (final Class iFace : this.remoteAndLocalIfaces(resourceClass)) {
                try {
                    final Method iFaceMethod = iFace.getDeclaredMethod(javaMethod.getName(), (Class[])javaMethod.getParameterTypes());
                    if (iFaceMethod != null) {
                        return this.createDispatcher(abstractResourceMethod, iFaceMethod);
                    }
                    continue;
                }
                catch (NoSuchMethodException ex2) {}
                catch (SecurityException ex) {
                    Logger.getLogger(EJBRequestDispatcherProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
    
    private List<Class> remoteAndLocalIfaces(final Class<?> resourceClass) {
        final List<Class> allLocalOrRemoteIfaces = new LinkedList<Class>();
        if (resourceClass.isAnnotationPresent((Class<? extends Annotation>)Remote.class)) {
            allLocalOrRemoteIfaces.addAll(Arrays.asList((Class[])resourceClass.getAnnotation(Remote.class).value()));
        }
        if (resourceClass.isAnnotationPresent((Class<? extends Annotation>)Local.class)) {
            allLocalOrRemoteIfaces.addAll(Arrays.asList((Class[])resourceClass.getAnnotation(Local.class).value()));
        }
        for (final Class<?> i : resourceClass.getInterfaces()) {
            if (i.isAnnotationPresent((Class<? extends Annotation>)Remote.class) || i.isAnnotationPresent((Class<? extends Annotation>)Local.class)) {
                allLocalOrRemoteIfaces.add(i);
            }
        }
        return allLocalOrRemoteIfaces;
    }
    
    private RequestDispatcher createDispatcher(final AbstractResourceMethod abstractResourceMethod, final Method iFaceMethod) {
        return this.rdFactory.getDispatcher(abstractResourceMethod, new JavaMethodInvoker() {
            @Override
            public Object invoke(final Method m, final Object o, final Object... parameters) throws InvocationTargetException, IllegalAccessException {
                return iFaceMethod.invoke(o, parameters);
            }
        });
    }
    
    private boolean isSessionBean(final AbstractResource ar) {
        return ar.isAnnotationPresent((Class<? extends Annotation>)Stateless.class) || ar.isAnnotationPresent((Class<? extends Annotation>)Stateful.class);
    }
}
