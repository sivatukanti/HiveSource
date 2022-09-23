// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.logging.Level;
import java.security.PrivilegedActionException;
import com.sun.jersey.impl.SpiMessages;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Collection;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import java.util.Set;
import com.sun.jersey.spi.inject.ConstrainedToType;
import java.util.logging.Logger;

public class ProviderServices
{
    private static final Logger LOGGER;
    private final Class<? extends ConstrainedToType> constraintToType;
    private final ProviderFactory componentProviderFactory;
    private final Set<Class<?>> providers;
    private final Set providerInstances;
    
    public ProviderServices(final ProviderFactory componentProviderFactory, final Set<Class<?>> providers, final Set<?> providerInstances) {
        this(ConstrainedToType.class, componentProviderFactory, providers, providerInstances);
    }
    
    public ProviderServices(final Class<? extends ConstrainedToType> constraintToType, final ProviderFactory componentProviderFactory, final Set<Class<?>> providers, final Set<?> providerInstances) {
        this.constraintToType = constraintToType;
        this.componentProviderFactory = componentProviderFactory;
        this.providers = providers;
        this.providerInstances = providerInstances;
    }
    
    public void update(final Set<Class<?>> providers, final Set<?> providerInstances, final InjectableProviderFactory ipf) {
        final Set<Class<?>> addedProviders = this.diff(this.providers, providers);
        final Set<?> addedProviderInstances = this.diff(this.providerInstances, providerInstances);
        this.providers.clear();
        this.providers.addAll(providers);
        this.providerInstances.clear();
        this.providerInstances.addAll(providerInstances);
        final ProviderServices _ps = new ProviderServices(this.componentProviderFactory, addedProviders, addedProviderInstances);
        final InjectableProviderFactory _ipf = new InjectableProviderFactory();
        _ipf.configureProviders(_ps);
        ipf.update(_ipf);
    }
    
    private <T> Set<T> diff(final Set<T> s1, final Set<T> s2) {
        final Set<T> diff = new LinkedHashSet<T>();
        for (final T t : s1) {
            if (!s2.contains(t)) {
                diff.add(t);
            }
        }
        for (final T t : s2) {
            if (!s1.contains(t)) {
                diff.add(t);
            }
        }
        return diff;
    }
    
    public ProviderFactory getComponentProviderFactory() {
        return this.componentProviderFactory;
    }
    
    public <T> Set<T> getProviders(final Class<T> provider) {
        final Set<T> ps = new LinkedHashSet<T>();
        ps.addAll((Collection<? extends T>)this.getProviderInstances((Class<Object>)provider));
        for (final Class pc : this.getProviderClasses(provider)) {
            final Object o = this.getComponent(pc);
            if (o != null) {
                ps.add(provider.cast(o));
            }
        }
        return ps;
    }
    
    public <T> Set<T> getServices(final Class<T> provider) {
        final Set<T> ps = new LinkedHashSet<T>();
        for (final ProviderClass pc : this.getServiceClasses(provider)) {
            final Object o = this.getComponent(pc);
            if (o != null) {
                ps.add(provider.cast(o));
            }
        }
        return ps;
    }
    
    public <T> Set<T> getProvidersAndServices(final Class<T> provider) {
        final Set<T> ps = new LinkedHashSet<T>();
        ps.addAll((Collection<? extends T>)this.getProviderInstances((Class<Object>)provider));
        for (final ProviderClass pc : this.getProviderAndServiceClasses(provider)) {
            final Object o = this.getComponent(pc);
            if (o != null) {
                ps.add(provider.cast(o));
            }
        }
        return ps;
    }
    
    public <T> void getProviders(final Class<T> provider, final ProviderListener listener) {
        for (final T t : this.getProviderInstances(provider)) {
            listener.onAdd(t);
        }
        for (final ProviderClass pc : this.getProviderOnlyClasses(provider)) {
            final Object o = this.getComponent(pc);
            if (o != null) {
                listener.onAdd(provider.cast(o));
            }
        }
    }
    
    public <T> void getProvidersAndServices(final Class<T> provider, final ProviderListener listener) {
        for (final T t : this.getProviderInstances(provider)) {
            listener.onAdd(t);
        }
        for (final ProviderClass pc : this.getProviderAndServiceClasses(provider)) {
            final Object o = this.getComponent(pc);
            if (o != null) {
                listener.onAdd(provider.cast(o));
            }
        }
    }
    
