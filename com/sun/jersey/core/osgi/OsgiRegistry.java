// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.osgi;

import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.sun.jersey.spi.service.ServiceConfigurationError;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import com.sun.jersey.spi.service.ServiceFinder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.util.logging.Level;
import java.util.jar.JarInputStream;
import com.sun.jersey.impl.SpiMessages;
import java.util.LinkedList;
import java.net.URL;
import java.util.Enumeration;
import com.sun.jersey.core.spi.scanning.PackageNamesScanner;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.BundleReference;
import com.sun.jersey.core.reflection.ReflectionHelper;
import org.osgi.framework.Bundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.Map;
import org.osgi.framework.BundleContext;
import java.util.logging.Logger;
import org.osgi.framework.SynchronousBundleListener;

public final class OsgiRegistry implements SynchronousBundleListener
{
    private static final String CoreBundleSymbolicNAME = "com.sun.jersey.core";
    private static final Logger LOGGER;
    private final BundleContext bundleContext;
    private final Map<Long, Map<String, Callable<List<Class<?>>>>> factories;
    private final ReadWriteLock lock;
    private static OsgiRegistry instance;
    private Map<String, Bundle> classToBundleMapping;
    
    public static synchronized OsgiRegistry getInstance() {
        if (OsgiRegistry.instance == null) {
            final ClassLoader classLoader = ReflectionHelper.class.getClassLoader();
            if (classLoader instanceof BundleReference) {
                final BundleContext context = FrameworkUtil.getBundle((Class)OsgiRegistry.class).getBundleContext();
                if (context != null) {
                    (OsgiRegistry.instance = new OsgiRegistry(context)).hookUp();
                }
            }
        }
        return OsgiRegistry.instance;
    }
    
