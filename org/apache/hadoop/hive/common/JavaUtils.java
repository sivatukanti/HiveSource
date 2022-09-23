// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.commons.logging.LogFactory;
import java.lang.reflect.InvocationTargetException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;

public final class JavaUtils
{
    private static final Log LOG;
    private static final Method SUN_MISC_UTIL_RELEASE;
    
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = JavaUtils.class.getClassLoader();
        }
        return classLoader;
    }
    
    public static Class loadClass(final String className) throws ClassNotFoundException {
        return loadClass(className, true);
    }
    
    public static Class loadClass(final String className, final boolean init) throws ClassNotFoundException {
        return Class.forName(className, init, getClassLoader());
    }
    
    public static boolean closeClassLoadersTo(ClassLoader current, final ClassLoader stop) {
        if (!isValidHierarchy(current, stop)) {
            return false;
        }
        while (current != null && current != stop) {
            try {
                closeClassLoader(current);
            }
            catch (IOException e) {
                JavaUtils.LOG.info("Failed to close class loader " + current + Arrays.toString(((URLClassLoader)current).getURLs()), e);
            }
            current = current.getParent();
        }
        return true;
    }
    
    private static boolean isValidHierarchy(ClassLoader current, final ClassLoader stop) {
        if (current == null || stop == null || current == stop) {
            return false;
        }
        while (current != null && current != stop) {
            current = current.getParent();
        }
        return current == stop;
    }
    
    public static void closeClassLoader(final ClassLoader loader) throws IOException {
        if (loader instanceof Closeable) {
            ((Closeable)loader).close();
        }
        else if (JavaUtils.SUN_MISC_UTIL_RELEASE != null && loader instanceof URLClassLoader) {
            final PrintStream outputStream = System.out;
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final PrintStream newOutputStream = new PrintStream(byteArrayOutputStream);
            try {
                System.setOut(newOutputStream);
                JavaUtils.SUN_MISC_UTIL_RELEASE.invoke(null, loader);
                final String output = byteArrayOutputStream.toString("UTF8");
                JavaUtils.LOG.debug(output);
            }
            catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof IOException) {
                    throw (IOException)e.getTargetException();
                }
                throw new IOException(e.getTargetException());
            }
            catch (Exception e2) {
                throw new IOException(e2);
            }
            finally {
                System.setOut(outputStream);
                newOutputStream.close();
            }
        }
        LogFactory.release(loader);
    }
    
    public static String lockIdToString(final long extLockId) {
        return "lockid:" + extLockId;
    }
    
    private JavaUtils() {
    }
    
    static {
        LOG = LogFactory.getLog(JavaUtils.class);
        if (Closeable.class.isAssignableFrom(URLClassLoader.class)) {
            SUN_MISC_UTIL_RELEASE = null;
        }
        else {
            Method release = null;
            try {
                final Class<?> clazz = Class.forName("sun.misc.ClassLoaderUtil");
                release = clazz.getMethod("releaseLoader", URLClassLoader.class);
            }
            catch (Exception ex) {}
            SUN_MISC_UTIL_RELEASE = release;
        }
    }
}