    public <T> List<T> getInstances(final Class<T> provider, final String[] classNames) {
        final List<T> ps = new LinkedList<T>();
        for (final String className : classNames) {
            try {
                final Class<T> c = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(className));
                if (provider.isAssignableFrom(c)) {
                    final Object o = this.getComponent(c);
                    if (o != null) {
                        ps.add(provider.cast(o));
                    }
                }
                else {
                    ProviderServices.LOGGER.severe("The class " + className + " is not assignable to the class " + provider.getName() + ". This class is ignored.");
                }
            }
            catch (ClassNotFoundException ex) {
                ProviderServices.LOGGER.severe("The class " + className + " could not be found" + ". This class is ignored.");
            }
            catch (PrivilegedActionException pae) {
                final Throwable thrown = pae.getCause();
                if (thrown instanceof ClassNotFoundException) {
                    ProviderServices.LOGGER.severe("The class " + className + " could not be found" + ". This class is ignored.");
                }
                else if (thrown instanceof NoClassDefFoundError) {
                    ProviderServices.LOGGER.severe(SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), className, provider));
                }
                else if (thrown instanceof ClassFormatError) {
                    ProviderServices.LOGGER.severe(SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), className, provider));
                }
                else {
                    ProviderServices.LOGGER.severe(SpiMessages.PROVIDER_CLASS_COULD_NOT_BE_LOADED(className, provider.getName(), thrown.getLocalizedMessage()));
                }
            }
        }
        return ps;
    }
    
    public <T> List<T> getInstances(final Class<T> provider, final Class<? extends T>[] classes) {
        final List<T> ps = new LinkedList<T>();
        for (final Class<? extends T> c : classes) {
            final Object o = this.getComponent(c);
            if (o != null) {
                ps.add(provider.cast(o));
            }
        }
        return ps;
    }
    
    private Object getComponent(final Class provider) {
        final ComponentProvider cp = this.componentProviderFactory.getComponentProvider(provider);
        return (cp != null) ? cp.getInstance() : null;
    }
    
    private Object getComponent(final ProviderClass provider) {
        final ComponentProvider cp = this.componentProviderFactory.getComponentProvider(provider);
        return (cp != null) ? cp.getInstance() : null;
    }
    
    private <T> Set<T> getProviderInstances(final Class<T> service) {
        final Set<T> sp = new LinkedHashSet<T>();
        for (final Object p : this.providerInstances) {
            if (service.isInstance(p) && this.constrainedTo(p.getClass())) {
                sp.add(service.cast(p));
            }
        }
        return sp;
    }
    
    private Set<Class> getProviderClasses(final Class<?> service) {
        final Set<Class> sp = new LinkedHashSet<Class>();
        for (final Class p : this.providers) {
            if (service.isAssignableFrom(p) && this.constrainedTo(p)) {
                sp.add(p);
            }
        }
        return sp;
    }
    
    private Set<ProviderClass> getProviderAndServiceClasses(final Class<?> service) {
        final Set<ProviderClass> sp = this.getProviderOnlyClasses(service);
        this.getServiceClasses(service, sp);
        return sp;
    }
    
    private Set<ProviderClass> getProviderOnlyClasses(final Class<?> service) {
        final Set<ProviderClass> sp = new LinkedHashSet<ProviderClass>();
        for (final Class c : this.getProviderClasses(service)) {
            sp.add(new ProviderClass(c));
        }
        return sp;
    }
    
    private Set<ProviderClass> getServiceClasses(final Class<?> service) {
        final Set<ProviderClass> sp = new LinkedHashSet<ProviderClass>();
        this.getServiceClasses(service, sp);
        return sp;
    }
    
    private void getServiceClasses(final Class<?> service, final Set<ProviderClass> sp) {
        ProviderServices.LOGGER.log(Level.CONFIG, "Searching for providers that implement: " + service);
        Class[] arr$;
        final Class<?>[] pca = (Class<?>[])(arr$ = ServiceFinder.find(service, true).toClassArray());
        for (final Class pc : arr$) {
            if (this.constrainedTo(pc)) {
                ProviderServices.LOGGER.log(Level.CONFIG, "    Provider found: " + pc);
            }
        }
        arr$ = pca;
        for (final Class pc : arr$) {
            if (this.constrainedTo(pc)) {
                if (service.isAssignableFrom(pc)) {
                    sp.add(new ProviderClass(pc, true));
                }
                else {
                    ProviderServices.LOGGER.log(Level.CONFIG, "Provider " + pc.getName() + " won't be used because its not assignable to " + service.getName() + ". This might be caused by clashing " + "container-provided and application-bundled Jersey classes.");
                }
            }
        }
    }
    
    private boolean constrainedTo(final Class<?> p) {
        final ConstrainedTo ct = p.getAnnotation(ConstrainedTo.class);
        return ct == null || ct.value().isAssignableFrom(this.constraintToType);
    }
    
    static {
        LOGGER = Logger.getLogger(ProviderServices.class.getName());
    }
    
    public class ProviderClass
    {
        final boolean isServiceClass;
        final Class c;
        
        ProviderClass(final Class c) {
            this.c = c;
            this.isServiceClass = false;
        }
        
        ProviderClass(final Class c, final boolean isServiceClass) {
            this.c = c;
            this.isServiceClass = isServiceClass;
        }
    }
    
    public interface ProviderListener<T>
    {
        void onAdd(final T p0);
    }
}
