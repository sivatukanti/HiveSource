// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import com.google.inject.internal.util.$ImmutableMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$MapMaker;
import com.google.inject.internal.cglib.core.$Predicate;
import com.google.inject.internal.cglib.core.$DefaultNamingPolicy;
import com.google.inject.internal.cglib.proxy.$Enhancer;
import com.google.inject.internal.cglib.reflect.$FastClass;
import java.util.Map;
import com.google.inject.internal.cglib.core.$NamingPolicy;
import java.util.logging.Logger;

public final class BytecodeGen
{
    static final Logger logger;
    static final ClassLoader GUICE_CLASS_LOADER;
    static final String GUICE_INTERNAL_PACKAGE;
    static final String CGLIB_PACKAGE;
    static final $NamingPolicy FASTCLASS_NAMING_POLICY;
    static final $NamingPolicy ENHANCER_NAMING_POLICY;
    private static final boolean CUSTOM_LOADER_ENABLED;
    private static final Map<ClassLoader, ClassLoader> CLASS_LOADER_CACHE;
    
    private static ClassLoader canonicalize(final ClassLoader classLoader) {
        return (classLoader != null) ? classLoader : SystemBridgeHolder.SYSTEM_BRIDGE.getParent();
    }
    
    public static ClassLoader getClassLoader(final Class<?> type) {
        return getClassLoader(type, type.getClassLoader());
    }
    
    private static ClassLoader getClassLoader(final Class<?> type, ClassLoader delegate) {
        if (!BytecodeGen.CUSTOM_LOADER_ENABLED) {
            return delegate;
        }
        if (type.getName().startsWith("java.")) {
            return BytecodeGen.GUICE_CLASS_LOADER;
        }
        delegate = canonicalize(delegate);
        if (delegate == BytecodeGen.GUICE_CLASS_LOADER || delegate instanceof BridgeClassLoader) {
            return delegate;
        }
        if (Visibility.forType(type) != Visibility.PUBLIC) {
            return delegate;
        }
        if (delegate != SystemBridgeHolder.SYSTEM_BRIDGE.getParent()) {
            return BytecodeGen.CLASS_LOADER_CACHE.get(delegate);
        }
        return SystemBridgeHolder.SYSTEM_BRIDGE;
    }
    
    public static $FastClass newFastClass(final Class<?> type, final Visibility visibility) {
        final $FastClass.Generator generator = new $FastClass.Generator();
        generator.setType(type);
        if (visibility == Visibility.PUBLIC) {
            generator.setClassLoader(getClassLoader(type));
        }
        generator.setNamingPolicy(BytecodeGen.FASTCLASS_NAMING_POLICY);
        BytecodeGen.logger.fine("Loading " + type + " FastClass with " + generator.getClassLoader());
        return generator.create();
    }
    
    public static $Enhancer newEnhancer(final Class<?> type, final Visibility visibility) {
        final $Enhancer enhancer = new $Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setUseFactory(false);
        if (visibility == Visibility.PUBLIC) {
            enhancer.setClassLoader(getClassLoader(type));
        }
        enhancer.setNamingPolicy(BytecodeGen.ENHANCER_NAMING_POLICY);
        BytecodeGen.logger.fine("Loading " + type + " Enhancer with " + enhancer.getClassLoader());
        return enhancer;
    }
    
