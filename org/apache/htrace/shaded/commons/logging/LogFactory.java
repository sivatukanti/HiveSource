// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.commons.logging;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Properties;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.io.PrintStream;

public abstract class LogFactory
{
    public static final String PRIORITY_KEY = "priority";
    public static final String TCCL_KEY = "use_tccl";
    public static final String FACTORY_PROPERTY = "org.apache.htrace.shaded.commons.logging.LogFactory";
    public static final String FACTORY_DEFAULT = "org.apache.htrace.shaded.commons.logging.impl.LogFactoryImpl";
    public static final String FACTORY_PROPERTIES = "commons-logging.properties";
    protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
    public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.htrace.shaded.commons.logging.diagnostics.dest";
    private static PrintStream diagnosticsStream;
    private static String diagnosticPrefix;
    public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.htrace.shaded.commons.logging.LogFactory.HashtableImpl";
    private static final String WEAK_HASHTABLE_CLASSNAME = "org.apache.htrace.shaded.commons.logging.impl.WeakHashtable";
    private static ClassLoader thisClassLoader;
    protected static Hashtable factories;
    protected static LogFactory nullClassLoaderFactory;
    
    protected LogFactory() {
    }
    
    public abstract Object getAttribute(final String p0);
    
    public abstract String[] getAttributeNames();
    
    public abstract Log getInstance(final Class p0) throws LogConfigurationException;
    
    public abstract Log getInstance(final String p0) throws LogConfigurationException;
    
    public abstract void release();
    
    public abstract void removeAttribute(final String p0);
    
    public abstract void setAttribute(final String p0, final Object p1);
    
