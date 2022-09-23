// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Collection;
import org.datanucleus.enhancer.jdo.JDOClassEnhancer;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.datanucleus.NucleusContext;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public class RuntimeEnhancer
{
    protected static final Localiser LOCALISER;
    private final ClassLoaderResolver clr;
    private final NucleusContext nucleusContext;
    Map<ClassLoader, EnhancerClassLoader> runtimeLoaderByLoader;
    List<String> classEnhancerOptions;
    
    public RuntimeEnhancer() {
        this(null, null);
    }
    
    public RuntimeEnhancer(final String api) {
        this(api, null);
    }
    
    public RuntimeEnhancer(final String api, final Map contextProps) {
        this.runtimeLoaderByLoader = new HashMap<ClassLoader, EnhancerClassLoader>();
        this.classEnhancerOptions = new ArrayList<String>();
        this.nucleusContext = new NucleusContext(api, NucleusContext.ContextType.ENHANCEMENT, contextProps);
        this.clr = this.nucleusContext.getClassLoaderResolver(null);
        this.classEnhancerOptions.add("generate-primary-key");
        this.classEnhancerOptions.add("generate-default-constructor");
    }
    
    public void setClassEnhancerOption(final String optionName) {
        this.classEnhancerOptions.add(optionName);
    }
    
    public void unsetClassEnhancerOption(final String optionName) {
        this.classEnhancerOptions.remove(optionName);
    }
    
    public byte[] enhance(final String className, final byte[] classdefinition, final ClassLoader loader) {
        EnhancerClassLoader runtimeLoader = this.runtimeLoaderByLoader.get(loader);
        if (runtimeLoader == null) {
            runtimeLoader = new EnhancerClassLoader(loader);
            this.runtimeLoaderByLoader.put(loader, runtimeLoader);
        }
        this.clr.setPrimary(runtimeLoader);
        try {
            Class clazz = null;
            try {
                clazz = this.clr.classForName(className);
            }
            catch (ClassNotResolvedException e1) {
                DataNucleusEnhancer.LOGGER.debug(StringUtils.getStringFromStackTrace(e1));
                return null;
            }
            final AbstractClassMetaData acmd = this.nucleusContext.getMetaDataManager().getMetaDataForClass(clazz, this.clr);
            if (acmd == null) {
                DataNucleusEnhancer.LOGGER.debug("Class " + className + " cannot be enhanced because no metadata has been found.");
                return null;
            }
            final ClassEnhancer classEnhancer = new JDOClassEnhancer((ClassMetaData)acmd, this.clr, this.nucleusContext.getMetaDataManager(), classdefinition);
            classEnhancer.setOptions(this.classEnhancerOptions);
            classEnhancer.enhance();
            return classEnhancer.getClassBytes();
        }
        catch (Throwable ex) {
            DataNucleusEnhancer.LOGGER.error(StringUtils.getStringFromStackTrace(ex));
            return null;
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    public static class EnhancerClassLoader extends ClassLoader
    {
        EnhancerClassLoader(final ClassLoader loader) {
            super(loader);
        }
        
        @Override
        protected synchronized Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
            final Class c = super.findLoadedClass(name);
            if (c != null) {
                return c;
            }
            if (name.startsWith("java.")) {
                return super.loadClass(name, resolve);
            }
            if (name.startsWith("javax.")) {
                return super.loadClass(name, resolve);
            }
            if (name.startsWith("org.datanucleus.jpa.annotations") || name.startsWith("org.datanucleus.api.jpa.annotations")) {
                return super.loadClass(name, resolve);
            }
            final String resource = StringUtils.replaceAll(name, ".", "/") + ".class";
            try {
                final URL url = super.getResource(resource);
                if (url == null) {
                    throw new ClassNotFoundException(name);
                }
                final InputStream is = url.openStream();
                try {
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    final byte[] b = new byte[2048];
                    int count;
                    while ((count = is.read(b, 0, 2048)) != -1) {
                        os.write(b, 0, count);
                    }
                    final byte[] bytes = os.toByteArray();
                    return this.defineClass(name, bytes, 0, bytes.length);
                }
                finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
            catch (SecurityException e2) {
                return super.loadClass(name, resolve);
            }
            catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
    }
}