    public void bundleChanged(final BundleEvent event) {
        if (event.getType() == 32) {
            this.register(event.getBundle());
        }
        else if (event.getType() == 64 || event.getType() == 16) {
            final Bundle unregisteredBundle = event.getBundle();
            this.lock.writeLock().lock();
            try {
                this.factories.remove(unregisteredBundle.getBundleId());
                if (unregisteredBundle.getSymbolicName().equals("com.sun.jersey.core")) {
                    this.bundleContext.removeBundleListener((BundleListener)this);
                    this.factories.clear();
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }
    
    private void setOSGiPackageScannerResourceProvider() {
        PackageNamesScanner.setResourcesProvider(new PackageNamesScanner.ResourcesProvider() {
            @Override
            public Enumeration<URL> getResources(final String packagePath, final ClassLoader classLoader) throws IOException {
                final List<URL> result = new LinkedList<URL>();
                OsgiRegistry.this.classToBundleMapping.clear();
                for (final Bundle bundle : OsgiRegistry.this.bundleContext.getBundles()) {
                    for (final String bundlePackagePath : new String[] { packagePath, "WEB-INF/classes/" + packagePath }) {
                        final Enumeration<URL> enumeration = (Enumeration<URL>)bundle.findEntries(bundlePackagePath, "*", false);
                        if (enumeration != null) {
                            while (enumeration.hasMoreElements()) {
                                final URL url = enumeration.nextElement();
                                final String path = url.getPath();
                                final String className = (packagePath + path.substring(path.lastIndexOf(47))).replace('/', '.').replace(".class", "");
                                OsgiRegistry.this.classToBundleMapping.put(className, bundle);
                                result.add(url);
                            }
                        }
                    }
                    final Enumeration<URL> jars = (Enumeration<URL>)bundle.findEntries("/", "*.jar", true);
                    if (jars != null) {
                        while (jars.hasMoreElements()) {
                            final URL jar = jars.nextElement();
                            final InputStream inputStream = classLoader.getResourceAsStream(jar.getPath());
                            if (inputStream == null) {
                                OsgiRegistry.LOGGER.config(SpiMessages.OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(jar));
                            }
                            else {
                                JarInputStream jarInputStream;
                                try {
                                    jarInputStream = new JarInputStream(inputStream);
                                }
                                catch (IOException ex) {
                                    OsgiRegistry.LOGGER.log(Level.CONFIG, SpiMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex);
                                    try {
                                        inputStream.close();
                                    }
                                    catch (IOException ex3) {}
                                    continue;
                                }
                                try {
                                    JarEntry jarEntry;
                                    while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                                        final String jarEntryName = jarEntry.getName();
                                        if (jarEntryName.endsWith(".class") && jarEntryName.contains(packagePath)) {
                                            OsgiRegistry.this.classToBundleMapping.put(jarEntryName.replace(".class", "").replace('/', '.'), bundle);
                                            result.add(bundle.getResource(jarEntryName));
                                        }
                                    }
                                }
                                catch (Exception ex2) {
                                    OsgiRegistry.LOGGER.log(Level.CONFIG, SpiMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex2);
                                }
                                finally {
                                    try {
                                        jarInputStream.close();
                                    }
                                    catch (IOException ex4) {}
                                }
                            }
                        }
                    }
                }
                return Collections.enumeration(result);
            }
        });
    }
    
    public Class<?> classForNameWithException(final String className) throws ClassNotFoundException {
        final Bundle bundle = this.classToBundleMapping.get(className);
        if (bundle == null) {
            throw new ClassNotFoundException(className);
        }
        return (Class<?>)bundle.loadClass(className);
    }
    
    private OsgiRegistry(final BundleContext bundleContext) {
        this.factories = new HashMap<Long, Map<String, Callable<List<Class<?>>>>>();
        this.lock = new ReentrantReadWriteLock();
        this.classToBundleMapping = new HashMap<String, Bundle>();
        this.bundleContext = bundleContext;
    }
    
    private void hookUp() {
        this.setOSGiPackageScannerResourceProvider();
        this.setOSGiServiceFinderIteratorProvider();
        this.bundleContext.addBundleListener((BundleListener)this);
        this.registerExistingBundles();
        final Bundle jerseyServerBundle = this.getJerseyServerBundle(this.bundleContext);
        RuntimeDelegate runtimeDelegate = null;
        try {
            if (jerseyServerBundle == null) {
                OsgiRegistry.LOGGER.config("jersey-client bundle registers JAX-RS RuntimeDelegate");
                runtimeDelegate = (RuntimeDelegate)this.getClass().getClassLoader().loadClass("com.sun.ws.rs.ext.RuntimeDelegateImpl").newInstance();
            }
            else {
                OsgiRegistry.LOGGER.config("jersey-server bundle activator registers JAX-RS RuntimeDelegate instance");
                runtimeDelegate = (RuntimeDelegate)this.getClass().getClassLoader().loadClass("com.sun.jersey.server.impl.provider.RuntimeDelegateImpl").newInstance();
            }
        }
        catch (Exception e) {
            OsgiRegistry.LOGGER.log(Level.SEVERE, "Unable to create RuntimeDelegate instance.", e);
        }
        RuntimeDelegate.setInstance(runtimeDelegate);
    }
    
    private Bundle getJerseyServerBundle(final BundleContext bc) {
        for (final Bundle b : bc.getBundles()) {
            final String symbolicName = b.getSymbolicName();
            if (symbolicName != null && (symbolicName.endsWith("jersey-server") || symbolicName.endsWith("jersey-gf-server"))) {
                return b;
            }
        }
        return null;
    }
    
    private void registerExistingBundles() {
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            if (bundle.getState() == 4 || bundle.getState() == 8 || bundle.getState() == 32 || bundle.getState() == 16) {
                this.register(bundle);
            }
        }
    }
    
    private void setOSGiServiceFinderIteratorProvider() {
        ServiceFinder.setIteratorProvider(new OsgiServiceFinder());
    }
    
    private void register(final Bundle bundle) {
        if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
            OsgiRegistry.LOGGER.log(Level.FINEST, "checking bundle {0}", bundle.getBundleId());
        }
        this.lock.writeLock().lock();
        Map<String, Callable<List<Class<?>>>> map;
        try {
            map = this.factories.get(bundle.getBundleId());
            if (map == null) {
                map = new ConcurrentHashMap<String, Callable<List<Class<?>>>>();
                this.factories.put(bundle.getBundleId(), map);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        final Enumeration e = bundle.findEntries("META-INF/services/", "*", false);
        if (e != null) {
            while (e.hasMoreElements()) {
                final URL u = e.nextElement();
                final String url = u.toString();
                if (url.endsWith("/")) {
                    continue;
                }
                final String factoryId = url.substring(url.lastIndexOf("/") + 1);
                map.put(factoryId, new BundleSpiProvidersLoader(factoryId, u, bundle));
            }
        }
    }
    
    private List<Class<?>> locateAllProviders(final String serviceName) {
        this.lock.readLock().lock();
        try {
            final List<Class<?>> result = new LinkedList<Class<?>>();
            for (final Map<String, Callable<List<Class<?>>>> value : this.factories.values()) {
                if (value.containsKey(serviceName)) {
                    try {
                        result.addAll(value.get(serviceName).call());
                    }
                    catch (Exception ex) {}
                }
            }
            return result;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(OsgiRegistry.class.getName());
    }
    
    private final class OsgiServiceFinder<T> extends ServiceFinder.ServiceIteratorProvider<T>
    {
        final ServiceFinder.ServiceIteratorProvider defaultIterator;
        
        private OsgiServiceFinder() {
            this.defaultIterator = new ServiceFinder.DefaultServiceIteratorProvider();
        }
        
        @Override
        public Iterator<T> createIterator(final Class<T> serviceClass, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            final List<Class<?>> providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<T>() {
                    Iterator<Class<?>> it = providerClasses.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }
                    
                    @Override
                    public T next() {
                        final Class<T> nextClass = (Class<T>)this.it.next();
                        try {
                            return serviceClass.cast(nextClass.newInstance());
                        }
                        catch (Exception ex) {
                            final ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(nextClass.getName(), serviceClass, ex.getLocalizedMessage()));
                            sce.initCause(ex);
                            throw sce;
                        }
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createIterator(serviceClass, serviceName, loader, ignoreOnClassNotFound);
        }
        
        @Override
        public Iterator<Class<T>> createClassIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            final List<Class<?>> providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<Class<T>>() {
                    Iterator<Class<?>> it = providerClasses.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }
                    
                    @Override
                    public Class<T> next() {
                        return (Class<T>)this.it.next();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createClassIterator(service, serviceName, loader, ignoreOnClassNotFound);
        }
    }
    
    private static class BundleSpiProvidersLoader implements Callable<List<Class<?>>>
    {
        private final String spi;
        private final URL spiRegistryUrl;
        private final String spiRegistryUrlString;
        private final Bundle bundle;
        
        BundleSpiProvidersLoader(final String spi, final URL spiRegistryUrl, final Bundle bundle) {
            this.spi = spi;
            this.spiRegistryUrl = spiRegistryUrl;
            this.spiRegistryUrlString = spiRegistryUrl.toExternalForm();
            this.bundle = bundle;
        }
        
        @Override
        public List<Class<?>> call() throws Exception {
            BufferedReader reader = null;
            try {
                if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
                    OsgiRegistry.LOGGER.log(Level.FINEST, "Loading providers for SPI: {0}", this.spi);
                }
                reader = new BufferedReader(new InputStreamReader(this.spiRegistryUrl.openStream(), "UTF-8"));
                final List<Class<?>> providerClasses = new ArrayList<Class<?>>();
                String providerClassName;
                while ((providerClassName = reader.readLine()) != null) {
                    if (providerClassName.trim().length() == 0) {
                        continue;
                    }
                    if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
                        OsgiRegistry.LOGGER.log(Level.FINEST, "SPI provider: {0}", providerClassName);
                    }
                    providerClasses.add(this.bundle.loadClass(providerClassName));
                }
                return providerClasses;
            }
            catch (Exception e) {
                OsgiRegistry.LOGGER.log(Level.WARNING, "exception caught while creating factories: " + e);
                throw e;
            }
            catch (Error e2) {
                OsgiRegistry.LOGGER.log(Level.WARNING, "error caught while creating factories: " + e2);
                throw e2;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException ioe) {
                        OsgiRegistry.LOGGER.log(Level.FINE, "Error closing SPI registry stream:" + this.spiRegistryUrl, ioe);
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            return this.spiRegistryUrlString;
        }
        
        @Override
        public int hashCode() {
            return this.spiRegistryUrlString.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof BundleSpiProvidersLoader && this.spiRegistryUrlString.equals(((BundleSpiProvidersLoader)obj).spiRegistryUrlString);
        }
    }
}
