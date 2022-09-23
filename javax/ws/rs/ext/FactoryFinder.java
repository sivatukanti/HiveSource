// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.ext;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;

class FactoryFinder
{
    static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException ex) {}
                return cl;
            }
        });
    }
    
    private static Object newInstance(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            }
            else {
                try {
                    spiClass = Class.forName(className, false, classLoader);
                }
                catch (ClassNotFoundException ex) {
                    spiClass = Class.forName(className);
                }
            }
            return spiClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw x;
        }
        catch (Exception x2) {
            throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static Object find(final String factoryId, final String fallbackClassName) throws ClassNotFoundException {
        final ClassLoader classLoader = getContextClassLoader();
        final String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            }
            else {
                is = classLoader.getResourceAsStream(serviceId);
            }
            if (is != null) {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                final String factoryClassName = rd.readLine();
                rd.close();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return newInstance(factoryClassName, classLoader);
                }
            }
        }
        catch (Exception ex) {}
        try {
            final String javah = System.getProperty("java.home");
            final String configFile = javah + File.separator + "lib" + File.separator + "jaxrs.properties";
            final File f = new File(configFile);
            if (f.exists()) {
                final Properties props = new Properties();
                props.load(new FileInputStream(f));
                final String factoryClassName2 = props.getProperty(factoryId);
                return newInstance(factoryClassName2, classLoader);
            }
        }
        catch (Exception ex2) {}
        try {
            final String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, classLoader);
            }
        }
        catch (SecurityException ex3) {}
        if (fallbackClassName == null) {
            throw new ClassNotFoundException("Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(fallbackClassName, classLoader);
    }
}
