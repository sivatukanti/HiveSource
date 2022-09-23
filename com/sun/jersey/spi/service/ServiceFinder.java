// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.service;

import java.security.Permission;
import java.lang.reflect.ReflectPermission;
import java.util.NoSuchElementException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.TreeSet;
import com.sun.jersey.core.osgi.OsgiRegistry;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.HashSet;
import java.net.URLConnection;
import com.sun.jersey.impl.SpiMessages;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Set;
import java.util.logging.Logger;

public final class ServiceFinder<T> implements Iterable<T>
{
    private static final Logger LOGGER;
    private static final String MODULE_VERSION = "META-INF/jersey-module-version";
    private static final String PREFIX = "META-INF/services/";
    private static final String MODULE_VERSION_VALUE;
    private static final Set<String> MODULES_BLACKLIST;
    private final Class<T> serviceClass;
    private final String serviceName;
    private final ClassLoader classLoader;
    private final boolean ignoreOnClassNotFound;
    
    private static String getModuleVersion() {
        try {
            final String resource = ServiceFinder.class.getName().replace(".", "/") + ".class";
            final URL url = getResource(ServiceFinder.class.getClassLoader(), resource);
            if (url == null) {
                ServiceFinder.LOGGER.log(Level.FINE, "Error getting " + ServiceFinder.class.getName() + " class as a resource");
                return null;
            }
            return getJerseyModuleVersion(ServiceFinder.class.getName(), url);
        }
        catch (IOException ioe) {
            ServiceFinder.LOGGER.log(Level.FINE, "Error loading META-INF/jersey-module-version associated with " + ServiceFinder.class.getName(), ioe);
            return null;
        }
    }
    
    private static Enumeration<URL> filterServiceURLsWithVersion(final String serviceName, final Enumeration<URL> serviceUrls) {
        if (ServiceFinder.MODULE_VERSION_VALUE == null || !serviceUrls.hasMoreElements()) {
            return serviceUrls;
        }
        final List<URL> urls = Collections.list(serviceUrls);
        final ListIterator<URL> li = urls.listIterator();
        while (li.hasNext()) {
            final URL url = li.next();
            if (isServiceInBlacklistedModule(url)) {
                final String jerseyModuleVersion = getJerseyModuleVersion(serviceName, url);
                if (jerseyModuleVersion == null || ServiceFinder.MODULE_VERSION_VALUE.equals(jerseyModuleVersion)) {
                    continue;
                }
                li.remove();
            }
        }
        return Collections.enumeration(urls);
    }
    
    private static boolean isServiceInBlacklistedModule(final URL serviceUrl) {
        final String service = serviceUrl.toString();
        for (final String module : ServiceFinder.MODULES_BLACKLIST) {
            if (service.contains(module)) {
                return true;
            }
        }
        return false;
    }
    
