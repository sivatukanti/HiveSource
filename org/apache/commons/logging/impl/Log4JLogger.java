// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.logging.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.Logger;
import java.io.Serializable;
import org.apache.commons.logging.Log;

public class Log4JLogger implements Log, Serializable
{
    private static final long serialVersionUID = 5160705895411730424L;
    private static final String FQCN;
    private transient volatile Logger logger;
    private final String name;
    private static final Priority traceLevel;
    
    public Log4JLogger() {
        this.logger = null;
        this.name = null;
    }
    
    public Log4JLogger(final String name) {
        this.logger = null;
        this.name = name;
        this.logger = this.getLogger();
    }
    
    public Log4JLogger(final Logger logger) {
        this.logger = null;
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
        this.getLogger().log(Log4JLogger.FQCN, Level.DEBUG, message, null);
    }
    
    public void debug(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Level.DEBUG, message, t);
    }
    
    public void info(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Level.INFO, message, null);
    }
    
    public void info(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Level.INFO, message, t);
    }
    
    public void warn(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Level.WARN, message, null);
    }
    
    public void warn(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Level.WARN, message, t);
    }
    
    public void error(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Level.ERROR, message, null);
    }
    
    public void error(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Level.ERROR, message, t);
    }
    
    public void fatal(final Object message) {
        this.getLogger().log(Log4JLogger.FQCN, Level.FATAL, message, null);
    }
    
    public void fatal(final Object message, final Throwable t) {
        this.getLogger().log(Log4JLogger.FQCN, Level.FATAL, message, t);
    }
    
    public Logger getLogger() {
        Logger result = this.logger;
        if (result == null) {
            synchronized (this) {
                result = this.logger;
                if (result == null) {
                    result = (this.logger = Logger.getLogger(this.name));
                }
            }
        }
        return result;
    }
    
    public boolean isDebugEnabled() {
        return this.getLogger().isDebugEnabled();
    }
    
    public boolean isErrorEnabled() {
        return this.getLogger().isEnabledFor(Level.ERROR);
    }
    
    public boolean isFatalEnabled() {
        return this.getLogger().isEnabledFor(Level.FATAL);
    }
    
    public boolean isInfoEnabled() {
        return this.getLogger().isInfoEnabled();
    }
    
    public boolean isTraceEnabled() {
        return this.getLogger().isEnabledFor(Log4JLogger.traceLevel);
    }
    
    public boolean isWarnEnabled() {
        return this.getLogger().isEnabledFor(Level.WARN);
    }
    
    static {
        FQCN = Log4JLogger.class.getName();
        if (!Priority.class.isAssignableFrom(Level.class)) {
            throw new InstantiationError("Log4J 1.2 not available");
        }
        Priority _traceLevel;
        try {
            _traceLevel = (Priority)Level.class.getDeclaredField("TRACE").get(null);
        }
        catch (Exception ex) {
            _traceLevel = Level.DEBUG;
        }
        traceLevel = _traceLevel;
    }
}
