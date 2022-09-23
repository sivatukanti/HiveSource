// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.logging;

public abstract class AbstractInternalLogger implements InternalLogger
{
    protected AbstractInternalLogger() {
    }
    
    public boolean isEnabled(final InternalLogLevel level) {
        switch (level) {
            case DEBUG: {
                return this.isDebugEnabled();
            }
            case INFO: {
                return this.isInfoEnabled();
            }
            case WARN: {
                return this.isWarnEnabled();
            }
            case ERROR: {
                return this.isErrorEnabled();
            }
            default: {
                throw new Error();
            }
        }
    }
    
    public void log(final InternalLogLevel level, final String msg, final Throwable cause) {
        switch (level) {
            case DEBUG: {
                this.debug(msg, cause);
                break;
            }
            case INFO: {
                this.info(msg, cause);
                break;
            }
            case WARN: {
                this.warn(msg, cause);
                break;
            }
            case ERROR: {
                this.error(msg, cause);
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    public void log(final InternalLogLevel level, final String msg) {
        switch (level) {
            case DEBUG: {
                this.debug(msg);
                break;
            }
            case INFO: {
                this.info(msg);
                break;
            }
            case WARN: {
                this.warn(msg);
                break;
            }
            case ERROR: {
                this.error(msg);
                break;
            }
            default: {
                throw new Error();
            }
        }
    }
}
