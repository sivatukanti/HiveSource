// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.wadl.config;

import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.lang.reflect.Method;
import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.File;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;
import com.sun.jersey.server.wadl.generators.WadlGeneratorJAXBGrammarGenerator;
import com.sun.jersey.server.wadl.WadlGenerator;
import java.util.List;
import java.util.logging.Logger;

class WadlGeneratorLoader
{
    private static final Logger LOGGER;
    
    static WadlGenerator loadWadlGenerators(final List<WadlGenerator> wadlGenerators) throws Exception {
        WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
        if (wadlGenerators != null && !wadlGenerators.isEmpty()) {
            for (final WadlGenerator generator : wadlGenerators) {
                generator.setWadlGeneratorDelegate(wadlGenerator);
                wadlGenerator = generator;
            }
        }
        wadlGenerator.init();
        return wadlGenerator;
    }
    
    static WadlGenerator loadWadlGeneratorDescriptions(final WadlGeneratorDescription... wadlGeneratorDescriptions) throws Exception {
        final List<WadlGeneratorDescription> list = (wadlGeneratorDescriptions != null) ? Arrays.asList(wadlGeneratorDescriptions) : null;
        return loadWadlGeneratorDescriptions(list);
    }
    
    static WadlGenerator loadWadlGeneratorDescriptions(final List<WadlGeneratorDescription> wadlGeneratorDescriptions) throws Exception {
        WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
        final CallbackList callbacks = new CallbackList();
        try {
            if (wadlGeneratorDescriptions != null && !wadlGeneratorDescriptions.isEmpty()) {
                for (final WadlGeneratorDescription wadlGeneratorDescription : wadlGeneratorDescriptions) {
                    final WadlGeneratorControl control = loadWadlGenerator(wadlGeneratorDescription, wadlGenerator);
                    wadlGenerator = control.wadlGenerator;
                    callbacks.add(control.callback);
                }
            }
            wadlGenerator.init();
        }
        finally {
            callbacks.callback();
        }
        return wadlGenerator;
    }
    
    private static WadlGeneratorControl loadWadlGenerator(final WadlGeneratorDescription wadlGeneratorDescription, final WadlGenerator wadlGeneratorDelegate) throws Exception {
        WadlGeneratorLoader.LOGGER.info("Loading wadlGenerator " + wadlGeneratorDescription.getGeneratorClass().getName());
        final WadlGenerator generator = (WadlGenerator)wadlGeneratorDescription.getGeneratorClass().newInstance();
        generator.setWadlGeneratorDelegate(wadlGeneratorDelegate);
        CallbackList callbacks = null;
        if (wadlGeneratorDescription.getProperties() != null && !wadlGeneratorDescription.getProperties().isEmpty()) {
            callbacks = new CallbackList();
            for (final Map.Entry<Object, Object> entry : wadlGeneratorDescription.getProperties().entrySet()) {
                final Callback callback = setProperty(generator, entry.getKey().toString(), entry.getValue());
                callbacks.add(callback);
            }
        }
        return new WadlGeneratorControl(generator, callbacks);
    }
    
    private static Callback setProperty(final Object generator, final String propertyName, final Object propertyValue) throws Exception {
        Callback result = null;
        final String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        final Method setterMethod = getMethodByName(methodName, generator.getClass());
        if (setterMethod.getParameterTypes().length != 1) {
            throw new RuntimeException("Method " + methodName + " is not a setter, it does not expect exactly one parameter, but " + setterMethod.getParameterTypes().length);
        }
        final Class<?> paramClazz = setterMethod.getParameterTypes()[0];
        if (paramClazz.isAssignableFrom(propertyValue.getClass())) {
            setterMethod.invoke(generator, propertyValue);
        }
        else if (File.class.equals(paramClazz) && propertyValue instanceof String) {
            WadlGeneratorLoader.LOGGER.warning("Configuring the " + setterMethod.getDeclaringClass().getSimpleName() + " with the file based property " + propertyName + " is deprecated and will be removed" + " in future versions of jersey! You should use the InputStream based property instead.");
            final String filename = propertyValue.toString();
            if (filename.startsWith("classpath:")) {
                final String strippedFilename = filename.substring("classpath:".length());
                final URL resource = generator.getClass().getResource(strippedFilename);
                if (resource == null) {
                    throw new RuntimeException("The file '" + strippedFilename + "' does not exist in the classpath." + " It's loaded by the generator class, so if you use a relative filename it's relative to" + " the generator class, otherwise you might want to load it via an absolute classpath reference like" + " classpath:/somefile.xml");
                }
                final File file = new File(resource.toURI());
                setterMethod.invoke(generator, file);
            }
            else {
                setterMethod.invoke(generator, new File(filename));
            }
        }
        else if (InputStream.class.equals(paramClazz) && propertyValue instanceof String) {
            final String resource2 = propertyValue.toString();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = WadlGeneratorLoader.class.getClassLoader();
            }
            final InputStream is = loader.getResourceAsStream(resource2);
            if (is == null) {
                final String message = "The resource '" + resource2 + "' does not exist.";
                throw new RuntimeException(message);
            }
            result = new Callback() {
                @Override
                public void callback() {
                    try {
                        is.close();
                    }
                    catch (IOException e) {
                        WadlGeneratorLoader.LOGGER.log(Level.WARNING, "Could not close InputStream from resource " + resource2, e);
                    }
                }
            };
            try {
                setterMethod.invoke(generator, is);
            }
            catch (Exception e) {
                is.close();
                throw e;
            }
        }
        else {
            final Constructor<?> paramTypeConstructor = paramClazz.getConstructor(propertyValue.getClass());
            if (paramTypeConstructor == null) {
                throw new RuntimeException("The property '" + propertyName + "' could not be set" + " because the expected parameter is neither of type " + propertyValue.getClass() + " nor of any type that provides a constructor expecting a " + propertyValue.getClass() + "." + " The expected parameter is of type " + paramClazz.getName());
            }
            final Object typedPropertyValue = paramTypeConstructor.newInstance(propertyValue);
            setterMethod.invoke(generator, typedPropertyValue);
        }
        return result;
    }
    
    private static Method getMethodByName(final String methodName, final Class<?> clazz) {
        for (final Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new RuntimeException("Method '" + methodName + "' not found for class " + clazz.getName());
    }
    
    static {
        LOGGER = Logger.getLogger(WadlGeneratorLoader.class.getName());
    }
    
    private static class WadlGeneratorControl
    {
        WadlGenerator wadlGenerator;
        Callback callback;
        
        public WadlGeneratorControl(final WadlGenerator wadlGenerator, final Callback callback) {
            this.wadlGenerator = wadlGenerator;
            this.callback = callback;
        }
    }
    
    private static class CallbackList extends ArrayList<Callback> implements Callback
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public void callback() {
            for (final Callback callback : this) {
                callback.callback();
            }
        }
        
        @Override
        public boolean add(final Callback e) {
            return e != null && super.add(e);
        }
    }
    
    private interface Callback
    {
        void callback();
    }
}
