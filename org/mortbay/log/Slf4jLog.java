// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.log;

import org.slf4j.LoggerFactory;

public class Slf4jLog implements Logger
{
    private org.slf4j.Logger logger;
    
    public Slf4jLog() throws Exception {
        this("org.mortbay.log");
    }
    
    public Slf4jLog(final String name) {
        this.logger = LoggerFactory.getLogger(name);
    }
    
    public void debug(final String msg, final Object arg0, final Object arg1) {
        this.logger.debug(msg, arg0, arg1);
    }
    
    public void debug(final String msg, final Throwable th) {
        this.logger.debug(msg, th);
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public void info(final String msg, final Object arg0, final Object arg1) {
        this.logger.info(msg, arg0, arg1);
    }
    
    public void warn(final String msg, final Object arg0, final Object arg1) {
        this.logger.warn(msg, arg0, arg1);
    }
    
    public void warn(final String msg, final Throwable th) {
        if (th instanceof RuntimeException || th instanceof Error) {
            this.logger.error(msg, th);
        }
        else {
            this.logger.warn(msg, th);
        }
    }
    
    public Logger getLogger(final String name) {
        return new Slf4jLog(name);
    }
    
    public String toString() {
        return this.logger.toString();
    }
    
    public void setDebugEnabled(final boolean enabled) {
        this.warn("setDebugEnabled not implemented", null, null);
    }
}
