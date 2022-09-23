// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.NoOpLog;
import org.apache.commons.logging.Log;

public class ConfigurationLogger
{
    private final Log log;
    
    public ConfigurationLogger(final String loggerName) {
        this(createLoggerForName(loggerName));
    }
    
    public ConfigurationLogger(final Class<?> logCls) {
        this(createLoggerForClass(logCls));
    }
    
    protected ConfigurationLogger() {
        this((Log)null);
    }
    
    ConfigurationLogger(final Log wrapped) {
        this.log = wrapped;
    }
    
    public static ConfigurationLogger newDummyLogger() {
        return new ConfigurationLogger(new NoOpLog());
    }
    
    public boolean isDebugEnabled() {
        return this.getLog().isDebugEnabled();
    }
    
    public void debug(final String msg) {
        this.getLog().debug(msg);
    }
    
    public boolean isInfoEnabled() {
        return this.getLog().isInfoEnabled();
    }
    
    public void info(final String msg) {
        this.getLog().info(msg);
    }
    
    public void warn(final String msg) {
        this.getLog().warn(msg);
    }
    
    public void warn(final String msg, final Throwable ex) {
        this.getLog().warn(msg, ex);
    }
    
    public void error(final String msg) {
        this.getLog().error(msg);
    }
    
    public void error(final String msg, final Throwable ex) {
        this.getLog().error(msg, ex);
    }
    
    Log getLog() {
        return this.log;
    }
    
    private static Log createLoggerForName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Logger name must not be null!");
        }
        return LogFactory.getLog(name);
    }
    
    private static Log createLoggerForClass(final Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Logger class must not be null!");
        }
        return LogFactory.getLog(cls);
    }
}
