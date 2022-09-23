// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.apache.log4j.Logger;

public class Log4JLogger extends NucleusLogger
{
    private Logger logger;
    
    public Log4JLogger(final String logName) {
        this.logger = null;
        this.logger = Logger.getLogger(logName);
    }
    
    @Override
    public void debug(final Object msg) {
        this.logger.debug(msg);
    }
    
    @Override
    public void debug(final Object msg, final Throwable thr) {
        this.logger.debug(msg, thr);
    }
    
    @Override
    public void info(final Object msg) {
        this.logger.info(msg);
    }
    
    @Override
    public void info(final Object msg, final Throwable thr) {
        this.logger.info(msg, thr);
    }
    
    @Override
    public void warn(final Object msg) {
        this.logger.warn(msg);
    }
    
    @Override
    public void warn(final Object msg, final Throwable thr) {
        this.logger.warn(msg, thr);
    }
    
    @Override
    public void error(final Object msg) {
        this.logger.error(msg);
    }
    
    @Override
    public void error(final Object msg, final Throwable thr) {
        this.logger.error(msg, thr);
    }
    
    @Override
    public void fatal(final Object msg) {
        this.logger.fatal(msg);
    }
    
    @Override
    public void fatal(final Object msg, final Throwable thr) {
        this.logger.fatal(msg, thr);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
}
