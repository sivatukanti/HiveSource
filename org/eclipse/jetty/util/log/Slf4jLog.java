// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Slf4jLog extends AbstractLogger
{
    private final org.slf4j.Logger _logger;
    
    public Slf4jLog() throws Exception {
        this("org.eclipse.jetty.util.log");
    }
    
    public Slf4jLog(final String name) {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(name);
        if (logger instanceof LocationAwareLogger) {
            this._logger = new JettyAwareLogger((LocationAwareLogger)logger);
        }
        else {
            this._logger = logger;
        }
    }
    
    @Override
    public String getName() {
        return this._logger.getName();
    }
    
    @Override
    public void warn(final String msg, final Object... args) {
        this._logger.warn(msg, args);
    }
    
    @Override
    public void warn(final Throwable thrown) {
        this.warn("", thrown);
    }
    
    @Override
    public void warn(final String msg, final Throwable thrown) {
        this._logger.warn(msg, thrown);
    }
    
    @Override
    public void info(final String msg, final Object... args) {
        this._logger.info(msg, args);
    }
    
    @Override
    public void info(final Throwable thrown) {
        this.info("", thrown);
    }
    
    @Override
    public void info(final String msg, final Throwable thrown) {
        this._logger.info(msg, thrown);
    }
    
    @Override
    public void debug(final String msg, final Object... args) {
        this._logger.debug(msg, args);
    }
    
    @Override
    public void debug(final String msg, final long arg) {
        if (this.isDebugEnabled()) {
            this._logger.debug(msg, new Object[] { new Long(arg) });
        }
    }
    
    @Override
    public void debug(final Throwable thrown) {
        this.debug("", thrown);
    }
    
    @Override
    public void debug(final String msg, final Throwable thrown) {
        this._logger.debug(msg, thrown);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this._logger.isDebugEnabled();
    }
    
    @Override
    public void setDebugEnabled(final boolean enabled) {
        this.warn("setDebugEnabled not implemented", null, null);
    }
    
    @Override
    protected Logger newLogger(final String fullname) {
        return new Slf4jLog(fullname);
    }
    
    @Override
    public void ignore(final Throwable ignored) {
        if (Log.isIgnored()) {
            this.debug("IGNORED EXCEPTION ", ignored);
        }
    }
    
    @Override
    public String toString() {
        return this._logger.toString();
    }
}
