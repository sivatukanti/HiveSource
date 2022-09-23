// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.jboss.logging.Logger;

class JBossLogger extends AbstractInternalLogger
{
    private final Logger logger;
    
    JBossLogger(final Logger logger) {
        this.logger = logger;
    }
    
    public void debug(final String msg) {
        this.logger.debug((Object)msg);
    }
    
    public void debug(final String msg, final Throwable cause) {
        this.logger.debug((Object)msg, cause);
    }
    
    public void error(final String msg) {
        this.logger.error((Object)msg);
    }
    
    public void error(final String msg, final Throwable cause) {
        this.logger.error((Object)msg, cause);
    }
    
    public void info(final String msg) {
        this.logger.info((Object)msg);
    }
    
    public void info(final String msg, final Throwable cause) {
        this.logger.info((Object)msg, cause);
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
        this.logger.warn((Object)msg);
    }
    
    public void warn(final String msg, final Throwable cause) {
        this.logger.warn((Object)msg, cause);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.logger.getName());
    }
}
