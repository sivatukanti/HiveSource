// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.net.URL;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.URLClassLoader;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationClassLoader extends URLClassLoader
{
    public static final String SYSTEM_CLASSES_DEFAULT;
    private static final String PROPERTIES_FILE = "org.apache.hadoop.application-classloader.properties";
    private static final String SYSTEM_CLASSES_DEFAULT_KEY = "system.classes.default";
    private static final Logger LOG;
    private final ClassLoader parent;
    private final List<String> systemClasses;
    
    public ApplicationClassLoader(final URL[] urls, final ClassLoader parent, final List<String> systemClasses) {
        super(urls, parent);
        this.parent = parent;
        if (parent == null) {
            throw new IllegalArgumentException("No parent classloader!");
        }
        this.systemClasses = ((systemClasses == null || systemClasses.isEmpty()) ? Arrays.asList(StringUtils.getTrimmedStrings(ApplicationClassLoader.SYSTEM_CLASSES_DEFAULT)) : systemClasses);
        ApplicationClassLoader.LOG.info("classpath: " + Arrays.toString(urls));
        ApplicationClassLoader.LOG.info("system classes: " + this.systemClasses);
    }
    
    public ApplicationClassLoader(final String classpath, final ClassLoader parent, final List<String> systemClasses) throws MalformedURLException {
        this(constructUrlsFromClasspath(classpath), parent, systemClasses);
    }
    
    static URL[] constructUrlsFromClasspath(final String classpath) throws MalformedURLException {
        final List<URL> urls = new ArrayList<URL>();
        for (final String element : classpath.split(File.pathSeparator)) {
            if (element.endsWith("/*")) {
                final List<Path> jars = FileUtil.getJarsInDirectory(element);
                if (!jars.isEmpty()) {
                    for (final Path jar : jars) {
                        urls.add(jar.toUri().toURL());
                    }
                }
            }
            else {
                final File file = new File(element);
                if (file.exists()) {
                    urls.add(new File(element).toURI().toURL());
                }
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }
    
    @Override
    public URL getResource(final String name) {
        URL url = null;
        if (!isSystemClass(name, this.systemClasses)) {
            url = this.findResource(name);
            if (url == null && name.startsWith("/")) {
                if (ApplicationClassLoader.LOG.isDebugEnabled()) {
                    ApplicationClassLoader.LOG.debug("Remove leading / off " + name);
                }
                url = this.findResource(name.substring(1));
            }
        }
        if (url == null) {
            url = this.parent.getResource(name);
        }
        if (url != null && ApplicationClassLoader.LOG.isDebugEnabled()) {
            ApplicationClassLoader.LOG.debug("getResource(" + name + ")=" + url);
        }
        return url;
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }
    
    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (ApplicationClassLoader.LOG.isDebugEnabled()) {
            ApplicationClassLoader.LOG.debug("Loading class: " + name);
        }
        Class<?> c = this.findLoadedClass(name);
        ClassNotFoundException ex = null;
        if (c == null && !isSystemClass(name, this.systemClasses)) {
            try {
                c = this.findClass(name);
                if (ApplicationClassLoader.LOG.isDebugEnabled() && c != null) {
                    ApplicationClassLoader.LOG.debug("Loaded class: " + name + " ");
                }
            }
            catch (ClassNotFoundException e) {
                if (ApplicationClassLoader.LOG.isDebugEnabled()) {
                    ApplicationClassLoader.LOG.debug(e.toString());
                }
                ex = e;
            }
        }
        if (c == null) {
            c = this.parent.loadClass(name);
            if (ApplicationClassLoader.LOG.isDebugEnabled() && c != null) {
                ApplicationClassLoader.LOG.debug("Loaded class from parent: " + name + " ");
            }
        }
        if (c == null) {
            throw (ex != null) ? ex : new ClassNotFoundException(name);
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }
    
    public static boolean isSystemClass(final String name, final List<String> systemClasses) {
        boolean result = false;
        if (systemClasses != null) {
            String canonicalName;
            for (canonicalName = name.replace('/', '.'); canonicalName.startsWith("."); canonicalName = canonicalName.substring(1)) {}
            for (String c : systemClasses) {
                boolean shouldInclude = true;
                if (c.startsWith("-")) {
                    c = c.substring(1);
                    shouldInclude = false;
                }
                if (canonicalName.startsWith(c) && (c.endsWith(".") || canonicalName.length() == c.length() || (canonicalName.length() > c.length() && canonicalName.charAt(c.length()) == '$'))) {
                    if (!shouldInclude) {
                        return false;
                    }
                    result = true;
                }
            }
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ApplicationClassLoader.class.getName());
        try (final InputStream is = ApplicationClassLoader.class.getClassLoader().getResourceAsStream("org.apache.hadoop.application-classloader.properties")) {
            if (is == null) {
                throw new ExceptionInInitializerError("properties file org.apache.hadoop.application-classloader.properties is not found");
            }
            final Properties props = new Properties();
            props.load(is);
            final String systemClassesDefault = props.getProperty("system.classes.default");
            if (systemClassesDefault == null) {
                throw new ExceptionInInitializerError("property system.classes.default is not found");
            }
            SYSTEM_CLASSES_DEFAULT = systemClassesDefault;
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
