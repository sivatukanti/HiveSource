// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.apache.commons.logging.Log;

class CommonsLogger extends AbstractInternalLogger
{
    private final Log logger;
    private final String loggerName;
    
    CommonsLogger(final Log logger, final String loggerName) {
        this.logger = logger;
        this.loggerName = loggerName;
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
        return this.logger.isErrorEnabled();
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }
    
    public void warn(final String msg) {
        this.logger.warn(msg);
    }
    
    public void warn(final String msg, final Throwable cause) {
        this.logger.warn(msg, cause);
    }
    
    @Override
    public String toString() {
        return this.loggerName;
    }
}