    private static String getJerseyModuleVersion(final String serviceName, final URL serviceUrl) {
        InputStream stream = null;
        try {
            final String url = serviceUrl.toString();
            final String resource = url.endsWith("class") ? (serviceName.replace(".", "/") + ".class") : serviceName;
            final URL moduleVersionURL = new URL(url.replace(resource, "META-INF/jersey-module-version"));
            stream = moduleVersionURL.openStream();
            return new BufferedReader(new InputStreamReader(stream)).readLine();
        }
        catch (IOException ioe) {
            ServiceFinder.LOGGER.log(Level.FINE, "Error loading META-INF/jersey-module-version associated with " + ServiceFinder.class.getName());
            return null;
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException ioe2) {
                    ServiceFinder.LOGGER.log(Level.FINE, "Error closing stream associated with " + ServiceFinder.class.getName());
                }
            }
        }
    }
    
    private static URL getResource(final ClassLoader loader, final String name) throws IOException {
        if (loader == null) {
            return getResource(name);
        }
        final URL resource = loader.getResource(name);
        if (resource != null) {
            return resource;
        }
        return getResource(name);
    }
    
    private static URL getResource(final String name) throws IOException {
        if (ServiceFinder.class.getClassLoader() != null) {
            return ServiceFinder.class.getClassLoader().getResource(name);
        }
        return ClassLoader.getSystemResource(name);
    }
    
    private static Enumeration<URL> getResources(final ClassLoader loader, final String name) throws IOException {
        if (loader == null) {
            return getResources(name);
        }
        final Enumeration<URL> resources = loader.getResources(name);
        if (resources != null && resources.hasMoreElements()) {
            return resources;
        }
        return getResources(name);
    }
    
    private static Enumeration<URL> getResources(final String name) throws IOException {
        if (ServiceFinder.class.getClassLoader() != null) {
            return ServiceFinder.class.getClassLoader().getResources(name);
        }
        return ClassLoader.getSystemResources(name);
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service, final ClassLoader loader) throws ServiceConfigurationError {
        return find(service, loader, false);
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service, final ClassLoader loader, final boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
        return new ServiceFinder<T>(service, loader, ignoreOnClassNotFound);
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service) throws ServiceConfigurationError {
        return find(service, Thread.currentThread().getContextClassLoader(), false);
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service, final boolean ignoreOnClassNotFound) throws ServiceConfigurationError {
        return find(service, Thread.currentThread().getContextClassLoader(), ignoreOnClassNotFound);
    }
    
    public static ServiceFinder<?> find(final String serviceName) throws ServiceConfigurationError {
        return new ServiceFinder<Object>(Object.class, serviceName, Thread.currentThread().getContextClassLoader(), false);
    }
    
    public static void setIteratorProvider(final ServiceIteratorProvider sip) throws SecurityException {
        setInstance(sip);
    }
    
    private ServiceFinder(final Class<T> service, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
        this(service, service.getName(), loader, ignoreOnClassNotFound);
    }
    
    private ServiceFinder(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
        this.serviceClass = service;
        this.serviceName = serviceName;
        this.classLoader = loader;
        this.ignoreOnClassNotFound = ignoreOnClassNotFound;
    }
    
    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>)getInstance().createIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);
    }
    
    private Iterator<Class<T>> classIterator() {
        return (Iterator<Class<T>>)getInstance().createClassIterator(this.serviceClass, this.serviceName, this.classLoader, this.ignoreOnClassNotFound);
    }
    
    public T[] toArray() throws ServiceConfigurationError {
        final List<T> result = new ArrayList<T>();
        for (final T t : this) {
            result.add(t);
        }
        return result.toArray((T[])Array.newInstance(this.serviceClass, result.size()));
    }
    
    public Class<T>[] toClassArray() throws ServiceConfigurationError {
        final List<Class<T>> result = new ArrayList<Class<T>>();
        final Iterator<Class<T>> i = this.classIterator();
        while (i.hasNext()) {
            result.add(i.next());
        }
        return result.toArray((Class<T>[])Array.newInstance(Class.class, result.size()));
    }
    
    private static void fail(final String serviceName, final String msg, final Throwable cause) throws ServiceConfigurationError {
        final ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + msg);
        sce.initCause(cause);
        throw sce;
    }
    
    private static void fail(final String serviceName, final String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(serviceName + ": " + msg);
    }
    
    private static void fail(final String serviceName, final URL u, final int line, final String msg) throws ServiceConfigurationError {
        fail(serviceName, u + ":" + line + ": " + msg);
    }
    
    private static int parseLine(final String serviceName, final URL u, final BufferedReader r, final int lc, final List<String> names, final Set<String> returned) throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        final int ci = ln.indexOf(35);
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        final int n = ln.length();
        if (n != 0) {
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
                fail(serviceName, u, lc, SpiMessages.ILLEGAL_CONFIG_SYNTAX());
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(serviceName, u, lc, SpiMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                    fail(serviceName, u, lc, SpiMessages.ILLEGAL_PROVIDER_CLASS_NAME(ln));
                }
            }
            if (!returned.contains(ln)) {
                names.add(ln);
                returned.add(ln);
            }
        }
        return lc + 1;
    }
    
    private static Iterator<String> parse(final String serviceName, final URL u, final Set<String> returned) throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        final ArrayList<String> names = new ArrayList<String>();
        try {
            final URLConnection uConn = u.openConnection();
            uConn.setUseCaches(false);
            in = uConn.getInputStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(serviceName, u, r, lc, names, returned)) >= 0) {}
        }
        catch (IOException x) {
            fail(serviceName, ": " + x);
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException y) {
                fail(serviceName, ": " + y);
            }
        }
        finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException y2) {
                fail(serviceName, ": " + y2);
            }
        }
        return names.iterator();
    }
    
    static {
        LOGGER = Logger.getLogger(ServiceFinder.class.getName());
        MODULE_VERSION_VALUE = getModuleVersion();
        MODULES_BLACKLIST = new HashSet<String>() {
            {
                this.add("jersey-client");
                this.add("jersey-core");
                this.add("jersey-gf-server");
                this.add("jersey-gf-servlet");
                this.add("jersey-gf-statsproviders");
                this.add("jersey-grizzly");
                this.add("jersey-json");
                this.add("jersey-moxy");
                this.add("jersey-multipart");
                this.add("jersey-server");
                this.add("jersey-servlet");
                this.add("jersey-statsproviders");
                this.add("glassfish-embedded");
            }
        };
        final OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
        if (osgiRegistry != null) {
            ServiceFinder.LOGGER.log(Level.CONFIG, "Running in an OSGi environment");
        }
        else {
            ServiceFinder.LOGGER.log(Level.CONFIG, "Running in a non-OSGi environment");
        }
    }
    
    private static class AbstractLazyIterator<T>
    {
        final Class<T> service;
        final String serviceName;
        final ClassLoader loader;
        final boolean ignoreOnClassNotFound;
        Enumeration<URL> configs;
        Iterator<String> pending;
        Set<String> returned;
        String nextName;
        
        private AbstractLazyIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            this.configs = null;
            this.pending = null;
            this.returned = new TreeSet<String>();
            this.nextName = null;
            this.service = service;
            this.serviceName = serviceName;
            this.loader = loader;
            this.ignoreOnClassNotFound = ignoreOnClassNotFound;
        }
        
        protected final void setConfigs() {
            if (this.configs == null) {
                try {
                    final String fullName = "META-INF/services/" + this.serviceName;
                    this.configs = filterServiceURLsWithVersion(fullName, getResources(this.loader, fullName));
                }
                catch (IOException x) {
                    fail(this.serviceName, ": " + x);
                }
            }
        }
        
        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            this.setConfigs();
            while (this.nextName == null) {
                while (this.pending == null || !this.pending.hasNext()) {
                    if (!this.configs.hasMoreElements()) {
                        return false;
                    }
                    this.pending = parse(this.serviceName, this.configs.nextElement(), this.returned);
                }
                this.nextName = this.pending.next();
                if (this.ignoreOnClassNotFound) {
                    try {
                        AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader));
                    }
                    catch (ClassNotFoundException ex) {
                        this.handleClassNotFoundException();
                    }
                    catch (PrivilegedActionException pae) {
                        final Throwable thrown = pae.getException();
                        if (thrown instanceof ClassNotFoundException) {
                            this.handleClassNotFoundException();
                        }
                        else if (thrown instanceof NoClassDefFoundError) {
                            if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                                ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), this.nextName, this.service));
                            }
                            this.nextName = null;
                        }
                        else if (thrown instanceof ClassFormatError) {
                            if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                                ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), this.nextName, this.service));
                            }
                            this.nextName = null;
                        }
                        else {
                            if (thrown instanceof RuntimeException) {
                                throw (RuntimeException)thrown;
                            }
                            throw new IllegalStateException(thrown);
                        }
                    }
                }
            }
            return true;
        }
        
        private void handleClassNotFoundException() {
            if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
            }
            this.nextName = null;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static final class LazyClassIterator<T> extends AbstractLazyIterator<T> implements Iterator<Class<T>>
    {
        private LazyClassIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            super((Class)service, serviceName, loader, ignoreOnClassNotFound);
        }
        
        @Override
        public Class<T> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String cn = this.nextName;
            this.nextName = null;
            try {
                final Class<T> tClass = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(cn, this.loader));
                if (ServiceFinder.LOGGER.isLoggable(Level.FINEST)) {
                    ServiceFinder.LOGGER.log(Level.FINEST, "Loading next class: " + tClass.getName());
                }
                return tClass;
            }
            catch (ClassNotFoundException ex) {
                fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(cn, this.service));
            }
            catch (PrivilegedActionException pae) {
                final Throwable thrown = pae.getCause();
                if (thrown instanceof ClassNotFoundException) {
                    fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(cn, this.service));
                }
                else if (thrown instanceof NoClassDefFoundError) {
                    fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), cn, this.service));
                }
                else if (thrown instanceof ClassFormatError) {
                    fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), cn, this.service));
                }
                else {
                    fail(this.serviceName, SpiMessages.PROVIDER_CLASS_COULD_NOT_BE_LOADED(cn, this.service, thrown.getLocalizedMessage()), thrown);
                }
            }
            return null;
        }
    }
    
    private static final class LazyObjectIterator<T> extends AbstractLazyIterator<T> implements Iterator<T>
    {
        private T t;
        
        private LazyObjectIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            super((Class)service, serviceName, loader, ignoreOnClassNotFound);
        }
        
        @Override
        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            this.setConfigs();
            while (this.nextName == null) {
                while (this.pending == null || !this.pending.hasNext()) {
                    if (!this.configs.hasMoreElements()) {
                        return false;
                    }
                    this.pending = parse(this.serviceName, this.configs.nextElement(), this.returned);
                }
                this.nextName = this.pending.next();
                try {
                    this.t = this.service.cast(AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(this.nextName, this.loader)).newInstance());
                }
                catch (InstantiationException ex) {
                    if (this.ignoreOnClassNotFound) {
                        if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                            ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex.getLocalizedMessage()));
                        }
                        this.nextName = null;
                    }
                    else {
                        fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex.getLocalizedMessage()), ex);
                    }
                }
                catch (IllegalAccessException ex2) {
                    fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, ex2.getLocalizedMessage()), ex2);
                }
                catch (ClassNotFoundException ex4) {
                    this.handleClassNotFoundException();
                }
                catch (NoClassDefFoundError ex3) {
                    if (this.ignoreOnClassNotFound) {
                        if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                            ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(ex3.getLocalizedMessage(), this.nextName, this.service));
                        }
                        this.nextName = null;
                    }
                    else {
                        fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(ex3.getLocalizedMessage(), this.nextName, this.service), ex3);
                    }
                }
                catch (PrivilegedActionException pae) {
                    final Throwable cause = pae.getCause();
                    if (cause instanceof ClassNotFoundException) {
                        this.handleClassNotFoundException();
                    }
                    else if (cause instanceof ClassFormatError) {
                        if (this.ignoreOnClassNotFound) {
                            if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                                ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service));
                            }
                            this.nextName = null;
                        }
                        else {
                            fail(this.serviceName, SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(cause.getLocalizedMessage(), this.nextName, this.service), cause);
                        }
                    }
                    else {
                        fail(this.serviceName, SpiMessages.PROVIDER_COULD_NOT_BE_CREATED(this.nextName, this.service, cause.getLocalizedMessage()), cause);
                    }
                }
            }
            return true;
        }
        
        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String cn = this.nextName;
            this.nextName = null;
            return this.t;
        }
        
        private void handleClassNotFoundException() throws ServiceConfigurationError {
            if (this.ignoreOnClassNotFound) {
                if (ServiceFinder.LOGGER.isLoggable(Level.CONFIG)) {
                    ServiceFinder.LOGGER.log(Level.CONFIG, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
                }
                this.nextName = null;
            }
            else {
                fail(this.serviceName, SpiMessages.PROVIDER_NOT_FOUND(this.nextName, this.service));
            }
        }
    }
    
    public abstract static class ServiceIteratorProvider<T>
    {
        private static volatile ServiceIteratorProvider sip;
        
        private static ServiceIteratorProvider getInstance() {
            ServiceIteratorProvider result = ServiceIteratorProvider.sip;
            if (result == null) {
                synchronized (ServiceIteratorProvider.class) {
                    result = ServiceIteratorProvider.sip;
                    if (result == null) {
                        result = (ServiceIteratorProvider.sip = new DefaultServiceIteratorProvider());
                    }
                }
            }
            return result;
        }
        
        private static void setInstance(final ServiceIteratorProvider sip) throws SecurityException {
            final SecurityManager security = System.getSecurityManager();
            if (security != null) {
                final ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
                security.checkPermission(rp);
            }
            synchronized (ServiceIteratorProvider.class) {
                ServiceIteratorProvider.sip = sip;
            }
        }
        
        public abstract Iterator<T> createIterator(final Class<T> p0, final String p1, final ClassLoader p2, final boolean p3);
        
        public abstract Iterator<Class<T>> createClassIterator(final Class<T> p0, final String p1, final ClassLoader p2, final boolean p3);
    }
    
    public static final class DefaultServiceIteratorProvider<T> extends ServiceIteratorProvider<T>
    {
        @Override
        public Iterator<T> createIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            return new LazyObjectIterator<T>((Class)service, serviceName, loader, ignoreOnClassNotFound);
        }
        
        @Override
        public Iterator<Class<T>> createClassIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            return new LazyClassIterator<T>((Class)service, serviceName, loader, ignoreOnClassNotFound);
        }
    }
}
