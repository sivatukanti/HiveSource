// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.security.AccessController;
import java.util.Enumeration;
import java.util.Locale;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.Collections;
import java.util.Map;
import java.lang.reflect.Method;
import org.eclipse.jetty.util.Uptime;
import java.util.Iterator;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import org.eclipse.jetty.util.Loader;
import java.util.concurrent.ConcurrentMap;
import java.util.Properties;

public class Log
{
    public static final String EXCEPTION = "EXCEPTION ";
    public static final String IGNORED = "IGNORED EXCEPTION ";
    protected static final Properties __props;
    public static String __logClass;
    public static boolean __ignored;
    private static final ConcurrentMap<String, Logger> __loggers;
    private static Logger LOG;
    private static boolean __initialized;
    
    static void loadProperties(final String resourceName, final Properties props) {
        final URL testProps = Loader.getResource(Log.class, resourceName);
        if (testProps != null) {
            try (final InputStream in = testProps.openStream()) {
                final Properties p = new Properties();
                p.load(in);
                for (final Object key : p.keySet()) {
                    final Object value = p.get(key);
                    if (value != null) {
                        props.put(key, value);
                    }
                }
            }
            catch (IOException e) {
                System.err.println("[WARN] Error loading logging config: " + testProps);
                e.printStackTrace(System.err);
            }
        }
    }
    
    public static void initialized() {
        synchronized (Log.class) {
            if (Log.__initialized) {
                return;
            }
            Log.__initialized = true;
            final Boolean announce = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.announce", "true"));
            try {
                final Class<?> log_class = (Class<?>)((Log.__logClass == null) ? null : Loader.loadClass(Log.class, Log.__logClass));
                if (Log.LOG == null || (log_class != null && !Log.LOG.getClass().equals(log_class))) {
                    Log.LOG = (Logger)log_class.newInstance();
                    if (announce) {
                        Log.LOG.debug("Logging to {} via {}", Log.LOG, log_class.getName());
                    }
                }
            }
            catch (Throwable e) {
                initStandardLogging(e);
            }
            if (announce && Log.LOG != null) {
                Log.LOG.info(String.format("Logging initialized @%dms", Uptime.getUptime()), new Object[0]);
            }
        }
    }
    
    private static void initStandardLogging(final Throwable e) {
        if (e != null && Log.__ignored) {
            e.printStackTrace(System.err);
        }
        if (Log.LOG == null) {
            final Class<?> log_class = StdErrLog.class;
            Log.LOG = new StdErrLog();
            final Boolean announce = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.announce", "true"));
            if (announce) {
                Log.LOG.debug("Logging to {} via {}", Log.LOG, log_class.getName());
            }
        }
    }
    
    public static Logger getLog() {
        initialized();
        return Log.LOG;
    }
    
    public static void setLog(final Logger log) {
        Log.LOG = log;
        Log.__logClass = null;
    }
    
    public static Logger getRootLogger() {
        initialized();
        return Log.LOG;
    }
    
    static boolean isIgnored() {
        return Log.__ignored;
    }
    
    public static void setLogToParent(final String name) {
        final ClassLoader loader = Log.class.getClassLoader();
        if (loader != null && loader.getParent() != null) {
            try {
                final Class<?> uberlog = loader.getParent().loadClass("org.eclipse.jetty.util.log.Log");
                final Method getLogger = uberlog.getMethod("getLogger", String.class);
                final Object logger = getLogger.invoke(null, name);
                setLog(new LoggerLog(logger));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            setLog(getLogger(name));
        }
    }
    
    public static Logger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getName());
    }
    
    public static Logger getLogger(final String name) {
        initialized();
        if (name == null) {
            return Log.LOG;
        }
        Logger logger = Log.__loggers.get(name);
        if (logger == null) {
            logger = Log.LOG.getLogger(name);
        }
        return logger;
    }
    
    static ConcurrentMap<String, Logger> getMutableLoggers() {
        return Log.__loggers;
    }
    
    @ManagedAttribute("list of all instantiated loggers")
    public static Map<String, Logger> getLoggers() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends Logger>)Log.__loggers);
    }
    
    public static Properties getProperties() {
        return Log.__props;
    }
    
    static {
        __loggers = new ConcurrentHashMap<String, Logger>();
        __props = new Properties();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                Log.loadProperties("jetty-logging.properties", Log.__props);
                String osName = System.getProperty("os.name");
                if (osName != null && osName.length() > 0) {
                    osName = osName.toLowerCase(Locale.ENGLISH).replace(' ', '-');
                    Log.loadProperties("jetty-logging-" + osName + ".properties", Log.__props);
                }
                final Enumeration<String> systemKeyEnum = (Enumeration<String>)System.getProperties().propertyNames();
                while (systemKeyEnum.hasMoreElements()) {
                    final String key = systemKeyEnum.nextElement();
                    final String val = System.getProperty(key);
                    if (val != null) {
                        Log.__props.setProperty(key, val);
                    }
                }
                Log.__logClass = Log.__props.getProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.Slf4jLog");
                Log.__ignored = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.IGNORED", "false"));
                return null;
            }
        });
        Log.__initialized = false;
    }
}
