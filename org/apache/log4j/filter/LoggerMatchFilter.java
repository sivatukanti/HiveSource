// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;

public class LoggerMatchFilter extends Filter
{
    private boolean acceptOnMatch;
    private String loggerToMatch;
    
    public LoggerMatchFilter() {
        this.acceptOnMatch = true;
        this.loggerToMatch = "root";
    }
    
    public void setLoggerToMatch(final String logger) {
        if (logger == null) {
            this.loggerToMatch = "root";
        }
        else {
            this.loggerToMatch = logger;
        }
    }
    
    public String getLoggerToMatch() {
        return this.loggerToMatch;
    }
    
    public void setAcceptOnMatch(final boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
    
    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }
    
    public int decide(final LoggingEvent event) {
        final boolean matchOccured = this.loggerToMatch.equals(event.getLoggerName());
        if (!matchOccured) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
    }
}
