// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.component.spi;

import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.component.helpers.MessageFormatter;
import org.apache.log4j.Logger;
import org.apache.log4j.component.ULogger;

public final class Log4JULogger implements ULogger
{
    private final Logger logger;
    
    public Log4JULogger(final Logger l) {
        if (l == null) {
            throw new NullPointerException("l");
        }
        this.logger = l;
    }
    
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    public void debug(final Object msg) {
        this.logger.debug(msg);
    }
    
    public void debug(final Object parameterizedMsg, final Object param1) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(MessageFormatter.format(parameterizedMsg.toString(), param1));
        }
    }
    
    public void debug(final String parameterizedMsg, final Object param1, final Object param2) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(MessageFormatter.format(parameterizedMsg.toString(), param1, param2));
        }
    }
    
    public void debug(final Object msg, final Throwable t) {
        this.logger.debug(msg, t);
    }
    
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    public void info(final Object msg) {
        this.logger.info(msg);
    }
    
    public void info(final Object parameterizedMsg, final Object param1) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(MessageFormatter.format(parameterizedMsg.toString(), param1));
        }
    }
    
    public void info(final String parameterizedMsg, final Object param1, final Object param2) {
        if (this.logger.isInfoEnabled()) {
            this.logger.info(MessageFormatter.format(parameterizedMsg.toString(), param1, param2));
        }
    }
    
    public void info(final Object msg, final Throwable t) {
        this.logger.info(msg, t);
    }
    
    public boolean isWarnEnabled() {
        return this.logger.isEnabledFor(Level.WARN);
    }
    
    public void warn(final Object msg) {
        this.logger.warn(msg);
    }
    
    public void warn(final Object parameterizedMsg, final Object param1) {
        if (this.logger.isEnabledFor(Level.WARN)) {
            this.logger.warn(MessageFormatter.format(parameterizedMsg.toString(), param1));
        }
    }
    
    public void warn(final String parameterizedMsg, final Object param1, final Object param2) {
        if (this.logger.isEnabledFor(Level.WARN)) {
            this.logger.warn(MessageFormatter.format(parameterizedMsg.toString(), param1, param2));
        }
    }
    
    public void warn(final Object msg, final Throwable t) {
        this.logger.warn(msg, t);
    }
    
    public boolean isErrorEnabled() {
        return this.logger.isEnabledFor(Level.ERROR);
    }
    
    public void error(final Object msg) {
        this.logger.error(msg);
    }
    
    public void error(final Object parameterizedMsg, final Object param1) {
        if (this.logger.isEnabledFor(Level.ERROR)) {
            this.logger.error(MessageFormatter.format(parameterizedMsg.toString(), param1));
        }
    }
    
    public void error(final String parameterizedMsg, final Object param1, final Object param2) {
        if (this.logger.isEnabledFor(Level.ERROR)) {
            this.logger.error(MessageFormatter.format(parameterizedMsg.toString(), param1, param2));
        }
    }
    
    public void error(final Object msg, final Throwable t) {
        this.logger.error(msg, t);
    }
}
