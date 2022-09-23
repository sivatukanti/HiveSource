// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.util.logging.LogRecord;
import java.security.AccessController;
import java.net.URL;
import java.util.logging.LogManager;
import org.eclipse.jetty.util.Loader;
import java.security.PrivilegedAction;
import java.util.logging.Logger;
import java.util.logging.Level;

public class JavaUtilLog extends AbstractLogger
{
    private static final String THIS_CLASS;
    private static final boolean __source;
    private static boolean _initialized;
    private Level configuredLevel;
    private java.util.logging.Logger _logger;
    
    public JavaUtilLog() {
        this("org.eclipse.jetty.util.log.javautil");
    }
    
    public JavaUtilLog(final String name) {
        synchronized (JavaUtilLog.class) {
            if (!JavaUtilLog._initialized) {
                JavaUtilLog._initialized = true;
                final String properties = Log.__props.getProperty("org.eclipse.jetty.util.log.javautil.PROPERTIES", null);
                if (properties != null) {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                        @Override
                        public Object run() {
                            try {
                                final URL props = Loader.getResource(JavaUtilLog.class, properties);
                                if (props != null) {
                                    LogManager.getLogManager().readConfiguration(props.openStream());
                                }
                            }
                            catch (Throwable e) {
                                System.err.println("[WARN] Error loading logging config: " + properties);
                                e.printStackTrace(System.err);
                            }
                            return null;
                        }
                    });
                }
            }
        }
        this._logger = java.util.logging.Logger.getLogger(name);
        switch (AbstractLogger.lookupLoggingLevel(Log.__props, name)) {
            case 0: {
                this._logger.setLevel(Level.ALL);
                break;
            }
            case 1: {
                this._logger.setLevel(Level.FINE);
                break;
            }
            case 2: {
                this._logger.setLevel(Level.INFO);
                break;
            }
            case 3: {
                this._logger.setLevel(Level.WARNING);
                break;
            }
            case 10: {
                this._logger.setLevel(Level.OFF);
                break;
            }
        }
        this.configuredLevel = this._logger.getLevel();
    }
    
    @Override
    public String getName() {
        return this._logger.getName();
    }
    
    protected void log(final Level level, final String msg, final Throwable thrown) {
        final LogRecord record = new LogRecord(level, msg);
        if (thrown != null) {
            record.setThrown(thrown);
        }
        record.setLoggerName(this._logger.getName());
        if (JavaUtilLog.__source) {
            final StackTraceElement[] stack = new Throwable().getStackTrace();
            for (int i = 0; i < stack.length; ++i) {
                final StackTraceElement e = stack[i];
                if (!e.getClassName().equals(JavaUtilLog.THIS_CLASS)) {
                    record.setSourceClassName(e.getClassName());
                    record.setSourceMethodName(e.getMethodName());
                    break;
                }
            }
        }
        this._logger.log(record);
    }
    
    @Override
    public void warn(final String msg, final Object... args) {
        if (this._logger.isLoggable(Level.WARNING)) {
            this.log(Level.WARNING, this.format(msg, args), null);
        }
    }
    
    @Override
    public void warn(final Throwable thrown) {
        if (this._logger.isLoggable(Level.WARNING)) {
            this.log(Level.WARNING, "", thrown);
        }
    }
    
    @Override
    public void warn(final String msg, final Throwable thrown) {
        if (this._logger.isLoggable(Level.WARNING)) {
            this.log(Level.WARNING, msg, thrown);
        }
    }
    
    @Override
    public void info(final String msg, final Object... args) {
        if (this._logger.isLoggable(Level.INFO)) {
            this.log(Level.INFO, this.format(msg, args), null);
        }
    }
    
    @Override
    public void info(final Throwable thrown) {
        if (this._logger.isLoggable(Level.INFO)) {
            this.log(Level.INFO, "", thrown);
        }
    }
    
    @Override
    public void info(final String msg, final Throwable thrown) {
        if (this._logger.isLoggable(Level.INFO)) {
            this.log(Level.INFO, msg, thrown);
        }
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this._logger.isLoggable(Level.FINE);
    }
    
    @Override
    public void setDebugEnabled(final boolean enabled) {
        if (enabled) {
            this.configuredLevel = this._logger.getLevel();
            this._logger.setLevel(Level.FINE);
        }
        else {
            this._logger.setLevel(this.configuredLevel);
        }
    }
    
    @Override
    public void debug(final String msg, final Object... args) {
        if (this._logger.isLoggable(Level.FINE)) {
            this.log(Level.FINE, this.format(msg, args), null);
        }
    }
    
    @Override
    public void debug(final String msg, final long arg) {
        if (this._logger.isLoggable(Level.FINE)) {
            this.log(Level.FINE, this.format(msg, arg), null);
        }
    }
    
    @Override
    public void debug(final Throwable thrown) {
        if (this._logger.isLoggable(Level.FINE)) {
            this.log(Level.FINE, "", thrown);
        }
    }
    
    @Override
    public void debug(final String msg, final Throwable thrown) {
        if (this._logger.isLoggable(Level.FINE)) {
            this.log(Level.FINE, msg, thrown);
        }
    }
    
    @Override
    protected Logger newLogger(final String fullname) {
        return new JavaUtilLog(fullname);
    }
    
    @Override
    public void ignore(final Throwable ignored) {
        if (this._logger.isLoggable(Level.FINEST)) {
            this.log(Level.FINEST, "IGNORED EXCEPTION ", ignored);
        }
    }
    
    private String format(String msg, final Object... args) {
        msg = String.valueOf(msg);
        final String braces = "{}";
        final StringBuilder builder = new StringBuilder();
        int start = 0;
        for (final Object arg : args) {
            final int bracesIndex = msg.indexOf(braces, start);
            if (bracesIndex < 0) {
                builder.append(msg.substring(start));
                builder.append(" ");
                builder.append(arg);
                start = msg.length();
            }
            else {
                builder.append(msg.substring(start, bracesIndex));
                builder.append(String.valueOf(arg));
                start = bracesIndex + braces.length();
            }
        }
        builder.append(msg.substring(start));
        return builder.toString();
    }
    
    static {
        THIS_CLASS = JavaUtilLog.class.getName();
        __source = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.SOURCE", Log.__props.getProperty("org.eclipse.jetty.util.log.javautil.SOURCE", "true")));
        JavaUtilLog._initialized = false;
    }
}
