// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.commons.logging.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.Logger;
import java.io.Serializable;
import org.apache.htrace.shaded.commons.logging.Log;

public class Log4JLogger implements Log, Serializable
{
    private static final String FQCN;
    private transient Logger logger;
    private String name;
    private static Priority traceLevel;
    
    public Log4JLogger() {
        this.logger = null;
        this.name = null;
    }
    
    public Log4JLogger(final String name) {
        this.logger = null;
        this.name = null;
        this.name = name;
        this.logger = this.getLogger();
    }
    
    public Log4JLogger(final Logger logger) {
        this.logger = null;
        this.name = null;
        if (logger == null) {
            throw new IllegalArgumentException("Warning - null logger in constructor; possible log4j misconfiguration.");
        }
        this.name = logger.getName();
        this.logger = logger;
    }
    
    public void trace(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Log4JLogger.traceLevel, message, null);
    }
    
    public void trace(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Log4JLogger.traceLevel, message, t);
    }
    
    public void debug(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.DEBUG, message, null);
    }
    
    public void debug(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.DEBUG, message, t);
    }
    
    public void info(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.INFO, message, null);
    }
    
    public void info(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.INFO, message, t);
    }
    
    public void warn(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.WARN, message, null);
    }
    
    public void warn(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.WARN, message, t);
    }
    
    public void error(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.ERROR, message, null);
    }
    
    public void error(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.ERROR, message, t);
    }
    
    public void fatal(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.FATAL, message, null);
    }
    
    public void fatal(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Priority.FATAL, message, t);
    }
    
    public Logger getLogger() {
        if (this.logger == null) {
            this.logger = Logger.getLogger(this.name);
        }
        return this.logger;
    }
    
    public boolean isDebugEnabled() {
        return this.getLogger().isDebugEnabled();
    }
    
    public boolean isErrorEnabled() {
        return this.getLogger().isEnabledFor(Priority.ERROR);
    }
    
    public boolean isFatalEnabled() {
        return this.getLogger().isEnabledFor(Priority.FATAL);
    }
    
    public boolean isInfoEnabled() {
        return this.getLogger().isInfoEnabled();
    }
    
    public boolean isTraceEnabled() {
        return this.getLogger().isEnabledFor(Log4JLogger.traceLevel);
    }
    
    public boolean isWarnEnabled() {
        return this.getLogger().isEnabledFor(Priority.WARN);
    }
    
    static {
        FQCN = Log4JLogger.class.getName();
        if (!Priority.class.isAssignableFrom(Level.class)) {
            throw new InstantiationError("Log4J 1.2 not available");
        }
        try {
            Log4JLogger.traceLevel = (Priority)Level.class.getDeclaredField("TRACE").get(null);
        }
        catch (Exception ex) {
            Log4JLogger.traceLevel = Priority.DEBUG;
        }
    }
}
