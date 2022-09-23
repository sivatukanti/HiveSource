// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.net.URL;

public class Loader
{
    public static URL getResource(final Class loadClass, final String name, final boolean checkParents) throws ClassNotFoundException {
        URL url = null;
        for (ClassLoader loader = Thread.currentThread().getContextClassLoader(); url == null && loader != null; url = loader.getResource(name), loader = ((url == null && checkParents) ? loader.getParent() : null)) {}
        for (ClassLoader loader = (loadClass == null) ? null : loadClass.getClassLoader(); url == null && loader != null; url = loader.getResource(name), loader = ((url == null && checkParents) ? loader.getParent() : null)) {}
        if (url == null) {
            url = ClassLoader.getSystemResource(name);
        }
        return url;
    }
    
    public static Class loadClass(final Class loadClass, final String name) throws ClassNotFoundException {
        return loadClass(loadClass, name, false);
    }
    
    public static Class loadClass(final Class loadClass, final String name, final boolean checkParents) throws ClassNotFoundException {
        ClassNotFoundException ex = null;
        Class c = null;
        for (ClassLoader loader = Thread.currentThread().getContextClassLoader(); c == null && loader != null; loader = ((c == null && checkParents) ? loader.getParent() : null)) {
            try {
                c = loader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        for (ClassLoader loader = (loadClass == null) ? null : loadClass.getClassLoader(); c == null && loader != null; loader = ((c == null && checkParents) ? loader.getParent() : null)) {
            try {
                c = loader.loadClass(name);
            }
            catch (ClassNotFoundException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        if (c == null) {
            try {
                c = Class.forName(name);
            }
            catch (ClassNotFoundException e) {
                if (ex == null) {
                    ex = e;
                }
            }
        }
        if (c != null) {
            return c;
        }
        throw ex;
    }
    
    public static ResourceBundle getResourceBundle(final Class loadClass, final String name, final boolean checkParents, final Locale locale) throws MissingResourceException {
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
}
