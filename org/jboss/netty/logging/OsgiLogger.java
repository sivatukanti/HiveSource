// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

import org.osgi.service.log.LogService;

class OsgiLogger extends AbstractInternalLogger
{
    private final OsgiLoggerFactory parent;
    private final InternalLogger fallback;
    private final String name;
    private final String prefix;
    
    OsgiLogger(final OsgiLoggerFactory parent, final String name, final InternalLogger fallback) {
        this.parent = parent;
        this.name = name;
        this.fallback = fallback;
        this.prefix = '[' + name + "] ";
    }
    
    public void debug(final String msg) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(4, this.prefix + msg);
        }
        else {
            this.fallback.debug(msg);
        }
    }
    
    public void debug(final String msg, final Throwable cause) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(4, this.prefix + msg, cause);
        }
        else {
            this.fallback.debug(msg, cause);
        }
    }
    
    public void error(final String msg) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(1, this.prefix + msg);
        }
        else {
            this.fallback.error(msg);
        }
    }
    
    public void error(final String msg, final Throwable cause) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(1, this.prefix + msg, cause);
        }
        else {
            this.fallback.error(msg, cause);
        }
    }
    
    public void info(final String msg) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(3, this.prefix + msg);
        }
        else {
            this.fallback.info(msg);
        }
    }
    
    public void info(final String msg, final Throwable cause) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(3, this.prefix + msg, cause);
        }
        else {
            this.fallback.info(msg, cause);
        }
    }
    
    public boolean isDebugEnabled() {
        return true;
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public void warn(final String msg) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(2, this.prefix + msg);
        }
        else {
            this.fallback.warn(msg);
        }
    }
    
    public void warn(final String msg, final Throwable cause) {
        final LogService logService = this.parent.getLogService();
        if (logService != null) {
            logService.log(2, this.prefix + msg, cause);
        }
        else {
            this.fallback.warn(msg, cause);
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
