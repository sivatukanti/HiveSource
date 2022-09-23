// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration2.event.EventType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.sync.Synchronizer;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import org.apache.commons.logging.Log;
import org.apache.commons.configuration2.event.EventSource;

public final class ConfigurationUtils
{
    private static final String METHOD_CLONE = "clone";
    private static final Class<?>[] IMMUTABLE_CONFIG_IFCS;
    private static final Class<?>[] IMMUTABLE_HIERARCHICAL_CONFIG_IFCS;
    private static final EventSource DUMMY_EVENT_SOURCE;
    private static final Log LOG;
    
    private ConfigurationUtils() {
    }
    
    public static void dump(final Configuration configuration, final PrintStream out) {
        dump(configuration, new PrintWriter(out));
    }
    
    public static void dump(final Configuration configuration, final PrintWriter out) {
        final Iterator<String> keys = configuration.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object value = configuration.getProperty(key);
            out.print(key);
            out.print("=");
            out.print(value);
            if (keys.hasNext()) {
                out.println();
            }
        }
        out.flush();
    }
    
    public static String toString(final Configuration configuration) {
        final StringWriter writer = new StringWriter();
        dump(configuration, new PrintWriter(writer));
        return writer.toString();
    }
    
    public static void copy(final Configuration source, final Configuration target) {
        final Iterator<String> keys = source.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            target.setProperty(key, source.getProperty(key));
        }
    }
    
    public static void append(final Configuration source, final Configuration target) {
        final Iterator<String> keys = source.getKeys();
        while (keys.hasNext()) {
            final String key = keys.next();
            target.addProperty(key, source.getProperty(key));
        }
    }
    
    public static HierarchicalConfiguration<?> convertToHierarchical(final Configuration conf) {
        return convertToHierarchical(conf, null);
    }
    
    public static HierarchicalConfiguration<?> convertToHierarchical(final Configuration conf, final ExpressionEngine engine) {
        if (conf == null) {
            return null;
        }
        if (conf instanceof HierarchicalConfiguration) {
            final HierarchicalConfiguration<?> hc = (HierarchicalConfiguration<?>)conf;
            if (engine != null) {
                hc.setExpressionEngine(engine);
            }
            return hc;
        }
        final BaseHierarchicalConfiguration hc2 = new BaseHierarchicalConfiguration();
        if (engine != null) {
            hc2.setExpressionEngine(engine);
        }
        hc2.copy(conf);
        return hc2;
    }
    
    public static Configuration cloneConfiguration(final Configuration config) throws ConfigurationRuntimeException {
        if (config == null) {
            return null;
        }
        try {
            return (Configuration)clone(config);
        }
        catch (CloneNotSupportedException cnex) {
            throw new ConfigurationRuntimeException(cnex);
        }
    }
    
    public static Object cloneIfPossible(final Object obj) {
        try {
            return clone(obj);
        }
        catch (Exception ex) {
            return obj;
        }
    }
    
    static Object clone(final Object obj) throws CloneNotSupportedException {
        if (obj instanceof Cloneable) {
            try {
                final Method m = obj.getClass().getMethod("clone", (Class<?>[])new Class[0]);
                return m.invoke(obj, new Object[0]);
            }
            catch (NoSuchMethodException nmex) {
                throw new CloneNotSupportedException("No clone() method found for class" + obj.getClass().getName());
            }
            catch (IllegalAccessException iaex) {
                throw new ConfigurationRuntimeException(iaex);
            }
            catch (InvocationTargetException itex) {
                throw new ConfigurationRuntimeException(itex);
            }
        }
        throw new CloneNotSupportedException(obj.getClass().getName() + " does not implement Cloneable");
    }
    
    public static Synchronizer cloneSynchronizer(final Synchronizer sync) {
        if (sync == null) {
            throw new IllegalArgumentException("Synchronizer must not be null!");
        }
        if (NoOpSynchronizer.INSTANCE == sync) {
            return sync;
        }
        try {
            return (Synchronizer)sync.getClass().newInstance();
        }
        catch (Exception ex) {
            ConfigurationUtils.LOG.info("Cannot create new instance of " + sync.getClass());
            try {
                return (Synchronizer)clone(sync);
            }
            catch (CloneNotSupportedException cnex) {
                throw new ConfigurationRuntimeException("Cannot clone Synchronizer " + sync);
            }
        }
    }
    
    public static void enableRuntimeExceptions(final Configuration src) {
        if (!(src instanceof EventSource)) {
            throw new IllegalArgumentException("Configuration must implement EventSource!");
        }
        ((EventSource)src).addEventListener(ConfigurationErrorEvent.ANY, new EventListener<ConfigurationErrorEvent>() {
            @Override
            public void onEvent(final ConfigurationErrorEvent event) {
                throw new ConfigurationRuntimeException(event.getCause());
            }
        });
    }
    
    public static Class<?> loadClass(final String clsName) throws ClassNotFoundException {
        if (ConfigurationUtils.LOG.isDebugEnabled()) {
            ConfigurationUtils.LOG.debug("Loading class " + clsName);
        }
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            if (cl != null) {
                return cl.loadClass(clsName);
            }
        }
        catch (ClassNotFoundException cnfex) {
            ConfigurationUtils.LOG.info("Could not load class " + clsName + " using CCL. Falling back to default CL.", cnfex);
        }
        return ConfigurationUtils.class.getClassLoader().loadClass(clsName);
    }
    
    public static Class<?> loadClassNoEx(final String clsName) {
        try {
            return loadClass(clsName);
        }
        catch (ClassNotFoundException cnfex) {
            throw new ConfigurationRuntimeException("Cannot load class " + clsName, cnfex);
        }
    }
    
    public static ImmutableConfiguration unmodifiableConfiguration(final Configuration c) {
        return createUnmodifiableConfiguration(ConfigurationUtils.IMMUTABLE_CONFIG_IFCS, c);
    }
    
    public static ImmutableHierarchicalConfiguration unmodifiableConfiguration(final HierarchicalConfiguration<?> c) {
        return (ImmutableHierarchicalConfiguration)createUnmodifiableConfiguration(ConfigurationUtils.IMMUTABLE_HIERARCHICAL_CONFIG_IFCS, c);
    }
    
    private static ImmutableConfiguration createUnmodifiableConfiguration(final Class<?>[] ifcs, final Configuration c) {
        return (ImmutableConfiguration)Proxy.newProxyInstance(ConfigurationUtils.class.getClassLoader(), ifcs, new ImmutableConfigurationInvocationHandler(c));
    }
    
    public static EventSource asEventSource(final Object obj, final boolean mockIfUnsupported) {
        if (obj instanceof EventSource) {
            return (EventSource)obj;
        }
        if (!mockIfUnsupported) {
            throw new ConfigurationRuntimeException("Cannot cast to EventSource: " + obj);
        }
        return ConfigurationUtils.DUMMY_EVENT_SOURCE;
    }
    
    static {
        IMMUTABLE_CONFIG_IFCS = new Class[] { ImmutableConfiguration.class };
        IMMUTABLE_HIERARCHICAL_CONFIG_IFCS = new Class[] { ImmutableHierarchicalConfiguration.class };
        DUMMY_EVENT_SOURCE = new EventSource() {
            @Override
            public <T extends Event> void addEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
            }
            
            @Override
            public <T extends Event> boolean removeEventListener(final EventType<T> eventType, final EventListener<? super T> listener) {
                return false;
            }
        };
        LOG = LogFactory.getLog(ConfigurationUtils.class);
    }
}
