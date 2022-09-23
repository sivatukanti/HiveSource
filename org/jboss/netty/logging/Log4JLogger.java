// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.apache.log4j.Logger;

class Log4JLogger extends AbstractInternalLogger
{
    private final Logger logger;
    
    Log4JLogger(final Logger logger) {
        this.logger = logger;
    }
    
    public void debug(final String msg) {
        this.logger.debug(msg);
    }
    
    public void debug(final String msg, final Throwable cause) {
        this.logger.debug(msg, cause);
    }
    
    public void error(final String msg) {
        this.logger.error(msg);
    }
    
    public void error(final String msg, final Throwable cause) {
        this.logger.error(msg, cause);
    }
    
    public void info(final String msg) {
        this.logger.info(msg);
    }
    
    public void info(final String msg, final Throwable cause) {
        this.logger.info(msg, cause);
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public void warn(final String msg) {
        this.logger.warn(msg);
    }
    
    public void warn(final String msg, final Throwable cause) {
        this.logger.warn(msg, cause);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.logger.getName());
    }
}
