// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDK14Logger extends NucleusLogger
{
    private final Logger logger;
    
    public JDK14Logger(final String logName) {
        this.logger = Logger.getLogger(logName);
    }
    
    @Override
    public void debug(final Object msg) {
        this.log(Level.FINE, msg, null);
    }
    
    @Override
    public void debug(final Object msg, final Throwable thr) {
        this.log(Level.FINE, msg, thr);
    }
    
    @Override
    public void info(final Object msg) {
        this.log(Level.INFO, msg, null);
    }
    
    @Override
    public void info(final Object msg, final Throwable thr) {
        this.log(Level.INFO, msg, thr);
    }
    
    @Override
    public void warn(final Object msg) {
        this.log(Level.WARNING, msg, null);
    }
    
    @Override
    public void warn(final Object msg, final Throwable thr) {
        this.log(Level.WARNING, msg, thr);
    }
    
    @Override
    public void error(final Object msg) {
        this.log(Level.SEVERE, msg, null);
    }
    
    @Override
    public void error(final Object msg, final Throwable thr) {
        this.log(Level.SEVERE, msg, thr);
    }
    
    @Override
    public void fatal(final Object msg) {
        this.log(Level.SEVERE, msg, null);
    }
    
    @Override
    public void fatal(final Object msg, final Throwable thr) {
        this.log(Level.SEVERE, msg, thr);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }
    
    @Override
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }
    
    private void log(Level level, Object msg, final Throwable thrown) {
        if (msg == null) {
            level = Level.SEVERE;
            msg = "Missing [msg] parameter";
        }
        if (this.logger.isLoggable(level)) {
            final LogRecord result = new LogRecord(level, String.valueOf(msg));
            if (thrown != null) {
                result.setThrown(thrown);
            }
            final StackTraceElement[] stacktrace = new Throwable().getStackTrace();
            for (int i = 0; i < stacktrace.length; ++i) {
                final StackTraceElement element = stacktrace[i];
                if (!element.getClassName().equals(JDK14Logger.class.getName())) {
                    result.setSourceClassName(element.getClassName());
                    result.setSourceMethodName(element.getMethodName());
                    break;
                }
            }
            this.logger.log(result);
        }
    }
}
