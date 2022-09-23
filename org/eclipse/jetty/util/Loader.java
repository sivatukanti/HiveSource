// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.File;
import org.eclipse.jetty.util.resource.Resource;
import java.net.URLClassLoader;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.net.URL;

public class Loader
{
    public static URL getResource(final Class<?> loadClass, final String name) {
        URL url = null;
        final ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
        if (context_loader != null) {
            url = context_loader.getResource(name);
        }
        if (url == null && loadClass != null) {
            final ClassLoader load_loader = loadClass.getClassLoader();
            if (load_loader != null && load_loader != context_loader) {
                url = load_loader.getResource(name);
            }
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }
        return url;
    }
    
    public static Class loadClass(final Class loadClass, final String name) throws ClassNotFoundException {
        ClassNotFoundException ex = null;
        Class<?> c = null;
        final ClassLoader context_loader = Thread.currentThread().getContextClassLoader();
        if (context_loader != null) {
            try {
                c = context_loader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                ex = e;
            }
        }
        if (c == null && loadClass != null) {
            final ClassLoader load_loader = loadClass.getClassLoader();
            if (load_loader != null && load_loader != context_loader) {
                try {
                    c = load_loader.loadClass(name);
                }
                catch (ClassNotFoundException e2) {
                    if (ex == null) {
                        ex = e2;
                    }
                }
            }
        }
        if (c == null) {
            try {
                c = Class.forName(name);
            }
            catch (ClassNotFoundException e) {
                if (ex != null) {
                    throw ex;
                }
                throw e;
            }
        }
        return c;
    }
    
    public static ResourceBundle getResourceBundle(final Class<?> loadClass, final String name, final boolean checkParents, final Locale locale) throws MissingResourceException {
        MissingResourceException ex = null;
        ResourceBundle bundle = null;
        for (ClassLoader loader = Thread.currentThread().getContextClassLoader(); bundle == null && loader != null; loader = ((bundle == null && checkParents) ? loader.getParent() : null)) {
            try {
                bundle = ResourceBundle.getBundle(name, locale, loader);
            }
            catch (MissingResourceException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        for (ClassLoader loader = (loadClass == null) ? null : loadClass.getClassLoader(); bundle == null && loader != null; loader = ((bundle == null && checkParents) ? loader.getParent() : null)) {
            try {
                bundle = ResourceBundle.getBundle(name, locale, loader);
            }
            catch (MissingResourceException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle(name, locale);
            }
            catch (MissingResourceException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        if (bundle != null) {
            return bundle;
        }
        throw ex;
    }
    
    public static String getClassPath(ClassLoader loader) throws Exception {
        final StringBuilder classpath = new StringBuilder();
        while (loader != null && loader instanceof URLClassLoader) {
            final URL[] urls = ((URLClassLoader)loader).getURLs();
            if (urls != null) {
                for (int i = 0; i < urls.length; ++i) {
                    final Resource resource = Resource.newResource(urls[i]);
                    final File file = resource.getFile();
                    if (file != null && file.exists()) {
                        if (classpath.length() > 0) {
                            classpath.append(File.pathSeparatorChar);
                        }
                        classpath.append(file.getAbsolutePath());
                    }
                }
            }
            loader = loader.getParent();
        }
        return classpath.toString();
    }
}
