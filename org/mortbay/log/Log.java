// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.log;

import org.mortbay.util.Loader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;

public class Log
{
    private static final String[] __nestedEx;
    private static final Class[] __noArgs;
    public static final String EXCEPTION = "EXCEPTION ";
    public static final String IGNORED = "IGNORED";
    public static final String IGNORED_FMT = "IGNORED: {}";
    public static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED ";
    public static String __logClass;
    public static boolean __verbose;
    public static boolean __ignored;
    private static Logger __log;
    
    public static void setLog(final Logger log) {
        Log.__log = log;
    }
    
    public static Logger getLog() {
        return Log.__log;
    }
    
    public static void debug(final Throwable th) {
        if (Log.__log == null || !isDebugEnabled()) {
            return;
        }
        Log.__log.debug("EXCEPTION ", th);
        unwind(th);
    }
    
    public static void debug(final String msg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.debug(msg, null, null);
    }
    
    public static void debug(final String msg, final Object arg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.debug(msg, arg, null);
    }
    
    public static void debug(final String msg, final Object arg0, final Object arg1) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.debug(msg, arg0, arg1);
    }
    
    public static void ignore(final Throwable th) {
        if (Log.__log == null) {
            return;
        }
        if (Log.__ignored) {
            Log.__log.warn("IGNORED", th);
            unwind(th);
        }
        else if (Log.__verbose) {
            Log.__log.debug("IGNORED", th);
            unwind(th);
        }
    }
    
    public static void info(final String msg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.info(msg, null, null);
    }
    
    public static void info(final String msg, final Object arg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.info(msg, arg, null);
    }
    
    public static void info(final String msg, final Object arg0, final Object arg1) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.info(msg, arg0, arg1);
    }
    
    public static boolean isDebugEnabled() {
        return Log.__log != null && Log.__log.isDebugEnabled();
    }
    
    public static void warn(final String msg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.warn(msg, null, null);
    }
    
    public static void warn(final String msg, final Object arg) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.warn(msg, arg, null);
    }
    
    public static void warn(final String msg, final Object arg0, final Object arg1) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.warn(msg, arg0, arg1);
    }
    
    public static void warn(final String msg, final Throwable th) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.warn(msg, th);
        unwind(th);
    }
    
    public static void warn(final Throwable th) {
        if (Log.__log == null) {
            return;
        }
        Log.__log.warn("EXCEPTION ", th);
        unwind(th);
    }
    
    public static Logger getLogger(final String name) {
        if (Log.__log == null) {
            return Log.__log;
        }
        if (name == null) {
            return Log.__log;
        }
        return Log.__log.getLogger(name);
    }
    
    private static void unwind(final Throwable th) {
        if (th == null) {
            return;
        }
        for (int i = 0; i < Log.__nestedEx.length; ++i) {
            try {
                final Method get_target = th.getClass().getMethod(Log.__nestedEx[i], (Class<?>[])Log.__noArgs);
                final Throwable th2 = (Throwable)get_target.invoke(th, (Object[])null);
                if (th2 != null && th2 != th) {
                    warn("Nested in " + th + ":", th2);
                }
            }
            catch (Exception ex) {}
        }
    }
    
    static {
        __nestedEx = new String[] { "getTargetException", "getTargetError", "getException", "getRootCause" };
        __noArgs = new Class[0];
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                Log.__logClass = System.getProperty("org.mortbay.log.class", "org.mortbay.log.Slf4jLog");
                Log.__verbose = (System.getProperty("VERBOSE", null) != null);
                Log.__ignored = (System.getProperty("IGNORED", null) != null);
                return new Boolean(true);
            }
        });
        Class log_class = null;
        try {
            log_class = Loader.loadClass(Log.class, Log.__logClass);
            Log.__log = log_class.newInstance();
        }
        catch (Throwable e) {
            log_class = StdErrLog.class;
            Log.__log = new StdErrLog();
            Log.__logClass = log_class.getName();
            if (Log.__verbose) {
                e.printStackTrace();
            }
        }
        Log.__log.info("Logging to {} via {}", Log.__log, log_class.getName());
    }
}