    static {
        logger = Logger.getLogger(BytecodeGen.class.getName());
        GUICE_CLASS_LOADER = canonicalize(BytecodeGen.class.getClassLoader());
        GUICE_INTERNAL_PACKAGE = BytecodeGen.class.getName().replaceFirst("\\.internal\\..*$", ".internal");
        CGLIB_PACKAGE = $Enhancer.class.getName().replaceFirst("\\.cglib\\..*$", ".cglib");
        FASTCLASS_NAMING_POLICY = new $DefaultNamingPolicy() {
            @Override
            protected String getTag() {
                return "ByGuice";
            }
            
            @Override
            public String getClassName(final String prefix, final String source, final Object key, final $Predicate names) {
                return super.getClassName(prefix, "FastClass", key, names);
            }
        };
        ENHANCER_NAMING_POLICY = new $DefaultNamingPolicy() {
            @Override
            protected String getTag() {
                return "ByGuice";
            }
            
            @Override
            public String getClassName(final String prefix, final String source, final Object key, final $Predicate names) {
                return super.getClassName(prefix, "Enhancer", key, names);
            }
        };
        CUSTOM_LOADER_ENABLED = Boolean.parseBoolean(System.getProperty("guice.custom.loader", "true"));
        if (BytecodeGen.CUSTOM_LOADER_ENABLED) {
            CLASS_LOADER_CACHE = new $MapMaker().weakKeys().weakValues().makeComputingMap(($Function<? super Object, ?>)new $Function<ClassLoader, ClassLoader>() {
                public ClassLoader apply(@$Nullable final ClassLoader typeClassLoader) {
                    BytecodeGen.logger.fine("Creating a bridge ClassLoader for " + typeClassLoader);
                    return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                        public ClassLoader run() {
                            return new BridgeClassLoader(typeClassLoader);
                        }
                    });
                }
            });
        }
        else {
            CLASS_LOADER_CACHE = $ImmutableMap.of();
        }
    }
    
    private static class SystemBridgeHolder
    {
        static final BridgeClassLoader SYSTEM_BRIDGE;
        
        static {
            SYSTEM_BRIDGE = new BridgeClassLoader();
        }
    }
    
    public enum Visibility
    {
        PUBLIC {
            @Override
            public Visibility and(final Visibility that) {
                return that;
            }
        }, 
        SAME_PACKAGE {
            @Override
            public Visibility and(final Visibility that) {
                return this;
            }
        };
        
        public static Visibility forMember(final Member member) {
            if ((member.getModifiers() & 0x5) == 0x0) {
                return Visibility.SAME_PACKAGE;
            }
            Class[] parameterTypes;
            if (member instanceof Constructor) {
                parameterTypes = ((Constructor)member).getParameterTypes();
            }
            else {
                final Method method = (Method)member;
                if (forType(method.getReturnType()) == Visibility.SAME_PACKAGE) {
                    return Visibility.SAME_PACKAGE;
                }
                parameterTypes = method.getParameterTypes();
            }
            for (final Class<?> type : parameterTypes) {
                if (forType(type) == Visibility.SAME_PACKAGE) {
                    return Visibility.SAME_PACKAGE;
                }
            }
            return Visibility.PUBLIC;
        }
        
        public static Visibility forType(final Class<?> type) {
            return ((type.getModifiers() & 0x5) != 0x0) ? Visibility.PUBLIC : Visibility.SAME_PACKAGE;
        }
        
        public abstract Visibility and(final Visibility p0);
    }
    
    private static class BridgeClassLoader extends ClassLoader
    {
        BridgeClassLoader() {
        }
        
        BridgeClassLoader(final ClassLoader usersClassLoader) {
            super(usersClassLoader);
        }
        
        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("sun.reflect")) {
                return SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(name, resolve);
            }
            if (name.startsWith(BytecodeGen.GUICE_INTERNAL_PACKAGE) || name.startsWith(BytecodeGen.CGLIB_PACKAGE)) {
                if (null == BytecodeGen.GUICE_CLASS_LOADER) {
                    return SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(name, resolve);
                }
                try {
                    final Class<?> clazz = BytecodeGen.GUICE_CLASS_LOADER.loadClass(name);
                    if (resolve) {
                        this.resolveClass(clazz);
                    }
                    return clazz;
                }
                catch (Throwable t) {}
            }
            return this.classicLoadClass(name, resolve);
        }
        
        Class<?> classicLoadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }
    }
}
