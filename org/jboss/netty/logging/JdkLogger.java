// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

class JdkLogger extends AbstractInternalLogger
{
    private final Logger logger;
    private final String loggerName;
    
    JdkLogger(final Logger logger, final String loggerName) {
        this.logger = logger;
        this.loggerName = loggerName;
    }
    
    public void debug(final String msg) {
        this.logger.logp(Level.FINE, this.loggerName, null, msg);
    }
    
    public void debug(final String msg, final Throwable cause) {
        this.logger.logp(Level.FINE, this.loggerName, null, msg, cause);
    }
    
    public void error(final String msg) {
        this.logger.logp(Level.SEVERE, this.loggerName, null, msg);
    }
    
    public void error(final String msg, final Throwable cause) {
        this.logger.logp(Level.SEVERE, this.loggerName, null, msg, cause);
    }
    
    public void info(final String msg) {
        this.logger.logp(Level.INFO, this.loggerName, null, msg);
    }
    
    public void info(final String msg, final Throwable cause) {
        this.logger.logp(Level.INFO, this.loggerName, null, msg, cause);
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isLoggable(Level.FINE);
    }
    
    public boolean isErrorEnabled() {
        return this.logger.isLoggable(Level.SEVERE);
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isLoggable(Level.INFO);
    }
    
    public boolean isWarnEnabled() {
        return this.logger.isLoggable(Level.WARNING);
    }
    
    public void warn(final String msg) {
        this.logger.logp(Level.WARNING, this.loggerName, null, msg);
    }
    
    public void warn(final String msg, final Throwable cause) {
        this.logger.logp(Level.WARNING, this.loggerName, null, msg, cause);
    }
    
    @Override
    public String toString() {
        return this.loggerName;
    }
}
