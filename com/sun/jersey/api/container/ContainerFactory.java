// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container;

import com.sun.jersey.api.core.ClasspathResourceConfig;
import java.io.File;
import java.util.Iterator;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerNotifier;
import java.util.List;
import com.sun.jersey.spi.service.ServiceFinder;
import com.sun.jersey.spi.container.ContainerProvider;
import java.util.LinkedList;
import com.sun.jersey.spi.container.WebApplicationFactory;
import java.util.Set;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.core.DefaultResourceConfig;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;

public final class ContainerFactory
{
    private ContainerFactory() {
    }
    
    public static <A> A createContainer(final Class<A> type, final Class<?>... resourceClasses) throws ContainerException, IllegalArgumentException {
        final Set<Class<?>> resourceClassesSet = new HashSet<Class<?>>(Arrays.asList(resourceClasses));
        return createContainer(type, new DefaultResourceConfig(resourceClassesSet), null);
    }
    
    public static <A> A createContainer(final Class<A> type, final Set<Class<?>> resourceClasses) throws ContainerException, IllegalArgumentException {
        return createContainer(type, new DefaultResourceConfig(resourceClasses), null);
    }
    
    public static <A> A createContainer(final Class<A> type, final ResourceConfig resourceConfig) throws ContainerException, IllegalArgumentException {
        return createContainer(type, resourceConfig, null);
    }
    
    public static <A> A createContainer(final Class<A> type, final ResourceConfig resourceConfig, final IoCComponentProviderFactory factory) throws ContainerException, IllegalArgumentException {
        final WebApplication wa = WebApplicationFactory.createWebApplication();
        final LinkedList<ContainerProvider> cps = new LinkedList<ContainerProvider>();
        for (final ContainerProvider cp : ServiceFinder.find(ContainerProvider.class, true)) {
            cps.addFirst(cp);
        }
        for (final ContainerProvider<A> cp2 : cps) {
            final A c = cp2.createContainer(type, resourceConfig, wa);
            if (c != null) {
                if (!wa.isInitiated()) {
                    wa.initiate(resourceConfig, factory);
                }
                final Object o = resourceConfig.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier");
                if (o instanceof List) {
                    final List list = (List)o;
                    for (final Object elem : list) {
                        if (elem instanceof ContainerNotifier && c instanceof ContainerListener) {
                            final ContainerNotifier crf = (ContainerNotifier)elem;
                            crf.addListener((ContainerListener)c);
                        }
                    }
                }
                else if (o instanceof ContainerNotifier && c instanceof ContainerListener) {
                    final ContainerNotifier crf2 = (ContainerNotifier)o;
                    crf2.addListener((ContainerListener)c);
                }
                return c;
            }
        }
        throw new IllegalArgumentException("No container provider supports the type " + type);
    }
    
    @Deprecated
    public static <A> A createContainer(final Class<A> type, final String packageName) throws ContainerException, IllegalArgumentException {
        final String resourcesClassName = packageName + ".WebResources";
        try {
            final Class<?> resourcesClass = ContainerFactory.class.getClassLoader().loadClass(resourcesClassName);
            final ResourceConfig config = (ResourceConfig)resourcesClass.newInstance();
            return createContainer(type, config, null);
        }
        catch (ClassNotFoundException e) {
            throw new ContainerException(e);
        }
        catch (InstantiationException e2) {
            throw new ContainerException(e2);
        }
        catch (IllegalAccessException e3) {
            throw new ContainerException(e3);
        }
    }
    
    public static <A> A createContainer(final Class<A> type) {
        final String classPath = System.getProperty("java.class.path");
        final String[] paths = classPath.split(File.pathSeparator);
        return createContainer(type, paths);
    }
    
    public static <A> A createContainer(final Class<A> type, final String... paths) {
        final ClasspathResourceConfig config = new ClasspathResourceConfig(paths);
        return createContainer(type, config, null);
    }
}
