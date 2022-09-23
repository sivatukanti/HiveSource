// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.component.ULogger;

public final class NOPULogger implements ULogger
{
    public static final NOPULogger NOP_LOGGER;
    
    private NOPULogger() {
    }
    
    public static NOPULogger getLogger(final String name) {
        return NOPULogger.NOP_LOGGER;
    }
    
    public boolean isDebugEnabled() {
        return false;
    }
    
    public void debug(final Object msg) {
    }
    
    public void debug(final Object parameterizedMsg, final Object param1) {
    }
    
    public void debug(final String parameterizedMsg, final Object param1, final Object param2) {
    }
    
    public void debug(final Object msg, final Throwable t) {
    }
    
    public boolean isInfoEnabled() {
        return false;
    }
    
    public void info(final Object msg) {
    }
    
    public void info(final Object parameterizedMsg, final Object param1) {
    }
    
    public void info(final String parameterizedMsg, final Object param1, final Object param2) {
    }
    
    public void info(final Object msg, final Throwable t) {
    }
    
    public boolean isWarnEnabled() {
        return false;
    }
    
    public void warn(final Object msg) {
    }
    
    public void warn(final Object parameterizedMsg, final Object param1) {
    }
    
    public void warn(final String parameterizedMsg, final Object param1, final Object param2) {
    }
    
    public void warn(final Object msg, final Throwable t) {
    }
    
    public boolean isErrorEnabled() {
        return false;
    }
    
    public void error(final Object msg) {
    }
    
    public void error(final Object parameterizedMsg, final Object param1) {
    }
    
    public void error(final String parameterizedMsg, final Object param1, final Object param2) {
    }
    
    public void error(final Object msg, final Throwable t) {
    }
    
    static {
        NOP_LOGGER = new NOPULogger();
    }
}