    private static final Hashtable createFactoryStore() {
        Hashtable result = null;
        String storeImplementationClass;
        try {
            storeImplementationClass = getSystemProperty("org.apache.htrace.shaded.commons.logging.LogFactory.HashtableImpl", null);
        }
        catch (SecurityException ex) {
            storeImplementationClass = null;
        }
        if (storeImplementationClass == null) {
            storeImplementationClass = "org.apache.htrace.shaded.commons.logging.impl.WeakHashtable";
        }
        try {
            final Class implementationClass = Class.forName(storeImplementationClass);
            result = implementationClass.newInstance();
        }
        catch (Throwable t) {
            if (!"org.apache.htrace.shaded.commons.logging.impl.WeakHashtable".equals(storeImplementationClass)) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[ERROR] LogFactory: Load of custom hashtable failed");
                }
                else {
                    System.err.println("[ERROR] LogFactory: Load of custom hashtable failed");
                }
            }
        }
        if (result == null) {
            result = new Hashtable();
        }
        return result;
    }
    
    private static String trim(final String src) {
        if (src == null) {
            return null;
        }
        return src.trim();
    }
    
    public static LogFactory getFactory() throws LogConfigurationException {
        final ClassLoader contextClassLoader = getContextClassLoaderInternal();
        if (contextClassLoader == null && isDiagnosticsEnabled()) {
            logDiagnostic("Context classloader is null.");
        }
        LogFactory factory = getCachedFactory(contextClassLoader);
        if (factory != null) {
            return factory;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] LogFactory implementation requested for the first time for context classloader " + objectId(contextClassLoader));
            logHierarchy("[LOOKUP] ", contextClassLoader);
        }
        final Properties props = getConfigurationFile(contextClassLoader, "commons-logging.properties");
        ClassLoader baseClassLoader = contextClassLoader;
        if (props != null) {
            final String useTCCLStr = props.getProperty("use_tccl");
            if (useTCCLStr != null && !Boolean.valueOf(useTCCLStr)) {
                baseClassLoader = LogFactory.thisClassLoader;
            }
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] Looking for system property [org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
        }
        try {
            final String factoryClass = getSystemProperty("org.apache.htrace.shaded.commons.logging.LogFactory", null);
            if (factoryClass != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Creating an instance of LogFactory class '" + factoryClass + "' as specified by system property " + "org.apache.htrace.shaded.commons.logging.LogFactory");
                }
                factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
            }
            else if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] No system property [org.apache.commons.logging.LogFactory] defined.");
            }
        }
        catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(e.getMessage()) + "]. Trying alternative implementations...");
            }
        }
        catch (RuntimeException e2) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] An exception occurred while trying to create an instance of the custom factory class: [" + trim(e2.getMessage()) + "] as specified by a system property.");
            }
            throw e2;
        }
        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] Looking for a resource file of name [META-INF/services/org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
            }
            try {
                final InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/org.apache.commons.logging.LogFactory");
                if (is != null) {
                    BufferedReader rd;
                    try {
                        rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e3) {
                        rd = new BufferedReader(new InputStreamReader(is));
                    }
                    final String factoryClassName = rd.readLine();
                    rd.close();
                    if (factoryClassName != null && !"".equals(factoryClassName)) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("[LOOKUP]  Creating an instance of LogFactory class " + factoryClassName + " as specified by file '" + "META-INF/services/org.apache.commons.logging.LogFactory" + "' which was present in the path of the context" + " classloader.");
                        }
                        factory = newFactory(factoryClassName, baseClassLoader, contextClassLoader);
                    }
                }
                else if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] No resource file with name 'META-INF/services/org.apache.commons.logging.LogFactory' found.");
                }
            }
            catch (Exception ex) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(ex.getMessage()) + "]. Trying alternative implementations...");
                }
            }
        }
        if (factory == null) {
            if (props != null) {
                if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Looking in properties file for entry with key 'org.apache.commons.logging.LogFactory' to define the LogFactory subclass to use...");
                }
                final String factoryClass = props.getProperty("org.apache.htrace.shaded.commons.logging.LogFactory");
                if (factoryClass != null) {
                    if (isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file specifies LogFactory subclass '" + factoryClass + "'");
                    }
                    factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
                }
                else if (isDiagnosticsEnabled()) {
                    logDiagnostic("[LOOKUP] Properties file has no entry specifying LogFactory subclass.");
                }
            }
            else if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] No properties file available to determine LogFactory subclass from..");
            }
        }
        if (factory == null) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("[LOOKUP] Loading the default LogFactory implementation 'org.apache.commons.logging.impl.LogFactoryImpl' via the same classloader that loaded this LogFactory class (ie not looking in the context classloader).");
            }
            factory = newFactory("org.apache.htrace.shaded.commons.logging.impl.LogFactoryImpl", LogFactory.thisClassLoader, contextClassLoader);
        }
        if (factory != null) {
            cacheFactory(contextClassLoader, factory);
            if (props != null) {
                final Enumeration names = props.propertyNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    final String value = props.getProperty(name);
                    factory.setAttribute(name, value);
                }
            }
        }
        return factory;
    }
    
    public static Log getLog(final Class clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }
    
    public static Log getLog(final String name) throws LogConfigurationException {
        return getFactory().getInstance(name);
    }
    
    public static void release(final ClassLoader classLoader) {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for classloader " + objectId(classLoader));
        }
        synchronized (LogFactory.factories) {
            if (classLoader == null) {
                if (LogFactory.nullClassLoaderFactory != null) {
                    LogFactory.nullClassLoaderFactory.release();
                    LogFactory.nullClassLoaderFactory = null;
                }
            }
            else {
                final LogFactory factory = LogFactory.factories.get(classLoader);
                if (factory != null) {
                    factory.release();
                    LogFactory.factories.remove(classLoader);
                }
            }
        }
    }
    
    public static void releaseAll() {
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Releasing factory for all classloaders.");
        }
        synchronized (LogFactory.factories) {
            final Enumeration elements = LogFactory.factories.elements();
            while (elements.hasMoreElements()) {
                final LogFactory element = elements.nextElement();
                element.release();
            }
            LogFactory.factories.clear();
            if (LogFactory.nullClassLoaderFactory != null) {
                LogFactory.nullClassLoaderFactory.release();
                LogFactory.nullClassLoaderFactory = null;
            }
        }
    }
    
    protected static ClassLoader getClassLoader(final Class clazz) {
        try {
            return clazz.getClassLoader();
        }
        catch (SecurityException ex) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to get classloader for class '" + clazz + "' due to security restrictions - " + ex.getMessage());
            }
            throw ex;
        }
    }
    
    protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
        return directGetContextClassLoader();
    }
    
    private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                return LogFactory.directGetContextClassLoader();
            }
        });
    }
    
    protected static ClassLoader directGetContextClassLoader() throws LogConfigurationException {
        ClassLoader classLoader = null;
        try {
            final Method method = Thread.class.getMethod("getContextClassLoader", (Class[])null);
            try {
                classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Object[])null);
            }
            catch (IllegalAccessException e) {
                throw new LogConfigurationException("Unexpected IllegalAccessException", e);
            }
            catch (InvocationTargetException e2) {
                if (!(e2.getTargetException() instanceof SecurityException)) {
                    throw new LogConfigurationException("Unexpected InvocationTargetException", e2.getTargetException());
                }
            }
        }
        catch (NoSuchMethodException e3) {
            classLoader = getClassLoader(LogFactory.class);
        }
        return classLoader;
    }
    
    private static LogFactory getCachedFactory(final ClassLoader contextClassLoader) {
        LogFactory factory = null;
        if (contextClassLoader == null) {
            factory = LogFactory.nullClassLoaderFactory;
        }
        else {
            factory = LogFactory.factories.get(contextClassLoader);
        }
        return factory;
    }
    
    private static void cacheFactory(final ClassLoader classLoader, final LogFactory factory) {
        if (factory != null) {
            if (classLoader == null) {
                LogFactory.nullClassLoaderFactory = factory;
            }
            else {
                LogFactory.factories.put(classLoader, factory);
            }
        }
    }
    
    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader, final ClassLoader contextClassLoader) throws LogConfigurationException {
        final Object result = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                return LogFactory.createFactory(factoryClass, classLoader);
            }
        });
        if (result instanceof LogConfigurationException) {
            final LogConfigurationException ex = (LogConfigurationException)result;
            if (isDiagnosticsEnabled()) {
                logDiagnostic("An error occurred while loading the factory class:" + ex.getMessage());
            }
            throw ex;
        }
        if (isDiagnosticsEnabled()) {
            logDiagnostic("Created object " + objectId(result) + " to manage classloader " + objectId(contextClassLoader));
        }
        return (LogFactory)result;
    }
    
    protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader) {
        return newFactory(factoryClass, classLoader, null);
    }
    
    protected static Object createFactory(final String factoryClass, final ClassLoader classLoader) {
        Class logFactoryClass = null;
        try {
            if (classLoader != null) {
                try {
                    logFactoryClass = classLoader.loadClass(factoryClass);
                    if (LogFactory.class.isAssignableFrom(logFactoryClass)) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Loaded class " + logFactoryClass.getName() + " from classloader " + objectId(classLoader));
                        }
                    }
                    else if (isDiagnosticsEnabled()) {
                        logDiagnostic("Factory class " + logFactoryClass.getName() + " loaded from classloader " + objectId(logFactoryClass.getClassLoader()) + " does not extend '" + LogFactory.class.getName() + "' as loaded by this classloader.");
                        logHierarchy("[BAD CL TREE] ", classLoader);
                    }
                    return logFactoryClass.newInstance();
                }
                catch (ClassNotFoundException ex) {
                    if (classLoader == LogFactory.thisClassLoader) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Unable to locate any class called '" + factoryClass + "' via classloader " + objectId(classLoader));
                        }
                        throw ex;
                    }
                }
                catch (NoClassDefFoundError e) {
                    if (classLoader == LogFactory.thisClassLoader) {
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic("Class '" + factoryClass + "' cannot be loaded" + " via classloader " + objectId(classLoader) + " - it depends on some other class that cannot" + " be found.");
                        }
                        throw e;
                    }
                }
                catch (ClassCastException e3) {
                    if (classLoader == LogFactory.thisClassLoader) {
                        final boolean implementsLogFactory = implementsLogFactory(logFactoryClass);
                        String msg = "The application has specified that a custom LogFactory implementation should be used but Class '" + factoryClass + "' cannot be converted to '" + LogFactory.class.getName() + "'. ";
                        if (implementsLogFactory) {
                            msg = msg + "The conflict is caused by the presence of multiple LogFactory classes in incompatible classloaders. " + "Background can be found in http://commons.apache.org/logging/tech.html. " + "If you have not explicitly specified a custom LogFactory then it is likely that " + "the container has set one without your knowledge. " + "In this case, consider using the commons-logging-adapters.jar file or " + "specifying the standard LogFactory from the command line. ";
                        }
                        else {
                            msg += "Please check the custom implementation. ";
                        }
                        msg += "Help can be found @http://commons.apache.org/logging/troubleshooting.html.";
                        if (isDiagnosticsEnabled()) {
                            logDiagnostic(msg);
                        }
                        final ClassCastException ex2 = new ClassCastException(msg);
                        throw ex2;
                    }
                }
            }
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to load factory class via classloader " + objectId(classLoader) + " - trying the classloader associated with this LogFactory.");
            }
            logFactoryClass = Class.forName(factoryClass);
            return logFactoryClass.newInstance();
        }
        catch (Exception e2) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("Unable to create LogFactory instance.");
            }
            if (logFactoryClass != null && !LogFactory.class.isAssignableFrom(logFactoryClass)) {
                return new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", e2);
            }
            return new LogConfigurationException(e2);
        }
    }
    
    private static boolean implementsLogFactory(final Class logFactoryClass) {
        boolean implementsLogFactory = false;
        if (logFactoryClass != null) {
            try {
                final ClassLoader logFactoryClassLoader = logFactoryClass.getClassLoader();
                if (logFactoryClassLoader == null) {
                    logDiagnostic("[CUSTOM LOG FACTORY] was loaded by the boot classloader");
                }
                else {
                    logHierarchy("[CUSTOM LOG FACTORY] ", logFactoryClassLoader);
                    final Class factoryFromCustomLoader = Class.forName("org.apache.htrace.shaded.commons.logging.LogFactory", false, logFactoryClassLoader);
                    implementsLogFactory = factoryFromCustomLoader.isAssignableFrom(logFactoryClass);
                    if (implementsLogFactory) {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " implements LogFactory but was loaded by an incompatible classloader.");
                    }
                    else {
                        logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " does not implement LogFactory.");
                    }
                }
            }
            catch (SecurityException e) {
                logDiagnostic("[CUSTOM LOG FACTORY] SecurityException thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e.getMessage());
            }
            catch (LinkageError e2) {
                logDiagnostic("[CUSTOM LOG FACTORY] LinkageError thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + e2.getMessage());
            }
            catch (ClassNotFoundException e3) {
                logDiagnostic("[CUSTOM LOG FACTORY] LogFactory class cannot be loaded by classloader which loaded the custom LogFactory implementation. Is the custom factory in the right classloader?");
            }
        }
        return implementsLogFactory;
    }
    
    private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
            public Object run() {
                if (loader != null) {
                    return loader.getResourceAsStream(name);
                }
                return ClassLoader.getSystemResourceAsStream(name);
            }
        });
    }
    
    private static Enumeration getResources(final ClassLoader loader, final String name) {
        final PrivilegedAction action = new PrivilegedAction() {
            public Object run() {
                try {
                    if (loader != null) {
                        return loader.getResources(name);
                    }
                    return ClassLoader.getSystemResources(name);
                }
                catch (IOException e) {
                    if (LogFactory.isDiagnosticsEnabled()) {
                        logDiagnostic("Exception while trying to find configuration file " + name + ":" + e.getMessage());
                    }
                    return null;
                }
                catch (NoSuchMethodError e2) {
                    return null;
                }
            }
        };
        final Object result = AccessController.doPrivileged((PrivilegedAction<Object>)action);
        return (Enumeration)result;
    }
    
    private static Properties getProperties(final URL url) {
        final PrivilegedAction action = new PrivilegedAction() {
            public Object run() {
                try {
                    final InputStream stream = url.openStream();
                    if (stream != null) {
                        final Properties props = new Properties();
                        props.load(stream);
                        stream.close();
                        return props;
                    }
                }
                catch (IOException e) {
                    if (LogFactory.isDiagnosticsEnabled()) {
                        logDiagnostic("Unable to read URL " + url);
                    }
                }
                return null;
            }
        };
        return AccessController.doPrivileged((PrivilegedAction<Properties>)action);
    }
    
    private static final Properties getConfigurationFile(final ClassLoader classLoader, final String fileName) {
        Properties props = null;
        double priority = 0.0;
        URL propsUrl = null;
        try {
            final Enumeration urls = getResources(classLoader, fileName);
            if (urls == null) {
                return null;
            }
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final Properties newProps = getProperties(url);
                if (newProps != null) {
                    if (props == null) {
                        propsUrl = url;
                        props = newProps;
                        final String priorityStr = props.getProperty("priority");
                        priority = 0.0;
                        if (priorityStr != null) {
                            priority = Double.parseDouble(priorityStr);
                        }
                        if (!isDiagnosticsEnabled()) {
                            continue;
                        }
                        logDiagnostic("[LOOKUP] Properties file found at '" + url + "'" + " with priority " + priority);
                    }
                    else {
                        final String newPriorityStr = newProps.getProperty("priority");
                        double newPriority = 0.0;
                        if (newPriorityStr != null) {
                            newPriority = Double.parseDouble(newPriorityStr);
                        }
                        if (newPriority > priority) {
                            if (isDiagnosticsEnabled()) {
                                logDiagnostic("[LOOKUP] Properties file at '" + url + "'" + " with priority " + newPriority + " overrides file at '" + propsUrl + "'" + " with priority " + priority);
                            }
                            propsUrl = url;
                            props = newProps;
                            priority = newPriority;
                        }
                        else {
                            if (!isDiagnosticsEnabled()) {
                                continue;
                            }
                            logDiagnostic("[LOOKUP] Properties file at '" + url + "'" + " with priority " + newPriority + " does not override file at '" + propsUrl + "'" + " with priority " + priority);
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            if (isDiagnosticsEnabled()) {
                logDiagnostic("SecurityException thrown while trying to find/read config files.");
            }
        }
        if (isDiagnosticsEnabled()) {
            if (props == null) {
                logDiagnostic("[LOOKUP] No properties file of name '" + fileName + "' found.");
            }
            else {
                logDiagnostic("[LOOKUP] Properties file of name '" + fileName + "' found at '" + propsUrl + '\"');
            }
        }
        return props;
    }
    
    private static String getSystemProperty(final String key, final String def) throws SecurityException {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty(key, def);
            }
        });
    }
    
    private static void initDiagnostics() {
        String dest;
        try {
            dest = getSystemProperty("org.apache.htrace.shaded.commons.logging.diagnostics.dest", null);
            if (dest == null) {
                return;
            }
        }
        catch (SecurityException ex) {
            return;
        }
        if (dest.equals("STDOUT")) {
            LogFactory.diagnosticsStream = System.out;
        }
        else if (dest.equals("STDERR")) {
            LogFactory.diagnosticsStream = System.err;
        }
        else {
            try {
                final FileOutputStream fos = new FileOutputStream(dest, true);
                LogFactory.diagnosticsStream = new PrintStream(fos);
            }
            catch (IOException ex2) {
                return;
            }
        }
        String classLoaderName;
        try {
            final ClassLoader classLoader = LogFactory.thisClassLoader;
            if (LogFactory.thisClassLoader == null) {
                classLoaderName = "BOOTLOADER";
            }
            else {
                classLoaderName = objectId(classLoader);
            }
        }
        catch (SecurityException e) {
            classLoaderName = "UNKNOWN";
        }
        LogFactory.diagnosticPrefix = "[LogFactory from " + classLoaderName + "] ";
    }
    
    protected static boolean isDiagnosticsEnabled() {
        return LogFactory.diagnosticsStream != null;
    }
    
    private static final void logDiagnostic(final String msg) {
        if (LogFactory.diagnosticsStream != null) {
            LogFactory.diagnosticsStream.print(LogFactory.diagnosticPrefix);
            LogFactory.diagnosticsStream.println(msg);
            LogFactory.diagnosticsStream.flush();
        }
    }
    
    protected static final void logRawDiagnostic(final String msg) {
        if (LogFactory.diagnosticsStream != null) {
            LogFactory.diagnosticsStream.println(msg);
            LogFactory.diagnosticsStream.flush();
        }
    }
    
    private static void logClassLoaderEnvironment(final Class clazz) {
        if (!isDiagnosticsEnabled()) {
            return;
        }
        try {
            logDiagnostic("[ENV] Extension directories (java.ext.dir): " + System.getProperty("java.ext.dir"));
            logDiagnostic("[ENV] Application classpath (java.class.path): " + System.getProperty("java.class.path"));
        }
        catch (SecurityException ex) {
            logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
        }
        final String className = clazz.getName();
        ClassLoader classLoader;
        try {
            classLoader = getClassLoader(clazz);
        }
        catch (SecurityException ex2) {
            logDiagnostic("[ENV] Security forbids determining the classloader for " + className);
            return;
        }
        logDiagnostic("[ENV] Class " + className + " was loaded via classloader " + objectId(classLoader));
        logHierarchy("[ENV] Ancestry of classloader which loaded " + className + " is ", classLoader);
    }
    
    private static void logHierarchy(final String prefix, ClassLoader classLoader) {
        if (!isDiagnosticsEnabled()) {
            return;
        }
        if (classLoader != null) {
            final String classLoaderString = classLoader.toString();
            logDiagnostic(prefix + objectId(classLoader) + " == '" + classLoaderString + "'");
        }
        ClassLoader systemClassLoader;
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException ex) {
            logDiagnostic(prefix + "Security forbids determining the system classloader.");
            return;
        }
        if (classLoader != null) {
            final StringBuffer buf = new StringBuffer(prefix + "ClassLoader tree:");
        Label_0186:
            while (true) {
                do {
                    buf.append(objectId(classLoader));
                    if (classLoader == systemClassLoader) {
                        buf.append(" (SYSTEM) ");
                    }
                    try {
                        classLoader = classLoader.getParent();
                    }
                    catch (SecurityException ex2) {
                        buf.append(" --> SECRET");
                        break Label_0186;
                    }
                    buf.append(" --> ");
                    continue;
                    logDiagnostic(buf.toString());
                    return;
                } while (classLoader != null);
                buf.append("BOOT");
                continue Label_0186;
            }
        }
    }
    
    public static String objectId(final Object o) {
        if (o == null) {
            return "null";
        }
        return o.getClass().getName() + "@" + System.identityHashCode(o);
    }
    
    static {
        LogFactory.diagnosticsStream = null;
        LogFactory.factories = null;
        LogFactory.nullClassLoaderFactory = null;
        LogFactory.thisClassLoader = getClassLoader(LogFactory.class);
        initDiagnostics();
        logClassLoaderEnvironment(LogFactory.class);
        LogFactory.factories = createFactoryStore();
        if (isDiagnosticsEnabled()) {
            logDiagnostic("BOOTSTRAP COMPLETED");
        }
    }
}
