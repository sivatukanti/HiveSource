// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.security.AccessController;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import java.util.List;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import org.datanucleus.exceptions.ClassNotResolvedException;
import java.util.Collections;
import org.datanucleus.util.WeakValueMap;
import java.net.URL;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class ClassLoaderResolverImpl implements ClassLoaderResolver
{
    protected static final Localiser LOCALISER;
    protected final ClassLoader ecContextLoader;
    protected int ecContextLoaderHashCode;
    protected ClassLoader runtimeLoader;
    protected int runtimeLoaderHashCode;
    protected ClassLoader userRegisteredLoader;
    protected int userRegisteredLoaderHashCode;
    protected Map<String, Class> loadedClasses;
    protected Map<String, Class> unloadedClasses;
    protected Map<String, URL> resources;
    ThreadLocal primary;
    
    public ClassLoaderResolverImpl(final ClassLoader pmLoader) {
        this.ecContextLoaderHashCode = 0;
        this.runtimeLoaderHashCode = 0;
        this.userRegisteredLoaderHashCode = 0;
        this.loadedClasses = (Map<String, Class>)Collections.synchronizedMap((Map<String, Class>)new WeakValueMap());
        this.unloadedClasses = (Map<String, Class>)Collections.synchronizedMap((Map<String, Class>)new WeakValueMap());
        this.resources = Collections.synchronizedMap((Map<String, URL>)new WeakValueMap());
        this.primary = new ThreadLocal();
        this.ecContextLoader = pmLoader;
        if (pmLoader != null) {
            this.ecContextLoaderHashCode = this.ecContextLoader.hashCode();
        }
    }
    
    public ClassLoaderResolverImpl() {
        this.ecContextLoaderHashCode = 0;
        this.runtimeLoaderHashCode = 0;
        this.userRegisteredLoaderHashCode = 0;
        this.loadedClasses = (Map<String, Class>)Collections.synchronizedMap((Map<String, Class>)new WeakValueMap());
        this.unloadedClasses = (Map<String, Class>)Collections.synchronizedMap((Map<String, Class>)new WeakValueMap());
        this.resources = Collections.synchronizedMap((Map<String, URL>)new WeakValueMap());
        this.primary = new ThreadLocal();
        this.ecContextLoader = null;
    }
    
    @Override
    public Class classForName(final String name, final ClassLoader primary) {
        if (name == null) {
            final String msg = ClassLoaderResolverImpl.LOCALISER.msg("001000", null);
            throw new ClassNotResolvedException(msg);
        }
        if (name.equals(ClassNameConstants.BYTE)) {
            return Byte.TYPE;
        }
        if (name.equals(ClassNameConstants.CHAR)) {
            return Character.TYPE;
        }
        if (name.equals(ClassNameConstants.INT)) {
            return Integer.TYPE;
        }
        if (name.equals(ClassNameConstants.LONG)) {
            return Long.TYPE;
        }
        if (name.equals(ClassNameConstants.DOUBLE)) {
            return Double.TYPE;
        }
        if (name.equals(ClassNameConstants.FLOAT)) {
            return Float.TYPE;
        }
        if (name.equals(ClassNameConstants.SHORT)) {
            return Short.TYPE;
        }
        if (name.equals(ClassNameConstants.BOOLEAN)) {
            return Boolean.TYPE;
        }
        if (name.equals(ClassNameConstants.JAVA_LANG_STRING)) {
            return String.class;
        }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final String cacheKey = this.newCacheKey(name, primary, contextClassLoader);
        Class cls = this.loadedClasses.get(cacheKey);
        if (cls != null) {
            return cls;
        }
        cls = this.unloadedClasses.get(cacheKey);
        if (cls != null) {
            return cls;
        }
        cls = this.classOrNull(name, primary);
        if (cls == null && this.primary.get() != null) {
            cls = this.classOrNull(name, this.primary.get());
        }
        if (cls == null) {
            cls = this.classOrNull(name, contextClassLoader);
        }
        if (cls == null) {
            cls = this.classOrNull(name, this.ecContextLoader);
        }
        if (cls == null && this.runtimeLoader != null) {
            cls = this.classOrNull(name, this.runtimeLoader);
        }
        if (cls == null && this.userRegisteredLoader != null) {
            cls = this.classOrNull(name, this.userRegisteredLoader);
        }
        if (cls == null) {
            throw new ClassNotResolvedException(ClassLoaderResolverImpl.LOCALISER.msg("001000", name));
        }
        this.unloadedClasses.put(cacheKey, cls);
        return cls;
    }
    
    private Class classForNameWithInitialize(final String name, final ClassLoader primary) {
        if (name == null) {
            final String msg = ClassLoaderResolverImpl.LOCALISER.msg("001000", null);
            throw new ClassNotResolvedException(msg);
        }
        if (name.equals(ClassNameConstants.BYTE)) {
            return Byte.TYPE;
        }
        if (name.equals(ClassNameConstants.CHAR)) {
            return Character.TYPE;
        }
        if (name.equals(ClassNameConstants.INT)) {
            return Integer.TYPE;
        }
        if (name.equals(ClassNameConstants.LONG)) {
            return Long.TYPE;
        }
        if (name.equals(ClassNameConstants.DOUBLE)) {
            return Double.TYPE;
        }
        if (name.equals(ClassNameConstants.FLOAT)) {
            return Float.TYPE;
        }
        if (name.equals(ClassNameConstants.SHORT)) {
            return Short.TYPE;
        }
        if (name.equals(ClassNameConstants.BOOLEAN)) {
            return Boolean.TYPE;
        }
        if (name.equals(ClassNameConstants.JAVA_LANG_STRING)) {
            return String.class;
        }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final String cacheKey = this.newCacheKey(name, primary, contextClassLoader);
        Class cls = this.loadedClasses.get(cacheKey);
        if (cls != null) {
            return cls;
        }
        cls = this.ClassOrNullWithInitialize(name, primary);
        if (cls == null && this.primary.get() != null) {
            cls = this.ClassOrNullWithInitialize(name, this.primary.get());
        }
        if (cls == null) {
            cls = this.ClassOrNullWithInitialize(name, contextClassLoader);
        }
        if (cls == null) {
            cls = this.ClassOrNullWithInitialize(name, this.ecContextLoader);
        }
        if (cls == null && this.runtimeLoader != null) {
            cls = this.ClassOrNullWithInitialize(name, this.runtimeLoader);
        }
        if (cls == null && this.userRegisteredLoader != null) {
            cls = this.ClassOrNullWithInitialize(name, this.userRegisteredLoader);
        }
        if (cls == null) {
            final String msg2 = ClassLoaderResolverImpl.LOCALISER.msg("001000", name);
            throw new ClassNotResolvedException(msg2);
        }
        this.loadedClasses.put(cacheKey, cls);
        return cls;
    }
    
    private String newCacheKey(final String prefix, final ClassLoader primary, final ClassLoader contextClassLoader) {
        int h = 3;
        if (primary != null) {
            h ^= primary.hashCode();
        }
        if (contextClassLoader != null) {
            h ^= contextClassLoader.hashCode();
        }
        h ^= this.ecContextLoaderHashCode;
        h ^= this.runtimeLoaderHashCode;
        h ^= this.userRegisteredLoaderHashCode;
        return prefix + h;
    }
    
    @Override
    public Class classForName(final String name, final ClassLoader primary, final boolean initialize) {
        if (initialize) {
            return this.classForNameWithInitialize(name, primary);
        }
        return this.classForName(name, primary);
    }
    
    @Override
    public Class classForName(final String name) {
        return this.classForName(name, null);
    }
    
    @Override
    public Class classForName(final String name, final boolean initialize) {
        return this.classForName(name, null, initialize);
    }
    
    @Override
    public boolean isAssignableFrom(final String class_name_1, final String class_name_2) {
        if (class_name_1 == null || class_name_2 == null) {
            return false;
        }
        if (class_name_1.equals(class_name_2)) {
            return true;
        }
        final Class class_1 = this.classForName(class_name_1);
        final Class class_2 = this.classForName(class_name_2);
        return class_1.isAssignableFrom(class_2);
    }
    
    @Override
    public boolean isAssignableFrom(final String class_name_1, final Class class_2) {
        if (class_name_1 == null || class_2 == null) {
            return false;
        }
        if (class_name_1.equals(class_2.getName())) {
            return true;
        }
        try {
            Class class_3 = null;
            if (class_2.getClassLoader() != null) {
                class_3 = class_2.getClassLoader().loadClass(class_name_1);
            }
            else {
                class_3 = Class.forName(class_name_1);
            }
            return class_3.isAssignableFrom(class_2);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isAssignableFrom(final Class class_1, final String class_name_2) {
        if (class_1 == null || class_name_2 == null) {
            return false;
        }
        if (class_1.getName().equals(class_name_2)) {
            return true;
        }
        try {
            Class class_2 = null;
            if (class_1.getClassLoader() != null) {
                class_2 = class_1.getClassLoader().loadClass(class_name_2);
            }
            else {
                class_2 = Class.forName(class_name_2);
            }
            return class_1.isAssignableFrom(class_2);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    private Class classOrNull(final String name, final ClassLoader loader) {
        try {
            return (loader == null) ? null : Class.forName(name, false, loader);
        }
        catch (ClassNotFoundException cnfe) {}
        catch (NoClassDefFoundError noClassDefFoundError) {}
        return null;
    }
    
    private Class ClassOrNullWithInitialize(final String name, final ClassLoader loader) {
        try {
            return (loader == null) ? null : Class.forName(name, true, loader);
        }
        catch (ClassNotFoundException cnfe) {
            return null;
        }
        catch (NoClassDefFoundError ncdfe) {
            return null;
        }
    }
    
    @Override
    public void setRuntimeClassLoader(final ClassLoader loader) {
        this.runtimeLoader = loader;
        if (this.runtimeLoader == null) {
            this.runtimeLoaderHashCode = 0;
        }
        else {
            this.runtimeLoaderHashCode = loader.hashCode();
        }
    }
    
    @Override
    public void registerUserClassLoader(final ClassLoader loader) {
        this.userRegisteredLoader = loader;
        if (this.userRegisteredLoader == null) {
            this.userRegisteredLoaderHashCode = 0;
        }
        else {
            this.userRegisteredLoaderHashCode = loader.hashCode();
        }
    }
    
    @Override
    public Enumeration getResources(final String resourceName, final ClassLoader primary) throws IOException {
        final List list = new ArrayList();
        final ClassLoader userClassLoader = this.primary.get();
        final ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    String name = resourceName;
                    if (name.startsWith("/")) {
                        name = name.substring(1);
                    }
                    if (primary != null) {
                        final Enumeration primaryResourceEnum = primary.getResources(name);
                        while (primaryResourceEnum.hasMoreElements()) {
                            list.add(primaryResourceEnum.nextElement());
                        }
                    }
                    if (userClassLoader != null) {
                        final Enumeration primaryResourceEnum = userClassLoader.getResources(name);
                        while (primaryResourceEnum.hasMoreElements()) {
                            list.add(primaryResourceEnum.nextElement());
                        }
                    }
                    if (ctxClassLoader != null) {
                        final Enumeration resourceEnum = ctxClassLoader.getResources(name);
                        while (resourceEnum.hasMoreElements()) {
                            list.add(resourceEnum.nextElement());
                        }
                    }
                    if (ClassLoaderResolverImpl.this.ecContextLoader != null) {
                        final Enumeration pmResourceEnum = ClassLoaderResolverImpl.this.ecContextLoader.getResources(name);
                        while (pmResourceEnum.hasMoreElements()) {
                            list.add(pmResourceEnum.nextElement());
                        }
                    }
                    if (ClassLoaderResolverImpl.this.runtimeLoader != null) {
                        final Enumeration loaderResourceEnum = ClassLoaderResolverImpl.this.runtimeLoader.getResources(name);
                        while (loaderResourceEnum.hasMoreElements()) {
                            list.add(loaderResourceEnum.nextElement());
                        }
                    }
                    if (ClassLoaderResolverImpl.this.userRegisteredLoader != null) {
                        final Enumeration loaderResourceEnum = ClassLoaderResolverImpl.this.userRegisteredLoader.getResources(name);
                        while (loaderResourceEnum.hasMoreElements()) {
                            list.add(loaderResourceEnum.nextElement());
                        }
                    }
                }
                catch (IOException ex) {
                    throw new NucleusException(ex.getMessage(), ex);
                }
                return null;
            }
        });
        return Collections.enumeration(new LinkedHashSet<Object>(list));
    }
    
    @Override
    public URL getResource(final String resourceName, final ClassLoader primary) {
        final ClassLoader userClassLoader = this.primary.get();
        final URL url = AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction() {
            @Override
            public Object run() {
                String resName = resourceName;
                URL url = ClassLoaderResolverImpl.this.resources.get(resName);
                if (url != null) {
                    return url;
                }
                if (resName.startsWith("/")) {
                    resName = resName.substring(1);
                }
                if (primary != null) {
                    url = primary.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                if (userClassLoader != null) {
                    url = userClassLoader.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                final ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
                if (ctxClassLoader != null) {
                    url = ctxClassLoader.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                if (ClassLoaderResolverImpl.this.ecContextLoader != null) {
                    url = ClassLoaderResolverImpl.this.ecContextLoader.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                if (ClassLoaderResolverImpl.this.runtimeLoader != null) {
                    url = ClassLoaderResolverImpl.this.runtimeLoader.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                if (ClassLoaderResolverImpl.this.userRegisteredLoader != null) {
                    url = ClassLoaderResolverImpl.this.userRegisteredLoader.getResource(resName);
                    if (url != null) {
                        ClassLoaderResolverImpl.this.resources.put(resName, url);
                        return url;
                    }
                }
                return null;
            }
        });
        return url;
    }
    
    @Override
    public void setPrimary(final ClassLoader primary) {
        this.primary.set(primary);
    }
    
    @Override
    public void unsetPrimary() {
        this.primary.set(null);
    }
    
    @Override
    public String toString() {
        return "ClassLoaderResolver: primary=" + this.primary + " pmContextLoader=" + this.ecContextLoader + " runtimeLoader=" + this.runtimeLoader + " registeredLoader=" + this.userRegisteredLoader;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
