// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.Logger;
import org.apache.commons.logging.Log;

class LogAdapter
{
    private Log LOG;
    private Logger LOGGER;
    
    private LogAdapter(final Log LOG) {
        this.LOG = LOG;
    }
    
    private LogAdapter(final Logger LOGGER) {
        this.LOGGER = LOGGER;
    }
    
    @Deprecated
    public static LogAdapter create(final Log LOG) {
        return new LogAdapter(LOG);
    }
    
    public static LogAdapter create(final Logger LOGGER) {
        return new LogAdapter(LOGGER);
    }
    
    public void info(final String msg) {
        if (this.LOG != null) {
            this.LOG.info(msg);
        }
        else if (this.LOGGER != null) {
            this.LOGGER.info(msg);
        }
    }
    
    public void warn(final String msg, final Throwable t) {
        if (this.LOG != null) {
            this.LOG.warn(msg, t);
        }
        else if (this.LOGGER != null) {
            this.LOGGER.warn(msg, t);
        }
    }
    
    public void debug(final Throwable t) {
        if (this.LOG != null) {
            this.LOG.debug(t);
        }
        else if (this.LOGGER != null) {
            this.LOGGER.debug("", t);
        }
    }
    
    public void error(final String msg) {
        if (this.LOG != null) {
            this.LOG.error(msg);
        }
        else if (this.LOGGER != null) {
            this.LOGGER.error(msg);
        }
    }
}
